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
public class VNSwap {

	private static final String VNSWAP = "/sys/block/vnswap0";
	private static final String BLOCK_VNSWAP = "/dev/block/vnswap0";
    private static final String DISKSIZE_VNSWAP = "/sys/block/vnswap0/disksize";
	
    public static void setDisksize(final long value, final Context context) {
		    
        long size = value * 1024 * 1024;

        run(Control.write(String.valueOf(size), DISKSIZE_VNSWAP), DISKSIZE_VNSWAP, context);
    }

    public static int getDisksize() {
        long value = Utils.strToLong(Utils.readFile(DISKSIZE_VNSWAP)) / 1024 / 1024;

        return (int) value;
    }

   public static void enable(boolean enable, Context context) {
        if(enable){
			run(Control.chmod("644", BLOCK_VNSWAP), BLOCK_VNSWAP + "chmod", context);
            run("mkswap " + BLOCK_VNSWAP + " > /dev/null 2>&1", BLOCK_VNSWAP + "mkswap", context);
            run("swapon " + BLOCK_VNSWAP + " > /dev/null 2>&1", BLOCK_VNSWAP + "swapon", context);
        } else{
			run(Control.chmod("644", BLOCK_VNSWAP), BLOCK_VNSWAP + "chmod", context);
            run("swapoff " + BLOCK_VNSWAP + " > /dev/null 2>&1", BLOCK_VNSWAP + "swapoff", context);
			// run(Control.chmod("444", BLOCK_VNSWAP), BLOCK_VNSWAP + "chmod", context);
        }
    }

    public static boolean isEnabled(){
		// run(Control.chmod("644", DISKSIZE_VNSWAP), DISKSIZE_VNSWAP + "chmod", context);
        return Utils.strToLong(Utils.readFile(DISKSIZE_VNSWAP)) != 0;
		// run(Control.chmod("444", DISKSIZE_VNSWAP), DISKSIZE_VNSWAP + "chmod", context);
    }

    public static boolean supported() {
        return Utils.existFile(VNSWAP);
    }
	
	public static boolean hasEnable() {
        return Utils.existFile(VNSWAP);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.VM, id, context);
    }

}
