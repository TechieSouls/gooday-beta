package com.cenesbeta.api;

/**
 * Created by mandeep on 23/9/18.
 */

public class UserAPI {

    //POST
    public static String post_login_API = "/auth/user/authenticate";
    public static String post_signupAPI= "/api/users/";
    public static String post_signup_step1_API= "/api/users/signupstep1";
    public static String post_signup_step2_API= "/api/users/signupstep2";
    public static String post_sendVerificationCodeAPI = "/api/guest/sendVerificationCode";
    public static String post_checkVerificationCodeAPI = "/api/guest/checkVerificationCode";
    public static String post_imageUplaodAPI = "/api/user/profile/upload";
    public static String post_upload_profile_pic_v2 = "/api/user/profile/upload/v2";
    public static String post_deviceTokenSyncAPI = "/api/user/registerdevice";
    public static String post_syncPhoneContactsAPI = "/api/syncContacts";
    public static String post_changePasswordAPI = "/api/user/changePassword";
    public static String post_saveHolidayCalendar = "/api/user/holidayCalendar";
    public static String post_update_profile_data = "/api/user/update/";
    public static String post_userdetails = "/api/user/updateDetails";
    public static String post_update_password = "/auth/updatePassword";
    public static String post_delete_user_by_phone_password = "/api/deleteUserByPhonePassword";
    public static String post_validate_password = "/api/user/validatePassword";
    public static String post_sync_google_calendar = "/api/google/events/android/v2";
    public static String post_sync_outlook_calendar = " /api/outlook/events/android/v2";

    //GET
    public static String get_user_ip = "https://api6.ipify.org/?format=json";
    public static String get_country_by_ip_address = "/auth/getCountryByIpAddress";
    public static String get_user_stats = "/api/user/userStatsByUserId";
    public static String get_user_sync_details = "/api/user/syncDetails";

    public static String get_holidayCalendarByUserId = "/api/user/holidayCalendarByUserId";
    public static String get_forget_password_api = "/auth/forgetPassword";

    public static String get_forget_password_email_api = "/auth/forgetPassword/v2";
    public static String get_forget_password_send_email = "/auth/forgetPassword/v2/sendEmail";

    //DELETE
    public static String delete_sync_token = "api/user/deleteSyncBySyncId";



}
