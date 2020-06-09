package com.cenesbeta.fragment.guest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.AlertManager;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.R;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.api.UserAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.service.AuthenticateService;
import com.cenesbeta.util.CenesUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;
/**
 * Created by mandeep on 18/9/18.
 */

public class PhoneVerificationStep1Fragment extends CenesFragment {

    public final static String TAG = "PhoneVerificationStep1Fragment";
    private static Integer COUNTRY_LIST_REQUEST_CODE = 1001;

    private View fragmentView;
    private RelativeLayout rlPhoneVerificationStep1Continue, rlChooseCountryList, rlTemrsAndConds;
    private Button btSignupStep1Continue;
    private EditText etPhoneNumber;
    private TextView tvTermsAndConds, tvPhoneCountryCode, tvCountryName;
    private ImageView ivBugReport;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private AlertManager alertManager;
    private AuthenticateService authenticateService;
    private String etPhoneNumberStr;
    private String countryCode = "0";
    private String countryCodeStr = "";
    private Boolean isCountrySelected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (fragmentView != null) {
            return fragmentView;
        }
        View v = inflater.inflate(R.layout.fragment_phone_verification_step1, container, false);

        fragmentView = v;
        rlChooseCountryList = (RelativeLayout) v.findViewById(R.id.rl_choose_country_list);
        rlPhoneVerificationStep1Continue = (RelativeLayout) v.findViewById(R.id.rl_phone_verification_step1_continue);

        btSignupStep1Continue = (Button) v.findViewById(R.id.btn_phone_verification_step1_continue);
        btSignupStep1Continue.setEnabled(false);
        etPhoneNumber = (EditText) v.findViewById(R.id.et_phone_number);
        tvPhoneCountryCode = (TextView) v.findViewById(R.id.tv_phone_country_code);
        tvCountryName = (TextView) v.findViewById(R.id.tv_country_name);
        tvTermsAndConds = (TextView) v.findViewById(R.id.tv_temrs_and_conds);
        rlTemrsAndConds = (RelativeLayout) v.findViewById(R.id.rl_temrs_and_conds);

        ivBugReport = (ImageView) v.findViewById(R.id.iv_bug_report);

        rlChooseCountryList.setOnClickListener(onClickListener);
        btSignupStep1Continue.setOnClickListener(onClickListener);
        etPhoneNumber.addTextChangedListener(phoneNumberWatcher);
        ivBugReport.setOnClickListener(onClickListener);
        //etPhoneNumber.setOnFocusChangeListener(onFocusChangeListener);

        etPhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do something, e.g. set your TextView here via .setText()
                    if (etPhoneNumber.getText().toString().length() > 5) {
                        btSignupStep1Continue.setEnabled(true);
                        rlPhoneVerificationStep1Continue.setBackgroundColor(getResources().getColor(R.color.button_enable_color));
                    }
                    return false;
                }
                return false;
            }
        });


        Spanned htmlAsSpanned = Html.fromHtml(getString(R.string.tandc_text)); // used by TextView
        tvTermsAndConds.setText(htmlAsSpanned);
        tvTermsAndConds.setMovementMethod(LinkMovementMethod.getInstance());

        initializeVariables();


        new ProfileAsyncTask(cenesApplication, (GuestActivity)getActivity());
        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setApiUrl(UserAPI.get_user_ip);
        new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                try {
                    String ipAddress = response.getString("ip");
                    String queryStr = "ipAddress="+ipAddress;

                    AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
                    asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+UserAPI.get_country_by_ip_address);
                    asyncTaskDto.setQueryStr(queryStr);

                    new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject response) {
                            try {

                                boolean success = response.getBoolean("success");
                                if (success == true) {
                                    JSONObject countryDict = response.getJSONObject("data");
                                    countryCodeStr = countryDict.getString("iso_code");
                                } else {
                                    countryCodeStr = CenesUtils.getDeviceCountryCode(getContext());
                                }

                                countryCode = "+"+authenticateService.getPhoneCodeByCountryCode(countryCodeStr.toUpperCase());

                                String countryName = authenticateService.getCountryNameFromCountryCode(countryCodeStr.toUpperCase());

                                tvPhoneCountryCode.setText(countryCode);
                                tvCountryName.setText(countryName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(asyncTaskDto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute(asyncTaskDto);

        isCountrySelected = true;
        System.out.println("Country Code Selected : "+isCountrySelected);
        countryCodeStr = CenesUtils.getDeviceCountryCode(getContext());
        countryCode = "+"+authenticateService.getPhoneCodeByCountryCode(countryCodeStr.toUpperCase());
        tvPhoneCountryCode.setText(countryCode);
        String countryName = authenticateService.getCountryNameFromCountryCode(countryCodeStr.toUpperCase());
        tvCountryName.setText(countryName);


        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlTemrsAndConds.getLayoutParams();
        layoutParams.setMargins(0, getCenesActivity().getWindowManager().getDefaultDisplay().getHeight() - CenesUtils.dpToPx(140), 0, 0);
        rlTemrsAndConds.setLayoutParams(layoutParams);

        return v;
    }

    public void initializeVariables() {

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        alertManager = coreManager.getAlertManager();

        authenticateService = new AuthenticateService();

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
        try {
            JSONObject props = new JSONObject();
            props.put("Action","Verification Begins");
            props.put("Title","Step 1");
            props.put("Device","Android");
            mixpanel.track("PhoneVerification", props);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (etPhoneNumberStr.length() > 5) {
                btSignupStep1Continue.setEnabled(true);
                rlPhoneVerificationStep1Continue.setBackgroundColor(getResources().getColor(R.color.button_enable_color));
            } else {
                btSignupStep1Continue.setEnabled(false);
                rlPhoneVerificationStep1Continue.setBackgroundColor(getResources().getColor(R.color.button_disable_color));
            }
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_phone_verification_step1_continue:
                    if (isValid()) {
                        JSONObject postData = new JSONObject();
                        String phoneNumber = "";
                        try {
                            String initialNumber = etPhoneNumberStr.substring(0, 1);
                            if (initialNumber.equals("0")) {
                                phoneNumber = etPhoneNumberStr.substring(1, etPhoneNumberStr.length());
                            } else {
                                phoneNumber = etPhoneNumberStr;
                            }
                            postData.put("countryCode",tvPhoneCountryCode.getText().toString());
                            postData.put("phone",phoneNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        final String phoneNumberTemp = phoneNumber;
                        btSignupStep1Continue.setClickable(false);
                        new ProfileAsyncTask(cenesApplication, getActivity());
                        new ProfileAsyncTask.SendVerrificationTask(new ProfileAsyncTask.SendVerrificationTask.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject response) {

                                try {
                                    btSignupStep1Continue.setClickable(true);

                                    boolean success = response.getBoolean("success");

                                    if (success) {

                                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                                        try {
                                            JSONObject props = new JSONObject();
                                            props.put("Action","Verification Success");
                                            props.put("Title","Step 1");
                                            props.put("Device","Android");
                                            mixpanel.track("PhoneVerification", props);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        System.out.println("countryCodeStr : "+countryCodeStr);

                                        PhoneVerificationStep2Fragment ss2Fragment = new PhoneVerificationStep2Fragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("countryCodeStr", countryCodeStr);
                                        bundle.putString("countryCode", countryCode);
                                        bundle.putString("phoneNumber", phoneNumberTemp.replaceAll("\\s","").replaceAll("-",""));
                                        ss2Fragment.setArguments(bundle);

                                        ((GuestActivity) getActivity()).replaceFragment(ss2Fragment, PhoneVerificationStep1Fragment.TAG);

                                    } else {
                                        rlPhoneVerificationStep1Continue.setBackgroundColor(getResources().getColor(R.color.button_disable_color));
                                        btSignupStep1Continue.setEnabled(false);
                                        alertManager.getAlert((GuestActivity)getActivity(), response.getString("message"), "Alert", null, false, "OK");
                                    }
                            } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                        }).execute(postData);
                    }
                    break;
                case R.id.rl_choose_country_list:
                    SignupCountryListFragment scls = new SignupCountryListFragment();
                    scls.setTargetFragment(PhoneVerificationStep1Fragment.this, COUNTRY_LIST_REQUEST_CODE);
                    ((GuestActivity) getActivity()).replaceFragment(scls, SignupCountryListFragment.TAG);
                    break;
                case R.id.iv_bug_report:
                    break;
            }
        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {

                //rlTemrsAndConds.setVisibility(View.GONE);
            } else {
                //rlTemrsAndConds.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COUNTRY_LIST_REQUEST_CODE && resultCode==Activity.RESULT_OK) {
            final String cc = data.getStringExtra("countryCode");
            countryCode = cc;

            countryCodeStr = authenticateService.getCountryFromCountryCode(countryCode.replaceAll("\\+", ""));
            System.out.println("Country from country code : "+countryCodeStr);

            String countryName = authenticateService.getCountryNameFromCountryCode(countryCodeStr.toUpperCase());
            tvCountryName.setText(countryName);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvPhoneCountryCode.setText(cc+"");
                    isCountrySelected = true;
                }
            }, 500);
        }
    }

    public boolean isValid() {

        if (etPhoneNumber.getText().toString().length() == 0) {
            alertManager.getAlert((GuestActivity)getActivity(), "Phone Number cannot be empty.", "Alert", null, false, "OK");
            return false;
        }
        if (tvPhoneCountryCode.getText().toString() == "+00") {
            alertManager.getAlert((GuestActivity)getActivity(), "Please select valid country code.", "Alert", null, false, "OK");
            return false;
        }

        if(etPhoneNumber.getText().toString().contains("+")){
            alertManager.getAlert((GuestActivity)getActivity(), "Please enter phone number without country code, select country code from drop down only.", "Alert", null, false, "OK");
            return false;
        }

        return true;
    }

}
