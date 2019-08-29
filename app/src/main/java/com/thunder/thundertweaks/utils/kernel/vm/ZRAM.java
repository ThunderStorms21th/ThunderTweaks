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

    public static String getCompAlgorithm() {
        String value = Utils.readFile(ALGORITHM);
        switch (value){
            case "[lzo] lz4 deflate" :
                return "lzo";
            case "lzo [lz4] deflate" :
                return "lz4";
            case "lzo lz4 [deflate]" :
                return "deflate";
        }
        return null;
    }
    public static void setCompAlgorithm(String value, Context context) {
		run(Control.chmod("644", ZRAM), ZRAM + "chmod", context);
		run(Control.chmod("644", RESET), RESET + "chmod", context);
        run(Control.write("1", RESET), RESET, context);
        run(Control.write(String.valueOf(value), ALGORITHM), ALGORITHM, context);
        run(Control.chmod("444", ZRAM), ZRAM + "chmod", context);
		run(Control.chmod("444", RESET), RESET + "chmod", context);
    }

/*
    public static void enable(boolean enable, Context context) {
        if(enable){
            run("mkswap " + BLOCK + " > /dev/null 2>&1", BLOCK + "mkswap", context);
            run("swapon " + BLOCK + " > /dev/null 2>&1", BLOCK + "swapon", context);
        } else{
            String maxCompStrems = null;
            if (Utils.existFile(MAX_COMP_STREAMS)) {
                maxCompStrems = Utils.readFile(MAX_COMP_STREAMS);
            }
            run("swapoff " + BLOCK + " > /dev/null 2>&1", BLOCK + "swapoff", context);
            if (maxCompStrems != null) {
                run(Control.write(maxCompStrems, MAX_COMP_STREAMS), MAX_COMP_STREAMS, context);
            }
        }
    }
*/

   public static void enable(boolean enable, Context context) {
        if(enable){
			run(Control.chmod("644", BLOCK), BLOCK + "chmod", context);
            run("mkswap " + BLOCK + " > /dev/null 2>&1", BLOCK + "mkswap", context);
            run("swapon " + BLOCK + " > /dev/null 2>&1", BLOCK + "swapon", context);
        } else{
            run("swapoff " + BLOCK + " > /dev/null 2>&1", BLOCK + "swapoff", context);
			run(Control.chmod("444", BLOCK), BLOCK + "chmod", context);
        }
    }

    public static boolean isEnabled(){
		// run(Control.chmod("644", DISKSIZE), DISKSIZE + "chmod", context);
        return Utils.strToLong(Utils.readFile(DISKSIZE)) != 0;
		// run(Control.chmod("444", DISKSIZE), DISKSIZE + "chmod", context);
    }

    public static boolean supported() {
        return Utils.existFile(ZRAM);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.VM, id, context);
    }

}
