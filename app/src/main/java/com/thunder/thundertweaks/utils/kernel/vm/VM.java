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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class VM {

    private static final String PATH = "/proc/sys/vm";
    private static List<String> list;
    private static final List<String> COMMON_VM = Arrays.asList("admin_reserve_kbytes", "block_dump", "compact_memory",
            "compact_unevictable_allowed", "dirty_ratio", "dirty_bytes", "dirty_background_ratio", "dirty_background_bytes",
            "dirty_expire_centisecs","dirty_writeback_centisecs", "dirtytime_expire_seconds", "drop_caches", "extra_free_kbytes",
            "extfrag_threshold", "highmem_is_dirtyable", "laptop_mode", "legacy_va_layout", "lowmem_reserve_ratio",
            "mmap_rnd_compat_bits", "max_map_count", "min_free_kbytes", "min_free_order_shift", "mmap_min_addr",
            "mmap_rnd_bits", "mobile_page_compaction", "nr_pdflush_threads", "oom_dump_tasks", "oom_kill_allocating_task",
            "overcommit_kbytes", "overcommit_memory", "overcommit_ratio", "page-cluster", "panic_on_oom", "percpu_pagelist_fraction",
            "reap_mem_on_sigkill", "scan_unevictable_pages", "swap_ratio", "swap_ratio_enable", "swappiness", "stat_interval",
            "user_reserve_kbytes", "vfs_cache_pressure", "want_old_faultaround_pte", "watermark_scale_factor");
    private static final List<String> REMOVED_VM = Collections.singletonList("swappiness");
    private static final List<String> ALL_VM = getAllSupportedVm();


    private static List<String> getAllSupportedVm() {
        List<String> listVm = new ArrayList<>();
        listVm.add("swappiness");

        File f = new File(PATH);
        if (f.exists()){
            File[] ficheros = f.listFiles();
            for (File fichero : ficheros) {
                boolean blocked = false;
                for (String vm : REMOVED_VM) {
                    if (fichero.getName().contentEquals(vm)) {
                        blocked = true;
                        break;
                    }
                }
                if (!blocked) listVm.add(fichero.getName());
            }
        }
        return listVm;
    }

    public static void setValue(String value, int position, Context context, boolean completeList) {
        if (completeList) list = ALL_VM;
        else list = COMMON_VM;

        run(Control.write(value, PATH + "/" + list.get(position)), PATH + "/" +
                list.get(position), context);
    }

    public static String getValue(int position, boolean completeList) {
        if (completeList) list = ALL_VM;
        else list = COMMON_VM;

        return Utils.readFile(PATH + "/" + list.get(position));
    }

    public static String getName(int position, boolean completeList) {
        if (completeList) list = ALL_VM;
        else list = COMMON_VM;

        return list.get(position);
    }

    public static boolean exists(int position, boolean completeList) {
        if (completeList) list = ALL_VM;
        else list = COMMON_VM;

        return Utils.existFile(PATH + "/" + list.get(position));
    }

    public static int size(boolean completeList) {
        if (completeList) list = ALL_VM;
        else list = COMMON_VM;

        return list.size();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.VM, id, context);
    }

}
