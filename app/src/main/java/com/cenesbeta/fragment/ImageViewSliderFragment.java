package com.cenesbeta.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.ImageViewPagerAdapter;
import com.cenesbeta.util.CenesUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class ImageViewSliderFragment extends CenesFragment {

    private ImageView ivCloseButton;
    private ViewPager imageViewPage;
    private LinearLayout layoutDots;
    private ImageViewPagerAdapter imageViewPagerAdapter;
    public List<String> images;
    public Integer pageIndex = 0;
    private View[] dots;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

         View view = inflater.inflate(R.layout.fragment_image_slider, container, false);

         ivCloseButton = (ImageView) view.findViewById(R.id.iv_close_button);
         imageViewPage = (ViewPager) view.findViewById(R.id.vp_image_slider);
         layoutDots = (LinearLayout) view.findViewById(R.id.layoutDots);

         imageViewPagerAdapter = new ImageViewPagerAdapter(this, images);
         imageViewPage.setAdapter(imageViewPagerAdapter);
         imageViewPage.setOnPageChangeListener(onPageChangeListener);

         ivCloseButton.setOnClickListener(onClickListener);

         addBottomDots(pageIndex);
         imageViewPage.setCurrentItem(pageIndex);
         return view;
    }

    private void addBottomDots(int currentPage) {
        dots = new View[images.size()];

        /*int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        int[] colorsInactive = getResources().getDrawable(R.drawable.xml_circle_white);*/

        layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {

            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(8), CenesUtils.dpToPx(8));
            viewParams.setMargins(CenesUtils.dpToPx(5), 0, CenesUtils.dpToPx(5), 0);
            viewParams.gravity = Gravity.CENTER;
            dots[i] = new View(getContext());
            dots[i].setLayoutParams(viewParams);
            dots[i].setBackground(getResources().getDrawable(R.drawable.xml_circle_tranparent_white));
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setBackground(getResources().getDrawable(R.drawable.xml_circle_white));
        }
    }


    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_close_button:
                    ((CenesBaseActivity)getActivity()).popBackStack();
                    break;
            }
        }
    };
}
