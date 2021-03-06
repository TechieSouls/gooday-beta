package com.cenesbeta.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.cenesbeta.R;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.guest.SignupOptionsFragment;
import com.cenesbeta.fragment.guest.PhoneVerificationStep1Fragment;
import com.cenesbeta.fragment.guest.SignupStepSuccessFragment;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by mandeep on 18/9/18.
 */

public class GuestActivity extends CenesActivity {

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;

    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        fragmentManager = getSupportFragmentManager();

        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        User user = userManager.getUser();

        /*if (user == null) {
            replaceFragment(new GuestFragment(), null);
        } else */if (user == null) {
            replaceFragment(new PhoneVerificationStep1Fragment() , null);
        } else {
            replaceFragment(new SignupOptionsFragment(), null);
        }
        fragmentManager.beginTransaction().commit();
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

    public void clearFragmentsAndOpen(Fragment fragment, String tag) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(fragment, tag);
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

    public void clearAllFragmentsInBackstack() {
        if(getFragmentManager() != null) {
            FragmentManager fm = getSupportFragmentManager(); // or 'getSupportFragmentManager();'
            int count = fm.getBackStackEntryCount();
            for (int i = 0; i < count; ++i) {
                fm.popBackStack();
            }
        }
    }
}
