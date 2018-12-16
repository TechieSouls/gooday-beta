package com.cenes.materialcalendarview.decorators;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.cenes.materialcalendarview.CalendarDay;
import com.cenes.materialcalendarview.DayViewDecorator;
import com.cenes.materialcalendarview.DayViewFacade;
import com.cenes.materialcalendarview.MaterialCalendarView;

import java.util.Date;

public class OneDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    private Drawable drawable;

    public OneDayDecorator(CalendarDay date, Context context, int resId) {
        this.date = date;
        drawable = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
        view.setBackgroundDrawable(drawable);
        view.setSelectionDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
     */
    public void setDate(Date date) {
        this.date = CalendarDay.from(date);
    }
}
