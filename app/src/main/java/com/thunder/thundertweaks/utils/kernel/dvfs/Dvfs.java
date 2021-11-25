package com.thunder.thundertweaks.utils.kernel.dvfs;

import android.content.Context;
import android.util.SparseArray;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.fragments.statistics.OverallFragment;
import com.thunder.thundertweaks.utils.kernel.bus.VoltageMif;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.Device;
import com.thunder.thundertweaks.utils.root.Control;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Morogoku on 11/04/2017.
 */

public class Dvfs {

    private static final String DECISION_MODE = "/sys/devices/system/cpu/cpufreq/mp-cpufreq/cpu_dvfs_mode_control";
    private static final String THERMAL_CONTROL = "/sys/power/little_thermal_temp";
    private static final String MIF = VoltageMif.MIF;
    private static final String MIF_MAX_FREQ = MIF + "/max_freq";
    private static final String MIF_MIN_FREQ = MIF + "/min_freq";
	private static final String MIF_AVAILABLE_FREQ = MIF + "/available_frequencies";

    public static List<String> getAvailableFreq() {
        String freqs[] = Utils.readFile(MIF_AVAILABLE_FREQ).split(" ");
        List<String> AVAILABLE_FREQS = new ArrayList<>();
        for (String freq : freqs) {
            if (!AVAILABLE_FREQS.contains(freq)) {
                String freqString = freq;
                freqString = freqString.substring(0, freqString.length() - 3);
                AVAILABLE_FREQS.add(freqString + " MHz");
            }
        }
        return AVAILABLE_FREQS;
    }

    public static boolean hasAvailableFreq() {
        return Utils.existFile(MIF_AVAILABLE_FREQ);
    }

    public static void setDecisionMode(String value, Context context) {
        switch (value){
            case "Battery" :
                run(Control.write("0", DECISION_MODE), DECISION_MODE, context);
                break;
            case "Balance" :
                run(Control.write("1", DECISION_MODE), DECISION_MODE, context);
                break;
            case "Performance" :
                run(Control.write("2", DECISION_MODE), DECISION_MODE, context);
                break;
        }
    }

    public static String getDecisionMode() {
        if (Utils.readFile(DECISION_MODE) != null) {
            String value = Utils.readFile(DECISION_MODE);
            switch (value) {
                case "0":
                    return "Battery";
                case "1":
                    return "Balance";
                case "2":
                    return "Performance";
            }
        }
        return null;
    }

    public static boolean hasDecisionMode() {
        return Utils.existFile(DECISION_MODE);
    }

    public static void setThermalControl (String value, Context context){
        run(Control.write(String.valueOf(value), THERMAL_CONTROL), THERMAL_CONTROL, context);
    }

    public static String getThermalControl() {
        return Utils.readFile(THERMAL_CONTROL);
    }

    public static boolean hasThermalControl() {
        return Utils.existFile(THERMAL_CONTROL);
    }

    public static void setDevfreqMinFreq(String value, Context context) {
	String board = Device.getBoard();
      value = value.substring(0, value.length() - 4);
      run(Control.write(value + "000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
    }

    public static String getDevfreqMinFreq() {
			String board = Device.getBoard();
        if (Utils.readFile(MIF_MIN_FREQ) != null) {
            String value = Utils.readFile(MIF_MIN_FREQ);
            value = value.substring(0, value.length() - 3);
            return value + " MHz";
		}
        return null;
    }

    public static boolean hasDevfreqMinFreq() {
        return Utils.existFile(MIF_MIN_FREQ);
    }

    public static void setDevfreqMaxFreq(String value, Context context) {
			String board = Device.getBoard();
      value = value.substring(0, value.length() - 4);
      run(Control.write(value + "000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
    }

    public static String getDevfreqMaxFreq() {
			String board = Device.getBoard();
		if (Utils.readFile(MIF_MAX_FREQ) != null) {
            String value = Utils.readFile(MIF_MAX_FREQ);
            value = value.substring(0, value.length() - 3);
            return value + " MHz";
		}
        return null;
    }

    public static boolean hasDevfreqMaxFreq() {
        return Utils.existFile(MIF_MAX_FREQ);
    }
	

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.DVFS, id, context);
    }

    public static boolean supported() {
        return Utils.existFile(DECISION_MODE) && Utils.existFile(THERMAL_CONTROL) || Utils.existFile(MIF_MAX_FREQ) && Utils.existFile(MIF_MIN_FREQ);
    }
	
}
