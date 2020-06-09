package com.cenesbeta.service;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.cenesbeta.util.CenesUtils;

public class ResizeAnimation  extends Animation {
    final int startWidth;
    final int targetWidth;
    View view;

    public ResizeAnimation(View view, int targetWidth) {
        this.view = view;
        this.targetWidth = targetWidth;
        startWidth = view.getWidth();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth = (int) (0 + (targetWidth - 0) * interpolatedTime);
        System.out.println("StartWidth : "+startWidth +"  -  "+ newWidth);
        view.getLayoutParams().width = newWidth;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
