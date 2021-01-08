//-----------------------------------------------------------------------------
//
// (C) Brandon Valosek, 2011 <bvalosek@gmail.com>
// (C) Willi Ye, 2015 <williye97@gmail.com>
//
//-----------------------------------------------------------------------------
// Modified by Willi Ye to work with big.LITTLE

package com.bvalosek.cpuspy;

import android.os.SystemClock;
import androidx.annotation.NonNull;
import android.util.SparseArray;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Log;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.kernel.cpu.CPUFreq;
import com.thunder.thundertweaks.utils.kernel.gpu.GPUFreq;
import com.thunder.thundertweaks.utils.kernel.gpu.GPUFreqExynos;
import com.thunder.thundertweaks.utils.kernel.gpu.GPUFreqTmu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CpuStateMonitor is a class responsible for querying the system and getting
 * the time-in-state information, as well as allowing the user to set/reset
 * offsets to "restart" the state timers
 */
public class CpuStateMonitor {

    private final int mCore;
    private final String mGpu;

    private final List<CpuState> mStates = new ArrayList<>();
    private final SparseArray<Long> mOffsets = new SparseArray<>();

    CpuStateMonitor(int core, String gpu) {
        mGpu = gpu;
        mCore = core;
    }

    /**
     * exception class
     */
    public static class CpuStateMonitorException extends Exception {
        private CpuStateMonitorException(String s) {
            super(s);
        }
    }

    /**
     * simple struct for states/time
     */
    public static class CpuState implements Comparable<CpuState> {

        private int mFreq = 0;
        private long mDuration = 0;

        /**
         * init with freq and duration
         */
        private CpuState(int a, long b) {
            mFreq = a;
            mDuration = b;
        }

        public int getFreq() {
            return mFreq;
        }

        public long getDuration() {
            return mDuration;
        }

        /**
         * for sorting, compare the freqs
         */
        @Override
        public int compareTo(@NonNull CpuState another) {
            return Integer.compare(mFreq, another.mFreq);
        }
    }

    /**
     * @return List of CpuState with the offsets applied
     */
    public List<CpuState> getStates() {
        List<CpuState> states = new ArrayList<>();

        /*
         * check for an existing offset, and if it's not too big, subtract it
         * from the duration, otherwise just add it to the return List
         */
        for (CpuState state : mStates) {
            long duration = state.getDuration();
            if(mGpu != null || mCore == -1)
                duration = duration/4;
            if (mOffsets.indexOfKey(state.getFreq()) >= 0) {
                long offset = mOffsets.get(state.getFreq());
                if (offset <= duration) {
                    duration -= offset;
                } else {
                    /*
                     * offset > duration implies our offsets are now invalid, so
                     * clear and recall this function
                     */
                    mOffsets.clear();
                    return getStates();
                }
            }

            states.add(new CpuState(state.getFreq(), duration));
        }

        return states;
    }

    /**
     * @return Sum of all state durations including deep sleep, accounting for
     * offsets
     */
    public long getTotalStateTime() {
        long sum = 0;

        for (CpuState state : mStates) {
            sum += state.getDuration();
        }

        if(mGpu != null || mCore == -1)
            sum = sum/4;

        return sum;
    }

    /**
     * @return Map of freq->duration of all the offsets
     */
    SparseArray<Long> getOffsets() {
        return mOffsets;
    }

    /**
     * Sets the offset map (freq->duration offset)
     */
    void setOffsets(SparseArray<Long> offsets) {
        mOffsets.clear();
        for (int i = 0; i < offsets.size(); i++) {
            mOffsets.put(offsets.keyAt(i), offsets.valueAt(i));
        }
    }

    /**
     * Updates the current time in states and then sets the offset map to the
     * current duration, effectively "zeroing out" the timers
     */
    public void setOffsets() throws CpuStateMonitorException {
        mOffsets.clear();
        updateStates();

        for (CpuState state : mStates) {
            mOffsets.put(state.getFreq(), state.getDuration());
        }
    }

    /**
     * removes state offsets
     */
    public void removeOffsets() {
        mOffsets.clear();
    }

    /**
     * list of all the CPU frequency states, which contains both a
     * frequency and a duration (time spent in that state
     */
    public void updateStates() throws CpuStateMonitorException {
        CPUFreq cpuFreq = CPUFreq.getInstance();

        mStates.clear();
        try {
            String file;
            if(mGpu != null || mCore == -1){
                String states = Utils.readFile(mGpu);
                if (states.isEmpty()) {
                    throw new CpuStateMonitorException("Problem opening time-in-states file");
                }
                readInStates(states.split("\\r?\\n"));
            } else {
                if (Utils.existFile(Utils.strFormat(CPUFreq.TIME_STATE, mCore))) {
                    file = Utils.strFormat(CPUFreq.TIME_STATE, mCore);
                } else {
                    file = Utils.strFormat(CPUFreq.TIME_STATE_2, mCore);
                }
                boolean offline = cpuFreq.isOffline(mCore);
                if (offline) {
                    cpuFreq.onlineCpu(mCore, true, false, null);
                }
                String states = Utils.readFile(file);
                if (offline) {
                    cpuFreq.onlineCpu(mCore, false, false, null);
                }
                if (states.isEmpty()) {
                    throw new CpuStateMonitorException("Problem opening time-in-states file");
                }
                readInStates(states.split("\\r?\\n"));
            }
        } catch (Exception e) {
            throw new CpuStateMonitorException("Problem opening time-in-states file");
        }

        /*
         * deep sleep time determined by difference between elapsed (total) boot
         * time and the system uptime (awake)
         */
        long sleepTime = (SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()) / 10;
        mStates.add(new CpuState(0, sleepTime));

        Collections.sort(mStates, Collections.reverseOrder());
    }

    /**
     * read from a provided BufferedReader the state lines into the States
     * member field
     */
    private void readInStates(String[] states) throws CpuStateMonitorException {
        try {
            // split open line and convert to Integers
            for (String line : states) {
                String[] nums = line.split(" ");
                mStates.add(new CpuState(Utils.strToInt(nums[0]), Utils.strToLong(nums[1])));
            }
        } catch (Exception e) {
            throw new CpuStateMonitorException("Problem processing time-in-states file");
        }
    }

}
