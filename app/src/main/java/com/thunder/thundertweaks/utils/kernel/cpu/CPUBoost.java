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
package com.thunder.thundertweaks.utils.kernel.cpu;

import android.content.Context;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 05.05.16.
 */
public class CPUBoost {

    private static CPUBoost sInstance;

    public static CPUBoost getInstance() {
        if (sInstance == null) {
            sInstance = new CPUBoost();
        }
        return sInstance;
    }

    private static final String CPU_BOOST = "/sys/module/cpu_boost/parameters";
    private static final String CPU_BOOST_EXYNOS = "/sys/kernel/cpu_input_boost";
	private static final String CPU_BOOST_EXYNOS8890 = "/sys/module/cpu_input_boost_8890/parameters";
    private static final String CPU_WQ_AFFINITY = "/sys/bus/workqueue/devices/writeback/cpumask";
    private static final String CPU_IRQ_AFFINITY = "/proc/irq/default_smp_affinity";
    private static final String CPU_IDLE_EXYNOS = "/sys/module/exynos_acme/parameters";

    private static final List<String> sEnable = new ArrayList<>();

    static {
        sEnable.add(CPU_BOOST + "/cpu_boost");
        sEnable.add(CPU_BOOST + "/cpuboost_enable");
        sEnable.add(CPU_BOOST + "/input_boost_enabled");
        sEnable.add(CPU_BOOST_EXYNOS + "/enabled");
    //    sEnable.add(CPU_BOOST_EXYNOS8890 + "/input_boost_enabled");
    }

    private static final String CPU_BOOST_DEBUG_MASK = CPU_BOOST + "/debug_mask";
    private static final String CPU_BOOST_MS = CPU_BOOST + "/boost_ms";
    private static final String CPU_BOOST_SYNC_THRESHOLD = CPU_BOOST + "/sync_threshold";
    private static final String CPU_BOOST_INPUT_MS = CPU_BOOST + "/input_boost_ms";
    private static final String CPU_BOOST_INPUT_BOOST_FREQ = CPU_BOOST + "/input_boost_freq";
    private static final String CPU_BOOST_WAKEUP = CPU_BOOST + "/wakeup_boost";
    private static final String CPU_BOOST_HOTPLUG = CPU_BOOST + "/hotplug_boost";
	private static final String CPU_BOOST_EXYNOS8890_ENABLE = CPU_BOOST_EXYNOS8890 + "/input_boost_enabled";
    private static final String CPU_BOOST_EXYNOS_INPUT_MS = CPU_BOOST_EXYNOS + "/ib_duration_ms";
    private static final String CPU_BOOST_EXYNOS_BOOST_FREQ = CPU_BOOST_EXYNOS + "/ib_freqs";
    private static final String CPU_BOOST_EXYNOS8890_DURATION = CPU_BOOST_EXYNOS8890 + "/input_boost_duration";
    private static final String CPU_BOOST_EXYNOS8890_FREQ_HP = CPU_BOOST_EXYNOS8890 + "/input_boost_freq_hp";
    private static final String CPU_BOOST_EXYNOS8890_FREQ_LP = CPU_BOOST_EXYNOS8890 + "/input_boost_freq_lp";
    private static final String CPU_BOOST_EXYNOS8890_FREQ_PERF = CPU_BOOST_EXYNOS8890 + "/input_boost_freq_perf";
	private static final String CPU_BOOST_EXYNOS8890_MAX_LP = CPU_BOOST_EXYNOS8890 + "/input_boost_max_lp";
    private static final String CPU_IDLE_EXYNOS_ENABLE = CPU_IDLE_EXYNOS + "/enable_suspend_freqs";
    private static final String CPU_IDLE_EXYNOS_SCREEN_OFF_MIN_LP = CPU_IDLE_EXYNOS + "/cpu0_suspend_min_freq";
    private static final String CPU_IDLE_EXYNOS_SCREEN_OFF_MIN_HP = CPU_IDLE_EXYNOS + "/cpu4_suspend_min_freq";
    private static final String CPU_IDLE_EXYNOS_SCREEN_OFF_MAX_LP = CPU_IDLE_EXYNOS + "/cpu0_suspend_max_freq";
    private static final String CPU_IDLE_EXYNOS_SCREEN_OFF_MAX_HP = CPU_IDLE_EXYNOS + "/cpu4_suspend_max_freq";

    private String ENABLE;

    private CPUBoost() {
        for (String file : sEnable) {
            if (Utils.existFile(file)) {
                ENABLE = file;
                break;
            }
        }
    }
	
    public void setCpuBoostExynosInputFreq(String value1, String value2, Context context) {
        String value = value1 + " " + value2;
        run(Control.write(String.valueOf(value), CPU_BOOST_EXYNOS_BOOST_FREQ), CPU_BOOST_EXYNOS_BOOST_FREQ, context);
    }

    public List<String> getCpuBootExynosInputFreq() {
        String freqs[] = Utils.readFile(CPU_BOOST_EXYNOS_BOOST_FREQ).split(" ");
        List<String> INPUT_FREQS = new ArrayList<>();
        for (String freq : freqs) {
            INPUT_FREQS.add(freq.trim());
        }
        return INPUT_FREQS;
    }

    public boolean hasCpuBoostExynosInputFreq() {
        return Utils.existFile(CPU_BOOST_EXYNOS_BOOST_FREQ);
    }

    public boolean hasCpuBoostExynosInputMs() {
        return Utils.existFile(CPU_BOOST_EXYNOS_INPUT_MS);
    }

    public void setCpuBoostExynosInputMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_EXYNOS_INPUT_MS), CPU_BOOST_EXYNOS_INPUT_MS, context);
    }

    public String getCpuBootExynosInputMs() {
        return Utils.readFile(CPU_BOOST_EXYNOS_INPUT_MS);
    }

    public void enableCpuBoostWakeup(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", CPU_BOOST_WAKEUP), CPU_BOOST_WAKEUP, context);
    }

    public boolean isCpuBoostWakeupEnabled() {
        return Utils.readFile(CPU_BOOST_WAKEUP).equals("Y");
    }

    public boolean hasCpuBoostWakeup() {
        return Utils.existFile(CPU_BOOST_WAKEUP);
    }

    public void enableCpuBoostHotplug(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", CPU_BOOST_HOTPLUG), CPU_BOOST_HOTPLUG, context);
    }

    public boolean isCpuBoostHotplugEnabled() {
        return Utils.readFile(CPU_BOOST_HOTPLUG).equals("Y");
    }

    public boolean hasCpuBoostHotplug() {
        return Utils.existFile(CPU_BOOST_HOTPLUG);
    }

	public void enableCpuBoostInput(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", CPU_BOOST_EXYNOS8890_ENABLE), CPU_BOOST_EXYNOS8890_ENABLE, context);
    }

    public boolean isCpuBoostInputEnabled() {
        return Utils.readFile(CPU_BOOST_EXYNOS8890_ENABLE).equals("Y");
    }

    public boolean hasCpuBoostInput() {
        return Utils.existFile(CPU_BOOST_EXYNOS8890_ENABLE);
    }

    public void setCpuBoostInputMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_INPUT_MS), CPU_BOOST_INPUT_MS, context);
    }

    public int getCpuBootInputMs() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_INPUT_MS));
    }

    public boolean hasCpuBoostInputMs() {
        return Utils.existFile(CPU_BOOST_INPUT_MS);
    }

    public void setCpuBoostInputFreq(int value, int core, Context context) {
        if (Utils.readFile(CPU_BOOST_INPUT_BOOST_FREQ).contains(":")) {
            run(Control.write(core + ":" + value, CPU_BOOST_INPUT_BOOST_FREQ),
                    CPU_BOOST_INPUT_BOOST_FREQ + core, context);
        } else {
            run(Control.write(String.valueOf(value), CPU_BOOST_INPUT_BOOST_FREQ),
                    CPU_BOOST_INPUT_BOOST_FREQ, context);
        }
    }

    public List<Integer> getCpuBootInputFreq() {
        CPUFreq cpuFreq = CPUFreq.getInstance();

        List<Integer> list = new ArrayList<>();
        String value = Utils.readFile(CPU_BOOST_INPUT_BOOST_FREQ);
        if (value.contains(":")) {
            for (String line : value.split(" ")) {
                int core = Utils.strToInt(line.split(":")[0]);
                String freq = line.split(":")[1];
                try {
                    list.add(freq.equals("0") ? 0 : cpuFreq.getFreqs(core).indexOf(Utils.strToInt(freq)) + 1);
                } catch (NullPointerException ignored) {
                }
            }
        } else {
            list.add(value.equals("0") ? 0 : cpuFreq.getFreqs().indexOf(Utils.strToInt(value)) + 1);
        }
        return list;
    }

    public boolean hasCpuBoostInputFreq() {
        return Utils.existFile(CPU_BOOST_INPUT_BOOST_FREQ);
    }

    public void setCpuBoostDurationMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_EXYNOS8890_DURATION), CPU_BOOST_EXYNOS8890_DURATION, context);
    }

    public int getCpuBoostDurationMs() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_EXYNOS8890_DURATION));
    }

    public boolean hasCpuBoostDurationMs() {
        return Utils.existFile(CPU_BOOST_EXYNOS8890_DURATION);
    }

	public void setCpuBoostFreqHp(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_EXYNOS8890_FREQ_HP),
                CPU_BOOST_EXYNOS8890_FREQ_HP, context);
    }

    public static int getCpuBoostFreqHp() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_EXYNOS8890_FREQ_HP));
    }

    public static boolean hasCpuBoostFreqHp() {
        return Utils.existFile(CPU_BOOST_EXYNOS8890_FREQ_HP);
    }

	public void setCpuBoostFreqLp(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_EXYNOS8890_FREQ_LP),
                CPU_BOOST_EXYNOS8890_FREQ_LP, context);
    }

    public static int getCpuBoostFreqLp() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_EXYNOS8890_FREQ_LP));
    }

    public static boolean hasCpuBoostFreqLp() {
        return Utils.existFile(CPU_BOOST_EXYNOS8890_FREQ_LP);
    }

	public void setCpuBoostMaxLp(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_EXYNOS8890_MAX_LP),
                CPU_BOOST_EXYNOS8890_MAX_LP, context);
    }

    public static int getCpuBoostMaxLp() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_EXYNOS8890_MAX_LP));
    }

    public static boolean hasCpuBoostMaxLp() {
        return Utils.existFile(CPU_BOOST_EXYNOS8890_MAX_LP);
    }

	public void setCpuBoostMaxPerf(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_EXYNOS8890_FREQ_PERF),
                CPU_BOOST_EXYNOS8890_FREQ_PERF, context);
    }

    public static int getCpuBoostMaxPerf() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_EXYNOS8890_FREQ_PERF));
    }

    public static boolean hasCpuBoostMaxPerf() {
        return Utils.existFile(CPU_BOOST_EXYNOS8890_FREQ_PERF);
    }

    public void setCpuBoostSyncThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_SYNC_THRESHOLD), CPU_BOOST_SYNC_THRESHOLD, context);
    }

    public int getCpuBootSyncThreshold() {
        return CPUFreq.getInstance().getFreqs().indexOf(Utils.strToInt(Utils.readFile(CPU_BOOST_SYNC_THRESHOLD))) + 1;
    }

    public boolean hasCpuBoostSyncThreshold() {
        return Utils.existFile(CPU_BOOST_SYNC_THRESHOLD);
    }

    public void setCpuBoostMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_MS), CPU_BOOST_MS, context);
    }

    public int getCpuBootMs() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_MS));
    }

    public boolean hasCpuBoostMs() {
        return Utils.existFile(CPU_BOOST_MS);
    }

    public void enableCpuBoostDebugMask(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CPU_BOOST_DEBUG_MASK), CPU_BOOST_DEBUG_MASK, context);
    }

    public boolean isCpuBoostDebugMaskEnabled() {
        return Utils.readFile(CPU_BOOST_DEBUG_MASK).equals("1");
    }

    public boolean hasCpuBoostDebugMask() {
        return Utils.existFile(CPU_BOOST_DEBUG_MASK);
    }

    public void setwqAffinity(String value, Context context) {
        run(Control.write(String.valueOf(value), CPU_WQ_AFFINITY), CPU_WQ_AFFINITY, context);
    }

    public String getwqAffinity() {
        return Utils.readFile(CPU_WQ_AFFINITY);
    }

    public boolean haswqAffinity() {
        return Utils.existFile(CPU_WQ_AFFINITY);
    }

    public void setirqAffinity(String value, Context context) {
        run(Control.write(String.valueOf(value), CPU_IRQ_AFFINITY), CPU_IRQ_AFFINITY, context);
    }

    public String getirqAffinity() {
        return Utils.readFile(CPU_IRQ_AFFINITY);
    }

    public boolean hasirqAffinity() {
        return Utils.existFile(CPU_IRQ_AFFINITY);
    }

    public void enableCpuIdle(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", CPU_IDLE_EXYNOS_ENABLE), CPU_IDLE_EXYNOS_ENABLE, context);
    }

    public boolean isCpuIdleEnabled() {
        return Utils.readFile(CPU_IDLE_EXYNOS_ENABLE).equals("Y");
    }

    public void setCpuIdleMaxLp(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_IDLE_EXYNOS_SCREEN_OFF_MAX_LP),
                CPU_IDLE_EXYNOS_SCREEN_OFF_MAX_LP, context);
    }

    public static int getCpuIdleMaxLp() {
        return Utils.strToInt(Utils.readFile(CPU_IDLE_EXYNOS_SCREEN_OFF_MAX_LP));
    }

    public static boolean hasCpuIdleMaxLp() {
        return Utils.existFile(CPU_IDLE_EXYNOS_SCREEN_OFF_MAX_LP);
    }

    public void setCpuIdleMaxPerf(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_IDLE_EXYNOS_SCREEN_OFF_MAX_HP),
                CPU_IDLE_EXYNOS_SCREEN_OFF_MAX_HP, context);
    }

    public static int getCpuIdleMaxPerf() {
        return Utils.strToInt(Utils.readFile(CPU_IDLE_EXYNOS_SCREEN_OFF_MAX_HP));
    }

    public static boolean hasCpuIdleMaxPerf() {
        return Utils.existFile(CPU_IDLE_EXYNOS_SCREEN_OFF_MAX_HP);
    }

    public void setCpuIdleScreenOffMinPerf(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_IDLE_EXYNOS_SCREEN_OFF_MIN_HP),
                CPU_IDLE_EXYNOS_SCREEN_OFF_MIN_HP, context);
    }

    public static int getCpuIdleScreenOffMinPerf() {
        return Utils.strToInt(Utils.readFile(CPU_IDLE_EXYNOS_SCREEN_OFF_MIN_HP));
    }

    public static boolean hasCpuIdleScreenOffMinPerf() {
        return Utils.existFile(CPU_IDLE_EXYNOS_SCREEN_OFF_MIN_HP);
    }

    public void setCpuIdleScreenOffMinLp(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_IDLE_EXYNOS_SCREEN_OFF_MIN_LP),
                CPU_IDLE_EXYNOS_SCREEN_OFF_MIN_LP, context);
    }

    public static int getCpuIdleScreenOffMinLp() {
        return Utils.strToInt(Utils.readFile(CPU_IDLE_EXYNOS_SCREEN_OFF_MIN_LP));
    }

    public static boolean hasCpuIdleScreenOffMinLp() {
        return Utils.existFile(CPU_IDLE_EXYNOS_SCREEN_OFF_MIN_LP);
    }

    public boolean hasCpuIdle() {
        return Utils.existFile(CPU_IDLE_EXYNOS_ENABLE);
    }

    public void enableCpuBoost(boolean enable, Context context) {
        run(Control.write(
                ENABLE.endsWith("cpuboost_enable") ? (enable ? "Y" : "N") : (enable ? "1" : "0"), ENABLE),
                ENABLE, context);
    }

    public boolean isEnabled() {
        String value = Utils.readFile(ENABLE);
        return value.equals("1") || value.equals("Y");
    }

    public boolean hasEnable() {
        return ENABLE != null;
    }

    public boolean supported() {
        return hasEnable() || hasCpuBoostDebugMask() || hasCpuBoostMs() || hasCpuBoostSyncThreshold()
                || hasCpuBoostInputFreq() || hasCpuBoostInputMs() || hasCpuBoostHotplug() || hasCpuBoostWakeup()
                || hasCpuBoostExynosInputFreq() || hasCpuBoostExynosInputMs() || hasCpuBoostInput()
				|| hasCpuBoostDurationMs() || hasCpuBoostFreqHp() || hasCpuBoostFreqLp() || hasCpuBoostMaxLp()
				|| hasCpuBoostMaxPerf() || haswqAffinity() || hasirqAffinity() || hasCpuIdle();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
