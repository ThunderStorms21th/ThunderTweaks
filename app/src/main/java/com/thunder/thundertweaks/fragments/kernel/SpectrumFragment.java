package com.thunder.thundertweaks.fragments.kernel;

import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.util.SparseArray;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.fragments.tools.OnBootFragment;
import com.thunder.thundertweaks.fragments.DescriptionFragment;
import com.thunder.thundertweaks.fragments.recyclerview.RecyclerViewFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.AppSettings;
import com.thunder.thundertweaks.utils.kernel.spectrum.ProfileTile;
import com.thunder.thundertweaks.utils.kernel.spectrum.Spectrum;
import com.thunder.thundertweaks.views.recyclerview.CardView;
import com.thunder.thundertweaks.views.recyclerview.DescriptionView;
import com.thunder.thundertweaks.views.recyclerview.RecyclerViewItem;
import com.thunder.thundertweaks.utils.root.Control;
import com.thunder.thundertweaks.views.recyclerview.SeekBarView;
import com.thunder.thundertweaks.views.recyclerview.SelectView;
import com.thunder.thundertweaks.views.recyclerview.SwitchView;

import java.util.List;
import java.util.Objects; 

// added spectrum 

import java.util.ArrayList;
import java.util.Arrays;

import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Morogoku on 28/07/2017.
 * Modded by nalas on 15/06/2019 - added ThundeRStormS card
 */

public class SpectrumFragment extends RecyclerViewFragment {
	
	int profile;
	
    @Override
    protected void init() {
        super.init();

		addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.spec_title), getString(R.string.spec_info)));
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
		// Spectrum.setProfile(profile);
		// Spectrum.setProfile(prof);
			
    }

    private CardView oldCard;
    private DescriptionView oldDesc;


    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        final int balColor = ContextCompat.getColor(getContext(), R.color.colorBalance);
        final int perColor = ContextCompat.getColor(getContext(), R.color.colorPerformance);
        final int batColor = ContextCompat.getColor(getContext(), R.color.colorBattery);
        final int gamColor = ContextCompat.getColor(getContext(), R.color.colorGaming);
		final int thunderColor = ContextCompat.getColor(getContext(), R.color.colorThundeRST);
		final int thundergColor = ContextCompat.getColor(getContext(), R.color.colorThundeRG);
		final int thunderrColor = ContextCompat.getColor(getContext(), R.color.colorThundeRR);
		final int thundersColor = ContextCompat.getColor(getContext(), R.color.colorThundeRS);
//		final int thunderdColor = ContextCompat.getColor(getContext(), R.color.colorThundeRD);
		final int thunderfColor = ContextCompat.getColor(getContext(), R.color.colorThundeRF);
		final int thunderaColor = ContextCompat.getColor(getContext(), R.color.colorThundeRA);

        //CardView Balanced
        final CardView card0 = new CardView(getActivity());
        card0.setTitle(getString(R.string.spec_balanced));
        card0.setExpandable(false);

        final DescriptionView desc0 = new DescriptionView();
        desc0.setSummary(getString(R.string.spec_balanced_summary));
        desc0.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_balanced));

        card0.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card0, desc0, 0, balColor);
            }
        });

        card0.addItem(desc0);
        items.add(card0);


        //CardView Performance
        final CardView card1 = new CardView(getActivity());
        card1.setTitle(getString(R.string.spec_performance));
        card1.setExpandable(false);

        final DescriptionView desc1 = new DescriptionView();
        desc1.setSummary(getString(R.string.spec_performance_summary));
        desc1.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_performance));

        card1.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card1, desc1, 1, perColor);
            }

        });

        card1.addItem(desc1);
        items.add(card1);


        //CardView Battery
        final CardView card2 = new CardView(getActivity());
        card2.setTitle(getString(R.string.spec_battery));
        card2.setExpandable(false);

        final DescriptionView desc2 = new DescriptionView();
        desc2.setSummary(getString(R.string.spec_battery_summary));
        desc2.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_battery));

        card2.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card2, desc2, 2, batColor);
            }

        });

        card2.addItem(desc2);
        items.add(card2);


        //CardView Gaming
        final CardView card3 = new CardView(getActivity());
        card3.setTitle(getString(R.string.spec_gaming));
        card3.setExpandable(false);

        final DescriptionView desc3 = new DescriptionView();
        desc3.setSummary(getString(R.string.spec_gaming_summary));
        desc3.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_game));

        card3.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card3, desc3, 3, gamColor);
            }

        });

        card3.addItem(desc3);
        items.add(card3);

        //CardView ThundeRStormS
        final CardView card4 = new CardView(getActivity());
        card4.setTitle(getString(R.string.spec_thunder));
        card4.setExpandable(false);

        final DescriptionView desc4 = new DescriptionView();
        desc4.setSummary(getString(R.string.spec_thunder_summary));
        desc4.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_thunder));

        card4.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card4, desc4, 4, thunderColor);
            }
        });

        card4.addItem(desc4);
        items.add(card4);
		
        //CardView ThundeRStormS - Game Over
        final CardView card5 = new CardView(getActivity());
        card5.setTitle(getString(R.string.spec_thunderg));
        card5.setExpandable(false);

        final DescriptionView desc5 = new DescriptionView();
        desc5.setSummary(getString(R.string.spec_thunderg_summary));
        desc5.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_battery));

        card5.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card5, desc5, 5, thundergColor);
            }
        });

        card5.addItem(desc5);
        items.add(card5);

        //CardView ThundeRStormS - rtakak
        final CardView card6 = new CardView(getActivity());
        card6.setTitle(getString(R.string.spec_thunderr));
        card6.setExpandable(false);

        final DescriptionView desc6 = new DescriptionView();
        desc6.setSummary(getString(R.string.spec_thunderr_summary));
        desc6.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_battery));

        card6.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card6, desc6, 6, thunderrColor);
            }
        });

        card6.addItem(desc6);
        items.add(card6);
		
        //CardView ThundeRStormS - Shariq
        final CardView card7 = new CardView(getActivity());
        card7.setTitle(getString(R.string.spec_thunders));
        card7.setExpandable(false);

        final DescriptionView desc7 = new DescriptionView();
        desc7.setSummary(getString(R.string.spec_thunders_summary));
        desc7.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_thunder));

        card7.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card7, desc7, 7, thundersColor);
            }
        });

        card7.addItem(desc7);
        items.add(card7);

		//CardView - Franz
        final CardView card8 = new CardView(getActivity());
        card8.setTitle(getString(R.string.spec_thunderf));
        card8.setExpandable(false);

        final DescriptionView desc8 = new DescriptionView();
        desc8.setSummary(getString(R.string.spec_thunderf_summary));
        desc8.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_battery));

        card8.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card8, desc8, 8, thunderfColor);
            }
        });
		
        card8.addItem(desc8);
        items.add(card8);
		
		//CardView ThundeRStormS-II
        final CardView card9 = new CardView(getActivity());
        card9.setTitle(getString(R.string.spec_thunder2));
        card9.setExpandable(false);

        final DescriptionView desc9 = new DescriptionView();
        desc9.setSummary(getString(R.string.spec_thunder2_summary));
        desc9.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_thunder));

        card9.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card9, desc9, 9, thunderColor);
            }
        });

        card9.addItem(desc9);
        items.add(card9);

		//CardView Ankit
        final CardView card10 = new CardView(getActivity());
        card10.setTitle(getString(R.string.spec_thundera));
        card10.setExpandable(false);

        final DescriptionView desc10 = new DescriptionView();
        desc10.setSummary(getString(R.string.spec_thundera_summary));
        desc10.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_battery));

        card10.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card10, desc10, 10, thunderaColor);
            }
        });

        card10.addItem(desc10);
        items.add(card10);

		// Initialice profile Sharedpreference
        int prof = Utils.strToInt(Spectrum.getProfile());
        AppSettings.saveInt("spectrum_profile", prof, getActivity());

        //Detects the selected profile on launch
        int mProfile = AppSettings.getInt("spectrum_profile", 0, getActivity());
        // int mProfile = AppSettings.getInt("spectrum_profile", 0, getActivity());
	
        if(mProfile == 0){
            card0.GrxSetInitSelection(true, balColor);
            desc0.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card0;
            oldDesc = desc0;
        } else if(mProfile == 1){
            card1.GrxSetInitSelection(true, perColor);
            desc1.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card1;
            oldDesc = desc1;
        } else if(mProfile == 2){
            card2.GrxSetInitSelection(true, batColor);
            desc2.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card2;
            oldDesc = desc2;
        } else if(mProfile == 3){
            card3.GrxSetInitSelection(true, gamColor);
            desc3.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card3;
            oldDesc = desc3;
        } else if(mProfile == 4){
            card4.GrxSetInitSelection(true, thunderColor);
            desc4.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card4;
            oldDesc = desc4;
        } else if(mProfile == 5){
            card5.GrxSetInitSelection(true, thundergColor);
            desc5.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card5;
            oldDesc = desc5;
        } else if(mProfile == 6){
            card6.GrxSetInitSelection(true, thunderrColor);
            desc6.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card6;
            oldDesc = desc6;
        } else if(mProfile == 7){
            card7.GrxSetInitSelection(true, thundersColor);
            desc7.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card7;
            oldDesc = desc7;
        } else if(mProfile == 8){
            card8.GrxSetInitSelection(true, thunderfColor);
            desc8.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card8;
            oldDesc = desc8;
        } else if(mProfile == 9){
            card9.GrxSetInitSelection(true, thunderColor);
            desc9.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card9;
            oldDesc = desc9;
        } else if(mProfile == 10){
            card10.GrxSetInitSelection(true, thunderaColor);
            desc10.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card10;
            oldDesc = desc10;
        }

    }

    // Method that completes card onClick tasks
    private void cardClick(CardView card, DescriptionView desc, int prof, int color) {
        if (oldCard != card && oldDesc != desc) {
            ColorStateList ogColor = card.getCardBackgroundColor();
            ColorStateList odColor = desc.getTextColors();
            card.setCardBackgroundColor(color);
            desc.setTextColor(Color.WHITE);
            if(oldCard != null) oldCard.setCardBackgroundColor(ogColor);
            if(oldDesc != null) oldDesc.setTextColor(odColor);
            Spectrum.setProfile(prof);
            oldCard = card;
            oldDesc = desc;
            AppSettings.saveInt("spectrum_profile", prof, getActivity());
        }
    }

	// added for Apply on Boot
    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SPECTRUM, id, context);
    }	

}
