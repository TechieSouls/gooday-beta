package com.cenes.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class RoundedDrawable extends BitmapDrawable {
    private Path p = new Path();

    public RoundedDrawable(Bitmap b) {
        super(b);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        p.rewind();
        p.addCircle(bounds.width() / 2,
                bounds.height() / 2,
                Math.min(bounds.width(), bounds.height()) / 2,
                Path.Direction.CW);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.clipPath(p);
        super.draw(canvas);
    }
}

