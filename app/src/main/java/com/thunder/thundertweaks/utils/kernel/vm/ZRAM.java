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
package com.thunder.thundertweaks.utils.kernel.vm;

import android.content.Context;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 03.08.16.
 */
public class ZRAM {

    private static final String ZRAM = "/sys/block/zram0";
    private static final String BLOCK = "/dev/block/zram0";
    private static final String DISKSIZE = "/sys/block/zram0/disksize";
    private static final String RESET = "/sys/block/zram0/reset";
    private static final String ALGORITHM = "/sys/block/zram0/comp_algorithm";
    private static final String MAX_COMP_STREAMS = "/sys/block/zram0/max_comp_streams";
	
	private static final String VNSWAP = "/sys/block/vnswap0";
	private static final String BLOCK_VNSWAP = "/dev/block/vnswap0";
    private static final String DISKSIZE_VNSWAP = "/sys/block/vnswap0/disksize";
	
    public static void setDisksize(final long value, final Context context) {
		    
			String maxCompStrems = null;
			if (Utils.existFile(MAX_COMP_STREAMS)) {
                maxCompStrems = Utils.readFile(MAX_COMP_STREAMS);
            }
			
        long size = value * 1024 * 1024;

        run(Control.write("1", RESET), RESET, context);
        run(Control.write(String.valueOf(size), DISKSIZE), DISKSIZE, context);
    }

    public static int getDisksize() {
        long value = Utils.strToLong(Utils.readFile(DISKSIZE)) / 1024 / 1024;

        return (int) value;
    }

    public static void setCompAlgorithm(String value, Context context) {
        run(Control.write(value, ALGORITHM), ALGORITHM, context);
    }

    public static String getCompAlgorithm() {
        String[] algorithms = Utils.readFile(ALGORITHM).split(" ");
        for (String algorithm : algorithms) {
            if (algorithm.startsWith("[") && algorithm.endsWith("]")) {
                return algorithm.replace("[", "").replace("]", "");
            }
        }
        return "";
    }

    public static List<String> getCompAlgorithms() {
        String[] algorithms = Utils.readFile(ALGORITHM).split(" ");
        List<String> list = new ArrayList<>();
        for (String algorithm : algorithms) {
            list.add(algorithm.replace("[", "").replace("]", ""));
        }
        return list;
    }

   public static void enable(boolean enable, Context context) {
        if(enable){
			run(Control.chmod("644", BLOCK), BLOCK + "chmod", context);
            run("mkswap " + BLOCK + " > /dev/null 2>&1", BLOCK + "mkswap", context);
            run("swapon " + BLOCK + " > /dev/null 2>&1", BLOCK + "swapon", context);
        } else{
			run(Control.chmod("644", BLOCK), BLOCK + "chmod", context);
            run("swapoff " + BLOCK + " > /dev/null 2>&1", BLOCK + "swapoff", context);
        }
    }

    public static boolean isEnabled(){
        return Utils.strToLong(Utils.readFile(DISKSIZE)) != 0;
    }

    public static boolean supported() {
        return Utils.existFile(ZRAM);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.VM, id, context);
    }

}
