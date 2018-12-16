package com.cenes.activity;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.cenes.R;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.leolin.shortcurtbadger.ShortcutBadger;
import com.cenes.service.ContactWatchService;


public class MainActivity extends CenesActivity {

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    User user;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cenesApplication = (CenesApplication)getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        user = userManager.getUser();

        if (user != null) {
            startActivity(new Intent(MainActivity.this, HomeScreenActivity.class));
            finish();
        }
    }
}
