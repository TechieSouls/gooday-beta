package com.cenes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cenes.R;
import com.cenes.fragment.WalkthroughFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohan on 9/8/17.
 */
public class WalkthroughActivity extends FragmentActivity {

    MyPageAdapter pageAdapter;
    ViewPager viewPager;
    ImageView ivDots;
    Button btSkip, btNext;
    RelativeLayout layoutFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walkthrough);

        btSkip = (Button) findViewById(R.id.bt_skip);
        btNext = (Button) findViewById(R.id.bt_next);
        ivDots = (ImageView) findViewById(R.id.iv_dots);
        layoutFooter = (RelativeLayout) findViewById(R.id.rl_footer);
        List<Fragment> fragments = getFragments();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        viewPager = (ViewPager) findViewById(R.id.vp);
        viewPager.setPageTransformer(false, new FadePageTransformer());
        viewPager.setOnPageChangeListener(mPageChangeListener);
        viewPager.setAdapter(pageAdapter);

        btSkip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == pageAdapter.getCount() - 1) {
                    finishActivity();
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });
    }

    public void finishActivity() {
        WalkthroughActivity.this.setResult(RESULT_OK);
        startActivity(new Intent(WalkthroughActivity.this,ChoiceActivity.class));
        finish();
    }

    private static class FadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            view.setTranslationX(view.getWidth() * -position);
            view.findViewById(R.id.tv_instructions).setTranslationX(view.getWidth() * position);
            view.findViewById(R.id.tv_title).setTranslationX(view.getWidth() * position);
            view.findViewById(R.id.iv).setTranslationX(view.getWidth() * position);
            if (position <= -1.0F || position >= 1.0F) {
                view.setAlpha(0.0F);
            } else if (position == 0.0F) {
                view.setAlpha(1.0F);
            } else {
                view.setAlpha(1.0F - Math.abs(position));
            }
        }
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();

        fList.add(WalkthroughFragment.newInstance(R.drawable.onboarding_1, R.string.title_1, R.string.ins_1,false));
        fList.add(WalkthroughFragment.newInstance(R.drawable.onboarding_2, R.string.title_2, R.string.ins_2,false));
/*
        fList.add(WalkthroughFragment.newInstance(R.drawable.onboarding_3, R.string.title_3, R.string.ins_3,false));
*/
        fList.add(WalkthroughFragment.newInstance(R.drawable.onboarding_3, R.string.gs_heading, R.string.gs_ins,true));
        return fList;
    }

    private final OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrolled(final int position, final float offset, final int offsetPixel) {
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
        }

        @Override
        public void onPageSelected(final int position) {

            if (position == 0) {
                ivDots.setImageResource(R.drawable.dots_3_1);
                layoutFooter.setVisibility(View.VISIBLE);
            } else if (position == 1) {
                ivDots.setImageResource(R.drawable.dots_3_2);
                layoutFooter.setVisibility(View.VISIBLE);
            } else if (position == 2) {
                layoutFooter.setVisibility(View.GONE);
                /*ivDots.setImageResource(R.drawable.dots_3);
                layoutFooter.setVisibility(View.VISIBLE);
            } else if (position == 3) {

                layoutFooter.setVisibility(View.GONE);*/
             }
        }
    };

    class MyPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
