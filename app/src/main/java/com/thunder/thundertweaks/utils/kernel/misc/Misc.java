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
package com.thunder.thundertweaks.utils.kernel.misc;

import android.content.Context;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.root.Control;
import com.thunder.thundertweaks.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class Misc {

    private static Misc sInstance;

    public static Misc getInstance() {
        if (sInstance == null) {
            sInstance = new Misc();
        }
        return sInstance;
    }

    private static final String DYNAMIC_FSYNC = "/sys/kernel/dyn_fsync/Dyn_fsync_active";
    private static final String GENTLE_FAIR_SLEEPERS = "/sys/kernel/sched/gentle_fair_sleepers";
    private static final String ARCH_POWER = "/sys/kernel/sched/arch_power";
    private static final String TCP_AVAILABLE_CONGESTIONS = "/proc/sys/net/ipv4/tcp_available_congestion_control";
    private static final String TCP_CONGESTIONS_CONTROL = "/proc/sys/net/ipv4/tcp_congestion_control";
    private static final String HOSTNAME_KEY = "net.hostname";
    private static final String WIREGUARD = "/sys/module/wireguard/version";
    private static final String CPUSET = "/dev/cpuset";
    private static final String[] PARAMETERS = {"abnormal/cpus", "background/cpus", "deoxp/cpus",
            "foreground/cpus", "moderate/cpus", "restricted/cpus", "sf/cpus", "system-background/cpus", "top-app/cpus"};

    private static final String DOZE = "dumpsys deviceidle";

    private final List<String> mLoggers = new ArrayList<>();
    private final List<String> mCrcs = new ArrayList<>();
    private final List<String> mFsyncs = new ArrayList<>();
    private final List<String> mMagisks = new ArrayList<>();
    private final List<String> mBatterySavers = new ArrayList<>();

    {
        mLoggers.add("/sys/kernel/logger_mode/logger_mode");
        mLoggers.add("/sys/module/logger/parameters/enabled");
        mLoggers.add("/sys/module/logger/parameters/log_enabled");

        mCrcs.add("/sys/module/mmc_core/parameters/crc");
        mCrcs.add("/sys/module/mmc_core/parameters/use_spi_crc");

        mFsyncs.add("/sys/devices/virtual/misc/fsynccontrol/fsync_enabled");
        mFsyncs.add("/sys/module/sync/parameters/fsync_enabled");

        mMagisks.add("/res/magisk");
        mMagisks.add("/sbin/magisk");
        mMagisks.add("/system/bin/magisk");
        mMagisks.add("/vendor/bin/magisk");
        mMagisks.add("/system/system/bin/magisk");
        mMagisks.add("/system_ext/system/bin/magisk");

        mBatterySavers.add("/sys/kernel/battery_saver/parameters/enabled");
        mBatterySavers.add("/sys/kernel/battery/parameters/battery_saver");
        mBatterySavers.add("/sys/module/battery_saver/parameters/enabled");
        mBatterySavers.add("/sys/module/battery/parameters/battery_saver");
    }

    private String LOGGER_FILE;
    private String CRC_FILE;
    private Boolean CRC_USE_INTEGER;
    private String FSYNC_FILE;
    private Boolean FSYNC_USE_INTEGER;
    private String MAGISK_BIN;
    private String RESETPROP;
    private String BATTERY_SAVER;
    private Boolean BATTERY_SAVER_INTEGER;

    private Misc() {
        for (String file : mLoggers) {
            if (Utils.existFile(file)) {
                LOGGER_FILE = file;
                break;
            }
        }

        for (String file : mCrcs) {
            if (Utils.existFile(file)) {
                CRC_FILE = file;
                CRC_USE_INTEGER = Character.isDigit(Utils.readFile(CRC_FILE).toCharArray()[0]);
                break;
            }
        }

        for (String file : mFsyncs) {
            if (Utils.existFile(file)) {
                FSYNC_FILE = file;
                FSYNC_USE_INTEGER = Character.isDigit(Utils.readFile(FSYNC_FILE).toCharArray()[0]);
                break;
            }
        }

        for (String file : mMagisks) {
            if (Utils.existFile(file)) {
                MAGISK_BIN = file;
                RESETPROP = MAGISK_BIN + " resetprop -v -n ";
                break;
            }
        }

        for (String file : mBatterySavers) {
            if (Utils.existFile(file)) {
                BATTERY_SAVER = file;
                BATTERY_SAVER_INTEGER = Character.isDigit(Utils.readFile(BATTERY_SAVER).toCharArray()[0]);
                break;
            }
        }
    }

    public boolean hasMagiskBin(){
        return Utils.existFile(MAGISK_BIN);
    }

    public void setProp(String prop, boolean value, Context context){
        run(RESETPROP + prop + " " + (value ? 1 : 0), prop, context);
    }

    public static List<String> doze(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.disabled));
        list.add(context.getString(R.string.doze_light));
        list.add(context.getString(R.string.doze_deep));
        list.add(context.getString(R.string.doze));
        return list;
    }

    public static int getDozeState() {
        if (RootUtils.runAndGetOutput(DOZE + " enabled").equals("1")) {
            return 3;
        } else if (RootUtils.runAndGetOutput(DOZE + " enabled deep").equals("1")) {
            return 2;
        } else if (RootUtils.runAndGetOutput(DOZE + " enabled light").equals("1")) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setDoze(int value, Context context) {
        switch (value) {
            case 0:
                run(Control.runShellCommand(DOZE + " disable"), DOZE, context);
                break;
            case 1:
                if (isForceDozeEnabled()) {
                    run(Control.runShellCommand(DOZE + " disable"), DOZE, context);
                } else if (isDeepDozeEnabled()) {
                    run(Control.runShellCommand(DOZE + " disable deep"), DOZE, context);
                }
                run(Control.runShellCommand(DOZE + " enable light"), DOZE, context);
                break;
            case 2:
                if (isForceDozeEnabled()) {
                    run(Control.runShellCommand(DOZE + " disable"), DOZE, context);
                } else if (isLightDozeEnabled()) {
                    run(Control.runShellCommand(DOZE + " disable light"), DOZE, context);
                }
                run(Control.runShellCommand(DOZE + " enable deep"), DOZE, context);
                break;
            case 3:
                run(Control.runShellCommand(DOZE + " enable" + " && " + DOZE + " force-idle"), DOZE, context);
                break;
        }
    }

    private static boolean isLightDozeEnabled() {
        return RootUtils.runAndGetOutput(DOZE + " enabled light").equals("1");
    }

    private static boolean isDeepDozeEnabled() {
        return RootUtils.runAndGetOutput(DOZE + " enabled deep").equals("1");

    private static boolean isForceDozeEnabled() {
        return RootUtils.runCommand(DOZE + " enabled").equals("1");
    }

    public static boolean hasDoze() {
        return RootUtils.runCommand(DOZE + " enabled").equals("1")
	|| RootUtils.runCommand(DOZE + " enabled").equals("0");
    }

    public static boolean hasWireguard() {
        return Utils.existFile(WIREGUARD);
    }

    public static String getWireguard() {
        return Utils.readFile(WIREGUARD);
    }

    public void setHostname(String value, Context context) {
        run(Control.setProp(HOSTNAME_KEY, value), HOSTNAME_KEY, context);
    }

    public String getHostname() {
        return RootUtils.getProp(HOSTNAME_KEY);
    }

    public void setTcpCongestion(String tcpCongestion, Context context) {
        run("sysctl -w net.ipv4.tcp_congestion_control=" + tcpCongestion, TCP_AVAILABLE_CONGESTIONS, context);
    }

    private static boolean isTCPCCExist() {
        return Utils.existFile(TCP_CONGESTIONS_CONTROL);
    }

    public String getTcpCongestion() {
        if (isTCPCCExist()) {
            return Utils.readFile(TCP_CONGESTIONS_CONTROL);
        } else {
            return getTcpAvailableCongestions().get(0);
        }
    }

    public List<String> getTcpAvailableCongestions() {
        return new ArrayList<>(Arrays.asList(Utils.readFile(TCP_AVAILABLE_CONGESTIONS).split(" ")));
    }

    public void enableArchPower(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ARCH_POWER), ARCH_POWER, context);
    }

    public boolean isArchPowerEnabled() {
        return Utils.readFile(ARCH_POWER).equals("1");
    }

    public boolean hasArchPower() {
        return Utils.existFile(ARCH_POWER);
    }

    public void enableGentleFairSleepers(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", GENTLE_FAIR_SLEEPERS), GENTLE_FAIR_SLEEPERS, context);
    }

    public boolean isGentleFairSleepersEnabled() {
        return Utils.readFile(GENTLE_FAIR_SLEEPERS).equals("1");
    }

    public boolean hasGentleFairSleepers() {
        return Utils.existFile(GENTLE_FAIR_SLEEPERS);
    }

    public void enableDynamicFsync(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", DYNAMIC_FSYNC), DYNAMIC_FSYNC, context);
    }

    public boolean isDynamicFsyncEnabled() {
        return Utils.readFile(DYNAMIC_FSYNC).equals("1");
    }

    public boolean hasDynamicFsync() {
        return Utils.existFile(DYNAMIC_FSYNC);
    }

    public void enableBatterySaver(boolean enable, Context context) {
        run(Control.write(BATTERY_SAVER_INTEGER ? enable ? "1" : "0" : enable ? "Y" : "N", BATTERY_SAVER),
                BATTERY_SAVER, context);
    }

    public boolean isBatterySaverEnabled() {
        return Utils.readFile(BATTERY_SAVER).equals(BATTERY_SAVER_INTEGER ? "1" : "Y");
    }

    public boolean hasBatterySaver() {
        return Utils.existFile(BATTERY_SAVER);
    }

    public void enableFsync(boolean enable, Context context) {
        run(Control.write(FSYNC_USE_INTEGER ? enable ? "1" : "0" : enable ? "Y" : "N", FSYNC_FILE),
                FSYNC_FILE, context);
    }

    public boolean isFsyncEnabled() {
        return Utils.readFile(FSYNC_FILE).equals(FSYNC_USE_INTEGER ? "1" : "Y");
    }

    public boolean hasFsync() {
        return FSYNC_FILE != null;
    }

    public void enableCrc(boolean enable, Context context) {
        run(Control.write(CRC_USE_INTEGER ? enable ? "1" : "0" : enable ? "Y" : "N", CRC_FILE),
                CRC_FILE, context);
    }

    public boolean isCrcEnabled() {
        return Utils.readFile(CRC_FILE).equals(CRC_USE_INTEGER ? "1" : "Y");
    }

    public boolean hasCrc() {
        return CRC_FILE != null;
    }

    public void enableLogger(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LOGGER_FILE), LOGGER_FILE, context);
    }

    public boolean isLoggerEnabled() {
        return Utils.readFile(LOGGER_FILE).equals("1");
    }

    public boolean hasLoggerEnable() {
        return LOGGER_FILE != null;
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

}
