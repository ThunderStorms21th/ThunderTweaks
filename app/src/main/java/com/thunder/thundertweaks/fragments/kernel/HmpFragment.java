package com.thunder.thundertweaks.fragments.kernel;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.fragments.recyclerview.RecyclerViewFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.kernel.hmp.Hmp;
import com.thunder.thundertweaks.utils.kernel.cpu.CPUFreq;
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
	private SeekBarView mDownCompThreshold;
	private SeekBarView mDownCompTimeout;
	private SeekBarView mSbUpThreshold;
    private SeekBarView mSbDownThreshold;

    @Override
    protected void init() {
        super.init();

        sProfiles.clear();
        sProfiles.put(R.string.stock, "524 214");
        sProfiles.put(R.string.balanced, "700 256");
		sProfiles.put(R.string.battery, "800 300");
		sProfiles.put(R.string.battery2, "900 340");
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

        if(mHmp.hasDownCompThreshold()) {
            mDownCompThreshold = new SeekBarView();
            mDownCompThreshold.setTitle(getString(R.string.hmp_down_comp_threshold));
            // mDownCompThreshold.setSummary(getString(R.string.hmp_down_comp_threshold_summary));
            mDownCompThreshold.setMax(213);
            mDownCompThreshold.setMin(1);
            mDownCompThreshold.setProgress(Utils.strToInt(mHmp.getDownCompThreshold()) -1);
            mDownCompThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mHmp.setDownCompThreshold((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            card.addItem(mDownCompThreshold);
        }

        if(mHmp.hasDownCompTimeout()) {
            mDownCompTimeout = new SeekBarView();
            mDownCompTimeout.setTitle(getString(R.string.hmp_down_comp_timeout));
            // mDownCompTimeout.setSummary(getString(R.string.hmp_down_comp_timeout_summary));
            mDownCompTimeout.setMax(500);
            mDownCompTimeout.setMin(1);
            mDownCompTimeout.setProgress(Utils.strToInt(mHmp.getDownCompTimeout()) -1);
            mDownCompTimeout.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mHmp.setDownCompTimeout((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            card.addItem(mDownCompTimeout);
        }

        if(mHmp.hasSbUpThreshold()) {
            mSbUpThreshold = new SeekBarView();
            mSbUpThreshold.setTitle(getString(R.string.hmp_sb_up_threshold));
            // mSbUpThreshold.setSummary(getString(R.string.hmp_sb_up_threshold_summary));
            mSbUpThreshold.setMax(1024);
            mSbUpThreshold.setMin(1);
            mSbUpThreshold.setProgress(Utils.strToInt(mHmp.getSbUpThreshold()) -1);
            mSbUpThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mHmp.setSbUpThreshold((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            card.addItem(mSbUpThreshold);
        }

        if(mHmp.hasSbDownThreshold()) {
            mSbDownThreshold = new SeekBarView();
            mSbDownThreshold.setTitle(getString(R.string.hmp_sb_down_threshold));
            // mSbDownThreshold.setSummary(getString(R.string.hmp_sb_down_threshold_summary));
            mSbDownThreshold.setMax(1024);
            mSbDownThreshold.setMin(1);
            mSbDownThreshold.setProgress(Utils.strToInt(mHmp.getSbDownThreshold()) -1);
            mSbDownThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mHmp.setSbDownThreshold((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            card.addItem(mSbDownThreshold);
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
