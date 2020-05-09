package com.cenesbeta.cardstackview.internal;

import android.view.animation.Interpolator;

import com.cenesbeta.cardstackview.Direction;

public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
