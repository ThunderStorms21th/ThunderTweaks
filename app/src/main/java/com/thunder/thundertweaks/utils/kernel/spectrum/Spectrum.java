package com.thunder.thundertweaks.utils.kernel.spectrum;

import android.os.AsyncTask;
import android.content.Context;
import android.util.SparseArray;

import com.thunder.thundertweaks.utils.root.RootUtils;
import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.AppSettings;
import com.thunder.thundertweaks.utils.root.Control;
import com.thunder.thundertweaks.utils.kernel.spectrum.Spectrum;

// added spectrum 

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects; 
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Morogoku on 28/07/2017.
 */

public class Spectrum {

    public static String getProfile(){
        return RootUtils.runCommand("getprop persist.spectrum.profile");
    }
	
	private String ENABLE;

    // Method that interprets a profile and sets it
    public static void setProfile(int profile) {
        int numProfiles = 10;
        if (profile > numProfiles || profile < 0) {
            setProp(0);
        } else {
            setProp(profile);
        }
    }

    // Method that sets system property
    // private static void setProp(final int profile) {
	public static void setProp(final int profile) {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                RootUtils.runCommand("setprop persist.spectrum.profile " + profile);
                return null;
            }
        }.execute();
    }

    public static boolean supported() {
        return RootUtils.getProp("spectrum.support").equals("1");
    }
	
	// added for Apply on Boot
    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SPECTRUM, id, context);
    }
	
}
