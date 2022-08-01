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
package com.thunder.thundertweaks.fragments.statistics;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.DescriptionFragment;
import com.thunder.thundertweaks.fragments.recyclerview.RecyclerViewFragment;
import com.thunder.thundertweaks.utils.AppSettings;
import com.thunder.thundertweaks.utils.Device;
import com.thunder.thundertweaks.utils.kernel.battery.Battery;
import com.thunder.thundertweaks.utils.root.RootUtils;
import com.thunder.thundertweaks.views.recyclerview.CardView;
import com.thunder.thundertweaks.views.recyclerview.DescriptionView;
import com.thunder.thundertweaks.views.recyclerview.RecyclerViewItem;
import com.thunder.thundertweaks.utils.kernel.gpu.GPUFreqExynos;
import com.thunder.thundertweaks.views.recyclerview.CardView;

import java.util.List;

/**
 * Created by willi on 28.04.16.
 */
public class DeviceFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        String processor = Device.CPUInfo.getInstance().getProcessor();
        String hardware = Device.CPUInfo.getInstance().getVendor();
        String features = Device.CPUInfo.getInstance().getFeatures();
        int ram = (int) Device.MemInfo.getInstance().getTotalMem();

        if (!processor.isEmpty()) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.processor), processor));
        }
        if (!hardware.isEmpty()) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.vendor), hardware));
        }
        if (!features.isEmpty()) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.features), features));
        }
        if (ram > 0) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.ram), ram + getString(R.string.mb)));
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        String issar;
        try{
            issar = (RootUtils.isSAR()) ? getString(R.string.yes) : getString(R.string.no);
        } catch (Exception e){
            issar = getString(R.string.not_available);
        }

        String[][] deviceInfos = {
                {getString(R.string.android_version), Device.getVersion()},
                {getString(R.string.android_api_level), String.valueOf(Device.getSDK())},
                {getString(R.string.sar), issar},
                {getString(R.string.android_codename), Device.getCodename()},
                /* {getString(R.string.gpu_driver_info), Device.getGPUDriverInfo()}, */
                {"GPU " + getString(R.string.gpu_driver_version), GPUFreqExynos.getInstance().getDriverVersion()},
                {"GPU " + getString(R.string.gpu_lib_version), AppSettings.getString("gpu_lib_version", "", getActivity())},
                {getString(R.string.fingerprint), Device.getFingerprint()},
                {getString(R.string.build_display_id), Device.getBuildDisplayId()},
                {getString(R.string.baseband), Device.getBaseBand()},
                {getString(R.string.bootloader), Device.getBootloader()},
                {getString(R.string.rom), Device.ROMInfo.getInstance().getVersion()},
                {getString(R.string.trustzone), Device.TrustZone.getInstance().getVersion()},
        };

        String[][] boardInfos = {
                {getString(R.string.hardware), Device.getHardware()},
                {getString(R.string.architecture), Device.getArchitecture()},
                {getString(R.string.battery_health), Battery.getHealthValue()},
                {getString(R.string.asv), Device.getAsv()},
                {getString(R.string.kernel), Device.getKernelVersion(true)}
        };

        CardView deviceCard = new CardView(getActivity());
        String vendor = Device.getVendor();
        vendor = vendor.substring(0, 1).toUpperCase() + vendor.substring(1);
        deviceCard.setTitle(vendor + " " + Device.getModel());

        CardView boardCard = new CardView(getActivity());
        boardCard.setTitle(Device.getBoard().toUpperCase());

        for (String[] deviceInfo : deviceInfos) {
            if (deviceInfo[1] != null && deviceInfo[1].isEmpty()) {
                continue;
            }
            DescriptionView info = new DescriptionView();
            info.setTitle(deviceInfo[0]);
            info.setSummary(deviceInfo[1]);
            deviceCard.addItem(info);
        }

        for (String[] boardInfo : boardInfos) {
            if (boardInfo[1] != null && boardInfo[1].isEmpty()) {
                continue;
            }
            DescriptionView info = new DescriptionView();
            info.setTitle(boardInfo[0]);
            info.setSummary(boardInfo[1]);
            boardCard.addItem(info);
        }

        items.add(deviceCard);
        items.add(boardCard);
    }
}
