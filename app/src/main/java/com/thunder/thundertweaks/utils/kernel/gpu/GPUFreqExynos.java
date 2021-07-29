/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.thunder.thundertweaks.utils.kernel.gpu;

import android.content.Context;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.root.Control;
import com.thunder.thundertweaks.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.thunder.thundertweaks.utils.root.RootUtils.chmod_editable;

/**
 * Created by willi on 12.05.16.
 */
public class GPUFreqExynos {

    private static GPUFreqExynos sIOInstance;

    public static GPUFreqExynos getInstance() {
        if (sIOInstance == null) {
            sIOInstance = new GPUFreqExynos();
        }
        return sIOInstance;
    }

    public static final String BACKUP = "/data/.thundertweaks/gpu_stock_voltage";

    private static final String MAX_FREQ_STOCK = "/sys/kernel/gpu/gpu_max_clock";
    private static final String MIN_FREQ_STOCK = "/sys/kernel/gpu/gpu_min_clock";
    private static final String AVAILABLE_FREQS_STOCK = "/sys/kernel/gpu/gpu_freq_table";
    private static final String CUR_VOLT_STOCK = "/sys/kernel/gpu/gpu_volt";
    private static final String DRIVER_VERSION = "/sys/module/mali_kbase/version";

    private static final String MAX_S7_FREQ_STOCK = "/sys/devices/platform/gpusysfs/gpu_max_clock";
    private static final String MIN_S7_FREQ_STOCK = "/sys/devices/platform/gpusysfs/gpu_min_clock";
    private static final String AVAILABLE_S7_FREQS_STOCK = "/sys/devices/platform/gpusysfs/gpu_freq_table";
	
    private static final String MAX_7885_FREQ = "/sys/devices/platform/11500000.mali/max_clock";
    private static final String MIN_7885_FREQ = "/sys/devices/platform/11500000.mali/min_clock";
    private static final String CUR_7885_FREQ = "/sys/devices/platform/11500000.mali/clock";
    private static final String CUR_7885_VOLT = "/sys/devices/platform/11500000.mali/vol";
    private static final String AVAILABLE_7885_FREQS = "/sys/devices/platform/11500000.mali/volt_table";
    private static final String AVAILABLE_7885_GOVERNORS = "/sys/devices/platform/11500000.mali/dvfs_governor";
    private static final String TUNABLE_HIGHSPEED_7885_CLOCK = "/sys/devices/platform/11500000.mali/highspeed_clock";
    private static final String TUNABLE_HIGHSPEED_7885_LOAD = "/sys/devices/platform/11500000.mali/highspeed_load";
    private static final String TUNABLE_HIGHSPEED_7885_DELAY = "/sys/devices/platform/11500000.mali/highspeed_delay";
    private static final String POWER_POLICY_7885 = "/sys/devices/platform/11500000.mali/power_policy";
    private static final String USAGE_7885 = "/sys/devices/platform/11500000.mali/utilization";

    private static final String MAX_78x0_FREQ = "/sys/devices/11400000.mali/max_clock";
    private static final String MIN_78x0_FREQ = "/sys/devices/11400000.mali/min_clock";
    private static final String CUR_78x0_FREQ = "/sys/devices/11400000.mali/clock";
    private static final String CUR_78x0_VOLT = "/sys/devices/11400000.mali/vol";
    private static final String AVAILABLE_78x0_FREQS = "/sys/devices/11400000.mali/volt_table";
    private static final String AVAILABLE_78x0_GOVERNORS = "/sys/devices/11400000.mali/dvfs_governor";
    private static final String TUNABLE_HIGHSPEED_78x0_CLOCK = "/sys/devices/11400000.mali/highspeed_clock";
    private static final String TUNABLE_HIGHSPEED_78x0_LOAD = "/sys/devices/11400000.mali/highspeed_load";
    private static final String TUNABLE_HIGHSPEED_78x0_DELAY = "/sys/devices/11400000.mali/highspeed_delay";
    private static final String POWER_POLICY_78x0 = "/sys/devices/11400000.mali/power_policy";
    private static final String USAGE_78x0 = "/sys/devices/11400000.mali/utilization";

    private static final String MAX_S7_FREQ = "/sys/devices/14ac0000.mali/max_clock";
    private static final String MIN_S7_FREQ = "/sys/devices/14ac0000.mali/min_clock";
    private static final String CUR_S7_FREQ = "/sys/devices/14ac0000.mali/clock";
    private static final String CUR_S7_VOLT = "/sys/devices/14ac0000.mali/vol";
    private static final String TUNABLE_DVFS_S7_FREQ = "/sys/devices/14ac0000.mali/dvfs";
    private static final String AVAILABLE_S7_FREQS = "/sys/devices/14ac0000.mali/volt_table";
    private static final String AVAILABLE_S7_GOVERNORS = "/sys/devices/14ac0000.mali/dvfs_governor";
    private static final String TUNABLE_HIGHSPEED_S7_CLOCK = "/sys/devices/14ac0000.mali/highspeed_clock";
    private static final String TUNABLE_HIGHSPEED_S7_LOAD = "/sys/devices/14ac0000.mali/highspeed_load";
    private static final String TUNABLE_HIGHSPEED_S7_DELAY = "/sys/devices/14ac0000.mali/highspeed_delay";
    private static final String POWER_POLICY_S7 = "/sys/devices/14ac0000.mali/power_policy";
    private static final String USAGE_S7 = "/sys/devices/14ac0000.mali/utilization";
    private static final String TIMESTATES_S7 = "/sys/devices/14ac0000.mali/time_in_state";

    private static final String MAX_S8_FREQ = "/sys/devices/platform/13900000.mali/max_clock";
    private static final String MIN_S8_FREQ = "/sys/devices/platform/13900000.mali/min_clock";
    private static final String CUR_S8_FREQ = "/sys/devices/platform/13900000.mali/clock";
    private static final String CUR_S8_VOLT = "/sys/devices/platform/13900000.mali/vol";
    private static final String TUNABLE_DVFS_S8_FREQ = "/sys/devices/platform/13900000.mali/dvfs";
    private static final String AVAILABLE_S8_FREQS = "/sys/devices/platform/13900000.mali/volt_table";
    private static final String AVAILABLE_S8_GOVERNORS = "/sys/devices/platform/13900000.mali/dvfs_governor";
    private static final String TUNABLE_HIGHSPEED_S8_CLOCK = "/sys/devices/platform/13900000.mali/highspeed_clock";
    private static final String TUNABLE_HIGHSPEED_S8_LOAD = "/sys/devices/platform/13900000.mali/highspeed_load";
    private static final String TUNABLE_HIGHSPEED_S8_DELAY = "/sys/devices/platform/13900000.mali/highspeed_delay";
    private static final String POWER_POLICY_S8 = "/sys/devices/platform/13900000.mali/power_policy";
    private static final String USAGE_S8 = "/sys/devices/platform/13900000.mali/utilization";
    private static final String TIMESTATES_S8 = "/sys/devices/platform/13900000.mali/time_in_state";

    private static final String MAX_S9_FREQ = "/sys/devices/platform/17500000.mali/max_clock";
    private static final String MIN_S9_FREQ = "/sys/devices/platform/17500000.mali/min_clock";
    private static final String CUR_S9_FREQ = "/sys/devices/platform/17500000.mali/clock";
    private static final String CUR_S9_VOLT = "/sys/devices/platform/17500000.mali/vol";
    private static final String TUNABLE_DVFS_S9_FREQ = "/sys/devices/platform/17500000.mali/dvfs";
    private static final String AVAILABLE_S9_FREQS = "/sys/devices/platform/17500000.mali/volt_table";
    private static final String AVAILABLE_S9_GOVERNORS = "/sys/devices/platform/17500000.mali/dvfs_governor";
    private static final String TUNABLE_HIGHSPEED_S9_CLOCK = "/sys/devices/platform/17500000.mali/highspeed_clock";
    private static final String TUNABLE_HIGHSPEED_S9_LOAD = "/sys/devices/platform/17500000.mali/highspeed_load";
    private static final String TUNABLE_HIGHSPEED_S9_DELAY = "/sys/devices/platform/17500000.mali/highspeed_delay";
    private static final String POWER_POLICY_S9 = "/sys/devices/platform/17500000.mali/power_policy";
    private static final String USAGE_S9 = "/sys/devices/platform/17500000.mali/utilization";
    private static final String TIMESTATES_S9 = "/sys/devices/platform/17500000.mali/time_in_state";

    private static final String MAX_S10_FREQ = "/sys/devices/platform/18500000.mali/max_clock";
    private static final String MIN_S10_FREQ = "/sys/devices/platform/18500000.mali/min_clock";
    private static final String CUR_S10_FREQ = "/sys/devices/platform/18500000.mali/clock";
    private static final String CUR_S10_VOLT = "/sys/devices/platform/18500000.mali/vol";
    private static final String TUNABLE_DVFS_S10_FREQ = "/sys/devices/platform/18500000.mali/dvfs";
    private static final String AVAILABLE_S10_FREQS = "/sys/devices/platform/18500000.mali/volt_table";
    private static final String AVAILABLE_S10_GOVERNORS = "/sys/devices/platform/18500000.mali/dvfs_governor";
    private static final String TUNABLE_HIGHSPEED_S10_CLOCK = "/sys/devices/platform/18500000.mali/highspeed_clock";
    private static final String TUNABLE_HIGHSPEED_S10_LOAD = "/sys/devices/platform/18500000.mali/highspeed_load";
    private static final String TUNABLE_HIGHSPEED_S10_DELAY = "/sys/devices/platform/18500000.mali/highspeed_delay";
    private static final String POWER_POLICY_S10 = "/sys/devices/platform/18500000.mali/power_policy";
    private static final String USAGE_S10 = "/sys/devices/platform/18500000.mali/utilization";
    private static final String TIMESTATES_S10 = "/sys/devices/platform/18500000.mali/time_in_state";

    private final HashMap<String, Integer> mAvailableVolts = new HashMap<>();
    private final HashMap<String, Integer> mCurrentFreqs = new HashMap<>();
    private final HashMap<String, Integer> mCurrentVolts = new HashMap<>();
    private final List<String> mMaxFreqs = new ArrayList<>();
    private final List<String> mMinFreqs = new ArrayList<>();
    private final List<String> mAvailableFreqs = new ArrayList<>();
    private final List<String> mScalingGovernors = new ArrayList<>();
    private final List<String> mDvfs = new ArrayList<>();
    private final List<String> mTimeStates = new ArrayList<>();
    private final HashMap<String, Integer> mTunableHighspeedClocks = new HashMap<>();
    private final HashMap<String, Integer> mTunableHighspeedLoads = new HashMap<>();
    private final HashMap<String, Integer> mTunableHighspeedDelays = new HashMap<>();
    private final HashMap<String, Integer> mPowerPolicies = new HashMap<>();
    private final HashMap<String, Integer> mUsage = new HashMap<>();

    {
        mAvailableVolts.put(AVAILABLE_7885_FREQS, 1000);
        mAvailableVolts.put(AVAILABLE_78x0_FREQS, 1000);
        mAvailableVolts.put(AVAILABLE_S7_FREQS, 1000);
        mAvailableVolts.put(AVAILABLE_S8_FREQS, 1000);
        mAvailableVolts.put(AVAILABLE_S9_FREQS, 1000);
        mAvailableVolts.put(AVAILABLE_S10_FREQS, 1000);

        mCurrentFreqs.put(CUR_7885_FREQ, 1);
        mCurrentFreqs.put(CUR_78x0_FREQ, 1);
        mCurrentFreqs.put(CUR_S7_FREQ, 1);
        mCurrentFreqs.put(CUR_S8_FREQ, 1);
        mCurrentFreqs.put(CUR_S9_FREQ, 1);
        mCurrentFreqs.put(CUR_S10_FREQ, 1);

        mMaxFreqs.add(MAX_7885_FREQ);
        mMaxFreqs.add(MAX_78x0_FREQ);
        mMaxFreqs.add(MAX_S7_FREQ);
        mMaxFreqs.add(MAX_S8_FREQ);

        mCurrentVolts.put(CUR_VOLT_STOCK, 1);
        mCurrentVolts.put(CUR_7885_VOLT, 1);
        mCurrentVolts.put(CUR_78x0_VOLT, 1);
        mCurrentVolts.put(CUR_S7_VOLT, 1);
        mCurrentVolts.put(CUR_S8_VOLT, 1);
        mCurrentVolts.put(CUR_S9_VOLT, 1);
        mCurrentVolts.put(CUR_S10_VOLT, 1);

        mMaxFreqs.add(MAX_S9_FREQ);
        mMaxFreqs.add(MAX_S10_FREQ);
        mMaxFreqs.add(MAX_S7_FREQ_STOCK);
        mMaxFreqs.add(MAX_FREQ_STOCK);

        mMinFreqs.add(MIN_7885_FREQ);
        mMinFreqs.add(MIN_78x0_FREQ);
        mMinFreqs.add(MIN_S7_FREQ);
        mMinFreqs.add(MIN_S8_FREQ);
        mMinFreqs.add(MIN_S9_FREQ);
        mMinFreqs.add(MIN_S10_FREQ);
        mMinFreqs.add(MIN_S7_FREQ_STOCK);
        mMinFreqs.add(MIN_FREQ_STOCK);

        mAvailableFreqs.add(AVAILABLE_7885_FREQS);
        mAvailableFreqs.add(AVAILABLE_78x0_FREQS);
        mAvailableFreqs.add(AVAILABLE_S7_FREQS);
        mAvailableFreqs.add(AVAILABLE_S8_FREQS);
        mAvailableFreqs.add(AVAILABLE_S9_FREQS);
        mAvailableFreqs.add(AVAILABLE_S10_FREQS);
        mAvailableFreqs.add(AVAILABLE_S7_FREQS_STOCK);
        mAvailableFreqs.add(AVAILABLE_FREQS_STOCK);

        mDvfs.add(TUNABLE_DVFS_S7_FREQ);
        mDvfs.add(TUNABLE_DVFS_S8_FREQ);
        mDvfs.add(TUNABLE_DVFS_S9_FREQ);
        mDvfs.add(TUNABLE_DVFS_S10_FREQ);

        mTimeStates.add(TIMESTATES_S7);
        mTimeStates.add(TIMESTATES_S8);
        mTimeStates.add(TIMESTATES_S9);
        mTimeStates.add(TIMESTATES_S10);

        mScalingGovernors.add(AVAILABLE_7885_GOVERNORS);
        mScalingGovernors.add(AVAILABLE_78x0_GOVERNORS);
        mScalingGovernors.add(AVAILABLE_S7_GOVERNORS);
        mScalingGovernors.add(AVAILABLE_S8_GOVERNORS);
        mScalingGovernors.add(AVAILABLE_S9_GOVERNORS);
        mScalingGovernors.add(AVAILABLE_S10_GOVERNORS);
		
        mTunableHighspeedClocks.put(TUNABLE_HIGHSPEED_7885_CLOCK, 1);
        mTunableHighspeedClocks.put(TUNABLE_HIGHSPEED_78x0_CLOCK, 1);
        mTunableHighspeedClocks.put(TUNABLE_HIGHSPEED_S7_CLOCK, 1);
        mTunableHighspeedClocks.put(TUNABLE_HIGHSPEED_S8_CLOCK, 1);
        mTunableHighspeedClocks.put(TUNABLE_HIGHSPEED_S9_CLOCK, 1);
        mTunableHighspeedClocks.put(TUNABLE_HIGHSPEED_S10_CLOCK, 1);

        mTunableHighspeedLoads.put(TUNABLE_HIGHSPEED_7885_LOAD, 1);
        mTunableHighspeedLoads.put(TUNABLE_HIGHSPEED_78x0_LOAD, 1);
        mTunableHighspeedLoads.put(TUNABLE_HIGHSPEED_S7_LOAD, 1);
        mTunableHighspeedLoads.put(TUNABLE_HIGHSPEED_S8_LOAD, 1);
        mTunableHighspeedLoads.put(TUNABLE_HIGHSPEED_S9_LOAD, 1);
        mTunableHighspeedLoads.put(TUNABLE_HIGHSPEED_S10_LOAD, 1);

        mTunableHighspeedDelays.put(TUNABLE_HIGHSPEED_7885_DELAY, 1);
        mTunableHighspeedDelays.put(TUNABLE_HIGHSPEED_78x0_DELAY, 1);
        mTunableHighspeedDelays.put(TUNABLE_HIGHSPEED_S7_DELAY, 1);
        mTunableHighspeedDelays.put(TUNABLE_HIGHSPEED_S8_DELAY, 1);
        mTunableHighspeedDelays.put(TUNABLE_HIGHSPEED_S9_DELAY, 1);
        mTunableHighspeedDelays.put(TUNABLE_HIGHSPEED_S10_DELAY, 1);

        mPowerPolicies.put(POWER_POLICY_7885, 1);
        mPowerPolicies.put(POWER_POLICY_78x0, 1);
        mPowerPolicies.put(POWER_POLICY_S7, 1);
        mPowerPolicies.put(POWER_POLICY_S8, 1);
        mPowerPolicies.put(POWER_POLICY_S9, 1);
        mPowerPolicies.put(POWER_POLICY_S10, 1);

        mUsage.put(USAGE_7885, 1);
        mUsage.put(USAGE_78x0, 1);
        mUsage.put(USAGE_S7, 1);
        mUsage.put(USAGE_S8, 1);
        mUsage.put(USAGE_S9, 1);
        mUsage.put(USAGE_S10, 1);
    }

    public String AVAILABLE_VOLTS;
    private Boolean AVAILABLE_VOLTS_EDITABLE;
    private int AVAILABLE_VOLTS_OFFSET;
    private String CUR_FREQ;
    private Integer CUR_FREQ_OFFSET;
    private String CUR_VOLT;
    private Integer CUR_VOLT_OFFSET;
    private List<Integer> AVAILABLE_FREQS;
    private List<Integer> AVAILABLE_FREQS_SORT;
    private String MAX_FREQ;
    private String MIN_FREQ;
    private String GOVERNOR;
    private String TUNABLE_DVFS;	
    private String TIMESTATES;
    private String TUNABLE_HIGHSPEED_CLOCK;
    private String TUNABLE_HIGHSPEED_LOAD;
    private String TUNABLE_HIGHSPEED_DELAY;
    private String POWER_POLICY;
    private String USAGE;

    private String SPLIT_NEW_LINE = "\\r?\\n";
    private String SPLIT_LINE = " ";


    private GPUFreqExynos() {
        for (String file : mAvailableVolts.keySet()) {
            if (Utils.existFile(file)) {
                AVAILABLE_VOLTS = file;
                AVAILABLE_VOLTS_EDITABLE = chmod_editable(file);
                AVAILABLE_VOLTS_OFFSET = mAvailableVolts.get(file);
                break;
            }
        }


        for (String file : mTimeStates) {
            if (Utils.existFile(file)) {
                TIMESTATES = file;
                break;
            }
        }

        for (String file : mCurrentFreqs.keySet()) {
            if (Utils.existFile(file)) {
                CUR_FREQ = file;
                CUR_FREQ_OFFSET = mCurrentFreqs.get(file);
                break;
            }
        }

        for (String file : mCurrentVolts.keySet()) {
            if (Utils.existFile(file)) {
                CUR_VOLT = file;
                CUR_VOLT_OFFSET = mCurrentVolts.get(file);
                break;
            }
        }

        for (String file : mMaxFreqs) {
            if (Utils.existFile(file)) {
                MAX_FREQ = file;
                break;
            }
        }

        for (String file : mMinFreqs) {
            if (Utils.existFile(file)) {
                MIN_FREQ = file;
                break;
            }
        }

        for (String file : mAvailableFreqs) {
            if (Utils.existFile(file)) {
                if ((file.equals(AVAILABLE_S7_FREQS_STOCK)) || (file.equals(AVAILABLE_FREQS_STOCK))){
                    String freqs[] = Utils.readFile(file).split(" ");
                    AVAILABLE_FREQS = new ArrayList<>();
                    AVAILABLE_FREQS_SORT = new ArrayList<>();
                    for (String freq : freqs) {
                        AVAILABLE_FREQS.add(Utils.strToInt(freq.trim()));
                        AVAILABLE_FREQS_SORT.add(Utils.strToInt(freq.trim()));
                    }
                } else {
                    String freqs[] = Utils.readFile(file).split("\\r?\\n");
                    AVAILABLE_FREQS = new ArrayList<>();
                    AVAILABLE_FREQS_SORT = new ArrayList<>();
                    for (String freq : freqs) {
                        String[] freqLine = freq.split(" ");
                        AVAILABLE_FREQS.add(Utils.strToInt(freqLine[0].trim()));
                        AVAILABLE_FREQS_SORT.add(Utils.strToInt(freqLine[0].trim()));
                    }
                }
                Collections.sort(AVAILABLE_FREQS_SORT);
                break;
            }
        }

        for (String file : mScalingGovernors) {
            if (Utils.existFile(file)) {
                GOVERNOR = file;
                break;
            }
        }

        for (String file : mDvfs) {
            if (Utils.existFile(file)) {
                TUNABLE_DVFS = file;
                break;
            }
        }

        for (String file : mTunableHighspeedClocks.keySet()) {
            if (Utils.existFile(file)) {
                TUNABLE_HIGHSPEED_CLOCK = file;
                break;
            }
        }

        for (String file : mTunableHighspeedLoads.keySet()) {
            if (Utils.existFile(file)) {
                TUNABLE_HIGHSPEED_LOAD = file;
                break;
            }
        }

        for (String file : mTunableHighspeedDelays.keySet()) {
            if (Utils.existFile(file)) {
                TUNABLE_HIGHSPEED_DELAY = file;
                break;
            }
        }

        for (String file : mPowerPolicies.keySet()) {
            if (Utils.existFile(file)) {
                POWER_POLICY = file;
                break;
            }
        }

        for (String file : mUsage.keySet()) {
            if (Utils.existFile(file)) {
                USAGE = file;
                break;
            }
        }
    }

    public boolean needRefactor;
    private String hz;
    public String refactor(String hz){
        if (hz.length() > 4){
            hz = String.valueOf(Utils.strToInt(hz) / 1000);
            needRefactor= true;
        }
        return hz;
    }

    public void setGovernor(String value, Context context) {
        switch (value){
            case "Default" :
                run(Control.write("0", GOVERNOR), GOVERNOR, context);
                break;
            case "Interactive" :
                run(Control.write("1", GOVERNOR), GOVERNOR, context);
                break;
            case "Static" :
                run(Control.write("2", GOVERNOR), GOVERNOR, context);
                break;
            case "Booster" :
                run(Control.write("3", GOVERNOR), GOVERNOR, context);
                break;
        }
    }

    public List<String> getAvailableGovernors() {
        String value = Utils.readFile(GOVERNOR);
        if (!value.isEmpty()) {
            String[] lines = value.split("\\r?\\n");
            List<String> governors = new ArrayList<>();
            for (String line : lines) {
                if (line.startsWith("[Current Governor]")){
                    break;
                }
                governors.add(line);
        }
            return governors;
        }
        return null;
    }

    public String getGovernor() {
        String value = Utils.readFile(GOVERNOR);
        if (!value.isEmpty()) {
            String[] lines = value.split("\\r?\\n");
            String governor = "";
            for (String line : lines) {
                if (line.startsWith("[Current Governor]")){
                    governor = line.replace("[Current Governor] ", "");
                }
            }
            return governor;
        }
        return null;
    }

    public boolean hasGovernor() {
        return GOVERNOR != null;
    }

    public boolean hasTimeState() {
        return TIMESTATES != null;
    }

    public String getTimeStatesLocation() {
        return TIMESTATES;
    }

    public void setMinFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MIN_FREQ), MIN_FREQ, context);
    }

    public int getMinFreq() {
        return Utils.strToInt(refactor(Utils.readFile(MIN_FREQ)));
    }

    public boolean hasMinFreq() {
        return MIN_FREQ != null;
    }

    public void setMaxFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_FREQ), MAX_FREQ, context);
    }

    public int getMaxFreq() {
        return Utils.strToInt(refactor(Utils.readFile(MAX_FREQ)));
    }

    public boolean hasMaxFreq() {
        return MAX_FREQ != null;
    }

    public List<String> getAdjustedFreqs(Context context) {
        List<String> list = new ArrayList<>();
        if (getAvailableFreqs() != null) {
            for (int freq : getAvailableFreqs()) {
                list.add(Utils.strToInt(refactor(String.valueOf(freq))) + context.getString(R.string.mhz));
            }
        }
        return list;
    }

    public List<String> getFreqs() {
        List<String> list = new ArrayList<>();
        if (getAvailableFreqs() != null) {
            for (int freq : getAvailableFreqs()) {
                list.add(String.valueOf((freq)));
            }
        }
        Collections.sort(list);
        return list;
    }

    public List<Integer> getAvailableFreqs() {
        if (AVAILABLE_FREQS == null) return null;
        return AVAILABLE_FREQS;
    }

    public List<Integer> getAvailableFreqsSort() {
        if (AVAILABLE_FREQS_SORT == null) return null;
        return AVAILABLE_FREQS_SORT;
    }

    public int getCurFreqOffset() {
        return CUR_FREQ_OFFSET;
    }

    public int getCurFreq() {
        return Utils.strToInt(refactor(Utils.readFile(CUR_FREQ)));
    }

    public boolean hasCurFreq() {
        return CUR_FREQ != null;
    }

    public int getCurVoltOffset() {
        return CUR_VOLT_OFFSET;
    }

    public int getCurVolt() {
        return Utils.strToInt(refactor(Utils.readFile(CUR_VOLT)));
    }

    public boolean hasCurVolt() {
        return CUR_VOLT != null;
    }

    public int getHighspeedClock() {
        return Utils.strToInt(Utils.readFile(TUNABLE_HIGHSPEED_CLOCK));
    }

    public void setHighspeedClock(String value, Context context) {
        run(Control.write(value, TUNABLE_HIGHSPEED_CLOCK), TUNABLE_HIGHSPEED_CLOCK, context);
    }

    public boolean hasHighspeedClock() {
        return TUNABLE_HIGHSPEED_CLOCK != null;
    }

    public int getHighspeedLoad() {
        return Utils.strToInt(Utils.readFile(TUNABLE_HIGHSPEED_LOAD));
    }

    public void setHighspeedLoad(int value, Context context) {
        run(Control.write(String.valueOf(value), TUNABLE_HIGHSPEED_LOAD), TUNABLE_HIGHSPEED_LOAD, context);
    }

    public boolean hasHighspeedLoad() {
        return TUNABLE_HIGHSPEED_LOAD != null;
    }

    public int getHighspeedDelay() {
        return Utils.strToInt(Utils.readFile(TUNABLE_HIGHSPEED_DELAY));
    }

    public void setHighspeedDelay(int value, Context context) {
        run(Control.write(String.valueOf(value), TUNABLE_HIGHSPEED_DELAY), TUNABLE_HIGHSPEED_DELAY, context);
    }

    public boolean hasHighspeedDelay() {
        return TUNABLE_HIGHSPEED_DELAY != null;
    }

    public void setVoltage(Integer freq, String voltage, Context context) {
        String volt = String.valueOf((int)(Utils.strToFloat(voltage) * AVAILABLE_VOLTS_OFFSET));
        run(Control.write(freq + " " + volt, AVAILABLE_VOLTS), AVAILABLE_VOLTS + freq, context);
    }

    public List<String> getStockVoltages() {
        String value = Utils.readFile(BACKUP);
        if (!value.isEmpty()) {
            String[] lines = value.split(SPLIT_NEW_LINE);
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(SPLIT_LINE);
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToFloat(voltageLine[1].trim()) / AVAILABLE_VOLTS_OFFSET));

                }
            }
            return voltages;
        }
        return null;
    }

    public List<String> getVoltages() {
        String value = Utils.readFile(AVAILABLE_VOLTS);
        if (!value.isEmpty()) {
            String[] lines = value.split(SPLIT_NEW_LINE);
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(SPLIT_LINE);
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToFloat(voltageLine[1].trim()) / AVAILABLE_VOLTS_OFFSET));

                }
            }
            return voltages;
        }
        return null;
    }

    public boolean hasVoltage() {
        return AVAILABLE_VOLTS != null;
    }

    public boolean hasEditableVoltage() {
        return AVAILABLE_VOLTS_EDITABLE;
    }

    public void setPowerPolicy(String value, Context context) {
        run(Control.write(value, POWER_POLICY), POWER_POLICY, context);
    }

    public String getPowerPolicy() {
        String[] policies = Utils.readFile(POWER_POLICY).split(" ");
        for (String policy : policies) {
            if (policy.startsWith("[") && policy.endsWith("]")) {
                return policy.replace("[", "").replace("]", "");
            }
        }
        return "";
    }

    public List<String> getPowerPolicies() {
        String[] policies = Utils.readFile(POWER_POLICY).split(" ");
        List<String> list = new ArrayList<>();
        for (String policy : policies) {
            list.add(policy.replace("[", "").replace("]", ""));
        }
        return list;
    }

    public boolean hasPowerPolicy() {
        return POWER_POLICY != null;
    }

    public void enableDvfs(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", TUNABLE_DVFS), TUNABLE_DVFS, context);
    }

    public boolean isDvfsEnabled() {
        return Utils.readFile(TUNABLE_DVFS).equals("1");
    }

    public boolean hasDvfs() {
        return TUNABLE_DVFS != null;
    }

    public int getUsage() {
        return Utils.strToInt(Utils.readFile(USAGE));
    }

    public boolean hasUsage() {
        return USAGE != null;
    }

    public int getVoltageOffset () {
        return AVAILABLE_VOLTS_OFFSET;
    }

    public boolean hasDriverVersion() {
        return Utils.existFile(DRIVER_VERSION);
    }

    public String getDriverVersion() {
        return Utils.readFile(DRIVER_VERSION);
    }

    public boolean supported() {
        return hasCurFreq() || hasCurVolt() || hasVoltage()
                || (hasMaxFreq() && getAvailableFreqs() != null)
                || (hasMinFreq() && getAvailableFreqs() != null)
                || hasGovernor()
                || hasHighspeedClock() || hasHighspeedLoad() || hasHighspeedDelay()
                || hasPowerPolicy() || hasDvfs() || hasUsage() ;
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }

}
