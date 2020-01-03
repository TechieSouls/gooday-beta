package com.cenesbeta.fragment.guest;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.cenesbeta.R;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.UserApiManager;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;

import org.json.JSONObject;

/**
 * Created by mandeep on 19/9/18.
 */

public class PhoneVerificationStep2Fragment extends CenesFragment {

    public final static String TAG = "PhoneVerificationStep2Fragment";

    private ImageView ivBackButtonImg;
    private TextView tvPhoneNumber, tvCounterDisplay;
    private EditText etOtp;
    private Button btnResendCode;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private AlertManager alertManager;
    private String countryCode, phoneNumber, countryCodeStr;
    private Integer counter = 30;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_phone_verification_step2, container, false);

        ivBackButtonImg = (ImageView) v.findViewById(R.id.iv_back_button_img);
        tvPhoneNumber = (TextView) v.findViewById(R.id.tv_phone_number);
        tvCounterDisplay = (TextView) v.findViewById(R.id.tv_counter_display);
        btnResendCode = (Button) v.findViewById(R.id.btn_resend_code);

        etOtp = (EditText) v.findViewById(R.id.et_otp);

        ivBackButtonImg.setOnClickListener(onClickListener);
        btnResendCode.setOnClickListener(onClickListener);
        etOtp.addTextChangedListener(etTextWatcher);

        btnResendCode.setEnabled(false);
        phoneNumber = getArguments().getString("phoneNumber");
        countryCode = getArguments().getString("countryCode");
        countryCodeStr = getArguments().getString("countryCodeStr");
        tvPhoneNumber.setText(countryCode+" "+phoneNumber);

        starCounter();
        initializeVariables();

        return v;
    }

    public void initializeVariables() {

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        alertManager = coreManager.getAlertManager();
        userManager = coreManager.getUserManager();

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_back_button_img:
                    getActivity().onBackPressed();
                    break;

                case R.id.btn_resend_code:
                    btnResendCode.setEnabled(false);
                    btnResendCode.setTextColor(Color.parseColor("#9A9A9A"));
                    counter = 30;
                    resendOtpCall();
                    starCounter();
                    break;
            }
        }
    };

    TextWatcher etTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 4) {
                verifyOtpByApi();
            }
        }
    };

    public void verifyOtpByApi() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("code", etOtp.getText().toString());
            postData.put("countryCode", countryCode);
            postData.put("phone", phoneNumber);
        } catch (Exception e){
            e.printStackTrace();
        }
        new ProfileAsyncTask(cenesApplication, getActivity());
        new ProfileAsyncTask.CheckVerificationCodeTask(new ProfileAsyncTask.CheckVerificationCodeTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                try {
                    if (response.getBoolean("success")) {
                        System.out.println("countryCodeStr : "+countryCodeStr);

                        User user = new User();
                        user.setCountry(countryCodeStr.toUpperCase());
                        user.setPhone(countryCode+phoneNumber);
                        userManager.deleteAll();
                        userManager.addUser(user);
                        SignupOptionsFragment signupOptionsFragment = new SignupOptionsFragment();
                        ((GuestActivity) getActivity()).replaceFragment(signupOptionsFragment, null);

                    } else {
                        alertManager.getAlert((GuestActivity)getActivity(), response.getString("message"), "Alert", null, false, "OK");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute(postData);
    }

    public void resendOtpCall() {

        try {
            JSONObject postData = new JSONObject();
            try {
                postData.put("countryCode",countryCode);
                postData.put("phone",phoneNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ProfileAsyncTask(cenesApplication, getActivity());
            new ProfileAsyncTask.SendVerrificationTask(new ProfileAsyncTask.SendVerrificationTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                }
            }).execute(postData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void starCounter() {
        Thread t = new Thread() {
            public void run() {
                while(counter > -1) {
                    try {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    if (counter < 10) {
                                        tvCounterDisplay.setText("00:0" + counter);
                                    } else {
                                        tvCounterDisplay.setText("00:" + counter);
                                    }
                                    if (counter == 0) {
                                        btnResendCode.setEnabled(true);
                                        btnResendCode.setTextColor(Color.parseColor("#F5A624"));
                                    }
                                }
                            });
                            sleep(1000);
                            counter--;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }
}
