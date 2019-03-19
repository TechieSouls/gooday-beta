package com.cenes.api;

/**
 * Created by mandeep on 23/9/18.
 */

public class UserAPI {

    //POST
    public static String post_signupAPI= "/api/users/";
    public static String post_sendVerificationCodeAPI = "/api/guest/sendVerificationCode";
    public static String post_checkVerificationCodeAPI = "/api/guest/checkVerificationCode";
    public static String post_imageUplaodAPI = "/api/user/profile/upload";
    public static String post_deviceTokenSyncAPI = "/api/user/registerdevice";
    public static String post_syncPhoneContactsAPI = "/api/syncContacts";
    public static String post_changePasswordAPI = "/api/user/changePassword";
    public static String post_saveHolidayCalendar = "/api/user/holidayCalendar";
    public static String post_update_profile_data = "/api/user/update/";

    //GET
    public static String get_holidayCalendarByUserId = "/api/user/holidayCalendarByUserId";
    public static String get_forget_password_api = "/auth/forgetPassword";

}
