package com.cenesbeta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cenesbeta.R;
import com.cenesbeta.fragment.CalenderSyncFragment;
import com.cenesbeta.fragment.HolidaySyncFragment;
import com.cenesbeta.fragment.PictureFragment;
import com.cenesbeta.fragment.guest.SignupStepSuccessFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by rohan on 10/10/17.
 */
public class CompleteYourProfileActivity extends CenesActivity {

    ViewPager viewPager;
    ImageView ivDots;
    RelativeLayout layoutFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_complete_your_profile);

        ivDots = (ImageView) findViewById(R.id.iv_dots);

        viewPager = (ViewPager) findViewById(R.id.vp);
        setupViewPager(viewPager);
        viewPager.setOnPageChangeListener(mPageChangeListener);

        /*btDoItLater.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (adapter.getItem(viewPager.getCurrentItem()) instanceof CalenderSyncFragment) {
                    ((CalenderSyncFragment) adapter.getItem(viewPager.getCurrentItem())).nextClickListener();
                }
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });*/

        /*btNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (adapter.getItem(viewPager.getCurrentItem()) instanceof PictureFragment) {
                    ((PictureFragment) adapter.getItem(viewPager.getCurrentItem())).nextClickListener();
                } else if (adapter.getItem(viewPager.getCurrentItem()) instanceof HolidaySyncFragment) {
                    ((HolidaySyncFragment) adapter.getItem(viewPager.getCurrentItem())).nextClickListener();
                }else if (adapter.getItem(viewPager.getCurrentItem()) instanceof CalenderSyncFragment) {
                    ((CalenderSyncFragment) adapter.getItem(viewPager.getCurrentItem())).nextClickListener();
                }
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    ViewPagerAdapter adapter;

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SignupStepSuccessFragment());
        adapter.addFragment(new HolidaySyncFragment());
        //adapter.addFragment(new MeTimeFragment());
        adapter.addFragment(new CalenderSyncFragment());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    private final ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(final int position, final float offset, final int offsetPixel) {
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
        }

        @Override
        public void onPageSelected(final int position) {
            //btNext.setText("NEXT");
            if (position == 0) {
            } else if (position == 1) {
            } /*else if (position == 2) {
                ivDots.setImageResource(R.drawable.dots_3);
            }*/ else if (position == 2) {
                //btNext.setText("FINISH");
            }
        }
    };
}
