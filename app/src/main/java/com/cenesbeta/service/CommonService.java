package com.cenesbeta.service;

import android.app.DatePickerDialog;

import java.util.Calendar;

public class CommonService {

    public static class DatePicker {

        // you may separate this or combined to caller class.
        public interface CallbackResponse {
            void processFinish(Long dateInMillis);
        }
        public CallbackResponse delegate = null;

        public DatePicker(CallbackResponse delegate) {
            this.delegate = delegate;
        }

        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                Calendar yesCalendar = Calendar.getInstance();
                yesCalendar.add(Calendar.DAY_OF_MONTH, dayOfMonth);
                yesCalendar.add(Calendar.MONTH, month);
                yesCalendar.add(Calendar.YEAR, year);

                delegate.processFinish(yesCalendar.getTimeInMillis());
            }
        };

    }

}
