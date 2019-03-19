package com.cenes.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cenes.application.CenesApplication;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

/**
 * Created by puneet on 11/8/17.
 */

public class CenesActivity extends AppCompatActivity {

    public SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getSharedPreferences("CenesPrefs", MODE_PRIVATE);
    }

    public CenesApplication getCenesApplication() {
        return (CenesApplication) getApplication();
    }

    public void hideSoftInput(View v) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showRequestTimeoutDialog() {
        getCenesApplication().getCoreManager().getAlertManager().getAlert(this, "Request Timed Out.", "Network Error", null, false, "OK");
    }

}
