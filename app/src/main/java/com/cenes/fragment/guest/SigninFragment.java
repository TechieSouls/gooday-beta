package com.cenes.fragment.guest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cenes.Manager.AlertManager;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.DeviceManager;
import com.cenes.Manager.InternetManager;
import com.cenes.Manager.UrlManager;
import com.cenes.Manager.ValidationManager;
import com.cenes.R;
import com.cenes.activity.CenesBaseActivity;
import com.cenes.activity.GuestActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.SignInActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.fragment.CenesFragment;
import com.cenes.fragment.dashboard.HomeFragment;
import com.cenes.service.InstabugService;
import com.cenes.util.CenesUtils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by mandeep on 6/10/18.
 */
public class SigninFragment  extends CenesFragment {

    public final static String TAG = "SigninFragment";

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    ValidationManager validationManager;
    InternetManager internetManager;
    UrlManager urlManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    User user = null;

    LinearLayout llSigningBackBtn;
    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    TextView tvForgotpasswordLink;
    ImageView ivBugReport;
    //Button buttonFbLogin;
    //LoginButton facebookLoginBtn;
    ImageView signinBackArrow;

    String email, password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Obtain the shared Tracker instance.
        CenesApplication application = (CenesApplication) getActivity().getApplication();

        View v = inflater.inflate(R.layout.fragment_signin, container, false);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //callbackManager = CallbackManager.Factory.create();

        initilizeComponents(v);
        init();

        return v;
    }

    public void initilizeComponents(View view) {

        llSigningBackBtn = (LinearLayout) view.findViewById(R.id.ll_signin_back);

        editTextEmail = (EditText) view.findViewById(R.id.et_email);
        editTextPassword = (EditText) view.findViewById(R.id.et_password);
        tvForgotpasswordLink = (TextView) view.findViewById(R.id.tv_forget_password);

        buttonLogin = (Button) view.findViewById(R.id.bt_login);

        ivBugReport = (ImageView) view.findViewById(R.id.iv_bug_report);

        buttonLogin.setOnClickListener(onClickListener);
        tvForgotpasswordLink.setOnClickListener(onClickListener);

        ivBugReport.setOnClickListener(onClickListener);
        //signinBackArrow = (ImageView) findViewById(R.id.signin_back_arrow);
        //signinBackArrow.setOnClickListener(onClickListener);

        //buttonFbLogin = (Button) findViewById(R.id.bt_fb_login);
        //buttonFbLogin.setOnClickListener(onClickListener);

        //facebookLoginBtn = (LoginButton) findViewById(R.id.bt_fb_join);
        //facebookLoginBtn.setReadPermissions(Arrays.asList("public_profile, email,user_events, user_friends"));
        //facebookLoginBtn.setOnClickListener(onClickListener);

    }


    public void init() {
        //----------------------------------
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        alertManager = coreManager.getAlertManager();
        validationManager = coreManager.getValidatioManager();
        internetManager = coreManager.getInternetManager();
        urlManager = coreManager.getUrlManager();
        deviceManager = coreManager.getDeviceManager();
        apiManager = coreManager.getApiManager();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.bt_login:
                    startSignInProcess();
                    break;
                case R.id.ll_signin_back:
                    startActivity(new Intent((SignInActivity)getActivity(),GuestActivity.class));
                    getActivity().finish();
                    break;
                case R.id.iv_bug_report:
                    new InstabugService().invokeBugReporting();
                    break;
                /*case R.id.bt_fb_join:
                    facebookLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.e("Fb status : ", "Facebook Id : " + loginResult.getAccessToken().getUserId() + ",Access Token : " + loginResult.getAccessToken().getToken());
                            user = new User();
                            user.setAuthType("facebook");
                            user.setFacebookID(loginResult.getAccessToken().getUserId());
                            user.setFacebookAuthToken(loginResult.getAccessToken().getToken());
                            user.setApiUrl(urlManager.getApiUrl("dev"));
                            new CenesFacebookLogin().execute(user);
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
                case R.id.bt_fb_login:
                    facebookLoginBtn.performClick();
                    break;*/
                case R.id.tv_forget_password:
                    ((SignInActivity) getActivity()).replaceFragment(new ForgotPasswordFragment(), "ForgotPasswordFragment");
                    break;
            }

        }
    };

    /* @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         callbackManager.onActivityResult(requestCode, resultCode, data);
     }*/
    public void startSignInProcess() {
        deviceManager.hideKeyBoard(editTextEmail, (SignInActivity)getActivity());
        user = null;
        user = new User();
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        validationProcess();
    }

    public void validationProcess() {

        boolean isValid = true;

        if (isValid && validationManager.isFieldBlank(email) && validationManager.isFieldBlank(password)) {
            alertManager.getAlert((SignInActivity)getActivity(), "Please Enter the Email", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid && validationManager.isFieldBlank(email)) {
            alertManager.getAlert((SignInActivity)getActivity(), "Please Enter the Email", "Info", null, false, "OK");
            isValid = false;
        }

        if (isValid && validationManager.isFieldBlank(password)) {
            alertManager.getAlert((SignInActivity)getActivity(), "Please Enter the Password", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid) {
            networkProcess();
        }
    }

    public void networkProcess() {
        if (internetManager.isInternetConnection((SignInActivity)getActivity())) {
            user.setEmail(email);
            user.setPassword(password);
            user.setApiUrl(urlManager.getApiUrl(email));

            new SignInProcess().execute();
        } else {
            alertManager.getAlert((SignInActivity)getActivity(), "No Internet Connection!", "Info", null, false, "OK");
        }
    }


    public class SignInProcess extends AsyncTask<Object, Object, Object> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog((SignInActivity)getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub

            JSONObject jsonObject = apiManager.logIn(user, (SignInActivity)getActivity());

            return jsonObject;
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
            progressDialog.cancel();
            progressDialog.dismiss();
            progressDialog = null;

//            {"createdAt":1503071853185,"updateAt":1503071853185,"errorCode":0,"errorDetail":null,"userId":5,"username":"abc1503071853181","email":"abc@abc.com","password":"cc4e7ba92ea0b1fc56e6ac67f682f3ea","facebookID":null,"authType":"email","facebookAuthToken":null,"name":"abc","photo":null,"token":"1503158253181eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmMxNTAzMDcxODUzMTgxIn0.HBBidMMaE3aiH7KLb3hiZuSNxIUElKcd1OFyRM0TTIg7BG1d9r7qYLntupwtqyEQCdsd8m1ZkNGs9oW9zXXw7g"}
            if (object != null) {
                JSONObject jsonObject = (JSONObject) object;
                if (jsonObject.has("errorCode")) {
                    try {
                        if (jsonObject.getInt("errorCode") == 0) {
                            User user = new User();
                            Boolean isUserExistsInDb = true;
                            if (jsonObject.has(user.USERID)) {
                                user = userManager.findUserByUserId(jsonObject.getLong(user.USERID));
                                if (user == null) {
                                    user = new User();
                                    isUserExistsInDb = false;
                                }
                                user.setUserId(jsonObject.getInt(user.USERID));
                            }
                            if (jsonObject.has(user.USERNAME)) {
                                user.setUsername(jsonObject.getString(user.USERNAME));
                            }
                            if (jsonObject.has(user.TOKEN)) {
                                user.setAuthToken(jsonObject.getString(user.TOKEN));
                            }
                            if (jsonObject.has(user.PHONE)) {
                                user.setPhone(jsonObject.getString(user.PHONE));
                            }
                            if (jsonObject.has(user.EMAIL)) {
                                user.setEmail(jsonObject.getString(user.EMAIL));
                            }
                            if (jsonObject.has(user.PHOTO)) {
                                user.setPicture(jsonObject.getString(user.PHOTO));
                            }
                            if (jsonObject.has(user.NAME)) {
                                user.setName(jsonObject.getString(user.NAME));
                            }

                            if (jsonObject.has("birthDate") && jsonObject.getString("birthDate") != "null") {
                                user.setBirthDate(Long.valueOf(jsonObject.getString("birthDate")));
                            }
                            if (jsonObject.has("gender") && !CenesUtils.isEmpty(jsonObject.getString("gender")) ) {
                                user.setGender(jsonObject.getString("gender"));
                            }

                            if (isUserExistsInDb) {
                                userManager.updateUser(user);
                            } else {
                                userManager.addUser(user);
                            }

                            if (token != null) {
                                JSONObject registerDeviceObj = new JSONObject();
                                registerDeviceObj.put("deviceToken",token);
                                registerDeviceObj.put("deviceType","android");
                                registerDeviceObj.put("userId",user.getUserId());
                                registerDeviceObj.put("model", CenesUtils.getDeviceModel());
                                registerDeviceObj.put("manufacturer", CenesUtils.getDeviceManufacturer());
                                registerDeviceObj.put("version", CenesUtils.getDeviceVersion());
                                new DeviceTokenSync().execute(registerDeviceObj);
                            }

                            //startActivity(new Intent((SignInActivity)getActivity(), HomeScreenActivity.class));
                            //startActivity(new Intent(SignInActivity.this, PictureActivity.class));
                            //startActivity(new Intent(SignInActivity.this, CompleteYourProfileActivity.class));
                            //getActivity().finish();
                            startActivity(new Intent((SignInActivity)getActivity(), CenesBaseActivity.class));
                            getActivity().finish();
                        } else {
                            if (jsonObject.has("errorDetail")) {
                                alertManager.getAlert((SignInActivity)getActivity(), jsonObject.getString("errorDetail"), "Error", null, false, "OK");
                            } else {
                                alertManager.getAlert((SignInActivity)getActivity(), "Some thing is going wrong", "Error", null, false, "OK");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.cancel();
                    alertManager.getAlert((SignInActivity)getActivity(), "Server Error", "Error", null, false, "OK");
                }

            } else {
                getCenesActivity().showRequestTimeoutDialog();
            }
        }

    }


/*
    class CenesFacebookLogin extends AsyncTask<Object, Object, Object> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(SignInActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub
            User user = (User)params[0];
            JSONObject jsonObject = apiManager.signUpByEmail(user, SignInActivity.this);
            try {
                if (jsonObject != null && jsonObject.has("errorCode") && jsonObject.getInt("errorCode") == 0) {
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
                    apiManager.syncFacebookEvents(user, SignInActivity.this);
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
                        User user = new User();
                        Boolean isUserExistsInDb = true;
                        if (jsonObject.has(user.USERID)) {
                            user = userManager.findUserByUserId(jsonObject.getLong(user.USERID));
                            if (user == null) {
                                user = new User();
                                isUserExistsInDb = false;
                            }
                            user.setUserId(jsonObject.getInt(user.USERID));
                        }
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
                            if (jsonObject.has(user.EMAIL)) {
                                user.setEmail(jsonObject.getString(user.EMAIL));
                            }
                            if (jsonObject.has("gender") && !CenesUtils.isEmpty(jsonObject.getString("gender")) ) {
                                user.setGender(jsonObject.getString("gender"));
                            }

                            if (isUserExistsInDb) {
                                userManager.updateUser(user);
                            } else {
                                userManager.addUser(user);
                            }
                            progressDialog.hide();
                            progressDialog.cancel();
                            progressDialog.dismiss();
                            progressDialog = null;
                            */
/*if (jsonObject.has("isNew") && !jsonObject.getBoolean("isNew")) {
                                startActivity(new Intent(SignInActivity.this, HomeScreenActivity.class));
                                finish();
                            } else {*//*

                            if (token != null && token.length() != 0) {
                                JSONObject registerDeviceObj = new JSONObject();
                                registerDeviceObj.put("deviceToken",token);
                                registerDeviceObj.put("deviceType","android");
                                registerDeviceObj.put("userId",user.getUserId());
                                new DeviceTokenSync().execute(registerDeviceObj);
                            }

                            startActivity(new Intent(SignInActivity.this, HomeScreenActivity.class));
                            finish();
                            //}
                        } else {
                            progressDialog.hide();
                            if (jsonObject.has("errorDetail")) {
                                alertManager.getAlert(SignInActivity.this, jsonObject.getString("errorDetail"), "Error", null, false, "OK");
                            } else {
                                alertManager.getAlert(SignInActivity.this, "Some thing is going wrong", "Error", null, false, "OK");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.hide();
                    alertManager.getAlert(SignInActivity.this, "Server Error", "Error", null, false, "OK");
                }

            } else {
                showRequestTimeoutDialog();
            }
        }
    }
*/

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
            apiManager.syncDeviceToekn(user,deviceTokenInfo,(SignInActivity)getActivity());
            return null;
        }
    }

    String token = "";

    @Override
    public void onResume() {
        super.onResume();

        if(FirebaseInstanceId.getInstance().getToken() != null) {
            token = FirebaseInstanceId.getInstance().getToken();
            System.out.println("tokken: " + FirebaseInstanceId.getInstance().getToken());
        } else {
            token = getActivity().getSharedPreferences("CenesPrefs", getActivity().MODE_PRIVATE).getString("FcmToken", "");
        }

        System.out.println("SignInActivity FCM Token: " + token);

        //CenesUtils.logEntries(null, "SignInActivity FCM Token: " + token, SignInActivity.this);
    }
}
