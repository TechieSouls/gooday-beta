package com.cenes.fragment.guest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cenes.Manager.AlertManager;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.DeviceManager;
import com.cenes.Manager.InternetManager;
import com.cenes.Manager.UrlManager;
import com.cenes.Manager.ValidationManager;
import com.cenes.R;
import com.cenes.application.CenesApplication;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.fragment.CenesFragment;
import com.cenes.util.RoundedImageView;

import org.json.JSONObject;
import org.w3c.dom.Text;

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

    RoundedImageView rivProfiePic;
    TextView fpEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Obtain the shared Tracker instance.
        CenesApplication application = (CenesApplication) getActivity().getApplication();

        View v = inflater.inflate(R.layout.fragment_forget_password_success, container, false);

        rivProfiePic = (RoundedImageView) v.findViewById(R.id.riv_profie_pic);
        fpEmail = (TextView) v.findViewById(R.id.fp_email);

        String userData = getArguments().getString("user");
        try {
            JSONObject user = new JSONObject(userData);
            fpEmail.setText(user.getString("email"));

        } catch (Exception e) {
            e.printStackTrace();
        }

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
}