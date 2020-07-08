package com.cenesbeta.materialcalendarview.decorators;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.cenesbeta.R;
import com.cenesbeta.materialcalendarview.CalendarDay;
import com.cenesbeta.materialcalendarview.DayViewDecorator;
import com.cenesbeta.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

import androidx.core.content.ContextCompat;

/**
 * Created by mandeep on 5/9/17.
 */

public class BackgroundDecorator implements DayViewDecorator {

    private HashSet<CalendarDay> dates;
    private Drawable drawable;
    boolean highlight;
    boolean imageBackground;
    Context context;

    public BackgroundDecorator(Context context, int resId, Collection<CalendarDay> dates, boolean highlight, boolean imageBackground) {
        if (context == null) {
            return;
        }
        this.drawable = ContextCompat.getDrawable(context, resId);
        this.imageBackground = imageBackground;
        this.dates = new HashSet<>(dates);
        this.context = context;
        this.highlight = highlight;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
        view.setSelectionDrawable(new ColorDrawable(Color.TRANSPARENT));
        //view.setDaysDisabled(true);
        if (highlight)
            view.addSpan(new ForegroundColorSpan(Color.WHITE));
        else
            view.addSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)));
    }
}
