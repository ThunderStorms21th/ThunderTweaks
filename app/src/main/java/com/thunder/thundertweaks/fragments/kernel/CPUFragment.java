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
package com.thunder.thundertweaks.fragments.kernel;

import android.text.InputType;
import android.util.SparseArray;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.fragments.BaseFragment;
import com.thunder.thundertweaks.fragments.DescriptionFragment;
import com.thunder.thundertweaks.fragments.recyclerview.RecyclerViewFragment;
import com.thunder.thundertweaks.utils.AppSettings;
import com.thunder.thundertweaks.utils.Device;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.ViewUtils;
import com.thunder.thundertweaks.utils.kernel.cpu.CPUBoost;
import com.thunder.thundertweaks.utils.kernel.cpu.CPUFreq;
import com.thunder.thundertweaks.utils.kernel.cpu.Misc;
import com.thunder.thundertweaks.views.dialog.Dialog;
import com.thunder.thundertweaks.views.recyclerview.CardView;
import com.thunder.thundertweaks.views.recyclerview.DescriptionView;
import com.thunder.thundertweaks.views.recyclerview.GenericSelectView2;
import com.thunder.thundertweaks.views.recyclerview.RecyclerViewItem;
import com.thunder.thundertweaks.views.recyclerview.SeekBarView;
import com.thunder.thundertweaks.views.recyclerview.SelectView;
import com.thunder.thundertweaks.views.recyclerview.SwitchView;
import com.thunder.thundertweaks.views.recyclerview.XYGraphView;
import com.thunder.thundertweaks.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 01.05.16.
 */
public class CPUFragment extends RecyclerViewFragment {

    private CPUFreq mCPUFreq;
    private CPUBoost mCPUBoost;

    private XYGraphView mCPUUsageBig;
    private SelectView mCPUMaxBig;
    private SelectView mCPUMinBig;
    private SelectView mCPUMaxSuspBig;
    private SelectView mCPUMinSuspBig;
    private SelectView mCPUMaxScreenOffBig;
    private SelectView mCPUGovernorBig;

    private XYGraphView mCPUUsageMid;
    private SelectView mCPUMaxMid;
    private SelectView mCPUMinMid;
    private SelectView mCPUMaxSuspMid;
    private SelectView mCPUMinSuspMid;
    private SelectView mCPUMaxScreenOffMid;
    private SelectView mCPUGovernorMid;

    private XYGraphView mCPUUsageLITTLE;
    private SelectView mCPUMaxLITTLE;
    private SelectView mCPUMinLITTLE;
    private SelectView mCPUMaxSuspLITTLE;
    private SelectView mCPUMinSuspLITTLE;
    private SelectView mCPUMaxScreenOffLITTLE;
    private SelectView mCPUGovernorLITTLE;

    private SparseArray<SwitchView> mCoresBig = new SparseArray<>();
    private SparseArray<SwitchView> mCoresMid = new SparseArray<>();
    private SparseArray<SwitchView> mCoresLITTLE = new SparseArray<>();

    private PathReaderFragment mGovernorTunableFragment;
    private Dialog mGovernorTunableErrorDialog;

    private List<GenericSelectView2> mInput = new ArrayList<>();
    private List<GenericSelectView2> mKERNEL = new ArrayList<>();
    private boolean mCompleteList;

    @Override
    protected BaseFragment getForegroundFragment() {
        return mGovernorTunableFragment = new PathReaderFragment();
    }

    @Override
    protected void init() {
        super.init();

        mCPUFreq = CPUFreq.getInstance(getActivity());
        mCPUBoost = CPUBoost.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        addViewPagerFragment(DescriptionFragment.newInstance(getString(mCPUFreq.getCpuCount() > 1 ?
                R.string.cores : R.string.cores_singular, mCPUFreq.getCpuCount()), Device.getBoard()));

        if (mGovernorTunableErrorDialog != null) {
            mGovernorTunableErrorDialog.show();
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mInput.clear();

        freqInit(items);
        if (mCPUBoost.supported()) {
            cpuBoostCpuInit(items);
        }
		
        if (Misc.hasCFSScheduler()) {
            cfsSchedulerInit(items);
        }
		
        if (Misc.hasCpuQuiet()) {
            cpuQuietInit(items);
        }
		
        if (Misc.hasFreqSuspend()) {
            cpuFreqSuspInit(items);
        }
		
        if (Misc.hasCpuTouchBoost()) {
            cpuTouchBoostInit(items);
        }
        if (Misc.hasDevFreqBoostFreq()) {
            cpuDevFreqBoostInit(items);
        }
		
        if (Misc.hasCpuFingerprintBoost()) {
            cpuFingerprintBoostInit(items);
        }
		
        if (Misc.hasMcPowerSaving()) {
            mcPowerSavingInit(items);
        }
        if (Misc.hasPowerSavingWq()) {
            powerSavingWqInit(items);
        }
        kernelTunablesInit(items);
    }

    private void freqInit(List<RecyclerViewItem> items) {
        mCPUUsageBig = new XYGraphView();
        if (mCPUFreq.isBigLITTLE()) {
            mCPUUsageBig.setTitle(getString(R.string.cpu_usage_string, getString(R.string.cluster_big)));
        } else {
            mCPUUsageBig.setTitle(getString(R.string.cpu_usage));
        }

        items.add(mCPUUsageBig);

        CardView bigCoresCard = new CardView(getActivity());
        if (mCPUFreq.isBigLITTLE()) {
            bigCoresCard.setTitle(getString(R.string.cores_string, getString(R.string.cluster_big)));
        }

        final List<Integer> bigCores = mCPUFreq.getBigCpuRange();

        mCoresBig.clear();
        for (final int core : bigCores) {
            SwitchView coreSwitch = new SwitchView();
            coreSwitch.setSummary(getString(R.string.core, core + 1));

            mCoresBig.put(core, coreSwitch);
            bigCoresCard.addItem(coreSwitch);
        }

        CardView bigFrequenciesCard = new CardView(getActivity());
        if (mCPUFreq.isBigLITTLE()) {
            bigFrequenciesCard.setTitle(getString(R.string.frequencies_string, getString(R.string.cluster_big)));
        }

        mCPUMaxBig = new SelectView();
        mCPUMaxBig.setTitle(getString(R.string.cpu_max_freq));
        mCPUMaxBig.setSummary(getString(R.string.cpu_max_freq_summary));
        mCPUMaxBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
        mCPUMaxBig.setOnItemSelected((selectView, position, item)
                -> mCPUFreq.setMaxFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                bigCores.get(bigCores.size() - 1), getActivity()));
        bigFrequenciesCard.addItem(mCPUMaxBig);

        mCPUMinBig = new SelectView();
        mCPUMinBig.setTitle(getString(R.string.cpu_min_freq));
        mCPUMinBig.setSummary(getString(R.string.cpu_min_freq_summary));
        mCPUMinBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
        mCPUMinBig.setOnItemSelected((selectView, position, item)
                -> mCPUFreq.setMinFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                bigCores.get(bigCores.size() - 1), getActivity()));
        bigFrequenciesCard.addItem(mCPUMinBig);

        if(mCPUFreq.hasBigAllCoresMaxFreq()) {
            SwitchView bigAll = new SwitchView();
            bigAll.setTitle(getString(R.string.big_cores_max_title));
            bigAll.setSummary(getString(R.string.big_cores_max_summary));
            bigAll.setChecked(mCPUFreq.isBigAllCoresMaxFreq());
            bigAll.addOnSwitchListener((switchView, isChecked) -> {
                if (!isChecked) {
                    mCPUFreq.setMaxFreq(2288000, bigCores.get(0),
                            bigCores.get(bigCores.size() - 1), getActivity());
                }
                mCPUFreq.enableBigAllCoresMaxFreq(isChecked, getActivity());
            });
            bigFrequenciesCard.addItem(bigAll);
        }

        if (mCPUFreq.hasMaxScreenOffFreq()) {
            mCPUMaxScreenOffBig = new SelectView();
            mCPUMaxScreenOffBig.setTitle(getString(R.string.cpu_max_screen_off_freq));
            mCPUMaxScreenOffBig.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            mCPUMaxScreenOffBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            mCPUMaxScreenOffBig.setOnItemSelected((selectView, position, item)
                    -> mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                    bigCores.get(bigCores.size() - 1), getActivity()));
            bigFrequenciesCard.addItem(mCPUMaxScreenOffBig);
        }

        if (mCPUFreq.hasMaxSuspFreq()) {
            mCPUMaxSuspBig = new SelectView();
            mCPUMaxSuspBig.setTitle(getString(R.string.cpu_max_suspend_freq));
            mCPUMaxSuspBig.setSummary(getString(R.string.cpu_max_suspend_freq_summary));
            mCPUMaxSuspBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            mCPUMaxSuspBig.setOnItemSelected((selectView, position, item)
                    -> mCPUFreq.setMaxSuspFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                    bigCores.get(bigCores.size() - 1), getActivity()));
            bigFrequenciesCard.addItem(mCPUMaxSuspBig);
        }
		
        if (mCPUFreq.hasMinSuspFreq()) {
            mCPUMinSuspBig = new SelectView();
            mCPUMinSuspBig.setTitle(getString(R.string.cpu_min_suspend_freq));
            mCPUMinSuspBig.setSummary(getString(R.string.cpu_min_suspend_freq_summary));
            mCPUMinSuspBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            mCPUMinSuspBig.setOnItemSelected((selectView, position, item)
                    -> mCPUFreq.setMinSuspFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                    bigCores.get(bigCores.size() - 1), getActivity()));
            bigFrequenciesCard.addItem(mCPUMinSuspBig);
        }

        CardView bigGovernorsCard = new CardView(getActivity());
        if (mCPUFreq.isBigLITTLE()) {
            bigGovernorsCard.setTitle(getString(R.string.governors_string, getString(R.string.cluster_big)));
        }

        mCPUGovernorBig = new SelectView();
        mCPUGovernorBig.setTitle(getString(R.string.cpu_governor));
        mCPUGovernorBig.setSummary(getString(R.string.cpu_governor_summary));
        mCPUGovernorBig.setItems(mCPUFreq.getGovernors());
        mCPUGovernorBig.setOnItemSelected((selectView, position, item)
                -> mCPUFreq.setGovernor(item, bigCores.get(0), bigCores.get(bigCores.size() - 1),
                getActivity()));
        bigGovernorsCard.addItem(mCPUGovernorBig);

        DescriptionView governorTunablesBig = new DescriptionView();
        governorTunablesBig.setTitle(getString(R.string.cpu_governor_tunables));
        governorTunablesBig.setSummary(getString(R.string.governor_tunables_summary));
        governorTunablesBig.setOnItemClickListener(item
                -> showGovernorTunables(bigCores.get(0), bigCores.get(bigCores.size() - 1)));
        bigGovernorsCard.addItem(governorTunablesBig);

        items.add(bigCoresCard);
        items.add(bigFrequenciesCard);
        items.add(bigGovernorsCard);

        if (mCPUFreq.isBigLITTLE()) {
            if (mCPUFreq.hasMidCpu()) {
                mCPUUsageMid = new XYGraphView();
                mCPUUsageMid.setTitle(getString(R.string.cpu_usage_string, getString(R.string.cluster_middle)));

                items.add(mCPUUsageMid);

                CardView MidCoresCard = new CardView(getActivity());
                MidCoresCard.setTitle(getString(R.string.cores_string, getString(R.string.cluster_middle)));

                final List<Integer> MidCores = mCPUFreq.getMidCpuRange();

                mCoresMid.clear();
                for (final int core : MidCores) {
                    SwitchView coreSwitch = new SwitchView();
                    coreSwitch.setSummary(getString(R.string.core, core + 1));

                    mCoresMid.put(core, coreSwitch);
                    MidCoresCard.addItem(coreSwitch);
                }

                CardView MidFrequenciesCard = new CardView(getActivity());
                MidFrequenciesCard.setTitle(getString(R.string.frequencies_string, getString(R.string.cluster_middle)));

                mCPUMaxMid = new SelectView();
                mCPUMaxMid.setTitle(getString(R.string.cpu_max_freq));
                mCPUMaxMid.setSummary(getString(R.string.cpu_max_freq_summary));
                mCPUMaxMid.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                mCPUMaxMid.setOnItemSelected((selectView, position, item)
                        -> mCPUFreq.setMaxFreq(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position),
                        MidCores.get(0), MidCores.get(MidCores.size() - 1), getActivity()));
                MidFrequenciesCard.addItem(mCPUMaxMid);

                mCPUMinMid = new SelectView();
                mCPUMinMid.setTitle(getString(R.string.cpu_min_freq));
                mCPUMinMid.setSummary(getString(R.string.cpu_min_freq_summary));
                mCPUMinMid.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                mCPUMinMid.setOnItemSelected((selectView, position, item)
                        -> mCPUFreq.setMinFreq(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position),
                        MidCores.get(0), MidCores.get(MidCores.size() - 1), getActivity()));
                MidFrequenciesCard.addItem(mCPUMinMid);

                if (mCPUFreq.hasMaxScreenOffFreq(mCPUFreq.getMidCpu())) {
                    mCPUMaxScreenOffMid = new SelectView();
                    mCPUMaxScreenOffMid.setTitle(getString(R.string.cpu_max_screen_off_freq));
                    mCPUMaxScreenOffMid.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
                    mCPUMaxScreenOffMid.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                    mCPUMaxScreenOffMid.setOnItemSelected((selectView, position, item)
                            -> mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position),
                            MidCores.get(0), MidCores.get(MidCores.size() - 1), getActivity()));
                    MidFrequenciesCard.addItem(mCPUMaxScreenOffMid);
                }
				
                if (mCPUFreq.hasMaxSuspFreq(mCPUFreq.getMidCpu())) {
                    mCPUMaxSuspMid = new SelectView();
                    mCPUMaxSuspMid.setTitle(getString(R.string.cpu_max_suspend_freq));
                    mCPUMaxSuspMid.setSummary(getString(R.string.cpu_max_suspend_freq_summary));
                    mCPUMaxSuspMid.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                    mCPUMaxSuspMid.setOnItemSelected((selectView, position, item)
                            -> mCPUFreq.setMaxSuspFreq(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position),
                            MidCores.get(0), MidCores.get(MidCores.size() - 1), getActivity()));
                    MidFrequenciesCard.addItem(mCPUMaxSuspMid);
                }

                if (mCPUFreq.hasMinSuspFreq(mCPUFreq.getMidCpu())) {
                    mCPUMinSuspMid = new SelectView();
                    mCPUMinSuspMid.setTitle(getString(R.string.cpu_min_suspend_freq));
                    mCPUMinSuspMid.setSummary(getString(R.string.cpu_min_suspend_freq_summary));
                    mCPUMinSuspMid.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                    mCPUMinSuspMid.setOnItemSelected((selectView, position, item)
                            -> mCPUFreq.setMinSuspFreq(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position),
                            MidCores.get(0), MidCores.get(MidCores.size() - 1), getActivity()));
                    MidFrequenciesCard.addItem(mCPUMinSuspMid);
                }				

                CardView MidGovernorsCard = new CardView(getActivity());
                MidGovernorsCard.setTitle(getString(R.string.governors_string, getString(R.string.cluster_middle)));

                mCPUGovernorMid = new SelectView();
                mCPUGovernorMid.setTitle(getString(R.string.cpu_governor));
                mCPUGovernorMid.setSummary(getString(R.string.cpu_governor_summary));
                mCPUGovernorMid.setItems(mCPUFreq.getGovernors());
                mCPUGovernorMid.setOnItemSelected((selectView, position, item)
                        -> mCPUFreq.setGovernor(item, MidCores.get(0), MidCores.get(MidCores.size() - 1),
                        getActivity()));
                MidGovernorsCard.addItem(mCPUGovernorMid);

                DescriptionView governorTunablesMid = new DescriptionView();
                governorTunablesMid.setTitle(getString(R.string.cpu_governor_tunables));
                governorTunablesMid.setSummary(getString(R.string.governor_tunables_summary));
                governorTunablesMid.setOnItemClickListener(item
                        -> showGovernorTunables(MidCores.get(0), MidCores.get(MidCores.size() - 1)));
                MidGovernorsCard.addItem(governorTunablesMid);

                items.add(MidCoresCard);
                items.add(MidFrequenciesCard);
                items.add(MidGovernorsCard);
            }

            mCPUUsageLITTLE = new XYGraphView();
            mCPUUsageLITTLE.setTitle(getString(R.string.cpu_usage_string, getString(R.string.cluster_little)));

            items.add(mCPUUsageLITTLE);

            CardView LITTLECoresCard = new CardView(getActivity());
            LITTLECoresCard.setTitle(getString(R.string.cores_string, getString(R.string.cluster_little)));

            final List<Integer> LITTLECores = mCPUFreq.getLITTLECpuRange();

            mCoresLITTLE.clear();
            for (final int core : LITTLECores) {
                SwitchView coreSwitch = new SwitchView();
                coreSwitch.setSummary(getString(R.string.core, core + 1));

                mCoresLITTLE.put(core, coreSwitch);
                LITTLECoresCard.addItem(coreSwitch);
            }

            CardView LITTLEFrequenciesCard = new CardView(getActivity());
            LITTLEFrequenciesCard.setTitle(getString(R.string.frequencies_string, getString(R.string.cluster_little)));

            mCPUMaxLITTLE = new SelectView();
            mCPUMaxLITTLE.setTitle(getString(R.string.cpu_max_freq));
            mCPUMaxLITTLE.setSummary(getString(R.string.cpu_max_freq_summary));
            mCPUMaxLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            mCPUMaxLITTLE.setOnItemSelected((selectView, position, item)
                    -> mCPUFreq.setMaxFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                    LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity()));
            LITTLEFrequenciesCard.addItem(mCPUMaxLITTLE);

            mCPUMinLITTLE = new SelectView();
            mCPUMinLITTLE.setTitle(getString(R.string.cpu_min_freq));
            mCPUMinLITTLE.setSummary(getString(R.string.cpu_min_freq_summary));
            mCPUMinLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            mCPUMinLITTLE.setOnItemSelected((selectView, position, item)
                    -> mCPUFreq.setMinFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                    LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity()));
            LITTLEFrequenciesCard.addItem(mCPUMinLITTLE);

            if (mCPUFreq.hasMaxScreenOffFreq(mCPUFreq.getLITTLECpu())) {
                mCPUMaxScreenOffLITTLE = new SelectView();
                mCPUMaxScreenOffLITTLE.setTitle(getString(R.string.cpu_max_screen_off_freq));
                mCPUMaxScreenOffLITTLE.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
                mCPUMaxScreenOffLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
                mCPUMaxScreenOffLITTLE.setOnItemSelected((selectView, position, item)
                        -> mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                        LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity()));
                LITTLEFrequenciesCard.addItem(mCPUMaxScreenOffLITTLE);
            }
			
            if (mCPUFreq.hasMaxSuspFreq(mCPUFreq.getLITTLECpu())) {
                mCPUMaxSuspLITTLE = new SelectView();
                mCPUMaxSuspLITTLE.setTitle(getString(R.string.cpu_max_suspend_freq));
                mCPUMaxSuspLITTLE.setSummary(getString(R.string.cpu_max_suspend_freq_summary));
                mCPUMaxSuspLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
                mCPUMaxSuspLITTLE.setOnItemSelected((selectView, position, item)
                        -> mCPUFreq.setMaxSuspFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                        LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity()));
                LITTLEFrequenciesCard.addItem(mCPUMaxSuspLITTLE);
            }

            if (mCPUFreq.hasMinSuspFreq(mCPUFreq.getLITTLECpu())) {
                mCPUMinSuspLITTLE = new SelectView();
                mCPUMinSuspLITTLE.setTitle(getString(R.string.cpu_min_suspend_freq));
                mCPUMinSuspLITTLE.setSummary(getString(R.string.cpu_min_suspend_freq_summary));
                mCPUMinSuspLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
                mCPUMinSuspLITTLE.setOnItemSelected((selectView, position, item)
                        -> mCPUFreq.setMinSuspFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                        LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity()));
                LITTLEFrequenciesCard.addItem(mCPUMinSuspLITTLE);
            }			

            CardView LITTLEGovernorsCard = new CardView(getActivity());
            LITTLEGovernorsCard.setTitle(getString(R.string.governors_string, getString(R.string.cluster_little)));

            mCPUGovernorLITTLE = new SelectView();
            mCPUGovernorLITTLE.setTitle(getString(R.string.cpu_governor));
            mCPUGovernorLITTLE.setSummary(getString(R.string.cpu_governor_summary));
            mCPUGovernorLITTLE.setItems(mCPUFreq.getGovernors());
            mCPUGovernorLITTLE.setOnItemSelected((selectView, position, item)
                    -> mCPUFreq.setGovernor(item, LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1),
                    getActivity()));
            LITTLEGovernorsCard.addItem(mCPUGovernorLITTLE);

            DescriptionView governorTunablesLITTLE = new DescriptionView();
            governorTunablesLITTLE.setTitle(getString(R.string.cpu_governor_tunables));
            governorTunablesLITTLE.setSummary(getString(R.string.governor_tunables_summary));
            governorTunablesLITTLE.setOnItemClickListener(item
                    -> showGovernorTunables(LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1)));
            LITTLEGovernorsCard.addItem(governorTunablesLITTLE);

            items.add(LITTLECoresCard);
            items.add(LITTLEFrequenciesCard);
            items.add(LITTLEGovernorsCard);
        }
    }

    private void showGovernorTunables(int min, int max) {
        boolean offline = mCPUFreq.isOffline(min);
        if (offline) {
            mCPUFreq.onlineCpu(min, true, false, null);
        }
        String governor = mCPUFreq.getGovernor(min, false);
        if (governor.isEmpty()) {
            mGovernorTunableErrorDialog = ViewUtils.dialogBuilder(getString(R.string.cpu_governor_tunables_read_error),
                    null,
                    (dialog, which) -> {
                    },
                    dialog -> mGovernorTunableErrorDialog = null, getActivity());
            mGovernorTunableErrorDialog.show();
        } else {
            setForegroundText(governor);
            mGovernorTunableFragment.setError(getString(R.string.tunables_error, governor));
            mGovernorTunableFragment.setPath(mCPUFreq.getGovernorTunablesPath(min, governor), min, max,
                    ApplyOnBootFragment.CPU);
            showForeground();
        }
        if (offline) {
            mCPUFreq.onlineCpu(min, false, false, null);
        }
    }

	private void cpuFreqSuspInit(List<RecyclerViewItem> items) {
        CardView fsiCard = new CardView(getActivity());
        fsiCard.setTitle(getString(R.string.freq_susp_enable));

        DescriptionView fsiDesc = new DescriptionView();
        fsiDesc.setSummary(getString(R.string.freq_susp_enable_summary));
        fsiCard.addItem(fsiDesc);

        SwitchView fsi = new SwitchView();
        fsi.setSummaryOn(getString(R.string.enabled));
        fsi.setSummaryOff(getString(R.string.disabled));
        fsi.setChecked(Misc.isFreqSuspendEnabled());
        fsi.addOnSwitchListener((switchView, isChecked)
                -> Misc.enableFreqSuspend(isChecked, getActivity()));

        fsiCard.addItem(fsi);
        items.add(fsiCard);		
    }

	private void cpuFingerprintBoostInit(List<RecyclerViewItem> items) {
        CardView fpbCard = new CardView(getActivity());
        fpbCard.setTitle(getString(R.string.fingerprint_boost));

        DescriptionView fpbDesc = new DescriptionView();
        fpbDesc.setSummary(getString(R.string.fingerprint_boost_summary));
        fpbCard.addItem(fpbDesc);

        SwitchView fp = new SwitchView();
        fp.setSummaryOn(getString(R.string.enabled));
        fp.setSummaryOff(getString(R.string.disabled));
        fp.setChecked(Misc.isCpuFingerprintBoostEnabled());
        fp.addOnSwitchListener((switchView, isChecked)
                -> Misc.enableCpuFingerprintBoost(isChecked, getActivity()));

        fpbCard.addItem(fp);
        items.add(fpbCard);		
    }

    private void cfsSchedulerInit(List<RecyclerViewItem> items) {
        SelectView cfsScheduler = new SelectView();
        cfsScheduler.setTitle(getString(R.string.cfs_scheduler_policy));
        cfsScheduler.setSummary(getString(R.string.cfs_scheduler_policy_summary));
        cfsScheduler.setItems(Misc.getAvailableCFSSchedulers());
        cfsScheduler.setItem(Misc.getCurrentCFSScheduler());
        cfsScheduler.setOnItemSelected((selectView, position, item)
                -> Misc.setCFSScheduler(item, getActivity()));

        items.add(cfsScheduler);
    }

    private void cpuQuietInit(List<RecyclerViewItem> items) {
		
        List<RecyclerViewItem> views = new ArrayList<>();
        CardView cpuQuietCard = new CardView(getActivity());
        cpuQuietCard.setTitle(getString(R.string.cpu_quiet));

        if (Misc.hasCpuQuietEnable()) {
            SwitchView cpuQuietEnable = new SwitchView();
            cpuQuietEnable.setSummary(getString(R.string.cpu_quiet));
            cpuQuietEnable.setChecked(Misc.isCpuQuietEnabled());
            cpuQuietEnable.addOnSwitchListener((switchView, isChecked)
                    -> Misc.enableCpuQuiet(isChecked, getActivity()));

            views.add(cpuQuietEnable);
        }

        if (Misc.hasCpuQuietGovernors()) {
            SelectView cpuQuietGovernors = new SelectView();
            cpuQuietGovernors.setSummary(getString(R.string.cpu_quiet_governor));
            cpuQuietGovernors.setItems(Misc.getCpuQuietAvailableGovernors());
            cpuQuietGovernors.setItem(Misc.getCpuQuietCurGovernor());
            cpuQuietGovernors.setOnItemSelected((selectView, position, item)
                    -> Misc.setCpuQuietGovernor(item, getActivity()));

            views.add(cpuQuietGovernors);
        }

        if (views.size() > 0) {
            DescriptionView descriptionView = new DescriptionView();
            descriptionView.setSummary(getString(R.string.cpu_quiet_summary));
            cpuQuietCard.addItem(descriptionView);

            for (RecyclerViewItem item : views) {
                cpuQuietCard.addItem(item);
            }
            items.add(cpuQuietCard);
		}
    }

    private void cpuBoostInit(List<RecyclerViewItem> items) {
        CardView cpuBoostCard = new CardView(getActivity());
        cpuBoostCard.setTitle(getString(R.string.ib_enabled));
		
        if (mCPUBoost.hasEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.ib_enabled));
            enable.setSummaryOn(getString(R.string.cpu_boost_summary_on));
            enable.setSummaryOff(getString(R.string.cpu_boost_summary_off));
            enable.setChecked(mCPUBoost.isEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
                    -> mCPUBoost.enableCpuBoost(isChecked, getActivity()));

            cpuBoostCard.addItem(enable);
        }

        if (mCPUBoost.hasCpuBoostExynosInputMs()) {
            GenericSelectView2 ms = new GenericSelectView2();
            ms.setTitle(getString(R.string.ib_duration_ms));
            ms.setValue(mCPUBoost.getCpuBootExynosInputMs() + " ms");
            ms.setValueRaw(ms.getValue().replace(" ms", ""));
            ms.setInputType(InputType.TYPE_CLASS_NUMBER);
            ms.setOnGenericValueListener((genericSelectView, value) -> {
                mCPUBoost.setCpuBoostExynosInputMs(Utils.strToInt(value), getActivity());
                genericSelectView.setValue(value + " ms");
            });

            cpuBoostCard.addItem(ms);
        }

        if (mCPUBoost.hasCpuBoostExynosInputFreq()) {
            List<String> freqs = mCPUBoost.getCpuBootExynosInputFreq();
            final String[] littleFreq = {freqs.get(0)};
            final String[] bigFreq = {freqs.get(1)};

            GenericSelectView2 little = new GenericSelectView2();
            little.setTitle(getString(R.string.ib_freq_little));
            little.setValue(littleFreq[0] + " Hz");
            little.setValueRaw(little.getValue().replace(" Hz", ""));
            little.setInputType(InputType.TYPE_CLASS_NUMBER);
            little.setOnGenericValueListener((genericSelectView, value) -> {
                mCPUBoost.setCpuBoostExynosInputFreq(value, bigFreq[0], getActivity());
                genericSelectView.setValue(value + " Hz");
                littleFreq[0] = value;
            });

            cpuBoostCard.addItem(little);

            GenericSelectView2 big = new GenericSelectView2();
            big.setTitle(getString(R.string.ib_freq_big));
            big.setValue(bigFreq[0] + " Hz");
            big.setValueRaw(big.getValue().replace(" Hz", ""));
            big.setInputType(InputType.TYPE_CLASS_NUMBER);
            big.setOnGenericValueListener((genericSelectView, value) -> {
                mCPUBoost.setCpuBoostExynosInputFreq(littleFreq[0], value, getActivity());
                genericSelectView.setValue(value + " Hz");
                bigFreq[0] = value;
            });

            cpuBoostCard.addItem(big);
        }

        if (mCPUBoost.hasCpuBoostDebugMask()) {
            SwitchView debugMask = new SwitchView();
            debugMask.setTitle(getString(R.string.debug_mask));
            debugMask.setSummary(getString(R.string.debug_mask_summary));
            debugMask.setChecked(mCPUBoost.isCpuBoostDebugMaskEnabled());
            debugMask.addOnSwitchListener((switchView, isChecked)
                    -> mCPUBoost.enableCpuBoostDebugMask(isChecked, getActivity()));

            cpuBoostCard.addItem(debugMask);
        }

        if (mCPUBoost.hasCpuBoostMs()) {
            SeekBarView ms = new SeekBarView();
            ms.setTitle(getString(R.string.interval));
            ms.setSummary(getString(R.string.interval_summary));
            ms.setUnit(getString(R.string.ms));
            ms.setMax(5000);
            ms.setOffset(10);
            ms.setProgress(mCPUBoost.getCpuBootMs() / 10);
            ms.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCPUBoost.setCpuBoostMs(position * 10, getActivity());
                }
            });

            cpuBoostCard.addItem(ms);
        }

        if (mCPUBoost.hasCpuBoostSyncThreshold() && mCPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(mCPUFreq.getAdjustedFreq(getActivity()));

            SelectView syncThreshold = new SelectView();
            syncThreshold.setTitle(getString(R.string.sync_threshold));
            syncThreshold.setSummary(getString(R.string.sync_threshold_summary));
            syncThreshold.setItems(list);
            syncThreshold.setItem(mCPUBoost.getCpuBootSyncThreshold());
            syncThreshold.setOnItemSelected((selectView, position, item)
                    -> mCPUBoost.setCpuBoostSyncThreshold(position == 0 ?
                            0 : mCPUFreq.getFreqs().get(position - 1),
                    getActivity()));

            cpuBoostCard.addItem(syncThreshold);
        }

        if (mCPUBoost.hasCpuBoostInputMs()) {
            SeekBarView inputMs = new SeekBarView();
            inputMs.setTitle(getString(R.string.input_interval));
            inputMs.setSummary(getString(R.string.input_interval_summary));
            inputMs.setUnit(getString(R.string.ms));
            inputMs.setMax(5000);
            inputMs.setOffset(10);
            inputMs.setProgress(mCPUBoost.getCpuBootInputMs() / 10);
            inputMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCPUBoost.setCpuBoostInputMs(position * 10, getActivity());
                }
            });

            cpuBoostCard.addItem(inputMs);
        }

        if (mCPUBoost.hasCpuBoostInputFreq()) {
            List<Integer> freqs = mCPUBoost.getCpuBootInputFreq();
            for (int i = 0; i < freqs.size(); i++) {
                List<String> list = new ArrayList<>();
                list.add(getString(R.string.disabled));
                list.addAll(mCPUFreq.getAdjustedFreq(i, getActivity()));

                SelectView inputCard = new SelectView();
                if (freqs.size() > 1) {
                    inputCard.setTitle(getString(R.string.input_boost_freq_core, i + 1));
                } else {
                    inputCard.setTitle(getString(R.string.input_boost_freq));
                }
                inputCard.setSummary(getString(R.string.input_boost_freq_summary));
                inputCard.setItems(list);
                inputCard.setItem(freqs.get(i));

                final int core = i;
                inputCard.setOnItemSelected((selectView, position, item)
                        -> mCPUBoost.setCpuBoostInputFreq(position == 0 ?
                                0 : mCPUFreq.getFreqs(core).get(position - 1),
                        core, getActivity()));

                cpuBoostCard.addItem(inputCard);
            }
        }

        if (mCPUBoost.hasCpuBoostWakeup()) {
            SwitchView wakeup = new SwitchView();
            wakeup.setTitle(getString(R.string.wakeup_boost));
            wakeup.setSummary(getString(R.string.wakeup_boost_summary));
            wakeup.setChecked(mCPUBoost.isCpuBoostWakeupEnabled());
            wakeup.addOnSwitchListener((switchView, isChecked)
                    -> mCPUBoost.enableCpuBoostWakeup(isChecked, getActivity()));

            cpuBoostCard.addItem(wakeup);
        }

        if (mCPUBoost.hasCpuBoostHotplug()) {
            SwitchView hotplug = new SwitchView();
            hotplug.setTitle(getString(R.string.hotplug_boost));
            hotplug.setSummary(getString(R.string.hotplug_boost_summary));
            hotplug.setChecked(mCPUBoost.isCpuBoostHotplugEnabled());
            hotplug.addOnSwitchListener((switchView, isChecked)
                    -> mCPUBoost.enableCpuBoostHotplug(isChecked, getActivity()));

            cpuBoostCard.addItem(hotplug);
        }

        if (cpuBoostCard.size() > 0) {
            items.add(cpuBoostCard);
        }
    }

    private void cpuBoostCpuInit(List<RecyclerViewItem> items) {
        CardView cpuInputBoostCard = new CardView(getActivity());
        cpuInputBoostCard.setTitle(getString(R.string.iib_enabled));

        if (mCPUBoost.hasCpuBoostInput()) {
            SwitchView enableib = new SwitchView();
            enableib.setTitle(getString(R.string.ib_enabled));
            enableib.setSummaryOn(getString(R.string.cpu_boost_summary_on));
            enableib.setSummaryOff(getString(R.string.cpu_boost_summary_off));
            enableib.setChecked(mCPUBoost.isEnabled());
            enableib.addOnSwitchListener((switchView, isChecked)
                    -> mCPUBoost.enableCpuBoostInput(isChecked, getActivity()));

            cpuInputBoostCard.addItem(enableib);
        }

        if (mCPUBoost.hasCpuBoostDurationMs()) {
            GenericSelectView2 dms = new GenericSelectView2();
            dms.setTitle(getString(R.string.iib_duration_ms));
            dms.setValue(mCPUBoost.getCpuBoostDurationMs() + " ms");
            dms.setValueRaw(dms.getValue().replace(" ms", ""));
            dms.setInputType(InputType.TYPE_CLASS_NUMBER);
            dms.setOnGenericValueListener(new GenericSelectView2.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView2 genericSelectView, String value) {
                    mCPUBoost.setCpuBoostDurationMs(Utils.strToInt(value), getActivity());
                    genericSelectView.setValue(value + " ms");
                }
            });

            cpuInputBoostCard.addItem(dms);
        }

		if (mCPUBoost.hasCpuBoostFreqHp() && mCPUFreq.getFreqs() != null) {
            SelectView bighp = new SelectView();
            bighp.setTitle("Boost Freq BIG Cluster");
            // bighp.setSummary(getString(R.string.iib_freq_hp));
            bighp.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            bighp.setItem((mCPUBoost.getCpuBoostFreqHp() / 1000)
                    + getString(R.string.mhz));
            bighp.setOnItemSelected((selectView, position, item)
                    -> mCPUBoost.setCpuBoostFreqHp(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            cpuInputBoostCard.addItem(bighp);
        }

		if (mCPUBoost.hasCpuBoostFreqLp() && mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()) != null) {
            SelectView littlelp = new SelectView();
            littlelp.setTitle("Boost Freq LITTLE Cluster");
            // littlelp.setSummary(getString(R.string.iib_freq_lp));
            littlelp.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(),getActivity()));
            littlelp.setItem((mCPUBoost.getCpuBoostFreqLp() / 1000)
                    + getString(R.string.mhz));
            littlelp.setOnItemSelected((selectView, position, item)
                    -> mCPUBoost.setCpuBoostFreqLp(
                    mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position), getActivity()));

            cpuInputBoostCard.addItem(littlelp);
        }

		if (mCPUBoost.hasCpuBoostMaxLp() && mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()) != null) {
            SelectView littleml = new SelectView();
            // littleml.setTitle("Boost Max Freq LITTLE Cluster");
            littleml.setSummary(getString(R.string.iib_freq_ml));
            littleml.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(),getActivity()));
            littleml.setItem((mCPUBoost.getCpuBoostMaxLp() / 1000)
                    + getString(R.string.mhz));
            littleml.setOnItemSelected((selectView, position, item)
                    -> mCPUBoost.setCpuBoostMaxLp(
                    mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position), getActivity()));
					
            cpuInputBoostCard.addItem(littleml);
        }

 		if (mCPUBoost.hasCpuBoostMaxPerf() && mCPUFreq.getFreqs() != null) {
            SelectView bigmp = new SelectView();
            // bigmp.setTitle("Boost Max Freq BIG Cluster");
            bigmp.setSummary(getString(R.string.iib_freq_mp));
            bigmp.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            bigmp.setItem((mCPUBoost.getCpuBoostMaxPerf() / 1000)
                    + getString(R.string.mhz));
            bigmp.setOnItemSelected((selectView, position, item)
                    -> mCPUBoost.setCpuBoostMaxPerf(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            cpuInputBoostCard.addItem(bigmp);
        }

        if (cpuInputBoostCard.size() > 0) {
            items.add(cpuInputBoostCard);
        }
    }

    private void cpuDevFreqBoostInit(List<RecyclerViewItem> items) {
        CardView cpuDevFreqBoostCard = new CardView(getActivity());
        cpuDevFreqBoostCard.setTitle(getString(R.string.iid_enabled));
  
        if (Misc.hasDevFreqBoostDur()) {
            SeekBarView dfms = new SeekBarView();
            dfms.setTitle(getString(R.string.iid_duration_ms));
            dfms.setSummary(getString(R.string.iid_duration_ms_summary));
            dfms.setUnit(getString(R.string.ms));
            dfms.setMax(1000);
            dfms.setOffset(10);
            dfms.setProgress(Misc.getDevFreqBoostDur() / 10);
            dfms.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Misc.setDevFreqBoostDur(position * 10, getActivity());
                }
            });

            cpuDevFreqBoostCard.addItem(dfms);
        }
		
        if (Misc.hasDevFreqBoostFreq()) {
            GenericSelectView2 dffq = new GenericSelectView2();
			dffq.setTitle(getString(R.string.iid_duration_freq));
			// dffq.setTitle(getString(R.string.iid_duration_freq_summary));
            dffq.setValue(Misc.getDevFreqBoostFreq() + " Hz");
            dffq.setValueRaw(dffq.getValue().replace(" Hz", ""));
            dffq.setInputType(InputType.TYPE_CLASS_NUMBER);
            dffq.setOnGenericValueListener(new GenericSelectView2.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView2 genericSelectView, String value) {
                    Misc.setDevFreqBoostFreq(Utils.strToInt(value), getActivity());
                    genericSelectView.setValue(value + " Hz");
                }
            });

            cpuDevFreqBoostCard.addItem(dffq);
        }
		
        if (cpuDevFreqBoostCard.size() > 0) {
            items.add(cpuDevFreqBoostCard);
        }
		
	}

    private void cpuTouchBoostInit(List<RecyclerViewItem> items) {
        CardView tbiCard = new CardView(getActivity());
        tbiCard.setTitle(getString(R.string.touch_boost));

        DescriptionView tbiDesc = new DescriptionView();
        tbiDesc.setSummary(getString(R.string.touch_boost_summary));
        tbiCard.addItem(tbiDesc);
	
        SwitchView touchBoost = new SwitchView();
		touchBoost.setSummaryOn(getString(R.string.enabled));
        touchBoost.setSummaryOff(getString(R.string.disabled));
        touchBoost.setChecked(Misc.isCpuTouchBoostEnabled());
        touchBoost.addOnSwitchListener((switchView, isChecked)
                -> Misc.enableCpuTouchBoost(isChecked, getActivity()));

		tbiCard.addItem(touchBoost);
		items.add(tbiCard);
    }

    private void mcPowerSavingInit(List<RecyclerViewItem> items) {
        SelectView mcPowerSaving = new SelectView();
        mcPowerSaving.setTitle(getString(R.string.mc_power_saving));
        mcPowerSaving.setSummary(getString(R.string.mc_power_saving_summary));
        mcPowerSaving.setItems(Arrays.asList(getResources().getStringArray(R.array.mc_power_saving_items)));
        mcPowerSaving.setItem(Misc.getCurMcPowerSaving());
        mcPowerSaving.setOnItemSelected((selectView, position, item)
                -> Misc.setMcPowerSaving(position, getActivity()));

        items.add(mcPowerSaving);
    }

    private void powerSavingWqInit(List<RecyclerViewItem> items) {
        CardView pwqCard = new CardView(getActivity());
        pwqCard.setTitle(getString(R.string.power_saving_wq));

        SwitchView powerSavingWq = new SwitchView();
		powerSavingWq.setSummaryOn(getString(R.string.enabled));
        powerSavingWq.setSummaryOff(getString(R.string.disabled));
        powerSavingWq.setChecked(Misc.isPowerSavingWqEnabled());
        powerSavingWq.addOnSwitchListener((switchView, isChecked)
                -> Misc.enablePowerSavingWq(isChecked, getActivity()));

		pwqCard.addItem(powerSavingWq);
        items.add(pwqCard);
    }

    private void refreshKERNEL() {
        getHandler().postDelayed(() -> {
            for (int i = 0; i < mKERNEL.size(); i++) {
                mKERNEL.get(i).setValue(Misc.getValue(i, mCompleteList));
                mKERNEL.get(i).setValueRaw(mKERNEL.get(i).getValue());
            }
        }, 250);
    }

    private void kernelTunablesInit (List<RecyclerViewItem> items){
        final CardView Cardkernel = new CardView(getActivity());
        Cardkernel.setTitle(getString(R.string.kernel_tunables));

        CardKernelTunablesInit(Cardkernel);

        if (Cardkernel.size() > 0) {
            items.add(Cardkernel);
        }
    }

    private void CardKernelTunablesInit(final CardView card) {
        card.clearItems();
        mKERNEL.clear();

        mCompleteList = AppSettings.getBoolean("kernel_show_complete_list", false, getActivity());

        SwitchView kv = new SwitchView();
        kv.setTitle(getString(R.string.kernel_tun_switch_title));
        kv.setSummary(getString(R.string.kernel_tun_switch_summary));
        kv.setChecked(mCompleteList);
        kv.addOnSwitchListener((switchView, isChecked) -> {
            mCompleteList = isChecked;
            AppSettings.saveBoolean("kernel_show_complete_list", mCompleteList, getActivity());
            getHandler().postDelayed(() -> CardKernelTunablesInit(card), 250);
        });

        card.addItem(kv);


        TitleView titk = new TitleView();
        if (mCompleteList) {
            titk.setText(getString(R.string.kernel_tun_tit_all));
        }
        else {
            titk.setText(getString(R.string.kernel_tun_tit_common));
        }

        card.addItem(titk);

        for (int i = 0; i < Misc.size(mCompleteList); i++) {
            GenericSelectView2 kernel = new GenericSelectView2();
            kernel.setTitle(Misc.getName(i, mCompleteList));
            kernel.setValue(Misc.getValue(i, mCompleteList));
            kernel.setValueRaw(kernel.getValue());
            kernel.setInputType(InputType.TYPE_CLASS_NUMBER);

            final int position = i;
            kernel.setOnGenericValueListener((genericSelectView, value) -> {
                Misc.setValue(value, position, getActivity(), mCompleteList);
                genericSelectView.setValue(value);
                refreshKERNEL();
            });

            card.addItem(kernel);
            mKERNEL.add(kernel);
        }
    }

    private float[] mCPUUsages;
    private boolean[] mCPUStates;
    private int[] mCPUFreqs;
    private int mCPUMaxFreqBig;
    private int mCPUMinFreqBig;
    private int mCPUMaxScreenOffFreqBig;
    private int mCPUMaxSuspFreqBig;
    private int mCPUMinSuspFreqBig;
    private String mCPUGovernorStrBig;
    private int mCPUMaxFreqMid;
    private int mCPUMinFreqMid;
    private int mCPUMaxScreenOffFreqMid;
    private int mCPUMaxSuspFreqMid;
    private int mCPUMinSuspFreqMid;
    private String mCPUGovernorStrMid;
    private int mCPUMaxFreqLITTLE;
    private int mCPUMinFreqLITTLE;
    private int mCPUMaxScreenOffFreqLITTLE;
    private int mCPUMaxSuspFreqLITTLE;
    private int mCPUMinSuspFreqLITTLE;
    private String mCPUGovernorStrLITTLE;

    @Override
    protected void refreshThread() {
        super.refreshThread();
        if (mCoresBig.size() == 0) return;

        try {
            mCPUUsages = mCPUFreq.getCpuUsage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mCPUStates = new boolean[mCPUFreq.getCpuCount()];
        for (int i = 0; i < mCPUStates.length; i++) {
            mCPUStates[i] = !mCPUFreq.isOffline(i);
        }

        mCPUFreqs = new int[mCPUFreq.getCpuCount()];
        for (int i = 0; i < mCPUFreqs.length; i++) {
            mCPUFreqs[i] = mCPUFreq.getCurFreq(i);
        }

        if (mCPUMaxBig != null) {
            mCPUMaxFreqBig = mCPUFreq.getMaxFreq(mCPUMaxFreqBig == 0);
        }
        if (mCPUMinBig != null) {
            mCPUMinFreqBig = mCPUFreq.getMinFreq(mCPUMinFreqBig == 0);
        }
        if (mCPUMaxScreenOffBig != null) {
            mCPUMaxScreenOffFreqBig = mCPUFreq.getMaxScreenOffFreq(mCPUMaxScreenOffFreqBig == 0);
        }
        if (mCPUMaxSuspBig != null) {
            mCPUMaxSuspFreqBig = mCPUFreq.getMaxSuspFreq(mCPUFreq.getBigCpu(),
                    mCPUMaxSuspFreqBig == 0);
        }
        if (mCPUMinSuspBig != null) {
            mCPUMinSuspFreqBig = mCPUFreq.getMinSuspFreq(mCPUFreq.getBigCpu(),
                    mCPUMinSuspFreqBig == 0);
        }		
        if (mCPUGovernorBig != null) {
            mCPUGovernorStrBig = mCPUFreq.getGovernor(mCPUGovernorStrBig == null);
        }
        if (mCPUMaxMid != null) {
            mCPUMaxFreqMid = mCPUFreq.getMaxFreq(mCPUFreq.getMidCpu(), mCPUMaxFreqMid == 0);
        }
        if (mCPUMinMid != null) {
            mCPUMinFreqMid = mCPUFreq.getMinFreq(mCPUFreq.getMidCpu(), mCPUMinFreqMid == 0);
        }
        if (mCPUMaxScreenOffMid != null) {
            mCPUMaxScreenOffFreqMid = mCPUFreq.getMaxScreenOffFreq(mCPUFreq.getMidCpu(),
                    mCPUMaxScreenOffFreqMid == 0);
        }
        if (mCPUMaxSuspMid != null) {
            mCPUMaxSuspFreqMid = mCPUFreq.getMaxSuspFreq(mCPUFreq.getMidCpu(),
                    mCPUMaxSuspFreqMid == 0);
        }
        if (mCPUMinSuspMid != null) {
            mCPUMinSuspFreqMid = mCPUFreq.getMinSuspFreq(mCPUFreq.getMidCpu(),
                    mCPUMinSuspFreqMid == 0);
        }
        if (mCPUGovernorMid != null) {
            mCPUGovernorStrMid = mCPUFreq.getGovernor(mCPUFreq.getMidCpu(),
                    mCPUGovernorStrMid == null);
        }
        if (mCPUMaxLITTLE != null) {
            mCPUMaxFreqLITTLE = mCPUFreq.getMaxFreq(mCPUFreq.getLITTLECpu(), mCPUMaxFreqLITTLE == 0);
        }
        if (mCPUMinLITTLE != null) {
            mCPUMinFreqLITTLE = mCPUFreq.getMinFreq(mCPUFreq.getLITTLECpu(), mCPUMinFreqLITTLE == 0);
        }
        if (mCPUMaxScreenOffLITTLE != null) {
            mCPUMaxScreenOffFreqLITTLE = mCPUFreq.getMaxScreenOffFreq(mCPUFreq.getLITTLECpu(),
                    mCPUMaxScreenOffFreqLITTLE == 0);
        }
        if (mCPUMaxSuspLITTLE != null) {
            mCPUMaxSuspFreqLITTLE = mCPUFreq.getMaxSuspFreq(mCPUFreq.getLITTLECpu(),
                    mCPUMaxSuspFreqLITTLE == 0);
        }
        if (mCPUMinSuspLITTLE != null) {
            mCPUMinSuspFreqLITTLE = mCPUFreq.getMinSuspFreq(mCPUFreq.getLITTLECpu(),
                    mCPUMinSuspFreqLITTLE == 0);
        }
        if (mCPUGovernorLITTLE != null) {
            mCPUGovernorStrLITTLE = mCPUFreq.getGovernor(mCPUFreq.getLITTLECpu(),
                    mCPUGovernorStrLITTLE == null);
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mCPUUsages != null && mCPUStates != null) {
            refreshUsages(mCPUUsages, mCPUUsageBig, mCPUFreq.getBigCpuRange(), mCPUStates);
            if (mCPUFreq.isBigLITTLE()) {
                refreshUsages(mCPUUsages, mCPUUsageLITTLE, mCPUFreq.getLITTLECpuRange(), mCPUStates);
                if (mCPUFreq.hasMidCpu()) {
                    refreshUsages(mCPUUsages, mCPUUsageMid, mCPUFreq.getMidCpuRange(), mCPUStates);
                }
            }
        }

        if (mCPUMaxBig != null && mCPUMaxFreqBig != 0) {
            mCPUMaxBig.setItem((mCPUMaxFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinBig != null && mCPUMinFreqBig != 0) {
            mCPUMinBig.setItem((mCPUMinFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxScreenOffBig != null && mCPUMaxScreenOffFreqBig != 0) {
            mCPUMaxScreenOffBig.setItem((mCPUMaxScreenOffFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxSuspBig != null && mCPUMaxSuspFreqBig != 0) {
            mCPUMaxSuspBig.setItem((mCPUMaxSuspFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinSuspBig != null && mCPUMinSuspFreqBig != 0) {
            mCPUMinSuspBig.setItem((mCPUMinSuspFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUGovernorBig != null && mCPUGovernorStrBig != null && !mCPUGovernorStrBig.isEmpty()) {
            mCPUGovernorBig.setItem(mCPUGovernorStrBig);
        }
        if (mCPUMaxMid != null && mCPUMaxFreqMid != 0) {
            mCPUMaxMid.setItem((mCPUMaxFreqMid / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinMid != null && mCPUMinFreqMid != 0) {
            mCPUMinMid.setItem((mCPUMinFreqMid / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxScreenOffMid != null && mCPUMaxScreenOffFreqMid != 0) {
            mCPUMaxScreenOffMid.setItem((mCPUMaxScreenOffFreqMid / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxSuspMid != null && mCPUMaxSuspFreqMid != 0) {
            mCPUMaxSuspMid.setItem((mCPUMaxSuspFreqMid / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinSuspMid != null && mCPUMinSuspFreqMid != 0) {
            mCPUMinSuspMid.setItem((mCPUMinSuspFreqMid / 1000) + getString(R.string.mhz));
        }
        if (mCPUGovernorMid != null && mCPUGovernorStrMid != null && !mCPUGovernorStrMid.isEmpty()) {
            mCPUGovernorMid.setItem(mCPUGovernorStrMid);
        }
        if (mCPUMaxLITTLE != null && mCPUMaxFreqLITTLE != 0) {
            mCPUMaxLITTLE.setItem((mCPUMaxFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinLITTLE != null && mCPUMinFreqLITTLE != 0) {
            mCPUMinLITTLE.setItem((mCPUMinFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxScreenOffLITTLE != null && mCPUMaxScreenOffFreqLITTLE != 0) {
            mCPUMaxScreenOffLITTLE.setItem((mCPUMaxScreenOffFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxSuspLITTLE != null && mCPUMaxSuspFreqLITTLE != 0) {
            mCPUMaxSuspLITTLE.setItem((mCPUMaxSuspFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinSuspLITTLE != null && mCPUMinSuspFreqLITTLE != 0) {
            mCPUMinSuspLITTLE.setItem((mCPUMinSuspFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUGovernorLITTLE != null && mCPUGovernorStrLITTLE != null && !mCPUGovernorStrLITTLE.isEmpty()) {
            mCPUGovernorLITTLE.setItem(mCPUGovernorStrLITTLE);
        }

        if (mCPUFreqs != null) {
            refreshCores(mCoresBig, mCPUFreqs);
            if (mCPUFreq.isBigLITTLE()) {
                refreshCores(mCoresLITTLE, mCPUFreqs);
                if (mCPUFreq.hasMidCpu()) {
                    refreshCores(mCoresMid, mCPUFreqs);
                }
            }
        }
    }

    private void refreshUsages(float[] usages, XYGraphView graph, List<Integer> cores,
                               boolean[] coreStates) {
        if (graph != null) {
            float average = 0;
            int size = 0;
            for (int core : cores) {
                if (core + 1 < usages.length) {
                    if (coreStates[core]) {
                        average += usages[core + 1];
                    }
                    size++;
                }
            }
            average /= size;
            graph.setText(Math.round(average) + "%");
            graph.addPercentage(Math.round(average));
        }
    }

    private void refreshCores(SparseArray<SwitchView> array, int[] freqs) {
        for (int i = 0; i < array.size(); i++) {
            SwitchView switchView = array.valueAt(i);
            if (switchView != null) {
                final int core = array.keyAt(i);
                int freq = freqs[core];

                String freqText = freq == 0 ? getString(R.string.offline) : (freq / 1000)
                        + getString(R.string.mhz);
                switchView.clearOnSwitchListener();
                switchView.setChecked(freq != 0);
                switchView.setSummary(getString(R.string.core, core + 1) + " - " + freqText);
                switchView.addOnSwitchListener((switchView1, isChecked) -> {
                    if (core == 0) {
                        Utils.toast(R.string.no_offline_core, getActivity());
                    } else {
                        mCPUFreq.onlineCpu(core, isChecked, true, getActivity());
                    }
                });
            }
        }
    }
}
