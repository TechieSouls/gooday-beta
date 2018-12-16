package com.cenes.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;

import com.cenes.R;
import com.cenes.bo.User;
import com.cenes.materialcalendarview.CalendarDay;
import com.logentries.logger.AndroidLogger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mandeep on 9/9/17.
 */

public class CenesUtils {

    public static SimpleDateFormat MMMddyyyy = new SimpleDateFormat("MMM dd,yyyy");
    public static SimpleDateFormat MMMMddy = new SimpleDateFormat("MMMM dd");
    public static SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm"); //24 Hour Format
    public static SimpleDateFormat hhmm = new SimpleDateFormat("hh:mm");
    public static SimpleDateFormat hhmmaa = new SimpleDateFormat("hh:mm aa");
    public static SimpleDateFormat ampm = new SimpleDateFormat("aa");
    public static SimpleDateFormat MMMdd = new SimpleDateFormat("MMM dd");
    public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat EEEEMMMddhhmma = new SimpleDateFormat("EEEE, MMM dd, hh:mm a");
    public static SimpleDateFormat EEEEMMdd = new SimpleDateFormat("EEEE, MM dd"); //Saturday, 10 16
    public static SimpleDateFormat EEEEMMMdd = new SimpleDateFormat("EEEE, MMM dd"); //Saturday, Mar 16
    public static SimpleDateFormat EEEEMMMMdd = new SimpleDateFormat("EEEE, MMMM dd"); //Saturday, March 16



    public static ProgressDialog showProcessing(Context context, String message) {
        ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public static void hideProcessing(Context context,ProgressDialog mProgressDialog){
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public static Set<CalendarDay> getDrawableMonthDateList(Calendar currentMonth) {
        Set<CalendarDay> calendarDays = new HashSet<>();
        Calendar startDay = Calendar.getInstance();
        startDay.set(Calendar.YEAR,currentMonth.get(Calendar.YEAR));
        startDay.set(Calendar.MONTH,currentMonth.get(Calendar.MONTH));
        startDay.set(Calendar.DAY_OF_MONTH,1);

        int lastDay = startDay.getActualMaximum(Calendar.DAY_OF_MONTH);
        int startDayOfMonth = startDay.get(Calendar.DAY_OF_MONTH);

        while (startDayOfMonth <= lastDay) {
            CalendarDay calDay = new CalendarDay(currentMonth.get(Calendar.YEAR),currentMonth.get(Calendar.MONTH),startDayOfMonth);
            calendarDays.add(calDay);
            startDayOfMonth ++;
        }
        return calendarDays;
    }
    public static String getDateSuffix( int day) {
        switch (day) {
            case 1: case 21: case 31:
                return ("st");

            case 2: case 22:
                return ("nd");

            case 3: case 23:
                return ("rd");

            default:
                return ("th");
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void logEntries(User user, String message, Context context) {
        if (context.getResources().getBoolean(R.bool.loggingOn)) {
            try {
                AndroidLogger logger = AndroidLogger.createInstance(context, false, true, false, null, 0, "8ce9f484-0b22-4803-9b7d-6f244b01fbf3", true);
                String info = "";
                if (user != null) {
                    info = "User Id : "+user.getUserId()+", Name : "+user.getName()+" ,";
                }
                logger.log(info+" Message : "+ message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Boolean isEmpty(String text) {
        if (text == null || text.equals("null") || text.length() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
