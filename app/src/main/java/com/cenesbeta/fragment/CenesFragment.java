package com.cenesbeta.fragment;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.cenesbeta.activity.CenesActivity;
import com.cenesbeta.activity.CenesBaseActivity;

/**
 * Created by rohan on 10/10/17.
 */

public abstract class CenesFragment extends Fragment {

    public CenesActivity getCenesActivity() {
        return (CenesActivity) getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((CenesActivity) getActivity()).hideSoftInput(getView());
        }
    }

    public void showAlert(String title, String message) {

        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", null).show();

    }

}
