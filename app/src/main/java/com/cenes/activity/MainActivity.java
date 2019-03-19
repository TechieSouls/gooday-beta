package com.cenes.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cenes.R;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.service.InstabugService;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;


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
