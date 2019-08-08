package com.cenesbeta.fragment.guest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cenesbeta.Manager.AlertManager;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.DeviceManager;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.Manager.ValidationManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.activity.SignInActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;

/**
 * Created by mandeep on 7/10/18.
 */

public class ForgotPasswordSuccessFragment  extends CenesFragment {

    public final static String TAG = "ForgotPasswordSuccessFragment";

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    ValidationManager validationManager;
    InternetManager internetManager;
    UrlManager urlManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    TextView tvForgetPassowrdSuccessMsg;
    Button btnFbBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Obtain the shared Tracker instance.
        CenesApplication application = (CenesApplication) getActivity().getApplication();

        View v = inflater.inflate(R.layout.fragment_forget_password_success, container, false);

        tvForgetPassowrdSuccessMsg = (TextView) v.findViewById(R.id.tv_forget_passowrd_success_msg);
        btnFbBack = (Button) v.findViewById(R.id.btn_fp_back);

        String userData = getArguments().getString("email");
        try {
            String fpSuccess = "Reset link has been sent to "+userData+". Please go to your mailbox to complete the request.";
            tvForgetPassowrdSuccessMsg.setText(fpSuccess);

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnFbBack.setOnClickListener(onClickListener);
        return v;
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
                case R.id.btn_fp_back:
                    startActivity(new Intent((SignInActivity)getActivity(), GuestActivity.class));
                    getActivity().finish();
                    break;
            }
        }
    };

}