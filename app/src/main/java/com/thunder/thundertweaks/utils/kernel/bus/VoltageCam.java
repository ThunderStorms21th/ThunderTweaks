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
package com.thunder.thundertweaks.utils.kernel.bus;

import android.content.Context;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 06.11.18.
 */
public class VoltageCam {

    public static final String BACKUP = "/data/.thundertweaks/busCam_stock_voltage";

    public static final String CAM = "/sys/class/devfreq/17000050.devfreq_cam";
    public static final String VOLTAGE = CAM + "/volt_table";
    public static final String TIMESTATES = CAM + "/time_in_state";

    private static final HashMap<String, Boolean> sVoltages = new HashMap<>();
    private static final HashMap<String, Integer> sOffset = new HashMap<>();
    private static final HashMap<String, Integer> sOffsetFreq = new HashMap<>();
    private static final HashMap<String, String> sSplitNewline = new HashMap<>();
    private static final HashMap<String, String> sSplitLine = new HashMap<>();
    private static final HashMap<String, Boolean> sAppend = new HashMap<>();

    static {
        sVoltages.put(VOLTAGE, false);

        sOffsetFreq.put(VOLTAGE, 1000);

        sOffset.put(VOLTAGE, 1000);

        sSplitNewline.put(VOLTAGE, "\\r?\\n");

        sSplitLine.put(VOLTAGE, " ");

        sAppend.put(VOLTAGE, false);
    }

    private static String PATH;
    private static String[] sFreqs;

    public static void setVoltage(String freq, String voltage, Context context) {
        int position = getFreqs().indexOf(freq);
        if (sAppend.get(PATH)) {
            String command = "";
            List<String> voltages = getVoltages();
            for (int i = 0; i < voltages.size(); i++) {
                if (i == position) {
                    command += command.isEmpty() ? voltage : " " + voltage;
                } else {
                    command += command.isEmpty() ? voltages.get(i) : " " + voltages.get(i);
                }
            }
            run(Control.write(command, PATH), PATH, context);
        } else {
            freq = String.valueOf(Utils.strToInt(freq) * sOffsetFreq.get(PATH));
            String volt = String.valueOf((int)(Utils.strToFloat(voltage) * sOffset.get(PATH)));
            run(Control.write(freq + " " + volt, PATH), PATH + freq, context);
        }

    }

    public static int getOffset () {
        return sOffset.get(PATH);
    }

    public static List<String> getStockVoltages() {
        String value = Utils.readFile(BACKUP);
        if (!value.isEmpty()) {
            String[] lines = value.split(sSplitNewline.get(PATH));
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(sSplitLine.get(PATH));
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToFloat(voltageLine[1].trim()) / sOffset.get(PATH)));
                }
            }
            return voltages;
        }
        return null;
    }

    public static List<String> getVoltages() {
        String value = Utils.readFile(PATH);
        if (!value.isEmpty()) {
            String[] lines = value.split(sSplitNewline.get(PATH));
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(sSplitLine.get(PATH));
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToFloat(voltageLine[1].trim()) / sOffset.get(PATH)));
                }
            }
            return voltages;
        }
        return null;
    }

    public static List<String> getFreqs() {
        if (sFreqs == null) {
            String value = Utils.readFile(PATH);
            if (!value.isEmpty()) {
                String[] lines = value.split(sSplitNewline.get(PATH));
                sFreqs = new String[lines.length];
                for (int i = 0; i < sFreqs.length; i++) {
                    sFreqs[i] = String.valueOf(Utils.strToInt(lines[i]
                            .split(sSplitLine.get(PATH))[0].trim()) / sOffsetFreq.get(PATH));
                }
            }
        }
        if (sFreqs == null) return null;
        return Arrays.asList(sFreqs);
    }

    public static boolean hasTimeState() {
        return Utils.existFile(TIMESTATES);
    }

    public static String getTimeStatesLocation() {
        return TIMESTATES;
    }

    public static boolean supported() {
        if (PATH != null) return true;
        for (String path : sVoltages.keySet()) {
            if (Utils.existFile(path)) {
                PATH = path;
            }
        }
        return PATH != null;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.BUS_CAM, id, context);
    }

}
