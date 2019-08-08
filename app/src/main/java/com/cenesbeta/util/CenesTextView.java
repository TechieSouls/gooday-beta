package com.cenesbeta.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cenesbeta.R;

/**
 * Created by rohan on 2/11/17.
 */
public class CenesTextView extends TextView {

    public CenesTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private int postion;

    public CenesTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CenesTextView(Context context) {
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

    @Override
    public void setBackgroundResource(int resid) {
        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        int pr = getPaddingRight();
        int pb = getPaddingBottom();

        super.setBackgroundResource(resid);

        this.setPadding(pl, pt, pr, pb);
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }


}
