package com.cenes.fragment.guest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cenes.Manager.AlertManager;
import com.cenes.R;
import com.cenes.activity.GuestActivity;
import com.cenes.activity.SignInActivity;
import com.cenes.application.CenesApplication;
import com.cenes.backendManager.UserApiManager;
import com.cenes.coremanager.CoreManager;
import com.cenes.fragment.CenesFragment;

import org.json.JSONObject;

/**
 * Created by mandeep on 18/9/18.
 */

public class SignupStep1Fragment extends CenesFragment {

    public final static String TAG = "SignupStep1Fragment";
    private Integer countryCodeFragmentCode = 101;

    private LinearLayout llSignupStep1Back, llCountryCodeDropdown;
    private Button btAlreadyLogin, btSignupStep1Continue;
    private EditText etPhoneNumber;
    TextView tvDropdownCountryCode;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserApiManager userApiManager;
    private AlertManager alertManager;

    private String etPhoneNumberStr;
    private String countryCode = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_guest_input_phone, container, false);

        llSignupStep1Back = (LinearLayout) v.findViewById(R.id.ll_signup_step1_back);
        llCountryCodeDropdown = (LinearLayout) v.findViewById(R.id.ll_country_code_dropdown);

        btAlreadyLogin = (Button) v.findViewById(R.id.bt_already_login);
        btAlreadyLogin.setText(Html.fromHtml("Already Have an Account? <b>Log In</b>"));
        btSignupStep1Continue = (Button) v.findViewById(R.id.bt_signup_step1_continue);
        etPhoneNumber = (EditText) v.findViewById(R.id.et_phone_number);
        tvDropdownCountryCode = (TextView) v.findViewById(R.id.tv_dropdown_country_code);

        btAlreadyLogin.setOnClickListener(onClickListener);
        llSignupStep1Back.setOnClickListener(onClickListener);
        llCountryCodeDropdown.setOnClickListener(onClickListener);
        btSignupStep1Continue.setOnClickListener(onClickListener);
        etPhoneNumber.addTextChangedListener(phoneNumberWatcher);

        initializeVariables();

        return v;
    }

    public void initializeVariables() {

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userApiManager = coreManager.getUserAppiManager();
        alertManager = coreManager.getAlertManager();
    }


    TextWatcher phoneNumberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            etPhoneNumberStr = editable.toString();
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_signup_step1_back:
                    getActivity().onBackPressed();
                break;
                case R.id.bt_already_login:
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                    getActivity().finish();
                    break;
                case R.id.bt_signup_step1_continue:

                    if (isValid()) {
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("countryCode",tvDropdownCountryCode.getText().toString());
                            postData.put("phone",etPhoneNumberStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new sendVerrificationNumber().execute(postData);
                    }
                    break;
                case R.id.ll_country_code_dropdown:
                    SignupCountryListFragment scls = new SignupCountryListFragment();
                    scls.setTargetFragment(SignupStep1Fragment.this, 1001);
                    ((GuestActivity) getActivity()).replaceFragment(scls, "SignupCountryListFragment");
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1001 && resultCode==Activity.RESULT_OK) {
            final String cc = data.getStringExtra("countryCode");
            countryCode = cc;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvDropdownCountryCode.setText(cc+"");
                }
            }, 500);


        }
    }

    class sendVerrificationNumber extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        ProgressDialog sendCodeDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            sendCodeDialog = new ProgressDialog((GuestActivity)getActivity());
            sendCodeDialog.setMessage("Sending Verification Code");
            sendCodeDialog.setIndeterminate(false);
            sendCodeDialog.setCanceledOnTouchOutside(false);
            sendCodeDialog.setCancelable(false);
            sendCodeDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsons) {

            JSONObject postDta = jsons[0];
            return userApiManager.sentVerificationCode(postDta);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            sendCodeDialog.dismiss();
            sendCodeDialog = null;
            try {
                if (jsonObject.getBoolean("success")) {
                    SignupStep2Fragment ss2Fragment = new SignupStep2Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("countryCode", countryCode);
                    bundle.putString("phoneNumber", etPhoneNumberStr.replaceAll("\\s","").replaceAll("-",""));
                    ss2Fragment.setArguments(bundle);

                    ((GuestActivity) getActivity()).replaceFragment(ss2Fragment, "SignupStep2Fragment");

                } else {
                    alertManager.getAlert((GuestActivity)getActivity(), jsonObject.getString("message"), "Alert", null, false, "OK");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isValid() {

        if (etPhoneNumber.getText().toString().length() == 0) {
            alertManager.getAlert((GuestActivity)getActivity(), "Phone Number cannot be empty", "Alert", null, false, "OK");
            return false;
        }
        if (tvDropdownCountryCode.getText().toString() == "+00") {
            alertManager.getAlert((GuestActivity)getActivity(), "Please select valid country code", "Alert", null, false, "OK");
            return false;
        }

        return true;
    }

}