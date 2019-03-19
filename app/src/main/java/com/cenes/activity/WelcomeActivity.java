package com.cenes.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenes.R;
import com.cenes.service.PrefManager;
import com.cenes.util.CenesUtils;
import com.cenes.util.RoundedImageView;

/**
 * Created by mandeep on 15/8/18.
 */

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private View[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext, btnGetStarted;
    private PrefManager prefManager;
    private View getStartedSeparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnGetStarted = (Button) findViewById(R.id.btn_get_started);
        getStartedSeparator = (View) findViewById(R.id.get_started_separator);

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.onboarding_step1,
                R.layout.onboarding_step2,
                //R.layout.onboarding_step3,
                R.layout.onboarding_step4,
                R.layout.onboarding_step5};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(WelcomeActivity.this,ChoiceActivity.class));
                startActivity(new Intent(WelcomeActivity.this,GuestActivity.class));
                finish();
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new View[layouts.length];

        /*int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        int[] colorsInactive = getResources().getDrawable(R.drawable.xml_circle_white);*/

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {

            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(10), CenesUtils.dpToPx(10));
            viewParams.setMargins(CenesUtils.dpToPx(5), 0, CenesUtils.dpToPx(5), 0);
            viewParams.gravity = Gravity.CENTER;
            dots[i] = new View(this);
            dots[i].setLayoutParams(viewParams);
            dots[i].setBackground(getResources().getDrawable(R.drawable.xml_circle_grey_dots));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setBackground(getResources().getDrawable(R.drawable.xml_circle_white));
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        //startActivity(new Intent(WelcomeActivity.this, ChoiceActivity.class));
        startActivity(new Intent(WelcomeActivity.this, GuestActivity.class));
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT

                btnGetStarted.setVisibility(View.VISIBLE);
                getStartedSeparator.setVisibility(View.VISIBLE);
                btnGetStarted.setText(getString(R.string.start));
                btnNext.setVisibility(View.GONE);
                btnSkip.setVisibility(View.GONE);
                dotsLayout.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnNext.setVisibility(View.VISIBLE);
                btnSkip.setVisibility(View.VISIBLE);
                dotsLayout.setVisibility(View.VISIBLE);
                btnGetStarted.setVisibility(View.GONE);
                getStartedSeparator.setVisibility(View.GONE);

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
