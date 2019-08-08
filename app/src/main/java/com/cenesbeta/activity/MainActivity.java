package com.cenesbeta.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cenesbeta.R;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.service.InstabugService;


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

        new InstabugService().initiateInstabug(getCenesApplication());

        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        user = userManager.getUser();

        if (user != null) {
            startActivity(new Intent(MainActivity.this, CenesBaseActivity.class));
            finish();
        }
    }
}
