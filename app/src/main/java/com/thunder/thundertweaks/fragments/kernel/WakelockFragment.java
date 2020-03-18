package com.thunder.thundertweaks.fragments.kernel;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.fragments.recyclerview.RecyclerViewFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.kernel.wakelock.Wakelock;
import com.thunder.thundertweaks.views.recyclerview.CardView;
import com.thunder.thundertweaks.views.recyclerview.RecyclerViewItem;
import com.thunder.thundertweaks.views.recyclerview.SeekBarView;
import com.thunder.thundertweaks.views.recyclerview.SwitchView;

import java.util.List;

/**
 * Created by Morogoku on 10/04/2017.
 */

public class WakelockFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        if (Wakelock.hasWakelock()) {
            wakelocksInit(items);
        }
    }

    private void wakelocksInit(List<RecyclerViewItem> items){

        CardView wake = new CardView(getActivity());
        wake.setTitle(getString(R.string.wkl_control));

        if(Wakelock.hasSensorHub()) {
            SwitchView sh = new SwitchView();
            sh.setTitle(getString(R.string.wkl_sensorhub));
            sh.setSummary(getString(R.string.wkl_sensorhub_summary));
            sh.setChecked(Wakelock.isSensorHubEnabled());
            sh.addOnSwitchListener((switchView, isChecked)
                    -> Wakelock.enableSensorHub(isChecked, getActivity()));

            wake.addItem(sh);
        }

        if(Wakelock.hasSSP()) {
            SwitchView ssp = new SwitchView();
            ssp.setTitle(getString(R.string.wkl_ssp));
            ssp.setSummary(getString(R.string.wkl_ssp_summary));
            ssp.setChecked(Wakelock.isSSPEnabled());
            ssp.addOnSwitchListener((switchView, isChecked)
                    -> Wakelock.enableSSP(isChecked, getActivity()));

            wake.addItem(ssp);
        }

        if(Wakelock.hasGPS()) {
            SwitchView gps = new SwitchView();
            gps.setTitle(getString(R.string.wkl_gps));
            gps.setSummary(getString(R.string.wkl_gps_summary));
            gps.setChecked(Wakelock.isGPSEnabled());
            gps.addOnSwitchListener((switchView, isChecked)
                    -> Wakelock.enableGPS(isChecked, getActivity()));

            wake.addItem(gps);
        }

        if(Wakelock.hasMMC0()) {
            SwitchView mmc0 = new SwitchView();
            mmc0.setTitle(getString(R.string.wkl_mmc0));
            mmc0.setSummary(getString(R.string.wkl_mmc0_summary));
            mmc0.setChecked(Wakelock.isMMC0Enabled());
            mmc0.addOnSwitchListener((switchView, isChecked)
                    -> Wakelock.enableMMC0(isChecked, getActivity()));

            wake.addItem(mmc0);
        }

        if(Wakelock.hasWireless()) {
            SwitchView wifi = new SwitchView();
            wifi.setTitle(getString(R.string.wkl_wireless));
            wifi.setSummary(getString(R.string.wkl_wireless_summary));
            wifi.setChecked(Wakelock.isWirelessEnabled());
            wifi.addOnSwitchListener((switchView, isChecked)
                    -> Wakelock.enableWireless(isChecked, getActivity()));

            wake.addItem(wifi);
        }

        if(Wakelock.hasWireless1()) {
            SwitchView wifi1 = new SwitchView();
            wifi1.setTitle(getString(R.string.wkl_wireless1));
            wifi1.setSummary(getString(R.string.wkl_wireless1_summary));
            wifi1.setChecked(Wakelock.isWireless1Enabled());
            wifi1.addOnSwitchListener((switchView, isChecked)
                    -> Wakelock.enableWireless1(isChecked, getActivity()));

            wake.addItem(wifi1);
        }

        if(Wakelock.hasWireless2()) {
            SwitchView wifi2 = new SwitchView();
            wifi2.setTitle(getString(R.string.wkl_wireless2));
            wifi2.setSummary(getString(R.string.wkl_wireless2_summary));
            wifi2.setChecked(Wakelock.isWireless2Enabled());
            wifi2.addOnSwitchListener((switchView, isChecked)
                    -> Wakelock.enableWireless2(isChecked, getActivity()));

            wake.addItem(wifi2);
        }

        if(Wakelock.hasWireless3()) {
            SwitchView wifi3 = new SwitchView();
            wifi3.setTitle(getString(R.string.wkl_wireless3));
            wifi3.setSummary(getString(R.string.wkl_wireless3_summary));
            wifi3.setChecked(Wakelock.isWireless3Enabled());
            wifi3.addOnSwitchListener((switchView, isChecked)
                    -> Wakelock.enableWireless3(isChecked, getActivity()));

            wake.addItem(wifi3);
        }

        if(Wakelock.hasBluetooth()) {
            SwitchView bt = new SwitchView();
            bt.setTitle(getString(R.string.wkl_bluetooth));
            bt.setSummary(getString(R.string.wkl_bluetooth_summary));
            bt.setChecked(Wakelock.isBluetoothEnabled());
            bt.addOnSwitchListener((switchView, isChecked)
                    -> Wakelock.enableBluetooth(isChecked, getActivity()));

            wake.addItem(bt);
        }

        if(Wakelock.hasBattery()) {
            SeekBarView bat = new SeekBarView();
            bat.setTitle(getString(R.string.wkl_battery));
            bat.setSummary(getString(R.string.wkl_battery_summary));
            bat.setMax(15);
            bat.setMin(1);
            bat.setProgress(Utils.strToInt(Wakelock.getBattery()) - 1);
            bat.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelock.setBattery((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wake.addItem(bat);
        }

        if(Wakelock.hasNFC()) {
            SeekBarView nfc = new SeekBarView();
            nfc.setTitle(getString(R.string.wkl_nfc));
            nfc.setSummary(getString(R.string.wkl_nfc_summary));
            nfc.setMax(3);
            nfc.setMin(1);
            nfc.setProgress(Utils.strToInt(Wakelock.getNFC()) - 1);
            nfc.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelock.setNFC((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wake.addItem(nfc);
        }

        if (wake.size() > 0) {
            items.add(wake);
        }
    }
}
