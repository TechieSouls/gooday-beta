package com.cenesbeta.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.os.ConfigurationCompat;
import android.telephony.TelephonyManager;
import android.util.TypedValue;

import com.cenesbeta.R;
import com.cenesbeta.bo.User;
import com.cenesbeta.materialcalendarview.CalendarDay;
import com.logentries.logger.AndroidLogger;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
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
    public static SimpleDateFormat hmmaa = new SimpleDateFormat("h:mm aa");
    public static SimpleDateFormat ampm = new SimpleDateFormat("aa");
    public static SimpleDateFormat MMMdd = new SimpleDateFormat("MMM dd");
    public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat EEEEMMMddhhmma = new SimpleDateFormat("EEEE, MMM dd, hh:mm a");
    public static SimpleDateFormat EEEEMMdd = new SimpleDateFormat("EEEE, MM dd"); //Saturday, 10 16
    public static SimpleDateFormat EEEEMMMdd = new SimpleDateFormat("EEEE, MMM dd"); //Saturday, Mar 16
    public static SimpleDateFormat EEEEMMMMdd = new SimpleDateFormat("EEEE, MMMM dd"); //Saturday, March 16
    public static SimpleDateFormat ddMMMYYYY = new SimpleDateFormat("dd MMM yyyy"); //7 Mar 1967
    public static SimpleDateFormat EEEE = new SimpleDateFormat("EEEE"); //THHURSDAY
    public static SimpleDateFormat ddMMM = new SimpleDateFormat("ddMMM");
    public static String[] facebookPermissions = {"public_profile", "email", "user_friends", "user_birthday"};


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

    public static int spToPx(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
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

    public static String getDeviceCountryCode() {
        //Locale locale = Locale.getDefault();
        Locale locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
        return locale.getCountry();
    }

    public static String getDeviceCountryCode(Context context) {
        String countryCode;

        // try to get country code from TelephonyManager service
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm != null) {
            // query first getSimCountryIso()
            countryCode = tm.getSimCountryIso();
            if (countryCode != null && countryCode.length() == 2)
                return countryCode;

            /*if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                // special case for CDMA Devices
                countryCode = getCDMACountryIso();
            } else {*/
                // for 3G devices (with SIM) query getNetworkCountryIso()
                countryCode = tm.getNetworkCountryIso();
            //}

            if (countryCode != null && countryCode.length() == 2)
                return countryCode;
        }

        // if network country not available (tablets maybe), get country code from Locale class
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            countryCode = context.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            countryCode = context.getResources().getConfiguration().locale.getCountry();
        }

        if (countryCode != null && countryCode.length() == 2)
            return  countryCode;

        // general fallback to "us"
        return "US";
    }


    public static Map<String, String> getMapFromJson(JSONObject jsonObject) {

        Map<String, String> response = new HashMap<>();

        try {
            Iterator<String> jsonKeys = jsonObject.keys();
            while (jsonKeys.hasNext()) {
                String key = jsonKeys.next();
                response.put(key, jsonObject.getString(key)) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  response;
    }

    public static boolean isValidLength(String field, int length) {
        if (field.length() >= length) {
            return true;
        }
        return false;
    }

    public static int daysBetween(Long pastDateInMillis, Long currentDateInMillis){

        Calendar currentCal = Calendar.getInstance();
        currentCal.setTimeInMillis(currentDateInMillis);

        Calendar pastCal = Calendar.getInstance();
        pastCal.setTimeInMillis(pastDateInMillis);

        int days = currentCal.get(Calendar.DAY_OF_MONTH) - pastCal.get(Calendar.DAY_OF_MONTH);

        return days;
    }

    public static int differenceInHours(Long pastDateInMillis, Long currentDateInMillis){

        Calendar currentCal = Calendar.getInstance();
        currentCal.setTimeInMillis(currentDateInMillis);

        Calendar pastCal = Calendar.getInstance();
        pastCal.setTimeInMillis(pastDateInMillis);

        int hours = currentCal.get(Calendar.HOUR_OF_DAY) - pastCal.get(Calendar.HOUR_OF_DAY);
        return hours;
    }

    public static String getDeviceManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        return capitalize(manufacturer);
    }

    public static String getDeviceModel() {
        String model = Build.MODEL;
        return capitalize(model);
    }

    public static String getDeviceVersion() {
        String version = Build.VERSION.RELEASE;
        return version;
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
