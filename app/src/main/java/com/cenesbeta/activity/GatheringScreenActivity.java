package com.cenesbeta.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.cenesbeta.fragment.gathering.CreateGatheringFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.fragment.gathering.GatheringsFragment;
import com.cenesbeta.fragment.NavigationFragment;

/**
 * Created by mandeep on 2/11/17.
 */

public class GatheringScreenActivity extends CenesActivity {


    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    InternetManager internetManager;

    public static DrawerLayout mDrawerLayout;

    private FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    private ImageView footerHomeIcon, footerReminderIcon, footerAlarmIcon, footerGatheringIcon, footerDiaryIcon, footerMetimeIcon;
    public LinearLayout footerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        if (intent != null && intent.getStringExtra("dataFrom") != null && intent.getStringExtra("dataFrom").equals("push")) {

            final Long eventId = intent.getLongExtra("eventId", 0l);


            Bundle bundle = new Bundle();
            bundle.putString("dataFrom", "push");
            bundle.putLong("eventId", eventId);
            bundle.putString("message", intent.getStringExtra("message"));
            bundle.putString("title", intent.getStringExtra("title"));

//            InvitationFragment invitationFragment = new InvitationFragment();
//            invitationFragment.setArguments(bundle);

            GatheringsFragment gatheringsFragment = new GatheringsFragment();
            gatheringsFragment.setArguments(bundle);

            replaceFragment(gatheringsFragment, null);

        } else if (intent != null && intent.getStringExtra("dataFrom") != null && intent.getStringExtra("dataFrom").equals("gathering_push")) {
            final Long eventId = intent.getLongExtra("eventId", 0l);

            Bundle bundle = new Bundle();
            bundle.putString("dataFrom", "list");
            bundle.putLong("eventId", intent.getLongExtra("eventId", 0l));

            /*GatheringsFragment gatheringsFragment = new GatheringsFragment();
            gatheringsFragment.setArguments(bundle);*/
            GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
            gatheringPreviewFragment.setArguments(bundle);
            replaceFragment(gatheringPreviewFragment, null);
        } else if (intent != null && intent.getStringExtra("dataFrom") != null && intent.getStringExtra("dataFrom").equals("list")) {
            Bundle bundle = new Bundle();
            bundle.putString("dataFrom", "list");
            bundle.putLong("eventId", intent.getLongExtra("eventId", 0l));

            /*CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
            createGatheringFragment.setArguments(bundle);

            replaceFragment(createGatheringFragment, null);*/
            GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
            gatheringPreviewFragment.setArguments(bundle);
            replaceFragment(gatheringPreviewFragment, null);
        } else if (intent != null && intent.getStringExtra("dataFrom") != null && intent.getStringExtra("dataFrom").equals("fabButton")) {
            replaceFragment(new CreateGatheringFragment(), null);
        } else {
            replaceFragment(new GatheringsFragment(), null);
        }
    }

    public void init() {
        footerLayout = (LinearLayout) findViewById(R.id.rl_footer);
        footerHomeIcon = (ImageView) findViewById(R.id.footer_home_icon);
        footerReminderIcon = (ImageView) findViewById(R.id.footer_reminder_icon);
        footerAlarmIcon = (ImageView) findViewById(R.id.footer_alarm_icon);
        footerGatheringIcon = (ImageView) findViewById(R.id.footer_gathering_icon);
        footerDiaryIcon = (ImageView) findViewById(R.id.footer_diary_icon);
        footerMetimeIcon = (ImageView) findViewById(R.id.footer_metime_icon);

        footerHomeIcon.setOnClickListener(onClickListener);
        footerReminderIcon.setOnClickListener(onClickListener);
        footerAlarmIcon.setOnClickListener(onClickListener);
        footerDiaryIcon.setOnClickListener(onClickListener);
        footerMetimeIcon.setOnClickListener(onClickListener);

        footerHomeIcon.setImageResource(R.drawable.home_icon_unselected);
        footerGatheringIcon.setImageResource(R.drawable.gathering_icon_selected);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.footer_home_icon:
                    startActivity(new Intent(GatheringScreenActivity.this, HomeScreenActivity.class));
                    finish();
                    break;
                case R.id.footer_reminder_icon:
                    startActivity(new Intent(GatheringScreenActivity.this, ReminderActivity.class));
                    finish();
                    break;
                case R.id.footer_alarm_icon:
                    startActivity(new Intent(GatheringScreenActivity.this, AlarmActivity.class));
                    finish();
                    break;
                case R.id.footer_diary_icon:
                    startActivity(new Intent(GatheringScreenActivity.this, DiaryActivity.class));
                    finish();
                    break;
                case R.id.footer_metime_icon:
                    startActivity(new Intent(GatheringScreenActivity.this, MeTimeActivity.class));
                    finish();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    public void removeAllFragments(FragmentManager fragmentManager) {
        while (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    public void clearBackStackInclusive(String tag) {
        getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void hideFooter() {
        footerLayout.setVisibility(View.GONE);
    }

    public void showFooter() {
        footerLayout.setVisibility(View.VISIBLE);
    }
}
