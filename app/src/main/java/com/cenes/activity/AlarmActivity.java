package com.cenes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cenes.R;
import com.cenes.fragment.AddAlarmFragment;
import com.cenes.fragment.AlarmsFragment;
import com.cenes.fragment.NavigationFragment;

import java.util.List;

/**
 * Created by mandeep on 24/10/17.
 */

public class AlarmActivity extends CenesActivity {

    public static DrawerLayout mDrawerLayout;

    private FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    private ImageView footerHomeIcon, footerReminderIcon, footerAlarmIcon, footerGatheringIcon, footerDiaryIcon;
    public LinearLayout footerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.settings_container, new NavigationFragment(), null).commit();

        init();

        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("dataFrom") != null && intent.getStringExtra("dataFrom").equals("fabButton")) {
            replaceFragment(new AddAlarmFragment(), null);
        } else {
            replaceFragment(new AlarmsFragment(), null);
        }
    }

    public void init() {
        footerLayout = (LinearLayout) findViewById(R.id.rl_footer);
        footerHomeIcon = (ImageView) findViewById(R.id.footer_home_icon);
        footerReminderIcon = (ImageView) findViewById(R.id.footer_reminder_icon);
        footerAlarmIcon = (ImageView) findViewById(R.id.footer_alarm_icon);
        footerGatheringIcon = (ImageView) findViewById(R.id.footer_gathering_icon);
        footerDiaryIcon = (ImageView) findViewById(R.id.footer_diary_icon);

        footerHomeIcon.setOnClickListener(onClickListener);
        footerReminderIcon.setOnClickListener(onClickListener);
        footerGatheringIcon.setOnClickListener(onClickListener);
        footerDiaryIcon.setOnClickListener(onClickListener);

        footerHomeIcon.setImageResource(R.drawable.home_icon_grey);
        footerAlarmIcon.setImageResource(R.drawable.alarm_icon_selected);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.footer_home_icon:
                    startActivity(new Intent(AlarmActivity.this, HomeScreenActivity.class));
                    finish();
                    break;
                case R.id.footer_reminder_icon:
                    startActivity(new Intent(AlarmActivity.this, ReminderActivity.class));
                    finish();
                    break;
                case R.id.footer_gathering_icon:
                    startActivity(new Intent(AlarmActivity.this, GatheringScreenActivity.class));
                    finish();
                    break;
                case R.id.footer_diary_icon:
                    startActivity(new Intent(AlarmActivity.this, DiaryActivity.class));
                    finish();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (getAddAlarmFragment() instanceof AddAlarmFragment && AddAlarmFragment.saveDataOnBackPressed) {
            ((AddAlarmFragment) getAddAlarmFragment()).saveDataOnAlarmOptionsBackBtn();
            AddAlarmFragment.saveDataOnBackPressed = false;
        } else {
            super.onBackPressed();
        }
    }

    public void replaceFragment(Fragment fragment, String tag) {

        try {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (tag != null) {
                fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
                fragmentTransaction.addToBackStack(tag);
            } else {
                fragmentTransaction.replace(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearFragmentsAndOpen(Fragment fragment) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(fragment, null);
    }

    public Fragment getAddAlarmFragment() {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && !(fragment instanceof NavigationFragment) && fragment instanceof AddAlarmFragment)
                    return fragment;
            }
        }
        return null;
    }

    public void hideFooter() {
        footerLayout.setVisibility(View.GONE);
    }

    public void showFooter() {
        footerLayout.setVisibility(View.VISIBLE);
    }

}
