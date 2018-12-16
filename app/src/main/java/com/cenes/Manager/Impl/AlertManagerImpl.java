package com.cenes.Manager.Impl;

import com.cenes.Manager.AlertManager;
import com.cenes.application.CenesApplication;
import android.content.Context;
import android.content.Intent;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by puneet on 11/8/17.
 */

public class AlertManagerImpl implements AlertManager {

    CenesApplication cenesApplication;
    private boolean isPopUp = true;
    Dialog dialog;

    public AlertManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    @Override
    public void getAlert(final Context context, String message, String title,
                         final Intent intent, final boolean isFinish, String buttonString) {
        try {
            if (isPopUp) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                if(title != null) {
                    alertDialogBuilder.setTitle(title);
                }
                alertDialogBuilder.setMessage(message);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setNeutralButton(buttonString,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                if (intent != null) {
                                    ((AppCompatActivity) context).startActivity(intent);
                                }
                                dialog.cancel();
                                isPopUp = true;
                                if (isFinish) {
                                    ((AppCompatActivity) context).finish();
                                }

                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                isPopUp = false;

            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
}
