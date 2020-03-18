package com.thunder.thundertweaks.utils.kernel.wakelock;

import android.content.Context;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.root.Control;

/**
 * Created by Morogoku on 09/04/2017.
 */

public class Wakelock {

    private static final String PARENT = "/sys/module/wakeup/parameters";
    private static final String SENSORHUB = PARENT + "/enable_sensorhub_wl";
    private static final String SSP = PARENT + "/enable_ssp_wl";
    private static final String GPS = PARENT + "/enable_bcmdhd4359_wl";
    private static final String MMC0 = PARENT + "/enable_mmc0_detect_wl";
    private static final String WIRELESS = PARENT + "/enable_wlan_wake_wl";
    private static final String WIRELESS1 = PARENT + "/enable_wlan_ctrl_wake_wl";
    private static final String WIRELESS2 = PARENT + "/enable_wlan_rx_wake_wl";
    private static final String WIRELESS3 = PARENT + "/enable_wlan_wd_wake_wl";
    private static final String BLUETOOTH = PARENT + "/enable_bluedroid_timer_wl";
    private static final String BATTERY = "/sys/module/sec_battery/parameters/wl_polling";
    private static final String NFC = "/sys/module/sec_nfc/parameters/wl_nfc";


    public static void enableSensorHub(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", SENSORHUB), SENSORHUB, context);
    }

    public static boolean isSensorHubEnabled() {
        return Utils.readFile(SENSORHUB).equals("Y");
    }

    public static boolean hasSensorHub() {
        return Utils.existFile(SENSORHUB);
    }

    public static void enableSSP(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", SSP), SSP, context);
    }

    public static boolean isSSPEnabled() {
        return Utils.readFile(SSP).equals("Y");
    }

    public static boolean hasSSP() {
        return Utils.existFile(SSP);
    }

    public static void enableGPS(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", GPS), GPS, context);
    }

    public static boolean isGPSEnabled() {
        return Utils.readFile(GPS).equals("Y");
    }

    public static boolean hasGPS() {
        return Utils.existFile(GPS);
    }

    public static void enableMMC0(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", MMC0), MMC0, context);
    }

    public static boolean isMMC0Enabled() {
        return Utils.readFile(MMC0).equals("Y");
    }

    public static boolean hasMMC0() {
        return Utils.existFile(MMC0);
    }

    public static void enableWireless(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", WIRELESS), WIRELESS, context);
    }

    public static boolean isWirelessEnabled() {
        return Utils.readFile(WIRELESS).equals("Y");
    }

    public static boolean hasWireless() {
        return Utils.existFile(WIRELESS);
    }

    public static void enableWireless1(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", WIRELESS1), WIRELESS1, context);
    }

    public static boolean isWireless1Enabled() {
        return Utils.readFile(WIRELESS1).equals("Y");
    }

    public static boolean hasWireless1() {
        return Utils.existFile(WIRELESS1);
    }

    public static void enableWireless2(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", WIRELESS2), WIRELESS2, context);
    }

    public static boolean isWireless2Enabled() {
        return Utils.readFile(WIRELESS2).equals("Y");
    }

    public static boolean hasWireless2() {
        return Utils.existFile(WIRELESS2);
    }

    public static void enableWireless3(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", WIRELESS3), WIRELESS3, context);
    }

    public static boolean isWireless3Enabled() {
        return Utils.readFile(WIRELESS3).equals("Y");
    }

    public static boolean hasWireless3() {
        return Utils.existFile(WIRELESS3);
    }

    public static void enableBluetooth(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", BLUETOOTH), BLUETOOTH, context);
    }

    public static boolean isBluetoothEnabled() {
        return Utils.readFile(BLUETOOTH).equals("Y");
    }

    public static boolean hasBluetooth() {
        return Utils.existFile(BLUETOOTH);
    }

    public static void setBattery(int value, Context context) {
        run(Control.write(String.valueOf(value), BATTERY), BATTERY, context);
    }

    public static String getBattery() {
        return Utils.readFile(BATTERY);
    }

    public static boolean hasBattery() {
        return Utils.existFile(BATTERY);
    }

    public static void setNFC(int value, Context context) {
        run(Control.write(String.valueOf(value), NFC), NFC, context);
    }

    public static String getNFC() {
        return Utils.readFile(NFC);
    }

    public static boolean hasNFC() {
        return Utils.existFile(NFC);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.WAKELOCK, id, context);
    }

    public static boolean hasWakelock() {
        return Utils.existFile(PARENT);
    }

    public static boolean supported() {
        return Utils.existFile(PARENT);
    }
}