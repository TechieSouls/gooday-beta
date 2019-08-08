package com.cenesbeta.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cenesbeta.R;
import com.cenesbeta.fragment.AddAlarmFragment;
import com.cenesbeta.fragment.NavigationFragment;
import com.cenesbeta.fragment.metime.MeTimeCardFragment;
import com.cenesbeta.fragment.metime.MeTimeFragment;

import java.util.List;

/**
 * Created by mandeep on 8/1/19.
 */

public class MeTimeActivity extends CenesActivity {

    public static DrawerLayout mDrawerLayout;

    private FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    public ImageView footerHomeIcon, footerGatheringIcon;
    public LinearLayout rlFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_metime);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.settings_container, new NavigationFragment(), null).commit();

        init();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (!fragment.isVisible()) continue;

                if (fragment instanceof MeTimeCardFragment) {

                    rlFooter.setVisibility(View.VISIBLE);
                }
            }
        }

        super.onBackPressed();
    }

    public void init() {

        footerHomeIcon = (ImageView) findViewById(R.id.footer_home_icon);
        footerGatheringIcon = (ImageView) findViewById(R.id.footer_gathering_icon);
        rlFooter = (LinearLayout) findViewById(R.id.rl_footer);

        MeTimeFragment meTimeFragment = new MeTimeFragment();
        replaceFragment(meTimeFragment, null);
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

}
