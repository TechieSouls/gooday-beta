package com.cenes.fragment.guest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import org.json.JSONObject;

/**
 * Created by mandeep on 6/10/18.
 */

public class ForgotPasswordFragment extends CenesFragment {

    public final static String TAG = "ForgotPasswordFragment";

    Button buttonForgotPasswordSubmit;
    LinearLayout llSigningBackBtn;
    EditText editForgotPasswordEmail;

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

        llSigningBackBtn.setOnClickListener(onClickListener);
        buttonForgotPasswordSubmit.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_fp_submit:
                    String email = editForgotPasswordEmail.getText().toString();
                    new ForgotPasswordRequest().execute(email);
                    break;
                case R.id.ll_signin_back:
                    getActivity().onBackPressed();
                    break;
            }
        }
    };

    class ForgotPasswordRequest extends AsyncTask<String,JSONObject,JSONObject> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog((SignInActivity)getActivity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String email = strings[0];
            String queryStr = "?email="+email;
            JSONObject userResp = apiManager.forgotPassword(urlManager.getApiUrl("dev"),queryStr,(SignInActivity)getActivity());
            return userResp;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            progressDialog.cancel();
            progressDialog.dismiss();
            progressDialog = null;
            try {
                if (response.getBoolean("success")) {
                    Toast.makeText(getActivity().getApplicationContext(),"Reset Password link sent to your Email.",Toast.LENGTH_LONG).show();
                    getView().findViewById(R.id.ll_login_form).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.ll_forgetpassowrd_form).setVisibility(View.GONE);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"Invalid Email Address",Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
