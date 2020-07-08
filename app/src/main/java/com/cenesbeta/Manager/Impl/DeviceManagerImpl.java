package com.cenesbeta.Manager.Impl;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.cenesbeta.Manager.DeviceManager;
import com.cenesbeta.application.CenesApplication;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by puneet on 11/8/17.
 */

public class DeviceManagerImpl implements DeviceManager {

    CenesApplication cenesApplication;

    public DeviceManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    @Override
    public void hideKeyBoard(EditText editText, AppCompatActivity appCompatActivity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) appCompatActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void showKeyBoard(EditText editText, AppCompatActivity appCompatActivity) {
        try {
            InputMethodManager imm = (InputMethodManager) appCompatActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {

        }
    }
}
