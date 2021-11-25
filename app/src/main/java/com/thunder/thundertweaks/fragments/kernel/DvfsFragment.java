package com.thunder.thundertweaks.fragments.kernel;

import android.util.SparseArray;
import android.text.InputType;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.fragments.recyclerview.RecyclerViewFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.Device;
import com.thunder.thundertweaks.utils.kernel.dvfs.Dvfs;
import com.thunder.thundertweaks.fragments.statistics.OverallFragment;
import com.thunder.thundertweaks.utils.kernel.bus.VoltageMif;
import com.thunder.thundertweaks.views.recyclerview.CardView;
import com.thunder.thundertweaks.views.recyclerview.GenericSelectView2;
import com.thunder.thundertweaks.views.recyclerview.RecyclerViewItem;
import com.thunder.thundertweaks.views.recyclerview.SeekBarView;
import com.thunder.thundertweaks.views.recyclerview.SelectView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Morogoku on 11/04/2017.
 */

public class DvfsFragment extends RecyclerViewFragment {

    private Dvfs mDvfs;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        if (Dvfs.hasDecisionMode()) {
            DecisionModeInit(items);
        }

        if (Dvfs.hasThermalControl()) {
            ThermalControlInit(items);
        }

        if (Dvfs.hasDevfreqMinFreq()) {
            DvfsFreqInit(items);
        }
	}

   private void DecisionModeInit(List<RecyclerViewItem> items) {
        CardView dec = new CardView(getActivity());
        dec.setTitle(getString(R.string.dvfs_decision_mode));

        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList("Battery", "Balance", "Performance"));

        SelectView selectView = new SelectView();
        selectView.setTitle(getString(R.string.dvfs_decision_mode));
        selectView.setSummary(getString(R.string.dvfs_decision_mode_summary));
        selectView.setItems(list);
        selectView.setItem(Dvfs.getDecisionMode());
        selectView.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Dvfs.setDecisionMode(item, getActivity());
            }
        });

        dec.addItem(selectView);

        if (dec.size() > 0) {
            items.add(dec);
        }
   }

   private void ThermalControlInit(List<RecyclerViewItem> items) {
        CardView term = new CardView(getActivity());
        term.setTitle(getString(R.string.dvfs_thermal_control));

        SeekBarView seekBar = new SeekBarView();
        seekBar.setTitle(getString(R.string.dvfs_thermal_control));
        seekBar.setSummary(getString(R.string.dvfs_thermal_control_summary));
        seekBar.setMax(90);
        seekBar.setMin(30);
        seekBar.setProgress(Utils.strToInt(Dvfs.getThermalControl()) - 30);
        seekBar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Dvfs.setThermalControl(String.valueOf(position + 30), getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        term.addItem(seekBar);

        if (term.size() > 0) {
            items.add(term);
        }
   }

	String board = Device.getBoard();

   private void DvfsFreqInit(List<RecyclerViewItem> items) {
        CardView devmif = new CardView(getActivity());
        devmif.setTitle(getString(R.string.dvfs_mif_control));
		List<String> list = new ArrayList<>();		

	if(Dvfs.hasAvailableFreq()) {
        list.addAll(Dvfs.getAvailableFreq());
	} else { 
		list.addAll(Arrays.asList("not supported"));
	}
        SelectView mif_min = new SelectView();
        mif_min.setTitle(getString(R.string.mif_min_freq));
        mif_min.setSummary(getString(R.string.mif_min_freq_summary));
        mif_min.setItems(list);
        mif_min.setItem(Dvfs.getDevfreqMinFreq());
        mif_min.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView mif_min, int position, String item) {
                Dvfs.setDevfreqMinFreq(item, getActivity());
            }
        });

        devmif.addItem(mif_min);

        SelectView mif_max = new SelectView();
        mif_max.setTitle(getString(R.string.mif_max_freq));
        mif_max.setSummary(getString(R.string.mif_max_freq_summary));
        mif_max.setItems(list);
        mif_max.setItem(Dvfs.getDevfreqMaxFreq());
        mif_max.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView mif_max, int position, String item) {
                Dvfs.setDevfreqMaxFreq(item, getActivity());
            }
        });

        devmif.addItem(mif_max);

        if (devmif.size() > 0) {
            items.add(devmif);
        }
   }
}
