/*
 * Copyright (C) 2019-2022 nalas <pn2604@gmail.com>
 *
 * This file is part of ThunderTweaks.
 *
 * ThunderTweaks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ThunderTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.thunder.thundertweaks.fragments.kernel;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.fragments.DescriptionFragment;
import com.thunder.thundertweaks.fragments.recyclerview.RecyclerViewFragment;
import com.thunder.thundertweaks.fragments.statistics.OverallFragment;
import com.thunder.thundertweaks.utils.Log;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.kernel.cpu.CPUFreq;
import com.thunder.thundertweaks.utils.kernel.game.GmcControl;
import com.thunder.thundertweaks.utils.kernel.gpu.GPUFreqExynos;
import com.thunder.thundertweaks.utils.kernel.bus.VoltageMif;
import com.thunder.thundertweaks.utils.kernel.dvfs.Dvfs;
import com.thunder.thundertweaks.views.recyclerview.CardView;
import com.thunder.thundertweaks.views.recyclerview.DescriptionView;
import com.thunder.thundertweaks.views.recyclerview.GenericSelectView;
import com.thunder.thundertweaks.views.recyclerview.RecyclerViewItem;
import com.thunder.thundertweaks.views.recyclerview.SeekBarView;
import com.thunder.thundertweaks.views.recyclerview.SelectView;
import com.thunder.thundertweaks.views.recyclerview.SwitchView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GmcFragment extends RecyclerViewFragment {
    private GmcControl mGmcControl;
    private GPUFreqExynos mGPUFreqExynos;
    private CPUFreq mCPUFreq;
    private Dvfs mDvfs;
    private SeekBarView mSeekbarProf = new SeekBarView();
	
    @Override
    protected void init() {
        super.init();
		
        mGmcControl = GmcControl.getInstance();
        mGPUFreqExynos = GPUFreqExynos.getInstance();
        mCPUFreq = CPUFreq.getInstance(getActivity());
        mDvfs = Dvfs.getInstance();
		
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        if (GmcControl.supported()) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.gmcControl),getString(R.string.gmcControl_Summary)));
        }
    }
	
    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (GmcControl.supported()) {
            gmcControlInit(items);
        }
    }

    private void gmcControlInit(List<RecyclerViewItem> items) {
        CardView gmcControlCard = new CardView(getActivity());
		List<String> list = new ArrayList<>();	
		
        if(mGmcControl.hasIsGame()){
            SwitchView is_game = new SwitchView();
            is_game.setTitle(getString(R.string.gmcControl_is_game));
            is_game.setSummary(getString(R.string.gmcControl_is_game_desc));
            is_game.setChecked(mGmcControl.isEnabledIsGame());
            is_game.addOnSwitchListener((switchView, isChecked) ->
                    mGmcControl.enableIsGame(isChecked, getActivity())
            );
            gmcControlCard.addItem(is_game);
        }

        if(mGmcControl.hasRun()){
            SwitchView run = new SwitchView();
            run.setTitle(getString(R.string.gmcControl_run));
            run.setSummary(getString(R.string.gmcControl_run_desc));
            run.setChecked(mGmcControl.isEnabledRun());
            run.addOnSwitchListener((switchView, isChecked) ->
                    mGmcControl.enableRun(isChecked, getActivity())
            );
            gmcControlCard.addItem(run);
        }

        if(mGmcControl.hasBindCPU()){
            SwitchView bindcpu = new SwitchView();
            bindcpu.setTitle(getString(R.string.gmcControl_bindcpu));
            bindcpu.setSummary(getString(R.string.gmcControl_bindcpu_desc));
            bindcpu.setChecked(mGmcControl.isEnabledBindCPU());
            bindcpu.addOnSwitchListener((switchView, isChecked) ->
                    mGmcControl.enableBindCPU(isChecked, getActivity())
            );
            gmcControlCard.addItem(bindcpu);
        }
		
        if(mCPUFreq.getFreqs() != null) {

            if (mGmcControl.hasBIGMax()) {
                SelectView BIGMax = new SelectView();
                BIGMax.setTitle(getString(R.string.gmcControl_BIG_max));
                BIGMax.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
                BIGMax.setItem((mGmcControl.getBIGMax() / 1000) + getString(R.string.mhz));
                BIGMax.setOnItemSelected((selectView, position, item)
                        -> mGmcControl.setBIGMax(mCPUFreq.getFreqs().get(position), getActivity()));

                gmcControlCard.addItem(BIGMax);
            }

            if (mGmcControl.hasBIGMin()) {
                SelectView BIGMin = new SelectView();
                BIGMin.setTitle(getString(R.string.gmcControl_BIG_min));
                BIGMin.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
                BIGMin.setItem((mGmcControl.getBIGMin() / 1000) + getString(R.string.mhz));
                BIGMin.setOnItemSelected((selectView, position, item)
                        -> mGmcControl.setBIGMin(mCPUFreq.getFreqs().get(position), getActivity()));

                gmcControlCard.addItem(BIGMin);
            }
	
            if (mCPUFreq.isBigLITTLE()) {
                if (mCPUFreq.hasMidCpu()) {

                    if (mGmcControl.hasMIDDLEMax()) {
                        SelectView MIDDLEMax = new SelectView();
                        MIDDLEMax.setTitle(getString(R.string.gmcControl_MIDDLE_max));
                        MIDDLEMax.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                        MIDDLEMax.setItem((mGmcControl.getMIDDLEMax() / 1000) + getString(R.string.mhz));
                        MIDDLEMax.setOnItemSelected((selectView, position, item)
                                -> mGmcControl.setMIDDLEMax(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position), getActivity()));

                        gmcControlCard.addItem(MIDDLEMax);
                    }

                    if (mGmcControl.hasMIDDLEMin()) {
                        SelectView MIDDLEMin = new SelectView();
                        MIDDLEMin.setTitle(getString(R.string.gmcControl_MIDDLE_min));
                        MIDDLEMin.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                        MIDDLEMin.setItem((mGmcControl.getMIDDLEMin() / 1000) + getString(R.string.mhz));
                        MIDDLEMin.setOnItemSelected((selectView, position, item)
                                -> mGmcControl.setMIDDLEMin(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position), getActivity()));

                        gmcControlCard.addItem(MIDDLEMin);
                    }

                    if (mGmcControl.hasMIDDLESse()) {
                        SelectView MIDDLESse = new SelectView();
                        MIDDLESse.setTitle(getString(R.string.gmcControl_MIDDLE_sse));
                        MIDDLESse.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                        MIDDLESse.setItem((mGmcControl.getMIDDLESse() / 1000) + getString(R.string.mhz));
                        MIDDLESse.setOnItemSelected((selectView, position, item)
                                -> mGmcControl.setMIDDLESse(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position), getActivity()));

                        gmcControlCard.addItem(MIDDLESse);
                    }
                }

                if (mGmcControl.hasLITTLEMax()) {
                    SelectView LITTLEMax = new SelectView();
                    LITTLEMax.setTitle(getString(R.string.gmcControl_LITTLE_max));
                    LITTLEMax.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
                    LITTLEMax.setItem((mGmcControl.getLITTLEMax() / 1000) + getString(R.string.mhz));
                    LITTLEMax.setOnItemSelected((selectView, position, item)
                            -> mGmcControl.setLITTLEMax(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position), getActivity()));

                    gmcControlCard.addItem(LITTLEMax);
                }
				
                if (mGmcControl.hasLITTLEMin()) {
                    SelectView LITTLEMin = new SelectView();
                    LITTLEMin.setTitle(getString(R.string.gmcControl_LITTLE_min));
                    LITTLEMin.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
                    LITTLEMin.setItem((mGmcControl.getLITTLEMin() / 1000) + getString(R.string.mhz));
                    LITTLEMin.setOnItemSelected((selectView, position, item)
                            -> mGmcControl.setLITTLEMin(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position), getActivity()));

                    gmcControlCard.addItem(LITTLEMin);
                }
            }

        if(mGPUFreqExynos.getAvailableFreqs() != null) {
			
            if(mGmcControl.hasGPUMax()) {
                SelectView GPUMax = new SelectView();
                GPUMax.setTitle(getString(R.string.gmcControl_GPU_max));
                GPUMax.setItems(mGPUFreqExynos.getAdjustedFreqs(getActivity()));
                GPUMax.setItem((mGmcControl.getGPUMax() / 1000) + getString(R.string.mhz));
                GPUMax.setOnItemSelected((selectView, position, item)
                        -> mGmcControl.setGPUMax(mGPUFreqExynos.getAvailableFreqs().get(position), getActivity()));

                gmcControlCard.addItem(GPUMax);
            }

            if(mGmcControl.hasGPULite()) {
                SelectView GPULite = new SelectView();
                GPULite.setTitle(getString(R.string.gmcControl_GPU_lite));
                GPULite.setItems(mGPUFreqExynos.getAdjustedFreqs(getActivity()));
                GPULite.setItem((mGmcControl.getGPULite() / 1000) + getString(R.string.mhz));
                GPULite.setOnItemSelected((selectView, position, item)
                        -> mGmcControl.setGPULite(mGPUFreqExynos.getAvailableFreqs().get(position), getActivity()));

                gmcControlCard.addItem(GPULite);
            }
			
            if(mGmcControl.hasGPUMin()) {
                SelectView GPUMin = new SelectView();
                GPUMin.setTitle(getString(R.string.gmcControl_GPU_min));
                GPUMin.setItems(mGPUFreqExynos.getAdjustedFreqs(getActivity()));
                GPUMin.setItem((mGmcControl.getGPUMin() / 1000) + getString(R.string.mhz));
                GPUMin.setOnItemSelected((selectView, position, item)
                        -> mGmcControl.setGPUMin(mGPUFreqExynos.getAvailableFreqs().get(position), getActivity()));

                gmcControlCard.addItem(GPUMin);
            }
        }		

		if(mDvfs.getAvailableFreq() != null) {
			
			if(Dvfs.hasAvailableFreq()) {
				list.addAll(Dvfs.getAvailableFreq());
			} else { 
				list.addAll(Arrays.asList("not supported"));
			}
			
			if(mGmcControl.hasMIFMin()) {
				SelectView minMIFfreq = new SelectView();
				minMIFfreq.setTitle(getString(R.string.mif_min_freq));
				minMIFfreq.setSummary(getString(R.string.mif_min_freq_summary));
				minMIFfreq.setItems(list);
				minMIFfreq.setItem((mGmcControl.getMIFMin() / 1000) + getString(R.string.mhz));
				minMIFfreq.setOnItemSelected(new SelectView.OnItemSelected() {
					@Override
					public void onItemSelected(SelectView minMIFfreq, int position, String item) {
						mGmcControl.setMIFMin(item, getActivity());
					}
				});

				gmcControlCard.addItem(minMIFfreq);
			}

			if(mGmcControl.hasMIFMax()) {
				SelectView maxMIFfreq = new SelectView();
				maxMIFfreq.setTitle(getString(R.string.mif_max_freq));
				maxMIFfreq.setSummary(getString(R.string.mif_max_freq_summary));
				maxMIFfreq.setItems(list);
				maxMIFfreq.setItem((mGmcControl.getMIFMax() / 1000) + getString(R.string.mhz));
				maxMIFfreq.setOnItemSelected(new SelectView.OnItemSelected() {
					@Override
					public void onItemSelected(SelectView maxMIFfreq, int position, String item) {
						mGmcControl.setMIFMax(item, getActivity());
					}
				});

				gmcControlCard.addItem(maxMIFfreq);
			}
		}
		
        if (mGmcControl.hasMaxLockDelay()) {
            SeekBarView maxlock_d = new SeekBarView();
            maxlock_d.setTitle(getString(R.string.gmcControl_maxlock_delay));
            maxlock_d.setSummary(getString(R.string.gmcControl_maxlock_delay_summary));
            maxlock_d.setUnit(getString(R.string.second));
            maxlock_d.setMax(20);
            maxlock_d.setMin(1);
            maxlock_d.setProgress(mGmcControl.getMaxLockDelay() - 1);
            maxlock_d.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mGmcControl.setMaxLockDelay((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            gmcControlCard.addItem(maxlock_d);
        }

	items.add(gmcControlCard);
	}
	}
}