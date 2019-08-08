package com.cenesbeta.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cenesbeta.AsyncTasks.CenesCommonAsyncTask;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.NotificationCountData;
import com.cenesbeta.fragment.NavigationFragment;
import com.cenesbeta.fragment.NotificationFragment;
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.fragment.gathering.GatheringsFragment;
import com.cenesbeta.fragment.metime.MeTimeCardFragment;
import com.cenesbeta.fragment.metime.MeTimeFragment;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

public class CenesBaseActivity extends CenesActivity {

    public DrawerLayout mDrawerLayout;

    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;
    public ImageView footerHomeIcon, footerGatheringIcon, footerMeTimeIcon;
    LinearLayout llFooter;
    public RelativeLayout rlLoadingBlock;
    public TextView tvLoadingMsg;
    public ImageView ivNotificationFloatingIcon;
    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private InternetManager internetManager;

    public Event parentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_cenes);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        internetManager = coreManager.getInternetManager();

        fragmentManager = getSupportFragmentManager();
        replaceFragment(new HomeFragment(), null);

        llFooter = (LinearLayout) findViewById(R.id.rl_footer);
        footerHomeIcon = (ImageView) findViewById(R.id.footer_home_icon);
        footerGatheringIcon = (ImageView) findViewById(R.id.footer_gathering_icon);
        footerMeTimeIcon = (ImageView) findViewById(R.id.footer_metime_icon);
        ivNotificationFloatingIcon = (ImageView) findViewById(R.id.iv_notification_floating_icon);

        rlLoadingBlock = (RelativeLayout) findViewById(R.id.rl_loading_block);
        tvLoadingMsg = (TextView) findViewById(R.id.tv_loading_msg);

        //Click Listeners
        footerHomeIcon.setOnClickListener(onClickListener);
        footerGatheringIcon.setOnClickListener(onClickListener);
        footerMeTimeIcon.setOnClickListener(onClickListener);
        ivNotificationFloatingIcon.setOnClickListener(onClickListener);

        notificationCountCall();

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                fragmentManager.beginTransaction().add(R.id.settings_container, new NavigationFragment(), null).commit();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.footer_home_icon:
                    System.out.println("Home Clicked From Base");
                    notificationCountCall();
                    replaceFragment(new HomeFragment(), null);
                    break;

                case R.id.footer_gathering_icon:
                    System.out.println("Gathering Clicked From Base");
                    notificationCountCall();
                    replaceFragment(new GatheringsFragment(), null);
                    break;

                case R.id.footer_metime_icon:
                    System.out.println("MeTime Clicked From Base");
                    notificationCountCall();
                    replaceFragment(new MeTimeFragment(), null);
                    break;

                case R.id.iv_notification_floating_icon:
                    clearBackStackInclusive(null);
                    replaceFragment(new NotificationFragment(), null);
                    break;
            }
        }
    };

    public void hideFooter() {
        llFooter.setVisibility(View.GONE);
    }

    public void showFooter() {
        llFooter.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        for (Fragment fragment: fragmentManager.getFragments()) {

            System.out.println("Back Pressed");
            if (fragment instanceof MeTimeCardFragment) {
                showFooter();
                activateFooterIcon(MeTimeFragment.TAG);
            }
        }
        super.onBackPressed();
    }

    public void replaceFragment(Fragment fragment , String tag) {

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

    public void activateFooterIcon(String tag) {

        if (tag.equals(HomeFragment.TAG)) {
            footerHomeIcon.setImageResource(R.drawable.home_icon_selected);
        } else {
            footerHomeIcon.setImageResource(R.drawable.home_icon_unselected);
        }

        if (tag.equals(GatheringsFragment.TAG)) {
            footerGatheringIcon.setImageResource(R.drawable.gathering_icon_selected);
        } else {
            footerGatheringIcon.setImageResource(R.drawable.gathering_icon_unselected);
        }

        if (tag.equals(MeTimeFragment.TAG)) {
            footerMeTimeIcon.setImageResource(R.drawable.metime_icon_selected);
        } else {
            footerMeTimeIcon.setImageResource(R.drawable.metime_icon_unselected);
        }
    }

    public void clearFragmentsAndOpen(Fragment fragment) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(fragment, null);
    }

    public FragmentManager getHomeActivityFragmentManager() {
        return fragmentManager;
    }

    public Fragment getVisibleFragment() {
        fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void clearBackStackInclusive(String tag) {
        getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void notificationCountCall() {
        if (internetManager.isInternetConnection(CenesBaseActivity.this)) {
            new CenesCommonAsyncTask(getCenesApplication(), this);
            new CenesCommonAsyncTask.NotificationBadgeCountTask(new CenesCommonAsyncTask.NotificationBadgeCountTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {
                    //System.out.println("Notification Data "+response.toString());

                    try {
                        if (response != null && response.getBoolean("success") == true) {
                            Gson gson = new Gson();
                            NotificationCountData notificationCountData = gson.fromJson(response.getString("data"), NotificationCountData.class);
                            if (notificationCountData.getBadgeCount() > 0) {
                                ivNotificationFloatingIcon.setVisibility(View.VISIBLE);
                            } else {
                                ivNotificationFloatingIcon.setVisibility(View.GONE);
                            }
                        } else {
                            ivNotificationFloatingIcon.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).execute();
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG).show();
        }

    }

}
