package com.cenes.backendManager;

import com.cenes.Manager.Impl.UrlManagerImpl;
import com.cenes.Manager.JsonParsing;
import com.cenes.api.UserAPI;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;

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

    public JSONObject uploadProfileImage(User user, File file) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPostMultipart(UrlManagerImpl.prodAPIUrl+ UserAPI.post_imageUplaodAPI, user, file);
    }

    public JSONObject syncDeviceToken(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_deviceTokenSyncAPI, postData,userToken);
    }

    public JSONObject syncDevicePhone(JSONObject postData, String userToken) {
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+UserAPI.post_syncPhoneContactsAPI, postData,userToken);
    }
}
