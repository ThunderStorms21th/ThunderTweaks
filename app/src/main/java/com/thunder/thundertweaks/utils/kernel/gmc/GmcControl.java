/*
 * Copyright (C) 2019-2022 nalas <pn2604@gmail.com>
 *
 * This file is part of ThunderTweaks.
 *
 * ThunderTweaks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ThunderTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
 package com.thunder.thundertweaks.utils.kernel.game;

import android.content.Context;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.Device;
import com.thunder.thundertweaks.utils.root.Control;

public class GmcControl {

    private static GmcControl sIOInstance;

    public static GmcControl getInstance() {
        if (sIOInstance == null) {
            sIOInstance = new GmcControl();
        }
        return sIOInstance;
    }

    private static final String GMC_CONTROL = "/sys/kernel/gmc";
    private static final String RUN = GMC_CONTROL + "/run";
    private static final String BINDCPU = GMC_CONTROL + "/bind_cpu";
    private static final String IS_GAME = GMC_CONTROL + "/is_game";
    private static final String MIF_MIN = GMC_CONTROL + "/mif_min";
    private static final String MIF_MAX = GMC_CONTROL + "/mif_max";
    private static final String LITTLE_MAX = GMC_CONTROL + "/cl0_max";
    private static final String MIDDLE_SSE = GMC_CONTROL + "/cl1_max_sse";
    private static final String MIDDLE_MAX = GMC_CONTROL + "/cl1_max";
    private static final String BIG_MAX = GMC_CONTROL + "/cl2_max";
    private static final String LITTLE_MIN = GMC_CONTROL + "/cl0_min";
    private static final String MIDDLE_MIN = GMC_CONTROL + "/cl1_min";
    private static final String BIG_MIN = GMC_CONTROL + "/cl2_min";
    private static final String GPU_LITE = GMC_CONTROL + "/gpu_lite";
    private static final String GPU_MAX = GMC_CONTROL + "/gpu_max";
    private static final String GPU_MIN = GMC_CONTROL + "/gpu_min";
    private static final String MAX_LOCK_D = GMC_CONTROL + "/maxlock_delay_sec";

    public static boolean hasGmcControl() {
        return Utils.existFile(GMC_CONTROL);
    }

    public static boolean hasIsGame() {
        return Utils.existFile(IS_GAME);
    }

    public static Boolean isEnabledIsGame() {
        return Utils.readFile(IS_GAME).equals("1");
    }

    public static void enableIsGame(Boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", IS_GAME), IS_GAME, context);
    }
	
    public static boolean hasRun() {
        return Utils.existFile(RUN);
    }

    public static Boolean isEnabledRun() {
        return Utils.readFile(RUN).equals("1");
    }

    public static void enableRun(Boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", RUN), RUN, context);
    }
	
    public static boolean hasBindCPU() {
        return Utils.existFile(RUN);
    }

    public static Boolean isEnabledBindCPU() {
        return Utils.readFile(BINDCPU).equals("1");
    }

    public static void enableBindCPU(Boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", BINDCPU), BINDCPU, context);
    }
	
    public static boolean hasMIFMin() {
        return Utils.existFile(MIF_MIN);
    }

    public void setMIFMin(String value, Context context) {
	String board = Device.getBoard();
      value = value.substring(0, value.length() - 4);
      run(Control.write(value + "000", MIF_MIN), MIF_MIN, context);
    }

    public static int getMIFMin() {
        return Utils.strToInt(Utils.readFile(MIF_MIN));
    }

    public static boolean hasMIFMax() {
        return Utils.existFile(MIF_MAX);
    }

    public void setMIFMax(String value, Context context) {
			String board = Device.getBoard();
      value = value.substring(0, value.length() - 4);
      run(Control.write(value + "000", MIF_MAX), MIF_MAX, context);
    }

    public static int getMIFMax() {
        return Utils.strToInt(Utils.readFile(MIF_MAX));
    }

    public static boolean hasLITTLEMax() {
        return Utils.existFile(LITTLE_MAX);
    }

    public void setLITTLEMax(int value, Context context) {
        run(Control.write(String.valueOf(value), LITTLE_MAX),
                LITTLE_MAX, context);
    }

    public static int getLITTLEMax() {
        return Utils.strToInt(Utils.readFile(LITTLE_MAX));
    }

    public static boolean hasLITTLEMin() {
        return Utils.existFile(LITTLE_MIN);
    }

    public void setLITTLEMin(int value, Context context) {
        run(Control.write(String.valueOf(value), LITTLE_MIN),
                LITTLE_MIN, context);
    }

    public static int getLITTLEMin() {
        return Utils.strToInt(Utils.readFile(LITTLE_MIN));
    }

    public static boolean hasMIDDLEMax() {
        return Utils.existFile(MIDDLE_MAX);
    }

    public void setMIDDLEMax(int value, Context context) {
        run(Control.write(String.valueOf(value), MIDDLE_MAX),
                MIDDLE_MAX, context);
    }

    public static int getMIDDLEMax() {
        return Utils.strToInt(Utils.readFile(MIDDLE_MAX));
    }


    public static boolean hasMIDDLEMin() {
        return Utils.existFile(MIDDLE_MIN);
    }

    public void setMIDDLEMin(int value, Context context) {
        run(Control.write(String.valueOf(value), MIDDLE_MIN),
                MIDDLE_MIN, context);
    }

    public static int getMIDDLEMin() {
        return Utils.strToInt(Utils.readFile(MIDDLE_MIN));
    }

    public static boolean hasMIDDLESse() {
        return Utils.existFile(MIDDLE_SSE);
    }

    public void setMIDDLESse(int value, Context context) {
        run(Control.write(String.valueOf(value), MIDDLE_SSE),
                MIDDLE_SSE, context);
    }

    public static int getMIDDLESse() {
        return Utils.strToInt(Utils.readFile(MIDDLE_SSE));
    }
	
    public static boolean hasBIGMax() {
        return Utils.existFile(BIG_MAX);
    }

    public void setBIGMax(int value, Context context) {
        run(Control.write(String.valueOf(value), BIG_MAX),
                BIG_MAX, context);
    }

    public static int getBIGMax() {
        return Utils.strToInt(Utils.readFile(BIG_MAX));
    }

    public static boolean hasBIGMin() {
        return Utils.existFile(BIG_MIN);
    }

    public void setBIGMin(int value, Context context) {
        run(Control.write(String.valueOf(value), BIG_MIN),
                BIG_MIN, context);
    }

    public static int getBIGMin() {
        return Utils.strToInt(Utils.readFile(BIG_MIN));
    }

    public static boolean hasGPUMax() {
        return Utils.existFile(GPU_MAX);
    }

    public void setGPUMax(int value, Context context) {
        run(Control.write(String.valueOf(value), GPU_MAX),
                GPU_MAX, context);
    }

    public static int getGPUMax() {
        return Utils.strToInt(Utils.readFile(GPU_MAX));
    }

    public static boolean hasGPULite() {
        return Utils.existFile(GPU_LITE);
    }

    public void setGPULite(int value, Context context) {
        run(Control.write(String.valueOf(value), GPU_LITE),
                GPU_LITE, context);
    }

    public static int getGPULite() {
        return Utils.strToInt(Utils.readFile(GPU_LITE));
    }

    public static boolean hasGPUMin() {
        return Utils.existFile(GPU_MIN);
    }

    public void setGPUMin(int value, Context context) {
        run(Control.write(String.valueOf(value), GPU_MIN),
                GPU_MIN, context);
    }

    public static int getGPUMin() {
        return Utils.strToInt(Utils.readFile(GPU_MIN));
    }

    public static boolean hasMaxLockDelay() {
        return Utils.existFile(MAX_LOCK_D);
    }

    public void setMaxLockDelay(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_LOCK_D),
                MAX_LOCK_D, context);
    }

    public static int getMaxLockDelay() {
        return Utils.strToInt(Utils.readFile(MAX_LOCK_D));
    }

    public static boolean supported() {
        return hasGmcControl();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GMC, id, context);
    }
}