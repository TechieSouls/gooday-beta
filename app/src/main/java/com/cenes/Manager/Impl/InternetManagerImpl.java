package com.cenes.Manager.Impl;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cenes.Manager.InternetManager;
import com.cenes.application.CenesApplication;

/**
 * Created by puneet on 11/8/17.
 */

public class InternetManagerImpl implements InternetManager {

    CenesApplication cenesApplication;
    public InternetManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    @Override
    public boolean isInternetConnection(AppCompatActivity appCompatActivity){
        ConnectivityManager connectivityManager = (ConnectivityManager) appCompatActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
