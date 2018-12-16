package com.cenes.fragment.guest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
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
import com.cenes.activity.SignUpActivity;
import com.cenes.application.CenesApplication;
import com.cenes.backendManager.UserApiManager;
import com.cenes.coremanager.CoreManager;
import com.cenes.fragment.CenesFragment;

import org.json.JSONObject;

/**
 * Created by mandeep on 19/9/18.
 */

public class SignupStep2Fragment extends CenesFragment {

    public final static String TAG = "FragmentSignupStep2";

    private LinearLayout llSignupStep2Back;
    private EditText etBox1, etBox2, etBox3, etBox4;
    private Button btKeypad1,btKeypad2, btKeypad3, btKeypad4, btKeypad5, btKeypad6, btKeypad7, btKeypad8;
    private Button btKeypad9, btKeypad0, btKeypadDelete;
    private TextView tvSignupStep2Guide;

    private String verificationCode = "";

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserApiManager userApiManager;
    private AlertManager alertManager;
    private String countryCode, phoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup_step2, container, false);
        llSignupStep2Back = (LinearLayout) v.findViewById(R.id.ll_signup_step2_back);

        etBox1 = (EditText) v.findViewById(R.id.et_box_1);
        etBox2 = (EditText) v.findViewById(R.id.et_box_2);
        etBox3 = (EditText) v.findViewById(R.id.et_box_3);
        etBox4 = (EditText) v.findViewById(R.id.et_box_4);

        btKeypad1 = (Button) v.findViewById(R.id.bt_keypad_1);
        btKeypad2 = (Button) v.findViewById(R.id.bt_keypad_2);
        btKeypad3 = (Button) v.findViewById(R.id.bt_keypad_3);
        btKeypad4 = (Button) v.findViewById(R.id.bt_keypad_4);
        btKeypad5 = (Button) v.findViewById(R.id.bt_keypad_5);
        btKeypad6 = (Button) v.findViewById(R.id.bt_keypad_6);
        btKeypad7 = (Button) v.findViewById(R.id.bt_keypad_7);
        btKeypad8 = (Button) v.findViewById(R.id.bt_keypad_8);
        btKeypad9 = (Button) v.findViewById(R.id.bt_keypad_9);
        btKeypad0 = (Button) v.findViewById(R.id.bt_keypad_0);
        btKeypadDelete = (Button) v.findViewById(R.id.bt_keypad_delete);

        tvSignupStep2Guide = (TextView) v.findViewById(R.id.tv_signup_step2_guide);

        llSignupStep2Back.setOnClickListener(onClickListener);
        btKeypad1.setOnClickListener(onClickListener);
        btKeypad2.setOnClickListener(onClickListener);
        btKeypad3.setOnClickListener(onClickListener);
        btKeypad4.setOnClickListener(onClickListener);
        btKeypad5.setOnClickListener(onClickListener);
        btKeypad6.setOnClickListener(onClickListener);
        btKeypad7.setOnClickListener(onClickListener);
        btKeypad8.setOnClickListener(onClickListener);
        btKeypad9.setOnClickListener(onClickListener);
        btKeypad0.setOnClickListener(onClickListener);
        btKeypadDelete.setOnClickListener(onClickListener);


        etBox1.addTextChangedListener(et1TextWatcher);
        etBox2.addTextChangedListener(et2TextWatcher);
        etBox3.addTextChangedListener(et3TextWatcher);
        etBox4.addTextChangedListener(et4TextWatcher);

        phoneNumber = getArguments().getString("phoneNumber");
        countryCode = getArguments().getString("countryCode");
        String guideText = tvSignupStep2Guide.getText().toString();
        tvSignupStep2Guide.setText(guideText.replaceAll("\\[phone_number\\]", phoneNumber)+"");

        initializeVariables();

        return v;
    }

    public void initializeVariables() {

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userApiManager = coreManager.getUserAppiManager();
        alertManager = coreManager.getAlertManager();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_signup_step2_back:
                    getActivity().onBackPressed();
                    break;
                case R.id.bt_signup_step1_continue:
                    ((GuestActivity) getActivity()).replaceFragment(new SignupStep2Fragment(), "SignupStep2Fragment");
                    break;

                case R.id.bt_keypad_1:
                    setTExtInEditBoxes("1");
                    break;
                case R.id.bt_keypad_2:
                    setTExtInEditBoxes("2");
                    break;
                case R.id.bt_keypad_3:
                    setTExtInEditBoxes("3");
                    break;
                case R.id.bt_keypad_4:
                    setTExtInEditBoxes("4");
                    break;
                case R.id.bt_keypad_5:
                    setTExtInEditBoxes("5");
                    break;
                case R.id.bt_keypad_6:
                    setTExtInEditBoxes("6");
                    break;
                case R.id.bt_keypad_7:
                    setTExtInEditBoxes("7");
                    break;
                case R.id.bt_keypad_8:
                    setTExtInEditBoxes("8");
                    break;
                case R.id.bt_keypad_9:
                    setTExtInEditBoxes("9");
                    break;
                case R.id.bt_keypad_0:
                    setTExtInEditBoxes("0");
                    break;
                case R.id.bt_keypad_delete:
                    setTExtInEditBoxes("-1");
                    break;
            }
        }
    };

    TextWatcher et1TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 1) {
                //verificationCode += editable.toString();
                //etBox2.requestFocus();
            }
        }
    };
    TextWatcher et2TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 1) {
                //verificationCode += editable.toString();
                //etBox3.requestFocus();
            }
        }
    };
    TextWatcher et3TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 1) {
                //verificationCode += editable.toString();
                //etBox4.requestFocus();
            }
        }
    };
    TextWatcher et4TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 1) {
                //verificationCode += editable.toString();
            }
        }
    };

    public void setTExtInEditBoxes(String number) {

        if (number == "-1") {
            if (verificationCode.length() > 0) {
                if (verificationCode.length() == 1) {
                    verificationCode = "";
                    etBox1.setText("");
                }
                if (verificationCode.length() == 2) {
                    verificationCode = verificationCode.substring(0,verificationCode.length() - 1);
                    etBox2.setText("");
                }
                if (verificationCode.length() == 3) {
                    verificationCode = verificationCode.substring(0,verificationCode.length() - 1);
                    etBox3.setText("");
                }
                if (verificationCode.length() == 4) {
                    verificationCode = verificationCode.substring(0,verificationCode.length() - 1);
                    etBox4.setText("");
                }
            }
        } else {
            if (verificationCode.length() < 4) {

                if (verificationCode.length() == 0) {
                    etBox1.setText(number);
                }
                if (verificationCode.length() == 1) {
                    etBox2.setText(number);
                }
                if (verificationCode.length() == 2) {
                    etBox3.setText(number);
                }
                if (verificationCode.length() == 3) {
                    etBox4.setText(number);
                }

                verificationCode += number;
            }

            if (verificationCode.length() == 4) {
                JSONObject postData = new JSONObject();
                try {
                     postData.put("code", verificationCode);
                     postData.put("countryCode", countryCode);
                     postData.put("phone", phoneNumber);
                } catch (Exception e){
                    e.printStackTrace();
                }
                new CheckVerificationCodeTask().execute(postData);
            }
        }

    }

    class CheckVerificationCodeTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog verifyCodeDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            verifyCodeDialog = new ProgressDialog((GuestActivity)getActivity());
            verifyCodeDialog.setMessage("Verifying Code");
            verifyCodeDialog.setIndeterminate(false);
            verifyCodeDialog.setCanceledOnTouchOutside(false);
            verifyCodeDialog.setCancelable(false);
            verifyCodeDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsons) {

            JSONObject popstData = jsons[0];
            return userApiManager.checkVerificationCode(popstData);

        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            verifyCodeDialog.dismiss();
            verifyCodeDialog = null;
            try {
                if (response.getBoolean("success")) {
                    //startActivity(new Intent((GuestActivity)getActivity(), SignUpActivity.class));
                    SignupStepSuccessFragment signupStepSuccessFragment = new SignupStepSuccessFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("phoneNumber", countryCode+phoneNumber);
                    signupStepSuccessFragment.setArguments(bundle);

                    ((GuestActivity) getActivity()).replaceFragment(signupStepSuccessFragment, "SignupStepSuccessFragment");

                } else {
                    etBox1.setText("");
                    etBox2.setText("");
                    etBox3.setText("");
                    etBox4.setText("");
                    verificationCode = "";
                    alertManager.getAlert((GuestActivity)getActivity(), response.getString("message"), "Alert", null, false, "OK");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}