package com.cenesbeta.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cenesbeta.activity.CenesActivity;
import com.cenesbeta.activity.CenesBaseActivity;

import androidx.fragment.app.Fragment;

/**
 * Created by rohan on 10/10/17.
 */

public abstract class CenesFragment extends Fragment {

    public CenesActivity getCenesActivity() {
        return (CenesActivity) getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((CenesActivity) getActivity()).hideSoftInput(getView());
        }
    }

    public void showAlert(String title, String message) {

        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", null).show();

    }

    public void rotate(float degree, ImageView imageView) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(500);
        rotateAnim.setFillAfter(true);
        imageView.startAnimation(rotateAnim);
    }

    public void hideKeyboard() {
        try {
            if (getActivity() != null && getActivity().getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
            } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void hideKeyboardAndClearFocus(EditText editText) {
        try {
            InputMethodManager inputManager = (InputMethodManager) ((CenesActivity)getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e){
            e.printStackTrace();
        }

        editText.clearFocus();
        editText.setFocusableInTouchMode(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
    }

    public View.OnTouchListener layoutTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hideKeyboard();
            return true;
        }
    };
}
