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

import androidx.appcompat.app.AlertDialog;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;

import com.thunder.thundertweaks.fragments.recyclerview.RecyclerViewFragment;
import com.thunder.thundertweaks.utils.AppSettings;
import com.thunder.thundertweaks.utils.Device;
import com.thunder.thundertweaks.utils.kernel.vm.VM;
import com.thunder.thundertweaks.utils.kernel.vm.ZRAM;
import com.thunder.thundertweaks.utils.kernel.vm.ZSwap;
// import com.thunder.thundertweaks.utils.kernel.vm.VNSwap;
import com.thunder.thundertweaks.views.recyclerview.CardView;
import com.thunder.thundertweaks.views.recyclerview.DescriptionView;
import com.thunder.thundertweaks.views.recyclerview.GenericSelectView2;
import com.thunder.thundertweaks.views.recyclerview.ProgressBarView;
import com.thunder.thundertweaks.views.recyclerview.RecyclerViewItem;
import com.thunder.thundertweaks.views.recyclerview.SeekBarView;
import com.thunder.thundertweaks.views.recyclerview.SelectView;
import com.thunder.thundertweaks.views.recyclerview.SwitchView;
import com.thunder.thundertweaks.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 29.06.16.
 */
public class VMFragment extends RecyclerViewFragment {

    private List<GenericSelectView2> mVMs = new ArrayList<>();
    private boolean mCompleteList;

    private Device.MemInfo mMemInfo;
    private ProgressBarView swap;
    private ProgressBarView mem;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        mMemInfo = Device.MemInfo.getInstance();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        memBarsInit(items);
        if (ZRAM.supported()) {
            zramInit(items);
        }
        // vnswapInit(items);
        zswapInit(items);
        vmTunablesInit(items);
    }

    private void memBarsInit (List<RecyclerViewItem> items){
        CardView card = new CardView(getActivity());
        card.setTitle(getString(R.string.memory));

        long swap_total = mMemInfo.getItemMb("SwapTotal");
        long swap_progress = swap_total - mMemInfo.getItemMb("SwapFree");

        swap = new ProgressBarView();
        swap.setTitle("SWAP");
        swap.setItems(swap_total, swap_progress);
        swap.setUnit(getResources().getString(R.string.mb));
        swap.setProgressColor(getResources().getColor(R.color.blueAccent));
        card.addItem(swap);

        long mem_total = mMemInfo.getItemMb("MemTotal");
        long mem_progress = mem_total - (mMemInfo.getItemMb("Cached") + mMemInfo.getItemMb("MemFree"));

        mem = new ProgressBarView();
        mem.setTitle("RAM");
        mem.setItems(mem_total, mem_progress);
        mem.setUnit(getResources().getString(R.string.mb));
        mem.setProgressColor(getResources().getColor(R.color.orangeAccent));
        card.addItem(mem);

        items.add(card);
    }

    private void vmTunablesInit (List<RecyclerViewItem> items){
        final CardView CardVm = new CardView(getActivity());
        CardVm.setTitle(getString(R.string.vm_tunables));

        CardVmTunablesInit(CardVm);

        if (CardVm.size() > 0) {
            items.add(CardVm);
        }
    }

    private void CardVmTunablesInit(final CardView card) {
        card.clearItems();
        mVMs.clear();

        mCompleteList = AppSettings.getBoolean("vm_show_complete_list", false, getActivity());

        SwitchView sv = new SwitchView();
        sv.setTitle(getString(R.string.vm_tun_switch_title));
        sv.setSummary(getString(R.string.vm_tun_switch_summary));
        sv.setChecked(mCompleteList);
        sv.addOnSwitchListener((switchView, isChecked) -> {
            mCompleteList = isChecked;
            AppSettings.saveBoolean("vm_show_complete_list", mCompleteList, getActivity());
            getHandler().postDelayed(() -> CardVmTunablesInit(card), 250);
        });

        card.addItem(sv);


        TitleView tit = new TitleView();
        if (mCompleteList) {
            tit.setText(getString(R.string.vm_tun_tit_all));
        }
        else {
            tit.setText(getString(R.string.vm_tun_tit_common));
        }

        card.addItem(tit);

        for (int i = 0; i < VM.size(mCompleteList); i++) {
            GenericSelectView2 vm = new GenericSelectView2();
            vm.setTitle(VM.getName(i, mCompleteList));
            vm.setValue(VM.getValue(i, mCompleteList));
            vm.setValueRaw(vm.getValue());
            vm.setInputType(InputType.TYPE_CLASS_NUMBER);

            final int position = i;
            vm.setOnGenericValueListener((genericSelectView, value) -> {
                VM.setValue(value, position, getActivity(), mCompleteList);
                genericSelectView.setValue(value);
                refreshVMs();
            });

            card.addItem(vm);
            mVMs.add(vm);
        }
    }

    private SwitchView zramSw;
    private SelectView zramComp;
    private SeekBarView zram;

    private void zramInit(List<RecyclerViewItem> items) {
        boolean isZramEnabled = ZRAM.isEnabled();

        zramSw = new SwitchView();
        zramComp = new SelectView();
        zram = new SeekBarView();

        CardView zramCard = new CardView(getActivity());
        zramCard.setTitle(getString(R.string.zram));

        DescriptionView zramDesc = new DescriptionView();
        zramDesc.setTitle(getString(R.string.disksize_summary));
        zramCard.addItem(zramDesc);

        zramSw.setTitle(getString(R.string.zram));
        zramSw.setSummary(getString(R.string.zramsw_summary));
        zramSw.setChecked(isZramEnabled);
        zramSw.addOnSwitchListener((switchView, isChecked) -> {
            if (isChecked){
                ZRAM.enable(true, getActivity());
                zramComp.setEnabled(false);
                zram.setEnabled(false);
            } else {
                if(ZSwap.hasEnable() && !ZSwap.isEnabled()) {
                    dialogZram();
                } else {
                    ZRAM.enable(false, getActivity());
                    zramComp.setEnabled(true);
                    zram.setEnabled(true);
                    zram.setProgress(0);
                    ZRAM.setDisksize(0, getActivity());
                }
            }
        });

        zramCard.addItem(zramSw);


        zramComp.setEnabled(!isZramEnabled);
        zramComp.setTitle(getString(R.string.zram_comp_algorithm));
        zramComp.setSummary(getString(R.string.zram_comp_algorithm_summary));
        zramComp.setItems(ZRAM.getCompAlgorithms());
        zramComp.setItem(ZRAM.getCompAlgorithm());
        zramComp.setOnItemSelected((selectView, position, item)
                -> ZRAM.setCompAlgorithm(item, getActivity()));

        zramCard.addItem(zramComp);
        int maxZramAllowed = (((int) Device.MemInfo.getInstance().getTotalMem() > 8192) ? 8192 : (int) Device.MemInfo.getInstance().getTotalMem());
        zram.setEnabled(!isZramEnabled);
        zram.setTitle(getString(R.string.disksize));
        zram.setSummary(getString(R.string.disksize_summary2));
        zram.setUnit(getString(R.string.mb));
        zram.setMax(maxZramAllowed);
        zram.setOffset(32);
        zram.setProgress(ZRAM.getDisksize() / 32);
        zram.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                ZRAM.setDisksize(position * 32, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        zramCard.addItem(zram);

        if (zramCard.size() > 0) {
            items.add(zramCard);
        }
    }

    private void zswapInit(List<RecyclerViewItem> items) {
        CardView zswapCard = new CardView(getActivity());
        zswapCard.setTitle(getString(R.string.zswap));

        if (ZSwap.hasEnable()) {
            SwitchView zswap = new SwitchView();
            zswap.setTitle(getString(R.string.zswap));
            zswap.setSummary(getString(R.string.zswap_summary));
            zswap.setChecked(ZSwap.isEnabled());
            zswap.addOnSwitchListener((switchView, isChecked)
                    -> ZSwap.enable(isChecked, getActivity()));

            zswapCard.addItem(zswap);
        }

        if (ZSwap.hasMaxPoolPercent()) {
            if(!AppSettings.getBoolean("memory_pool_percent", false, getActivity())) {
                SeekBarView maxPoolPercent = new SeekBarView();
                maxPoolPercent.setTitle(getString(R.string.memory_pool));
                maxPoolPercent.setSummary(getString(R.string.memory_pool_summary));
                maxPoolPercent.setUnit("%");
                maxPoolPercent.setMax(ZSwap.getStockMaxPoolPercent() / 10);
                maxPoolPercent.setProgress(ZSwap.getMaxPoolPercent() / 10);
                maxPoolPercent.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        ZSwap.setMaxPoolPercent(position * 10, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                zswapCard.addItem(maxPoolPercent);

            } else {
                SeekBarView maxPoolPercent = new SeekBarView();
                maxPoolPercent.setTitle(getString(R.string.memory_pool));
                maxPoolPercent.setSummary(getString(R.string.memory_pool_summary));
                maxPoolPercent.setUnit("%");
                maxPoolPercent.setMax(ZSwap.getStockMaxPoolPercent());
                maxPoolPercent.setProgress(ZSwap.getMaxPoolPercent());
                maxPoolPercent.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        ZSwap.setMaxPoolPercent(position, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                zswapCard.addItem(maxPoolPercent);
            }
        }

        if (ZSwap.hasMaxCompressionRatio()) {
            SeekBarView maxCompressionRatio = new SeekBarView();
            maxCompressionRatio.setTitle(getString(R.string.maximum_compression_ratio));
            maxCompressionRatio.setSummary(getString(R.string.maximum_compression_ratio_summary));
            maxCompressionRatio.setUnit("%");
            maxCompressionRatio.setProgress(ZSwap.getMaxCompressionRatio());
            maxCompressionRatio.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ZSwap.setMaxCompressionRatio(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            zswapCard.addItem(maxCompressionRatio);
        }

        if (zswapCard.size() > 0) {
            items.add(zswapCard);
        }
    }

	private void dialogZram() {

        AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        alert.setTitle(getString(R.string.wkl_alert_title));
        alert.setMessage(getString(R.string.zram_dialog));
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            zramSw.setChecked(true);
        });
        alert.setPositiveButton("OK", (dialog, id) -> {
            ZRAM.enable(false, getActivity());
            zram.setEnabled(true);
            zramComp.setEnabled(true);
            ZRAM.setDisksize(0, getActivity());
            getActivity().finish();
        });
        alert.setCancelable(false);

        alert.show();
    }

    private void refreshVMs() {
        getHandler().postDelayed(() -> {
            for (int i = 0; i < mVMs.size(); i++) {
                mVMs.get(i).setValue(VM.getValue(i, mCompleteList));
                mVMs.get(i).setValueRaw(mVMs.get(i).getValue());
            }
        }, 250);
    }

 //   protected void refresh() {
 //       super.refresh();

    protected void refreshBars() {

        if (swap != null) {
            long total = mMemInfo.getItemMb("SwapTotal");
            long progress = total - mMemInfo.getItemMb("SwapFree");
            swap.setItems(total, progress);
        }
        if (mem != null) {
            long total = mMemInfo.getItemMb("MemTotal");
            long progress = total - (mMemInfo.getItemMb("Cached") + mMemInfo.getItemMb("MemFree"));
            mem.setItems(total, progress);
        }
    }

}
