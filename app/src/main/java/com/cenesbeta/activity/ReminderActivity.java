package com.cenesbeta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.reminder.CreateReminderFragment;
import com.cenesbeta.fragment.NavigationFragment;
import com.cenesbeta.fragment.reminder.ReminderFragment;

/**
 * Created by mandeep on 1/9/17.
 */

public class ReminderActivity extends CenesActivity {

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    InternetManager internetManager;

    public static DrawerLayout mDrawerLayout;

    private FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    private ImageView footerHomeIcon, footerReminderIcon, footerAlarmIcon, footerGatheringIcon, footerDiaryIcon;
    public LinearLayout footerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_home);

        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        internetManager = coreManager.getInternetManager();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.settings_container, new NavigationFragment(), null).commit();

        init();

        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("dataFrom") != null && intent.getStringExtra("dataFrom").equals("fabButton")) {
            replaceFragment(new CreateReminderFragment(), null);
        } else {

            //new MarkNotificationReadTask().execute();
            replaceFragment(new ReminderFragment(), null);
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
        footerGatheringIcon.setOnClickListener(onClickListener);
        footerDiaryIcon.setOnClickListener(onClickListener);
        footerAlarmIcon.setOnClickListener(onClickListener);

        footerHomeIcon.setImageResource(R.drawable.home_icon_unselected);
        footerReminderIcon.setImageResource(R.drawable.reminder_icon_selected);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.footer_home_icon:
                    startActivity(new Intent(ReminderActivity.this, HomeScreenActivity.class));
                    finish();
                    break;
                case R.id.footer_gathering_icon:
                    startActivity(new Intent(ReminderActivity.this, GatheringScreenActivity.class));
                    finish();
                    break;
                case R.id.footer_diary_icon:
                    startActivity(new Intent(ReminderActivity.this, DiaryActivity.class));
                    finish();
                    break;
                case R.id.footer_alarm_icon:
                    startActivity(new Intent(ReminderActivity.this, AlarmActivity.class));
                    finish();
                    break;
            }
        }
    };

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

    public void hideFooter() {
        footerLayout.setVisibility(View.GONE);
    }

    public void showFooter() {
        footerLayout.setVisibility(View.VISIBLE);
    }
}
