package com.cenes.materialcalendarview.decorators;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

import com.cenes.materialcalendarview.CalendarDay;
import com.cenes.materialcalendarview.DayViewDecorator;
import com.cenes.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by mandeep on 5/9/17.
 */

public class BackgroundDecorator implements DayViewDecorator {

    private HashSet<CalendarDay> dates;
    private Drawable drawable;
    boolean highlight;
    Context context;

    public BackgroundDecorator(Context context, int resId, Collection<CalendarDay> dates, boolean highlight) {
        drawable = ContextCompat.getDrawable(context, resId);
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
            view.addSpan(new ForegroundColorSpan(Color.GRAY));
    }
}