package com.cenesbeta.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.cenesbeta.R;

/**
 * Created by rohan on 2/11/17.
 */
public class CenesEditText extends EditText {


    public CenesEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private int postion;

    public CenesEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CenesEditText(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CenesFont);
            String fontName = a.getString(R.styleable.CenesFont_fontName);
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        Log.d("Hey", "dispatchKeyEventPreIme(" + event + ")");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard
            InputMethodManager mgr = (InputMethodManager)

             getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
            // TODO: Hide your view as you do it in your activity
            clearFocus();
            setFocusableInTouchMode(false);
            setFocusable(false);
            setFocusableInTouchMode(true);
            setFocusable(true);

        }

        return true;
    }

}
