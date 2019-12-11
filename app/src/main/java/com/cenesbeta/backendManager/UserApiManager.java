package com.cenesbeta.backendManager;

import android.support.v7.app.AppCompatActivity;

import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.JsonParsing;
import com.cenesbeta.api.GatheringAPI;
import com.cenesbeta.api.UserAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by mandeep on 23/9/18.
 */

public class UserApiManager {
    CenesApplication cenesApplication;

    public UserApiManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public JSONObject sentVerificationCode(JSONObject postData) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+ UserAPI.post_sendVerificationCodeAPI, postData, null);
    }

    public JSONObject checkVerificationCode(JSONObject postData) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+ UserAPI.post_checkVerificationCodeAPI, postData, null);
    }

    public JSONObject emailSignup(JSONObject postData) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+ UserAPI.post_signupAPI, postData, null);
    }

    public JSONObject emailPostUserDetails(JSONObject postData, String token) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+ UserAPI.post_userdetails, postData, token);
    }

    public JSONObject emailSignupStep1(JSONObject postData) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+ UserAPI.post_signup_step1_API, postData, null);
    }

    public JSONObject emailSignupSignupStep2(JSONObject postData) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+ UserAPI.post_signup_step2_API, postData, null);
    }


    public JSONObject uploadProfileImage(User user, File file) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPostMultipart(UrlManagerImpl.prodImageApiDomain+ UserAPI.post_imageUplaodAPI, user, file);
    }

    public JSONObject syncDeviceToken(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_deviceTokenSyncAPI, postData,userToken);
    }

    public JSONObject syncDevicePhone(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_syncPhoneContactsAPI, postData,userToken);
    }

    public JSONObject changePassword(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_changePasswordAPI, postData,userToken);
    }

    public JSONObject updatePassword(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_update_password, postData,userToken);
    }


    public JSONObject syncHolidayCalendar(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_saveHolidayCalendar, postData,userToken);
    }

    public JSONObject holidayCalendarByUserId(String queryStr, String authToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ UserAPI.get_holidayCalendarByUserId+"?"+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject updateUserInfo(JSONObject postData, String userToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ UserAPI.post_update_profile_data;
            return jsonParsing.httpPost(apiUrl,postData,userToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject forgotPassword(String queryStr) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+UserAPI.get_forget_password_email_api+"?"+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject sendForgetPasswordEmail(String queryStr) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+UserAPI.get_forget_password_send_email+"?"+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject emailLogin(JSONObject postData) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ UserAPI.post_login_API;
            return jsonParsing.httpPost(apiUrl,postData,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject deleteAccountRequest(JSONObject postData, String userToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ UserAPI.post_delete_user_by_phone_password;
            return jsonParsing.httpPost(apiUrl,postData,userToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
