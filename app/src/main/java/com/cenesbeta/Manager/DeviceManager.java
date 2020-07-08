package com.cenesbeta.Manager;

import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Mandeep on 11/8/17.
 */

public interface DeviceManager {
    public void hideKeyBoard(EditText editText, AppCompatActivity appCompatActivity);
    public void showKeyBoard(EditText editText, AppCompatActivity appCompatActivity);
}
