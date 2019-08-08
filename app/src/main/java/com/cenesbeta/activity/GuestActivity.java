package com.cenesbeta.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cenesbeta.R;
import com.cenesbeta.fragment.guest.GuestFragment;
import com.cenesbeta.service.InstabugService;

import java.util.List;

/**
 * Created by mandeep on 18/9/18.
 */

public class GuestActivity extends CenesActivity {

    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        new InstabugService().initiateInstabug(getApplication());

        fragmentManager = getSupportFragmentManager();
        replaceFragment(new GuestFragment(), null);
        fragmentManager.beginTransaction().commit();

        ActivityCompat.requestPermissions(GuestActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 101);
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

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //If permission granted
       System.out.print(
               "Permission Added");
    }

}
