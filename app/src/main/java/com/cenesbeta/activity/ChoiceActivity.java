package com.cenesbeta.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.cenesbeta.Manager.AlertManager;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.util.CenesUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

/**
 * Created by puneet on 18/8/17.
 */

public class ChoiceActivity extends CenesActivity {

    private CallbackManager callbackManager;
    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    /*Button buttonEmailSignUp;
    LoginButton buttonJoinFB;
    TextView buttonLogin;*/

    Button btSignupMobile, btAlreadyLogin;
    ProgressDialog progressDialog;
    CenesApplication cenesApplication;
    CoreManager coreManager;
    ApiManager apiManager;
    UserManager userManager;
    AlertManager alertManager;
    UrlManager urlManager;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.signup_options);

        init();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().commit();

    }

    public void replaceFragment(Fragment fragment, String tag) {

        try {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (tag != null) {
                fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
                fragmentTransaction.addToBackStack(tag);
            } else {
                fragmentTransaction.replace(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*public void init() {
        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        apiManager = coreManager.getApiManager();
        userManager = coreManager.getUserManager();
        urlManager = coreManager.getUrlManager();

        buttonEmailSignUp = (Button) findViewById(R.id.bt_email_join);
        buttonJoinFB = (LoginButton) findViewById(R.id.bt_fb_join);
        buttonJoinFB.setReadPermissions(Arrays.asList("public_profile, email,user_events, user_friends"));
        buttonLogin = (TextView) findViewById(R.id.bt_login);
        buttonEmailSignUp.setOnClickListener(onClickListener);
        buttonLogin.setOnClickListener(onClickListener);
        buttonJoinFB.setOnClickListener(onClickListener);

        findViewById(R.id.bt_fb).setOnClickListener(onClickListener);

    }*/

    public void init() {
        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        apiManager = coreManager.getApiManager();
        userManager = coreManager.getUserManager();
        urlManager = coreManager.getUrlManager();

        btSignupMobile = (Button) findViewById(R.id.bt_signup_mobile);
        btAlreadyLogin = (Button) findViewById(R.id.bt_already_login);

        btSignupMobile.setOnClickListener(onClickListener);
        btAlreadyLogin.setOnClickListener(onClickListener);
        btAlreadyLogin.setText(Html.fromHtml("Already Have an Account? <b>Log In</b>"));
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.bt_signup_mobile:

                    break;

                case R.id.bt_already_login:
                    break;
            }
        }
    };

    /*View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.bt_fb_join:
                    buttonJoinFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.e("Fb status : ", "Facebook Id : " + loginResult.getAccessToken().getUserId() + ",Access Token : " + loginResult.getAccessToken().getToken());
                            user = new User();
                            user.setAuthType("facebook");
                            user.setFacebookID(loginResult.getAccessToken().getUserId());
                            user.setFacebookAuthToken(loginResult.getAccessToken().getToken());
                            user.setApiUrl(urlManager.getApiUrl("dev"));
                            new CenesFacebookLogin().execute();
                        }

                        @Override
                        public void onCancel() {
                            Log.e("Cancelled", "User cancelled dialog");
                        }

                        @Override
                        public void onError(FacebookException e) {
                            Log.e("Error : ", e.getMessage());
                        }

                    });
                    break;
                case R.id.bt_fb:
                    buttonJoinFB.performClick();
                    break;
                case R.id.bt_email_join:
                    startActivity(new Intent(ChoiceActivity.this, SignUpActivity.class));
                    finish();
                    break;
                case R.id.bt_login:
                    startActivity(new Intent(ChoiceActivity.this, SignInActivity.class));
                    finish();
                    break;
            }
        }
    };*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    class CenesFacebookLogin extends AsyncTask<Object, Object, Object> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(ChoiceActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading ....");
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub

            JSONObject jsonObject = apiManager.signUpByEmail(user, ChoiceActivity.this);
            try {
                if (jsonObject != null && jsonObject.has("errorCode") && jsonObject.getInt("errorCode") == 0) {
                    User user = new User();
                    if (jsonObject.has(user.TOKEN)) {
                        user.setAuthToken(jsonObject.getString(user.TOKEN));
                    }
                    if (jsonObject.has(user.FACEBOOKID)) {
                        user.setFacebookID(jsonObject.getString(user.FACEBOOKID));
                    }
                    if (jsonObject.has(user.FACEBOOKAUTHTOKEN)) {
                        user.setFacebookAuthToken(jsonObject.getString(user.FACEBOOKAUTHTOKEN));
                    }
                    user.setApiUrl(urlManager.getApiUrl("dev"));
                    apiManager.syncFacebookEvents(user, ChoiceActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);

//            {"createdAt":1503071853185,"updateAt":1503071853185,"errorCode":0,"errorDetail":null,"userId":5,"username":"abc1503071853181","email":"abc@abc.com","password":"cc4e7ba92ea0b1fc56e6ac67f682f3ea","facebookID":null,"authType":"email","facebookAuthToken":null,"name":"abc","photo":null,"token":"1503158253181eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmMxNTAzMDcxODUzMTgxIn0.HBBidMMaE3aiH7KLb3hiZuSNxIUElKcd1OFyRM0TTIg7BG1d9r7qYLntupwtqyEQCdsd8m1ZkNGs9oW9zXXw7g"}
            if (object != null) {
                JSONObject jsonObject = (JSONObject) object;
                if (jsonObject.has("errorCode")) {
                    try {
                        if (jsonObject.getInt("errorCode") == 0) {
                            if (jsonObject.has(user.USERID)) {
                                user.setUserId(jsonObject.getInt(user.USERID));
                            }
                            if (jsonObject.has(user.NAME)) {
                                user.setName(jsonObject.getString(user.NAME));
                            }
                            if (jsonObject.has(user.USERNAME)) {
                                user.setUsername(jsonObject.getString(user.USERNAME));
                            }
                            if (jsonObject.has(user.EMAIL)) {
                                user.setEmail(jsonObject.getString(user.EMAIL));
                            }
                            if (jsonObject.has("gender")) {
                                user.setGender(jsonObject.getString("gender"));
                            }
                            if (jsonObject.has(user.TOKEN)) {
                                user.setAuthToken(jsonObject.getString(user.TOKEN));
                            }
                            if (jsonObject.has(user.FACEBOOKID)) {
                                user.setFacebookID(jsonObject.getString(user.FACEBOOKID));
                            }
                            if (jsonObject.has(user.FACEBOOKAUTHTOKEN)) {
                                user.setFacebookAuthToken(jsonObject.getString(user.FACEBOOKAUTHTOKEN));
                            }
                            if (jsonObject.has(user.PHOTO)) {
                                user.setPicture(jsonObject.getString(user.PHOTO));
                            }
                            userManager.addUser(user);
                            progressDialog.cancel();

                            if (token != null && token.length() > 0) {
                                JSONObject registerDeviceObj = new JSONObject();
                                registerDeviceObj.put("deviceToken",token);
                                registerDeviceObj.put("deviceType","android");
                                registerDeviceObj.put("userId",user.getUserId());
                                new DeviceTokenSync().execute(registerDeviceObj);
                            }

                            startActivity(new Intent(ChoiceActivity.this, CompleteYourProfileActivity.class));
                            finish();

                        } else {
                            progressDialog.cancel();
                            if (jsonObject.has("errorDetail")) {
                                alertManager.getAlert(ChoiceActivity.this, jsonObject.getString("errorDetail"), "Error", null, false, "OK");
                            } else {
                                alertManager.getAlert(ChoiceActivity.this, "Some thing is going wrong", "Error", null, false, "OK");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.cancel();
                    alertManager.getAlert(ChoiceActivity.this, "Server Error", "Error", null, false, "OK");
                }

            } else {
                showRequestTimeoutDialog();
            }
        }
    }

    String token = "";

    @Override
    public void onResume() {
        super.onResume();

        if(FirebaseInstanceId.getInstance().getToken() != null) {
            token = FirebaseInstanceId.getInstance().getToken();
        } else {
            token = getSharedPreferences("CenesPrefs", MODE_PRIVATE).getString("FcmToken", "");
        }

        CenesUtils.logEntries(null, "ChoiceActivity FCM Token: " + token, ChoiceActivity.this);
    }

    class DeviceTokenSync extends AsyncTask<JSONObject,Object,Object>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(JSONObject... objects) {
            JSONObject deviceTokenInfo = objects[0];
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            apiManager.syncDeviceToekn(user,deviceTokenInfo,ChoiceActivity.this);
            return null;
        }
    }
}
