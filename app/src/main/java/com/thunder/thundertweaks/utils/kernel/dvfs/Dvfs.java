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
    private static final String MIF_MAX_FREQ = "/sys/devices/platform/17000010.devfreq_mif/devfreq/17000010.devfreq_mif/max_freq";
    private static final String MIF_MIN_FREQ = "/sys/devices/platform/17000010.devfreq_mif/devfreq/17000010.devfreq_mif/min_freq";
	private static final String MIF_AVAILABLE_FREQ = "/sys/devices/platform/17000010.devfreq_mif/devfreq/17000010.devfreq_mif/available_frequencies";

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

/*	public void setDevfreqMinFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MIF_MIN_FREQ),
                MIF_MIN_FREQ, context);
    }

    public static int getDevfreqMinFreq() {
        return Utils.strToInt(Utils.readFile(MIF_MIN_FREQ));
    }
*/

    public static void setDevfreqMinFreq(String value, Context context) {
	String board = Device.getBoard();
  if (board.equalsIgnoreCase("exynos9830") || board.equalsIgnoreCase("exynos990")) {
		switch (value){
            case "2730 MHz" :
                run(Control.write("2730000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
            case "2535 MHz" :
                run(Control.write("2535000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
            case "2288 MHz" :
                run(Control.write("2288000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "2028 MHz" :
                run(Control.write("2028000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "1716 MHz" :
                run(Control.write("1716000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "1539 MHz" :
                run(Control.write("1539000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "1352 MHz" :
                run(Control.write("1352000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "1014 MHz" :
                run(Control.write("1014000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "845 MHz" :
                run(Control.write("845000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "676 MHz" :
                run(Control.write("676000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "546 MHz" :
                run(Control.write("546000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "421 MHz" :
                run(Control.write("421000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
        }
  } else if (board.equalsIgnoreCase("exynos9820") || board.equalsIgnoreCase("exynos9825")) {
		switch (value){
			case "2093 MHz" :
                run(Control.write("2093000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "1794 MHz" :
                run(Control.write("1794000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "1539 MHz" :
                run(Control.write("1539000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "1352 MHz" :
                run(Control.write("1352000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "1014 MHz" :
                run(Control.write("1014000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "845 MHz" :
                run(Control.write("845000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "676 MHz" :
                run(Control.write("676000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "546 MHz" :
                run(Control.write("546000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
			case "421 MHz" :
                run(Control.write("421000", MIF_MIN_FREQ), MIF_MIN_FREQ, context);
                break;
        }
  } else {
		  switch (value){
		  case "not supported" :
          break;
		  }
		}
    }

    public static String getDevfreqMinFreq() {
        if (Utils.readFile(MIF_MIN_FREQ) != null) {
            String value = Utils.readFile(MIF_MIN_FREQ);
            switch (value) {
                case "2730000":
                    return "2730 MHz";
                case "2535000":
                    return "2535 MHz";
                case "2288000":
                    return "2288 MHz";
                case "2028000":
                    return "2028 MHz";
                case "1716000":
                    return "1716 MHz";
                case "1539000":
                    return "1539 MHz";
                case "1352000":
                    return "1352 MHz";
                case "1014000":
                    return "1014 MHz";
                case "845000":
                    return "845 MHz";
                case "676000":
                    return "676 MHz";
                case "546000":
                    return "546 MHz";
                case "421000":
                    return "421 MHz";
            }
        }
        return null;
    }

    public static boolean hasDevfreqMinFreq() {
        return Utils.existFile(MIF_MIN_FREQ);
    }

/*	public void setDevfreqMaxFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MIF_MAX_FREQ),
                MIF_MAX_FREQ, context);
    }

    public static int getDevfreqMaxFreq() {
        return Utils.strToInt(Utils.readFile(MIF_MAX_FREQ));
    }
*/

    public static void setDevfreqMaxFreq(String value, Context context) {
        switch (value){
            case "2730 MHz" :
                run(Control.write("2730000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
            case "2535 MHz" :
                run(Control.write("2535000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
            case "2288 MHz" :
                run(Control.write("2288000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
			case "2028 MHz" :
                run(Control.write("2028000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
			case "1716 MHz" :
                run(Control.write("1716000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
			case "1539 MHz" :
                run(Control.write("1539000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
			case "1352 MHz" :
                run(Control.write("1352000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
			case "1014 MHz" :
                run(Control.write("1014000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
			case "845 MHz" :
                run(Control.write("845000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
			case "676 MHz" :
                run(Control.write("676000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
			case "546 MHz" :
                run(Control.write("546000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
			case "421 MHz" :
                run(Control.write("421000", MIF_MAX_FREQ), MIF_MAX_FREQ, context);
                break;
        }
    }

    public static String getDevfreqMaxFreq() {
        if (Utils.readFile(MIF_MAX_FREQ) != null) {
            String value = Utils.readFile(MIF_MAX_FREQ);
            switch (value) {
                case "2730000":
                    return "2730 MHz";
                case "2535000":
                    return "2535 MHz";
                case "2288000":
                    return "2288 MHz";
                case "2028000":
                    return "2028 MHz";
                case "1716000":
                    return "1716 MHz";
                case "1539000":
                    return "1539 MHz";
                case "1352000":
                    return "1352 MHz";
                case "1014000":
                    return "1014 MHz";
                case "845000":
                    return "845 MHz";
                case "676000":
                    return "676 MHz";
                case "546000":
                    return "546 MHz";
                case "421000":
                    return "421 MHz";
            }
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
