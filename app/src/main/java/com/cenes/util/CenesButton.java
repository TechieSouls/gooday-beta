package com.cenes.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.cenes.R;

/**
 * Created by rohan on 2/11/17.
 */
public class CenesButton extends Button {

    public CenesButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private Integer mPostion;

    public Integer getPosition() {
        return mPostion;
    }

    public void setPosition(Integer mPostion) {
        this.mPostion = mPostion;
    }

    public CenesButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CenesButton(Context context) {
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
}
