package com.cenesbeta.Manager;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

/**
 * Created by puneet on 11/8/17.
 */

public interface DeviceManager {
    public void hideKeyBoard(EditText editText, AppCompatActivity appCompatActivity);
    public void showKeyBoard(EditText editText, AppCompatActivity appCompatActivity);
}
