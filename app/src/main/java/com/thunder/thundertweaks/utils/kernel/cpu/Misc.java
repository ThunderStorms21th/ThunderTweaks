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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by willi on 05.05.16.
 */
public class Misc {

    private static List<String> list;
    private static final String KERNEL_PATH = "/proc/sys/kernel";
    private static final List<String> COMMON_KERNEL = Arrays.asList("sched_child_runs_first", "sched_cstate_aware", "sched_latency_ns",
            "sched_min_granularity_ns", "sched_wakeup_granularity_ns", "sched_migration_cost_ns", "sched_sync_hint_enable", "sched_time_avg_ms", "sched_tunable_scaling");
    private static final List<String> REMOVED_KERNEL = Collections.singletonList("sched_child_runs_first");
    private static final List<String> ALL_KERNEL = getAllSupportedKernel();
	
    private static final String CPUQ_PATH = "/sys/devices/system/cpu/cpuquiet";
    private static final String CPU_MC_POWER_SAVING = "/sys/devices/system/cpu/sched_mc_power_savings";
    private static final String CPU_WQ_POWER_SAVING = "/sys/module/workqueue/parameters/power_efficient";
    private static final String CPU_DVFS_DISABLE = "/sys/power/disable_dvfs";
	private static final String CPU_FINGERPRINT_BOOST = "/sys/kernel/fp_boost/enabled";
    private static final String CPU_AVAILABLE_CFS_SCHEDULERS = "/sys/devices/system/cpu/sched_balance_policy/available_sched_balance_policy";
    private static final String CPU_CURRENT_CFS_SCHEDULER = "/sys/devices/system/cpu/sched_balance_policy/current_sched_balance_policy";

    private static final String CPU_QUIET = "/sys/devices/system/cpu/cpuquiet";
    private static final String CPU_QUIET_ENABLE = CPU_QUIET + "/cpuquiet_driver/enabled";
    private static final String CPU_QUIET_TEGRA_ENABLE = CPU_QUIET + "/tegra_cpuquiet/enable";
    private static final String CPU_QUIET_AVAILABLE_GOVERNORS = CPU_QUIET + "/available_governors";
    private static final String CPU_QUIET_CURRENT_GOVERNOR = CPU_QUIET + "/current_governor";

    private static final String CPU_TOUCH_BOOST = "/sys/module/msm_performance/parameters/touchboost";
	
    private static final String CPU_DEVFREQ_BOOST_DURATION = "/sys/module/devfreq_boost/parameters/devfreq_boost_dur";
    private static final String CPU_DEVFREQ_BOOST_FREQ = "/sys/module/devfreq_boost/parameters/devfreq_boost_freq";
	
	private static final String CPU_ENABLE_FREQ_SUSP = "/sys/module/exynos_acme/parameters/enable_suspend_freqs";

    private static String[] sAvailableCFSSchedulers;
    private static String[] sCpuQuietAvailableGovernors;

    public static void enableCpuDvfsDisabler(boolean enabled, Context context) {
        run(Control.write(enabled ? "1" : "0", CPU_DVFS_DISABLE), CPU_DVFS_DISABLE, context);
    }

    public static boolean isCpuDvfsDisablerEnabled() {
        return Utils.readFile(CPU_DVFS_DISABLE).equals("1");
    }

    public static boolean hasCpuDvfsDisabler() {
        return Utils.existFile(CPU_DVFS_DISABLE);
    }

    private static List<String> getAllSupportedKernel() {
        List<String> listKernel = new ArrayList<>();
        listKernel.add("sched_child_runs_first");

        File f = new File(KERNEL_PATH);
        if (f.exists()){
            File[] ficheros = f.listFiles();
            for (File fichero : ficheros) {
                boolean blocked = false;
                for (String kernel : REMOVED_KERNEL) {
                    if (fichero.getName().contentEquals(kernel)) {
                        blocked = true;
                        break;
                    }
                }
                if (!blocked) listKernel.add(fichero.getName());
            }
        }
        return listKernel;
    }

    public static void setValue(String value, int position, Context context, boolean completeList) {
        if (completeList) list = ALL_KERNEL;
        else list = COMMON_KERNEL;

        run(Control.write(value, KERNEL_PATH + "/" + list.get(position)), KERNEL_PATH + "/" +
                list.get(position), context);
    }

    public static String getValue(int position, boolean completeList) {
        if (completeList) list = ALL_KERNEL;
        else list = COMMON_KERNEL;

        return Utils.readFile(KERNEL_PATH + "/" + list.get(position));
    }

    public static String getName(int position, boolean completeList) {
        if (completeList) list = ALL_KERNEL;
        else list = COMMON_KERNEL;

        return list.get(position);
    }

    public static boolean exists(int position, boolean completeList) {
        if (completeList) list = ALL_KERNEL;
        else list = COMMON_KERNEL;

        return Utils.existFile(KERNEL_PATH + "/" + list.get(position));
    }

    public static int size(boolean completeList) {
        if (completeList) list = ALL_KERNEL;
        else list = COMMON_KERNEL;

        return list.size();
    }

    public static void enableFreqSuspend(boolean enabled, Context context) {
        run(Control.write(enabled ? "Y" : "N", CPU_ENABLE_FREQ_SUSP), CPU_ENABLE_FREQ_SUSP, context);
    }

    public static boolean isFreqSuspendEnabled() {
        return Utils.readFile(CPU_ENABLE_FREQ_SUSP).equals("Y");
    }

    public static boolean hasFreqSuspend() {
        return Utils.existFile(CPU_ENABLE_FREQ_SUSP);
    }

    public static void enableCpuTouchBoost(boolean enabled, Context context) {
        run(Control.write(enabled ? "1" : "0", CPU_TOUCH_BOOST), CPU_TOUCH_BOOST, context);
    }

    public static boolean isCpuTouchBoostEnabled() {
        return Utils.readFile(CPU_TOUCH_BOOST).equals("1");
    }

    public static boolean hasCpuTouchBoost() {
        return Utils.existFile(CPU_TOUCH_BOOST);
    }

    public static void setCpuQuietGovernor(String value, Context context) {
        run(Control.write(value, CPU_QUIET_CURRENT_GOVERNOR), CPU_QUIET_CURRENT_GOVERNOR, context);
    }

    public static String getCpuQuietCurGovernor() {
        return Utils.readFile(CPU_QUIET_CURRENT_GOVERNOR);
    }

    public static List<String> getCpuQuietAvailableGovernors() {
        if (sCpuQuietAvailableGovernors == null) {
            sCpuQuietAvailableGovernors = Utils.readFile(CPU_QUIET_AVAILABLE_GOVERNORS).split(" ");
        }
        return new ArrayList<>(Arrays.asList(sCpuQuietAvailableGovernors));
    }

    public static boolean hasCpuQuietGovernors() {
        return Utils.existFile(CPU_QUIET_AVAILABLE_GOVERNORS) && Utils.existFile(CPU_QUIET_CURRENT_GOVERNOR)
                && !Utils.readFile(CPU_QUIET_AVAILABLE_GOVERNORS).equals("none");
    }

    public static void enableCpuQuiet(boolean enabled, Context context) {
        if (Utils.existFile(CPU_QUIET_ENABLE)) {
            run(Control.write(enabled ? "1" : "0", CPU_QUIET_ENABLE), CPU_QUIET_ENABLE, context);
        } else {
            run(Control.write(enabled ? "1" : "0", CPU_QUIET_TEGRA_ENABLE), CPU_QUIET_TEGRA_ENABLE, context);
        }
    }

    public static boolean isCpuQuietEnabled() {
        return Utils.readFile(Utils.existFile(CPU_QUIET_ENABLE) ? CPU_QUIET_ENABLE
                : CPU_QUIET_TEGRA_ENABLE).equals("1");
    }

    public static boolean hasCpuQuietEnable() {
        return Utils.existFile(CPU_QUIET_ENABLE) || Utils.existFile(CPU_QUIET_TEGRA_ENABLE);
    }

    public static boolean hasCpuQuiet() {
        return Utils.existFile(CPU_QUIET);
    }

    public static void setCFSScheduler(String value, Context context) {
        run(Control.write(value, CPU_CURRENT_CFS_SCHEDULER), CPU_CURRENT_CFS_SCHEDULER, context);
    }

    public static String getCurrentCFSScheduler() {
        return Utils.readFile(CPU_CURRENT_CFS_SCHEDULER);
    }

    public static List<String> getAvailableCFSSchedulers() {
        if (sAvailableCFSSchedulers == null) {
            sAvailableCFSSchedulers = Utils.readFile(CPU_AVAILABLE_CFS_SCHEDULERS).split(" ");
        }
        return new ArrayList<>(Arrays.asList(sAvailableCFSSchedulers));
    }

    public static boolean hasCFSScheduler() {
        return Utils.existFile(CPU_AVAILABLE_CFS_SCHEDULERS) && Utils.existFile(CPU_CURRENT_CFS_SCHEDULER);
    }

    public static void enablePowerSavingWq(boolean enabled, Context context) {
        run(Control.chmod("644", CPU_WQ_POWER_SAVING), CPU_WQ_POWER_SAVING + "chmod", context);
        run(Control.write(enabled ? "Y" : "N", CPU_WQ_POWER_SAVING), CPU_WQ_POWER_SAVING, context);
    }

    public static boolean isPowerSavingWqEnabled() {
        return Utils.readFile(CPU_WQ_POWER_SAVING).equals("Y");
    }

    public static boolean hasPowerSavingWq() {
        return Utils.existFile(CPU_WQ_POWER_SAVING);
    }

    public static void enableCpuFingerprintBoost(boolean enabled, Context context) {
		run(Control.chmod("644", CPU_FINGERPRINT_BOOST), CPU_FINGERPRINT_BOOST + "chmod", context);
        run(Control.write(enabled ? "1" : "0", CPU_FINGERPRINT_BOOST), CPU_FINGERPRINT_BOOST, context);
    }

    public static boolean isCpuFingerprintBoostEnabled() {
        return Utils.readFile(CPU_FINGERPRINT_BOOST).equals("1");
    }

    public static boolean hasCpuFingerprintBoost() {
        return Utils.existFile(CPU_FINGERPRINT_BOOST);
    }

    public static void setMcPowerSaving(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_MC_POWER_SAVING), CPU_MC_POWER_SAVING, context);
    }

    public static int getCurMcPowerSaving() {
        return Utils.strToInt(Utils.readFile(CPU_MC_POWER_SAVING));
    }

    public static boolean hasMcPowerSaving() {
        return Utils.existFile(CPU_MC_POWER_SAVING);
    }
	
    public static void setDevFreqBoostDur(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_DEVFREQ_BOOST_DURATION), CPU_DEVFREQ_BOOST_DURATION, context);
    }

    public static int getDevFreqBoostDur() {
        return Utils.strToInt(Utils.readFile(CPU_DEVFREQ_BOOST_DURATION));
    }

    public static boolean hasDevFreqBoostDur() {
        return Utils.existFile(CPU_DEVFREQ_BOOST_DURATION);
    }
	
    public static void setDevFreqBoostFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_DEVFREQ_BOOST_FREQ), CPU_DEVFREQ_BOOST_FREQ, context);
    }

    public static int getDevFreqBoostFreq() {
        return Utils.strToInt(Utils.readFile(CPU_DEVFREQ_BOOST_FREQ));
    }

    public static boolean hasDevFreqBoostFreq() {
        return Utils.existFile(CPU_DEVFREQ_BOOST_FREQ);
    }
	
    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
