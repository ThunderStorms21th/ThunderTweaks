package com.thunder.thundertweaks.utils.kernel.hmp;

import android.content.Context;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.kernel.cpu.CPUFreq;
import com.thunder.thundertweaks.utils.root.Control;

/**
 * Created by MoroGoku on 10/10/2017.
 */

public class Hmp {

    private static Hmp sInstance;

    public static Hmp getInstance() {
        if (sInstance == null) {
            sInstance = new Hmp();
        }
        return sInstance;
    }

    private static final String UP_THRESHOLD = "/sys/kernel/hmp/up_threshold";
    private static final String DOWN_THRESHOLD = "/sys/kernel/hmp/down_threshold";
	private static final String DOWN_COMP_HIGH_FREQ = "/sys/kernel/hmp/down_compensation_high_freq";
	private static final String DOWN_COMP_MID_FREQ = "/sys/kernel/hmp/down_compensation_mid_freq";
	private static final String DOWN_COMP_LOW_FREQ = "/sys/kernel/hmp/down_compensation_low_freq";
	private static final String DOWN_COMP_THRESHOLD = "/sys/kernel/hmp/down_compensation_threshold";
	private static final String DOWN_COMP_TIMEOUT = "/sys/kernel/hmp/down_compensation_timeout";
	private static final String SB_UP_THRESHOLD = "/sys/kernel/hmp/sb_up_threshold";
	private static final String SB_DOWN_THRESHOLD = "/sys/kernel/hmp/sb_down_threshold";
	private static final String HMP_BOOST = "/sys/kernel/hmp/boost";
	private static final String DOWN_COMP_ENABLED = "/sys/kernel/hmp/down_compensation_enabled";

    public void setHmpProfile(String value, Context context){
        String hmp[] = value.split(" ");
        int up = Utils.strToInt(hmp[0]);
        int down = Utils.strToInt(hmp[1]);
        setUpThreshold(up, context);
        setDownThreshold(down, context);
    }

    public boolean hasHMPBoost(){
        return (Utils.existFile(HMP_BOOST));
    }

    public boolean isHMPBoostEnabled(){
        return Utils.readFile(HMP_BOOST).equals("0");
    }

    public void enableHMPBoost(boolean enable, Context context){
        run(Control.write(enable ? "0" : "1", HMP_BOOST), HMP_BOOST, context);
    }

    public boolean hasDownCompEnable(){
        return (Utils.existFile(DOWN_COMP_ENABLED));
    }

    public boolean isDownCompEnableEnabled(){
        return Utils.readFile(DOWN_COMP_ENABLED).equals("1");
    }

    public void enableDownCompEnable(boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", DOWN_COMP_ENABLED), DOWN_COMP_ENABLED, context);
    }

    public String getUpThreshold(){
        return Utils.readFile(UP_THRESHOLD);
    }

    public void setUpThreshold(int value, Context context){
        run(Control.write(String.valueOf(value), UP_THRESHOLD), UP_THRESHOLD, context);
    }

    public boolean hasUpThreshold() {
        return Utils.existFile(UP_THRESHOLD);
    }

    public String getDownThreshold(){
        return Utils.readFile(DOWN_THRESHOLD);
    }

    public void setDownThreshold(int value, Context context){
        run(Control.write(String.valueOf(value), DOWN_THRESHOLD), DOWN_THRESHOLD, context);
    }

    public boolean hasDownThreshold() {
        return Utils.existFile(DOWN_THRESHOLD);
    }

    public String getDownCompThreshold(){
        return Utils.readFile(DOWN_COMP_THRESHOLD);
    }

    public void setDownCompThreshold(int value, Context context){
        run(Control.write(String.valueOf(value), DOWN_COMP_THRESHOLD), DOWN_COMP_THRESHOLD, context);
    }

    public boolean hasDownCompThreshold() {
        return Utils.existFile(DOWN_COMP_THRESHOLD);
    }

    public String getDownCompTimeout(){
        return Utils.readFile(DOWN_COMP_TIMEOUT);
    }

    public void setDownCompTimeout(int value, Context context){
        run(Control.write(String.valueOf(value), DOWN_COMP_TIMEOUT), DOWN_COMP_TIMEOUT, context);
    }

    public boolean hasDownCompTimeout() {
        return Utils.existFile(DOWN_COMP_TIMEOUT);
    }

    public String getSbUpThreshold(){
        return Utils.readFile(SB_UP_THRESHOLD);
    }

    public void setSbUpThreshold(int value, Context context){
        run(Control.write(String.valueOf(value), SB_UP_THRESHOLD), SB_UP_THRESHOLD, context);
    }

    public boolean hasSbUpThreshold() {
        return Utils.existFile(SB_UP_THRESHOLD);
    }

    public String getSbDownThreshold(){
        return Utils.readFile(SB_DOWN_THRESHOLD);
    }

    public void setSbDownThreshold(int value, Context context){
        run(Control.write(String.valueOf(value), SB_DOWN_THRESHOLD), SB_DOWN_THRESHOLD, context);
    }

    public boolean hasSbDownThreshold() {
        return Utils.existFile(SB_DOWN_THRESHOLD);
    }

    public String getDownCompHighFreq(){
        return Utils.readFile(DOWN_COMP_HIGH_FREQ);
    }

    public void setDownCompHighFreq(int value, Context context){
        run(Control.write(String.valueOf(value), DOWN_COMP_HIGH_FREQ), DOWN_COMP_HIGH_FREQ, context);
    }

    public boolean hasDownCompHighFreq() {
        return Utils.existFile(DOWN_COMP_HIGH_FREQ);
    }

    public String getDownCompMidFreq(){
        return Utils.readFile(DOWN_COMP_MID_FREQ);
    }

    public void setDownCompMidFreq(int value, Context context){
        run(Control.write(String.valueOf(value), DOWN_COMP_MID_FREQ), DOWN_COMP_MID_FREQ, context);
    }

    public boolean hasDownCompMidFreq() {
        return Utils.existFile(DOWN_COMP_MID_FREQ);
    }

    public String getDownCompLowFreq(){
        return Utils.readFile(DOWN_COMP_LOW_FREQ);
    }

    public void setDownCompLowFreq(int value, Context context){
        run(Control.write(String.valueOf(value), DOWN_COMP_LOW_FREQ), DOWN_COMP_LOW_FREQ, context);
    }

    public boolean hasDownCompLowFreq() {
        return Utils.existFile(DOWN_COMP_LOW_FREQ);
    }

    public boolean supported() {
        return hasUpThreshold() || hasDownThreshold();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.HMP, id, context);
    }
}
