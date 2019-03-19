package com.cenes.fragment.guest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.cenes.AsyncTasks.ProfileAsyncTask;
import com.cenes.Manager.AlertManager;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.DeviceManager;
import com.cenes.Manager.InternetManager;
import com.cenes.Manager.UrlManager;
import com.cenes.Manager.ValidationManager;
import com.cenes.R;
import com.cenes.activity.SignInActivity;
import com.cenes.application.CenesApplication;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.fragment.CenesFragment;
import com.cenes.service.InstabugService;

import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by mandeep on 6/10/18.
 */

public class ForgotPasswordFragment extends CenesFragment {

    public final static String TAG = "ForgotPasswordFragment";

    Button buttonForgotPasswordSubmit;
    LinearLayout llSigningBackBtn;
    EditText editForgotPasswordEmail;
    ImageView ivBugReport;
    TextView tvLoginBackAgain;

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    ValidationManager validationManager;
    InternetManager internetManager;
    UrlManager urlManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Obtain the shared Tracker instance.
        CenesApplication application = (CenesApplication) getActivity().getApplication();

        View v = inflater.inflate(R.layout.fragment_forget_password, container, false);

        init();
        initializeComponents(v);

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

    public void initializeComponents(View view) {

        llSigningBackBtn = (LinearLayout) view.findViewById(R.id.ll_signin_back);
        buttonForgotPasswordSubmit = (Button) view.findViewById(R.id.bt_fp_submit);
        editForgotPasswordEmail = (EditText) view.findViewById(R.id.et_fp_email);
        ivBugReport = (ImageView) view.findViewById(R.id.iv_bug_report);
        tvLoginBackAgain = (TextView) view.findViewById(R.id.tv_login_back_again);

        llSigningBackBtn.setOnClickListener(onClickListener);
        buttonForgotPasswordSubmit.setOnClickListener(onClickListener);
        ivBugReport.setOnClickListener(onClickListener);
        tvLoginBackAgain.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_fp_submit:

                    if (editForgotPasswordEmail.getText().toString() == null || editForgotPasswordEmail.getText().toString() == "" || editForgotPasswordEmail.getText().toString().length() == 0) {
                        Toast.makeText(getActivity(), "Email Field Cannot be left Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        String email = editForgotPasswordEmail.getText().toString();
                        new ProfileAsyncTask(cenesApplication, getActivity());
                        new ProfileAsyncTask.ForgotPasswordRequest(new ProfileAsyncTask.ForgotPasswordRequest.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject response) {
                                try {
                                    if (response.getBoolean("success")) {

                                        ForgotPasswordSuccessFragment forgotPasswordSuccessFragment = new ForgotPasswordSuccessFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("email", editForgotPasswordEmail.getText().toString());
                                        forgotPasswordSuccessFragment.setArguments(bundle);
                                        ((SignInActivity)getActivity()).replaceFragment(forgotPasswordSuccessFragment, ForgotPasswordSuccessFragment.TAG);

                                    } else {
                                        //Toast.makeText(getActivity().getApplicationContext(),"Invalid Email Address",Toast.LENGTH_LONG).show();
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("User Not Found")
                                                .setMessage("No accounts match this information")
                                                .setCancelable(false)
                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Whatever...
                                                    }
                                                }).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(email);
                    }
                    break;
                case R.id.ll_signin_back:
                    getActivity().onBackPressed();
                    break;

                case R.id.iv_bug_report:
                    new InstabugService().invokeBugReporting();
                    break;

                case R.id.tv_login_back_again:
                    getActivity().onBackPressed();
                    break;
            }
        }
    };



}
