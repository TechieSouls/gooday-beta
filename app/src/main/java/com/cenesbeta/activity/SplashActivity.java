package com.cenesbeta.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.R;
import com.cenesbeta.api.CenesCommonAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Splash;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.SplashManagerImpl;
import com.cenesbeta.database.manager.SplashManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.dto.HomeScreenDto;
import com.cenesbeta.util.CenesConstants;
import com.google.gson.Gson;

import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import org.json.JSONObject;


/**
 * Created by puneet on 11/8/17.
 */

public class SplashActivity extends CenesActivity{

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    SplashManager splashManager;
    ImageView splashImageView;
    String imageURL = "";
    int duration = 3000;

    ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        splashImageView = (ImageView) findViewById(R.id.splash_image);

        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        splashManager = new SplashManagerImpl(getCenesApplication());

        final Splash localSplash = splashManager.getSplash();

        if(localSplash != null) {
            Glide.with(getApplicationContext()).load(localSplash.getSplashImage()).into(splashImageView);
            splashImageView.setVisibility(View.VISIBLE);

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
            }, duration);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
                asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ CenesCommonAPI.get_splash_screen_api);
                new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject response) {

                        try {
                            boolean success = response.getBoolean("success");
                            if (success == true) {
                                JSONObject data = response.getJSONObject("data");
                                if (data.has("enabled")) {
                                    boolean activeStatus = data.getBoolean("enabled");
                                    System.out.println("Splash_1");
                                    if(activeStatus == true) {
                                        System.out.println("Splash_2");

                                        imageURL = CenesConstants.imageDomain + "/" + data.getString("splashImage");
                                        System.out.println(imageURL);

                                        if(localSplash == null) {
                                            Glide.with(getApplicationContext()).load(imageURL).into(splashImageView);
                                            splashImageView.setVisibility(View.VISIBLE);

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
                                            }, duration);
                                        }

                                        splashManager.deleteSplash();
                                        Splash serverSplash = new Splash();
                                        serverSplash.setSplashImage(imageURL);
                                        splashManager.addSplash(serverSplash);
                                    }
                                } else {

                                    if (localSplash == null) {
                                        if(userManager.isUserExist()){
                                            startActivity(new Intent(SplashActivity.this,MainActivity.class));
                                            finish();
                                        } else{
                                            startActivity(new Intent(SplashActivity.this,WelcomeActivity.class));
                                            finish();
                                        }
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).execute(asyncTaskDto);


            }

        }, 0);

    }
}
