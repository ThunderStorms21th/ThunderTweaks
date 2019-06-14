package com.thunder.thundertweaks.fragments.kernel;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.fragments.recyclerview.RecyclerViewFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.kernel.hmp.Hmp;
import com.thunder.thundertweaks.views.recyclerview.CardView;
import com.thunder.thundertweaks.views.recyclerview.DescriptionView;
import com.thunder.thundertweaks.views.recyclerview.RecyclerViewItem;
import com.thunder.thundertweaks.views.recyclerview.SeekBarView;
import com.thunder.thundertweaks.views.recyclerview.TitleView;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by MoroGoku on 10/10/2017.
 */

public class HmpFragment  extends RecyclerViewFragment {

    private Hmp mHmp;

    private final LinkedHashMap<Integer, String> sProfiles = new LinkedHashMap<>();
    private SeekBarView mUpThreshold;
    private SeekBarView mDownThreshold;

    @Override
    protected void init() {
        super.init();

        sProfiles.clear();
        sProfiles.put(R.string.stock, "524 214");
        sProfiles.put(R.string.battery, "700 256");
        sProfiles.put(R.string.performance, "430 150");

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        mHmp = Hmp.getInstance();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        CardView card = new CardView(getActivity());
        card.setTitle(getString(R.string.hmp_long));

        DescriptionView hmp = new DescriptionView();
        hmp.setSummary(getString(R.string.hmp_desc));
        card.addItem(hmp);

        if(mHmp.hasUpThreshold()) {
            mUpThreshold = new SeekBarView();
            mUpThreshold.setTitle(getString(R.string.hmp_up_threshold));
            mUpThreshold.setSummary(getString(R.string.hmp_up_threshold_summary));
            mUpThreshold.setMax(1024);
            mUpThreshold.setMin(1);
            mUpThreshold.setProgress(Utils.strToInt(mHmp.getUpThreshold()) -1);
            mUpThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mHmp.setUpThreshold((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            card.addItem(mUpThreshold);
        }

        if(mHmp.hasDownThreshold()){
            mDownThreshold = new SeekBarView();
            mDownThreshold.setTitle(getString(R.string.hmp_down_threshold));
            mDownThreshold.setSummary(getString(R.string.hmp_down_threshold_summary));
            mDownThreshold.setMax(1024);
            mDownThreshold.setMin(1);
            mDownThreshold.setProgress(Utils.strToInt(mHmp.getDownThreshold()) -1);
            mDownThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mHmp.setDownThreshold((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            card.addItem(mDownThreshold);
        }

        if (card.size() > 0) {
            items.add(card);
        }

        if(mHmp.hasUpThreshold() && mHmp.hasDownThreshold()){

            TitleView profilesTitle = new TitleView();
            profilesTitle.setText(getString(R.string.profile));
            items.add(profilesTitle);

            for (int id : sProfiles.keySet()) {
                DescriptionView profile = new DescriptionView();
                profile.setTitle(getString(id));
                profile.setSummary(sProfiles.get(id));
                profile.setOnItemClickListener((item ) -> {
                        mHmp.setHmpProfile(((DescriptionView) item).getSummary().toString(), getActivity());
                        refreshHmpProfile();
                    });

                items.add(profile);
            }
        }
    }

    private void refreshHmpProfile() {
        getHandler().postDelayed(() -> {
                mUpThreshold.setProgress(Utils.strToInt(mHmp.getUpThreshold()) -1);
                mDownThreshold.setProgress(Utils.strToInt(mHmp.getDownThreshold()) -1);
        }, 250);
    }
}
