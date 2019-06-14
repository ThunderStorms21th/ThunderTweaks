package com.thunder.thundertweaks.fragments.other;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.fragments.recyclerview.RecyclerViewFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.views.recyclerview.DescriptionView;
import com.thunder.thundertweaks.views.recyclerview.ImageView;
import com.thunder.thundertweaks.views.recyclerview.RecyclerViewItem;
import com.thunder.thundertweaks.views.recyclerview.TitleView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Morogoku on 05/12/2017.
 */

public class DonationFragment extends RecyclerViewFragment {


    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        TitleView title = new TitleView();
        title.setText(getString(R.string.donation_title));

        items.add(title);

        DescriptionView desc1 = new DescriptionView();
        desc1.setDrawable(getResources().getDrawable(R.drawable.logo));
        desc1.setSummary(getString(R.string.donation_summary));
        desc1.setOnItemClickListener(item
                -> Utils.launchUrl("https://www.paypal.me/pnalas", Objects.requireNonNull(getActivity())));

        items.add(desc1);

        ImageView img1 = new ImageView();
            img1.setDrawable(getResources().getDrawable(R.drawable.ic_paypal));
        img1.setOnItemClickListener(item
                -> Utils.launchUrl("https://www.paypal.me/pnalas", Objects.requireNonNull(getActivity())));

        items.add(img1);

        DescriptionView desc2 = new DescriptionView();
        desc2.setDrawable(getResources().getDrawable(R.drawable.logo));
        desc2.setSummary(getString(R.string.donation_summary_mg));
        desc2.setOnItemClickListener(item
                -> Utils.launchUrl("https://www.paypal.me/pnalas", Objects.requireNonNull(getActivity())));

        items.add(desc2);


        String leng = Locale.getDefault().getLanguage();

        ImageView img2 = new ImageView();
        if(leng.contains("es")){
            img2.setDrawable(getResources().getDrawable(R.drawable.ic_donar_paypal));
        }else {
            img2.setDrawable(getResources().getDrawable(R.drawable.ic_donate_paypal));
        }
        img2.setOnItemClickListener(item
                -> Utils.launchUrl("https://www.paypal.me/pnalas", Objects.requireNonNull(getActivity())));

        items.add(img2);

    }
}
