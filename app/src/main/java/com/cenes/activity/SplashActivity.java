package com.cenes.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.cenes.R;
import com.cenes.application.CenesApplication;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.observer.ContactObserver;
import com.cenes.service.ContactWatchService;

import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Window;

/**
 * Created by puneet on 11/8/17.
 */

public class SplashActivity extends CenesActivity{

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;

    ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(userManager.isUserExist()){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                } else{
                    startActivity(new Intent(SplashActivity.this,WelcomeActivity.class));
                    finish();
                }
            }

        }, 500);

    }
}
