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
package com.thunder.thundertweaks.utils.kernel.battery;

import android.content.Context;
import androidx.annotation.NonNull;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.AppSettings;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.root.Control;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by willi on 26.06.16.
 */
public class Battery {

    private static Battery sInstance;

    public static Battery getInstance(@NonNull Context context) {
        if (sInstance == null) {
            setValues();
            sInstance = new Battery(context);
        }
        return sInstance;
    }

    public static String BATTERY_NODE;
    private static String UNSTABLE_CHARGE;
    private static String HV_INPUT;
    private static String HV_CHARGE;
    private static String AC_INPUT;
    private static String AC_CHARGE;
    private static String AC_INPUT_SCREEN;
    private static String AC_CHARGE_SCREEN;
    private static String USB_INPUT;
    private static String USB_CHARGE;
    private static String USB_INPUT2;
    private static String USB_CHARGE2;
    private static String WC_INPUT;
    private static String WC_CHARGE;
    private static String PS_INPUT;
    private static String PS_CHARGE;
    private static String CAR_INPUT;
    private static String CAR_CHARGE;
    private static String CHARGE_SOURCE;
    private static String HEALTH;
    private static String FG_FULLCAPNOM;
	
    public static void setValues() {
        for (String file : new String[] {
                // Add on this list needed values for battery sysfs nodes
                "/sys/devices/battery",
                "/sys/devices/battery.30",
                "/sys/devices/battery.55",
                "/sys/devices/platform/battery",
                "/sys/devices/platform/samsung_mobile_device/samsung_mobile_device:battery"}
                ) {
            if (Utils.existFile(file)) {
                BATTERY_NODE = file;
                UNSTABLE_CHARGE = BATTERY_NODE + "/unstable_power_detection";
                HV_INPUT = BATTERY_NODE + "/hv_input";
                HV_CHARGE = BATTERY_NODE + "/hv_charge";
                AC_INPUT = BATTERY_NODE + "/ac_input";
                AC_CHARGE = BATTERY_NODE + "/ac_charge";
                AC_INPUT_SCREEN = BATTERY_NODE + "/so_limit_input";
                AC_CHARGE_SCREEN = BATTERY_NODE + "/so_limit_charge";
                USB_INPUT = BATTERY_NODE + "/sdp_input";
                USB_CHARGE = BATTERY_NODE + "/sdp_charge";
				USB_INPUT2 = BATTERY_NODE + "/usb_input";
                USB_CHARGE2 = BATTERY_NODE + "/usb_charge";
                WC_INPUT = BATTERY_NODE + "/wc_input";
                WC_CHARGE = BATTERY_NODE + "/wc_charge";
                PS_INPUT = BATTERY_NODE + "/ps_input";
                PS_CHARGE = BATTERY_NODE + "/ps_charge";
                CAR_INPUT = BATTERY_NODE + "/car_input";
                CAR_CHARGE = BATTERY_NODE + "/car_charge";
                CHARGE_SOURCE = BATTERY_NODE + "/power_supply/battery/batt_charging_source";
                HEALTH = BATTERY_NODE + "/power_supply/battery/health";
                FG_FULLCAPNOM = BATTERY_NODE + "/power_supply/battery/fg_fullcapnom";
                break;
            }
        }
    }

    private static String FORCE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
	private static String ADAPTIVE_FAST_CHARGE = "/sys/class/sec/switch/afc_disable";
    private static String BLX = "/sys/devices/virtual/misc/batterylifeextender/charging_limit";
    private static String CHARGE_RATE = "/sys/kernel/thundercharge_control";
    private static String CHARGE_RATE_ENABLE = CHARGE_RATE + "/enabled";
    private static final String CHARGE_BYPASS = "/sys/class/power_supply/battery/battery_charging_enabled";
    private static String CUSTOM_CURRENT = CHARGE_RATE + "/custom_current";
    private static String STORE_MODE = "/sys/devices/battery/power_supply/battery/store_mode";
	private static String STORE_MODE2 = "/sys/devices/platform/battery/power_supply/battery/store_mode";
    private static String STORE_MODE_MAX = "/sys/module/sec_battery/parameters/store_mode_max";
    private static String STORE_MODE_MIN = "/sys/module/sec_battery/parameters/store_mode_min";
    private static String LIMIT_CAPACITY = "/sys/class/power_supply/battery/batt_full_capacity";

    private int mCapacity;
    private Battery(Context context) {
        if (BATTERY_NODE == null) {
            setValues();
        }
        if (mCapacity == 0) {
            try {
                Class<?> powerProfile = Class.forName("com.android.internal.os.PowerProfile");
                Constructor constructor = powerProfile.getDeclaredConstructor(Context.class);
                Object powerProInstance = constructor.newInstance(context);
                Method batteryCap = powerProfile.getMethod("getBatteryCapacity");
                mCapacity = Math.round((long) (double) batteryCap.invoke(powerProInstance));
            } catch (Exception e) {
                e.printStackTrace();
                mCapacity = 0;
            }
        }
    }

    public String getRemainingCapaticy() {
        float cap = Utils.strToInt(Utils.readFile(FG_FULLCAPNOM));
        if (cap != 0) {
            return String.valueOf(cap);
        } else {
            return null;
        }
    }

    public String getHealthValue() {
        String state = Utils.readFile(HEALTH);
        if (state == null){
            return null;
        } else {
            float cap = Utils.strToInt(Utils.readFile(FG_FULLCAPNOM));
            if (cap != 0) {
                float value = ((cap * 2) / getCapacity()) * 100;
                value = (value > 100) ? (value / 2) : value;
                value = (value > 100) ? 100 : value;
                return state + " / " +String.format("%.2f", value)+"%";
            } else {
                return state;
            }
        }
    }

    public void saveStockValues(Context context) {
        if (hasHvInput()) {
            AppSettings.saveString("bat_hv_input", getHvInput(), context);
        } else {
            AppSettings.remove("bat_hv_input", context);
        }
        if (hasHvCharge()) {
            AppSettings.saveString("bat_hv_charge", getHvCharge(), context);
        } else {
            AppSettings.remove("bat_hv_charge", context);
        }
        if (hasAcInput()) {
            AppSettings.saveString("bat_ac_input", getAcInput(), context);
        } else {
            AppSettings.remove("bat_ac_input", context);
        }
        if (hasAcCharge()) {
            AppSettings.saveString("bat_ac_charge", getAcCharge(), context);
        } else {
            AppSettings.remove("bat_ac_charge", context);
        }
        if (hasAcInputScreen()) {
            AppSettings.saveString("bat_ac_input_screen", getAcInputScreen(), context);
        } else {
            AppSettings.remove("bat_ac_input_screen", context);
        }
        if (hasAcChargeScreen()) {
            AppSettings.saveString("bat_ac_charge_screen", getAcChargeScreen(), context);
        } else {
            AppSettings.remove("bat_ac_charge_screen", context);
        }
        if (hasUsbInput()) {
            AppSettings.saveString("bat_usb_input", getUsbInput(), context);
        } else {
            AppSettings.remove("bat_usb_input", context);
        }
        if (hasUsbCharge()) {
            AppSettings.saveString("bat_usb_charge", getUsbCharge(), context);
        } else {
            AppSettings.remove("bat_usb_charge", context);
        }
        if (hasUsbInput2()) {
            AppSettings.saveString("bat_usb_input2", getUsbInput2(), context);
        } else {
            AppSettings.remove("bat_usb_input2", context);
        }
        if (hasUsbCharge2()) {
            AppSettings.saveString("bat_usb_charge2", getUsbCharge2(), context);
        } else {
            AppSettings.remove("bat_usb_charge2", context);
        }
        if (hasWcInput()) {
            AppSettings.saveString("bat_wc_input", getWcInput(), context);
        } else {
            AppSettings.remove("bat_wc_input", context);
        }
        if (hasWcCharge()) {
            AppSettings.saveString("bat_wc_charge", getWcCharge(), context);
        } else {
            AppSettings.remove("bat_wc_charge", context);
        }
        if (hasPsInput()) {
            AppSettings.saveString("bat_ps_input", getPsInput(), context);
        } else {
            AppSettings.remove("bat_ps_input", context);
        }
        if (hasPsCharge()) {
            AppSettings.saveString("bat_ps_charge", getPsCharge(), context);
        } else {
            AppSettings.remove("bat_ps_charge", context);
        }
        if (hasCarInput()) {
            AppSettings.saveString("bat_car_input", getCarInput(), context);
        } else {
            AppSettings.remove("bat_car_input", context);
        }
        if (hasCarCharge()) {
            AppSettings.saveString("bat_car_charge", getCarCharge(), context);
        } else {
            AppSettings.remove("bat_car_charge", context);
        }

        AppSettings.saveBoolean("battery_saved", true, context);
    }


    public boolean hasStoreMode(){
        return (Utils.existFile(STORE_MODE) && Utils.existFile(STORE_MODE_MAX)
                && Utils.existFile(STORE_MODE_MIN));
    }

    public boolean isStoreModeEnabled(){
        return Utils.readFile(STORE_MODE).equals("1");
    }

    public void enableStoreMode(boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", STORE_MODE), STORE_MODE, context);
    }

    public boolean hasStoreMode2(){
        return (Utils.existFile(STORE_MODE2) && Utils.existFile(STORE_MODE_MAX)
                && Utils.existFile(STORE_MODE_MIN));
    }

    public boolean isStoreModeEnabled2(){
        return Utils.readFile(STORE_MODE2).equals("1");
    }

    public void enableStoreMode2(boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", STORE_MODE2), STORE_MODE2, context);
    }

    public String getStoreModeMax(){
        return Utils.readFile(STORE_MODE_MAX);
    }

    public void setStoreModeMax(int value, Context context){
        run(Control.write(String.valueOf(value), STORE_MODE_MAX), STORE_MODE_MAX, context);
    }

    public String getStoreModeMin(){
        return Utils.readFile(STORE_MODE_MIN);
    }

    public void setStoreModeMin(int value, Context context){
        run(Control.write(String.valueOf(value), STORE_MODE_MIN), STORE_MODE_MIN, context);
    }

    public boolean hasLimitCapacity(){
        return Utils.existFile(LIMIT_CAPACITY);
    }

    public String getLimitCapacity(){
        return Utils.readFile(LIMIT_CAPACITY);
    }

    public void setLimitCapacity(int value, Context context){
        run(Control.write(String.valueOf(value), LIMIT_CAPACITY), LIMIT_CAPACITY, context);
    }

    public void setChargingCurrent(int value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_CURRENT), CUSTOM_CURRENT, context);
    }

    public int getChargingCurrent() {
        return Utils.strToInt(Utils.readFile(CUSTOM_CURRENT));
    }

    public boolean hasChargingCurrent() {
        return Utils.existFile(CUSTOM_CURRENT);
    }

    public void enableChargeRate(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CHARGE_RATE_ENABLE), CHARGE_RATE_ENABLE, context);
    }

    public boolean isChargeRateEnabled() {
        return Utils.readFile(CHARGE_RATE_ENABLE).equals("1");
    }

    public boolean hasChargeRateEnable() {
        return Utils.existFile(CHARGE_RATE_ENABLE);
    }

    public void enableChargeByPass(boolean enable, Context context) {
        run(Control.write(enable ? "0" : "1", CHARGE_BYPASS), CHARGE_BYPASS, context);
    }

    public boolean isChargeByPassEnabled() {
        return Utils.readFile(CHARGE_BYPASS).equals("0");
    }

    public boolean hasChargeByPassEnable() {
        return Utils.existFile(CHARGE_BYPASS);
    }

    public void setBlx(int value, Context context) {
        run(Control.write(String.valueOf(value == 0 ? 101 : value - 1), BLX), BLX, context);
    }

    public int getBlx() {
        int value = Utils.strToInt(Utils.readFile(BLX));
        return value > 100 ? 0 : value + 1;
    }

    public boolean hasBlx() {
        return Utils.existFile(BLX);
    }

    public void enableForceFastCharge(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", FORCE_FAST_CHARGE), FORCE_FAST_CHARGE, context);
    }

    public boolean isForceFastChargeEnabled() {
        return Utils.readFile(FORCE_FAST_CHARGE).equals("1");
    }

    public boolean hasForceFastCharge() {
        return Utils.existFile(FORCE_FAST_CHARGE);
    }

    public void enableAdaptiveFastCharge(boolean enable, Context context) {
        run(Control.write(!enable ? "1" : "0", ADAPTIVE_FAST_CHARGE), ADAPTIVE_FAST_CHARGE, context);
    }

    public boolean isAdaptiveFastChargeEnabled() {
        return !(Utils.readFile(ADAPTIVE_FAST_CHARGE).equals("1"));
    }

    public boolean hasAdaptiveFastCharge() {
        return Utils.existFile(ADAPTIVE_FAST_CHARGE);
    }


    public int getCapacity() {
        return mCapacity;
    }

    public boolean hasCapacity() {
        return getCapacity() != 0;
    }

    public boolean supported() {
        return hasCapacity();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.BATTERY, id, context);
    }

    /* Init Battery */

    public boolean hasHvInput() {
        return Utils.existFile(HV_INPUT);
    }

    public void setHvInput(int value, Context context) {
        run(Control.write(String.valueOf(value), HV_INPUT), HV_INPUT, context);
    }

    public String getHvInput() {
        return Utils.readFile(HV_INPUT);
    }

    public boolean hasHvCharge() {
        return Utils.existFile(HV_CHARGE);
    }

    public void setHvCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), HV_CHARGE), HV_CHARGE, context);
    }

    public String getHvCharge() {
        return Utils.readFile(HV_CHARGE);
    }

    public boolean hasAcInput() {
        return Utils.existFile(AC_INPUT);
    }

    public void setAcInput(int value, Context context) {
        run(Control.write(String.valueOf(value), AC_INPUT), AC_INPUT, context);
    }

    public String getAcInput() {
        return Utils.readFile(AC_INPUT);
    }

    public boolean hasAcCharge() {
        return Utils.existFile(AC_CHARGE);
    }

    public void setAcCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), AC_CHARGE), AC_CHARGE, context);
    }

    public String getAcCharge() {
        return Utils.readFile(AC_CHARGE);
    }

    public boolean hasAcInputScreen() {
        return Utils.existFile(AC_INPUT_SCREEN);
    }

    public void setAcInputScreen(int value, Context context) {
        run(Control.write(String.valueOf(value), AC_INPUT_SCREEN), AC_INPUT_SCREEN, context);
    }

    public String getAcInputScreen() {
        return Utils.readFile(AC_INPUT_SCREEN);
    }

    public boolean hasAcChargeScreen() {
        return Utils.existFile(AC_CHARGE_SCREEN);
    }

    public void setAcChargeScreen(int value, Context context) {
        run(Control.write(String.valueOf(value), AC_CHARGE_SCREEN), AC_CHARGE_SCREEN, context);
    }

    public String getAcChargeScreen() {
        return Utils.readFile(AC_CHARGE_SCREEN);
    }

    public boolean hasUsbInput() {
        return Utils.existFile(USB_INPUT);
    }

    public void setUsbInput(int value, Context context) {
        run(Control.write(String.valueOf(value), USB_INPUT), USB_INPUT, context);
    }

    public String getUsbInput() {
        return Utils.readFile(USB_INPUT);
    }

    public boolean hasUsbCharge() {
        return Utils.existFile(USB_CHARGE);
    }

    public void setUsbCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), USB_CHARGE), USB_CHARGE, context);
    }

    public String getUsbCharge() {
        return Utils.readFile(USB_CHARGE);
    }

    public boolean hasUsbInput2() {
        return Utils.existFile(USB_INPUT2);
    }

    public void setUsbInput2(int value, Context context) {
        run(Control.write(String.valueOf(value), USB_INPUT2), USB_INPUT2, context);
    }

    public String getUsbInput2() {
        return Utils.readFile(USB_INPUT2);
    }

    public boolean hasUsbCharge2() {
        return Utils.existFile(USB_CHARGE2);
    }

    public void setUsbCharge2(int value, Context context) {
        run(Control.write(String.valueOf(value), USB_CHARGE2), USB_CHARGE2, context);
    }

    public String getUsbCharge2() {
        return Utils.readFile(USB_CHARGE2);
    }

    public boolean hasWcInput() {
        return Utils.existFile(WC_INPUT);
    }

    public void setWcInput(int value, Context context) {
        run(Control.write(String.valueOf(value), WC_INPUT), WC_INPUT, context);
    }

    public String getWcInput() {
        return Utils.readFile(WC_INPUT);
    }

    public boolean hasWcCharge() {
        return Utils.existFile(WC_CHARGE);
    }

    public void setWcCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), WC_CHARGE), WC_CHARGE, context);
    }

    public String getWcCharge() {
        return Utils.readFile(WC_CHARGE);
    }

    public boolean hasPsInput() {
        return Utils.existFile(PS_INPUT);
    }

    public void setPsInput(int value, Context context) {
        run(Control.write(String.valueOf(value), PS_INPUT), PS_INPUT, context);
    }

    public String getPsInput() {
        return Utils.readFile(PS_INPUT);
    }

    public boolean hasPsCharge() {
        return Utils.existFile(PS_CHARGE);
    }

    public void setPsCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), PS_CHARGE), PS_CHARGE, context);
    }

    public String getPsCharge() {
        return Utils.readFile(PS_CHARGE);
    }

    public boolean hasCarCharge() {
        return Utils.existFile(CAR_CHARGE);
    }

    public void setCarCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), CAR_CHARGE), CAR_CHARGE, context);
    }

    public String getCarCharge() {
        return Utils.readFile(CAR_CHARGE);
    }

    public boolean hasCarInput() {
        return Utils.existFile(CAR_INPUT);
    }

    public void setCarInput(int value, Context context) {
        run(Control.write(String.valueOf(value), CAR_INPUT), CAR_INPUT, context);
    }

    public String getCarInput() {
        return Utils.readFile(CAR_INPUT);
    }

    public boolean hasChargeSource() {
        return Utils.existFile(CHARGE_SOURCE);
    }

    public static String getChargeSource(Context context) {
        String value = Utils.readFile(CHARGE_SOURCE);
        switch (value){
            case "0" :
                return context.getResources().getString(R.string.cs_unknown);
            case "1" :
                return context.getResources().getString(R.string.cs_battery);
            case "2" :
                return context.getResources().getString(R.string.cs_ups);
            case "3" :
                return context.getResources().getString(R.string.cs_main_ac);
            case "4" :
                return context.getResources().getString(R.string.cs_usb);
            case "5" :
                return context.getResources().getString(R.string.cs_usb_dedeicated);
            case "6" :
                return context.getResources().getString(R.string.cs_usb_charging);
            case "7" :
                return context.getResources().getString(R.string.cs_usb_accesory);
            case "8" :
                return context.getResources().getString(R.string.cs_battery_monitor);
            case "9" :
                return context.getResources().getString(R.string.cs_misc);
            case "10" :
                return context.getResources().getString(R.string.cs_wireless);
            case "11" :
                return context.getResources().getString(R.string.cs_hv_wireless);
            case "12" :
                return context.getResources().getString(R.string.cs_pma_wireless);
            case "13" :
                return context.getResources().getString(R.string.cs_car);
            case "14" :
                return context.getResources().getString(R.string.cs_uart_off);
            case "15" :
                return context.getResources().getString(R.string.cs_otg);
            case "16" :
                return context.getResources().getString(R.string.cs_lan);
            case "17" :
                return context.getResources().getString(R.string.cs_mhl_500);
            case "18" :
                return context.getResources().getString(R.string.cs_mhl_900);
            case "19" :
                return context.getResources().getString(R.string.cs_mhl_1500);
            case "20" :
                return context.getResources().getString(R.string.cs_mhl_usb);
            case "21" :
                return context.getResources().getString(R.string.cs_smart_otg);
            case "22" :
                return context.getResources().getString(R.string.cs_smart_notg);
            case "23" :
                return context.getResources().getString(R.string.cs_power_sharing);
            case "24" :
                return context.getResources().getString(R.string.cs_hv_mains);
            case "25" :
                return context.getResources().getString(R.string.cs_hv_mains_12);
            case "26" :
                return context.getResources().getString(R.string.cs_hv_prepare);
            case "27" :
                return context.getResources().getString(R.string.cs_hv_error);
            case "28" :
                return context.getResources().getString(R.string.cs_mhl_100);
            case "29" :
                return context.getResources().getString(R.string.cs_mhl_2000);
            case "30" :
                return context.getResources().getString(R.string.cs_hv_unknown);
            case "31" :
                return context.getResources().getString(R.string.cs_mdock);
            case "32" :
                return context.getResources().getString(R.string.cs_hmt_conected);
            case "33" :
                return context.getResources().getString(R.string.cs_hmt_charge);
            case "34" :
                return context.getResources().getString(R.string.cs_wireless_pack);
            case "35" :
                return context.getResources().getString(R.string.cs_wireless_pack_ta);
            case "36" :
                return context.getResources().getString(R.string.cs_wireless_stand);
            case "37" :
                return context.getResources().getString(R.string.cs_wireless_hv_stand);
            case "38" :
                return context.getResources().getString(R.string.cs_pdic);
            case "39" :
                return context.getResources().getString(R.string.cs_hv_mains);
            case "40" :
                return context.getResources().getString(R.string.cs_qc20);
            case "41" :
                return context.getResources().getString(R.string.cs_qc30);
            case "42" :
                return context.getResources().getString(R.string.cs_power_sharing);
        }
        return "Unknown source";
    }

    public boolean hasCharge() {
        return Utils.existFile(BATTERY_NODE);
    }

    public boolean hasUnstableCharge() {
        return Utils.existFile(UNSTABLE_CHARGE);
    }

    public boolean isUnstableChargeEnabled() {
        return Utils.readFile(UNSTABLE_CHARGE).equals("1");
    }

    public void enableUnstableCharge(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", UNSTABLE_CHARGE), UNSTABLE_CHARGE, context);
    }
}
