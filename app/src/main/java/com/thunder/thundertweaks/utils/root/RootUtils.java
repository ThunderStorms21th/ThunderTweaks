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
package com.thunder.thundertweaks.utils.root;

import android.os.Build;

import com.thunder.thundertweaks.utils.Log;
import com.thunder.thundertweaks.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by willi on 30.12.15.
 */
public class RootUtils {

    private static SU sInstance;
    private static final String MOUNTS_PATH = "/proc/mounts";

    public static boolean rootAccess() {
        SU su = getSU();
        su.runCommand("echo /testRoot/");
        return !su.mDenied;
    }

    public static boolean busyboxInstalled() {
        return existBinary("busybox") || existBinary("toybox");
    }

    private static boolean existBinary(String binary) {
        String paths;
        if (System.getenv("PATH") != null) {
            paths = System.getenv("PATH");
        } else {
            paths = "/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin";
        }
        for (String path : paths.split(":")) {
            if (!path.endsWith("/")) path += "/";
            if (Utils.existFile(path + binary, false) || Utils.existFile(path + binary)) {
                return true;
            }
        }
        return false;
    }

    public static boolean chmod_editable(String file) {
        return chmod_editable(file, getSU());
    }

    public static boolean chmod_editable(String file, SU su) {
        String permission;

        permission = list(file, su);
        if(permission != null){
            permission = permission.substring(0,10);

            return permission.contains("w");
        }

        return false;
    }

    public static void chmod(String file, String permission) {
        chmod(file, permission, getSU());
    }

    public static void chmod(String file, String permission, SU su) {
        su.runCommand("chmod " + permission + " " + file);
    }

    public static String list(String file){
        return list(file, getSU());
    }

    public static String list(String file, SU su){
        return su.runCommand("ls -l " + file);
    }

    public static String getProp(String prop) {
        return runCommand("getprop " + prop);
    }

    public static void mount(boolean writeable, String mountpoint) {
        mount(writeable, mountpoint, getSU());
    }

    public static void mount(boolean writeable, String mountpoint, SU su) {
        su.runCommand(String.format("mount -o remount,%s %s %s",
                writeable ? "rw" : "ro", mountpoint, mountpoint));
        su.runCommand(String.format("mount -o remount,%s %s",
                writeable ? "rw" : "ro", mountpoint));
        su.runCommand(String.format("mount -o %s,remount %s",
                writeable ? "rw" : "ro", mountpoint));
    }

    public static String runScript(String text, String... arguments) {
        RootFile script = new RootFile("/data/local/tmp/kerneladiutortmp.sh");
        script.mkdir();
        script.write(text, false);
        return script.execute(arguments);
    }

    public static void closeSU() {
        if (sInstance != null) sInstance.close();
        sInstance = null;
    }

    public static String runCommand(String command) {
        return getSU().runCommand(command);
    }

    public static SU getSU() {
        if (sInstance == null || sInstance.mClosed || sInstance.mDenied) {
            if (sInstance != null && !sInstance.mClosed) {
                sInstance.close();
            }
            sInstance = new SU();
        }
        return sInstance;
    }

    /*
     * Based on AndreiLux's SU code in Synapse
     * https://github.com/AndreiLux/Synapse/blob/master/src/main/java/com/af/synapse/utils/Utils.java#L238
     */
    public static class SU {

        private Process mProcess;
        private BufferedWriter mWriter;
        private BufferedReader mReader;
        private final boolean mRoot;
        private boolean mClosed;
        public boolean mDenied;
        private boolean mFirstTry;
        private boolean mLog;

        private ReentrantLock mLock = new ReentrantLock();

        public SU() {
            this(true, false);
        }

        public SU(boolean root, boolean log) {
            mRoot = root;
            try {
                if (log) {
                    Log.i(String.format("%s initialized", root ? "SU" : "SH"));
                }
                mFirstTry = true;
                mProcess = Runtime.getRuntime().exec(root ? "su" : "sh");
                mWriter = new BufferedWriter(new OutputStreamWriter(mProcess.getOutputStream()));
                mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
                mLog = log;
            } catch (IOException ignored) {
                if (log) {
                    Log.e(root ? "Failed to run shell as su" : "Failed to run shell as sh");
                }
                mDenied = true;
                mClosed = true;
            }
        }

        public String runCommand(final String command) {
            if (mClosed) return "";
            try {
                mLock.lock();

                StringBuilder sb = new StringBuilder();
                String callback = "/shellCallback/";
                mWriter.write(command + "\necho " + callback + "\n");
                mWriter.flush();

                int i;
                char[] buffer = new char[256];
                while (true) {
                    sb.append(buffer, 0, mReader.read(buffer));
                    if ((i = sb.indexOf(callback)) > -1) {
                        sb.delete(i, i + callback.length());
                        break;
                    }
                }
                mFirstTry = false;
                if (mLog) {
                    Log.i("run: " + command + " output: " + sb.toString().trim());
                }

                return sb.toString().trim();
            } catch (IOException e) {
                mClosed = true;
                e.printStackTrace();
                if (mFirstTry) mDenied = true;
            } catch (ArrayIndexOutOfBoundsException e) {
                mDenied = true;
            } catch (Exception e) {
                e.printStackTrace();
                mDenied = true;
            } finally {
                mLock.unlock();
            }
            return null;
        }

        public void close() {
            try {
                try {
                    mLock.lock();
                    if (mWriter != null) {
                        mWriter.write("exit\n");
                        mWriter.flush();

                        mWriter.close();
                    }
                    if (mReader != null) {
                        mReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (mProcess != null) {
                    try {
                        mProcess.waitFor();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mProcess.destroy();
                    if (mLog) {
                        Log.i(Utils.strFormat("%s mClosed: %d",
                                mRoot ? "SU" : "SH", mProcess.exitValue()));
                    }
                }
            } finally {
                mLock.unlock();
                mClosed = true;
            }
        }

    }

    public static boolean isSAR() throws Exception {
        BufferedReader mountsStream;
        mountsStream = new BufferedReader(new FileReader(MOUNTS_PATH));
        List<Mount> lines = new ArrayList<>();

        String line = mountsStream.readLine();
        while (line != null){
            lines.add(parseLine(line));
            line = mountsStream.readLine();
        }

        for (Mount mount : lines) {
            if ((mount.device.equals("/dev/root") && mount.mountpoint.equals("/")) ||
                    (mount.mountpoint.equals("/system") && !mount.type.equals("tmpfs") && !mount.device.equals("none")) ||
                    (mount.mountpoint.equals("/system_root") && !mount.type.equals("tmpfs"))) {
                return true;
            }
        }
    return false;
    }

    private static Mount parseLine(String line)throws Exception{
        String[] fields = line.split(" ");
        if (fields.length != 6){
            throw new Exception("Incorrect /proc/mounts format");
        }

        return new Mount(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
    }

    private static class Mount{
        String device, mountpoint, type;
        List<String> flags;
        int dummy0, dummy1;

         Mount(String device, String mountpoint, String type, String flags, String dummy0, String dummy1) {
            this.device = device;
            this.mountpoint = mountpoint;
            this.type = type;
            this.flags = new ArrayList<>(Arrays.asList(flags.split(",")));
            this.dummy0 = Integer.parseInt(dummy0);
            this.dummy1 = Integer.parseInt(dummy1);
        }
    }

}