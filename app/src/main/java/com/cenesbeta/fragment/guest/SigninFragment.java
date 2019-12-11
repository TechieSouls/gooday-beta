package com.cenesbeta.fragment.guest;

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

import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.AlertManager;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.DeviceManager;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.Manager.ValidationManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.activity.SignInActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.util.CenesUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONObject;

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

    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    TextView tvForgotpasswordLink;
    ImageView ivBugReport;
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

        editTextEmail = (EditText) view.findViewById(R.id.et_email);
        editTextPassword = (EditText) view.findViewById(R.id.et_password);
        tvForgotpasswordLink = (TextView) view.findViewById(R.id.tv_forget_password);

        buttonLogin = (Button) view.findViewById(R.id.bt_login);

        ivBugReport = (ImageView) view.findViewById(R.id.iv_bug_report);

        buttonLogin.setOnClickListener(onClickListener);
        tvForgotpasswordLink.setOnClickListener(onClickListener);

        ivBugReport.setOnClickListener(onClickListener);
        signinBackArrow = (ImageView) view.findViewById(R.id.iv_back_button);
        signinBackArrow.setOnClickListener(onClickListener);

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

                case R.id.iv_bug_report:
                    break;

                case R.id.iv_back_button:
                    ((GuestActivity)getActivity()).onBackPressed();
                    break;
                case R.id.tv_forget_password:
                    ((GuestActivity) getActivity()).replaceFragment(new ForgotPasswordFragment(), SigninFragment.TAG);
                    break;
            }

        }
    };

    /* @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         callbackManager.onActivityResult(requestCode, resultCode, data);
     }*/
    public void startSignInProcess() {
        deviceManager.hideKeyBoard(editTextEmail, (GuestActivity)getActivity());
        user = null;
        user = new User();
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        validationProcess();
    }

    public void validationProcess() {

        boolean isValid = true;

        if (isValid && validationManager.isFieldBlank(email)) {
            alertManager.getAlert((GuestActivity)getActivity(), "Please Enter the Email", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid && validationManager.isFieldBlank(email)) {
            alertManager.getAlert((GuestActivity)getActivity(), "Please Enter the Email", "Info", null, false, "OK");
            isValid = false;
        }

        if (isValid && validationManager.isFieldBlank(password)) {
            alertManager.getAlert((GuestActivity)getActivity(), "Please Enter the Password", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid) {
            networkProcess();
        }
    }

    public void networkProcess() {
        if (internetManager.isInternetConnection((GuestActivity)getActivity())) {
            user.setEmail(email);
            user.setPassword(password);
            user.setApiUrl(urlManager.getApiUrl(email));
            user.setAuthType(User.AuthenticateType.email);
            JSONObject postData = null;
            try {

                Gson gson = new Gson();
                postData = new JSONObject(gson.toJson(user));
                new ProfileAsyncTask(cenesApplication, getActivity());
                new ProfileAsyncTask.EmailoginTask(new ProfileAsyncTask.EmailoginTask.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject response) {

                        try {

                            Gson loginGson = new Gson();
                            user = loginGson.fromJson(response.toString(), User.class);
                            if (user.getUserId() != null) {

                                userManager.deleteAll();
                                userManager.addUser(user);

                                if (token != null) {
                                    JSONObject registerDeviceObj = new JSONObject();
                                    registerDeviceObj.put("deviceToken",token);
                                    registerDeviceObj.put("deviceType","android");
                                    registerDeviceObj.put("userId",user.getUserId());
                                    registerDeviceObj.put("model", CenesUtils.getDeviceModel());
                                    registerDeviceObj.put("manufacturer", CenesUtils.getDeviceManufacturer());
                                    registerDeviceObj.put("version", CenesUtils.getDeviceVersion());
                                    new ProfileAsyncTask.DeviceTokenSyncTask(new ProfileAsyncTask.DeviceTokenSyncTask.AsyncResponse() {
                                        @Override
                                        public void processFinish(JSONObject response) {

                                        }
                                    }).execute(registerDeviceObj);

                                }
                                startActivity(new Intent((GuestActivity)getActivity(), CenesBaseActivity.class));
                                getActivity().finish();
                            } else {
                                if (response.has("errorDetail")) {
                                    alertManager.getAlert((GuestActivity)getActivity(), response.getString("errorDetail"), "Error", null, false, "OK");
                                } else {
                                    alertManager.getAlert((GuestActivity)getActivity(), "Some thing is going wrong", "Error", null, false, "OK");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute(postData);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            alertManager.getAlert((GuestActivity)getActivity(), "No Internet Connection!", "Info", null, false, "OK");
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
    }
}
