package com.thunder.thundertweaks.utils.kernel.wake;

import android.content.Context;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.root.Control;

/**
 * Created by morogoku on 14.11.18.
 */

public class GestureVibration {

    private static final String GV = "/sys/android_touch/vib_strength";

    public static int get() {
        return Utils.strToInt(Utils.readFile(GV));
    }

    public static void set(int value, Context context) {
        run(Control.write(String.valueOf(value), GV), GV, context);
    }

    public static boolean supported() {
        return Utils.existFile(GV);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.WAKE, id, context);
    }
}
