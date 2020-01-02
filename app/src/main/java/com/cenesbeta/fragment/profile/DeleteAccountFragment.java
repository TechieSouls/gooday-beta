package com.cenesbeta.fragment.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.EventManagerImpl;
import com.cenesbeta.database.impl.MeTimeManagerImpl;
import com.cenesbeta.database.impl.NotificationManagerImpl;
import com.cenesbeta.database.impl.UserManagerImpl;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.guest.SignupCountryListFragment;
import com.cenesbeta.service.AuthenticateService;
import com.cenesbeta.util.CenesUtils;

import org.json.JSONObject;

public class DeleteAccountFragment extends CenesFragment {

    public static String TAG = "DeleteAccountFragment";
    private Integer SELECT_COUNTRY_REQUEST = 1001;

    private RelativeLayout rlDeleteAccountBar, rlSelectCountryBar;
    private LinearLayout llDeleteAccountForm;
    private TextView tvSelectedCountryName, tvCountryCodeDigit;
    private EditText etPhoneNumber, etPassword;
    private Button btnDeleteAccount;
    private ImageView ivProfilePic, homeIcon;
    private View fragmentView;

    private Boolean isCountrySelected = false;
    private  String countryCodeStr = "";
    private String countryCodeDigit = "";
    private AuthenticateService authenticateService;
    private CenesApplication cenesApplication;
    private User loggedInUser;
    private UserManagerImpl userManagerImpl;
    private EventManagerImpl eventManagerImpl;
    private MeTimeManagerImpl meTimeManagerImpl;
    private NotificationManagerImpl notificationManagerImpl;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        if (fragmentView != null) {
            return fragmentView;
        }
        View view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        fragmentView = view;

        ((CenesBaseActivity)getActivity()).hideFooter();

        rlDeleteAccountBar = (RelativeLayout) view.findViewById(R.id.rl_delete_account_bar);
        rlSelectCountryBar = (RelativeLayout) view.findViewById(R.id.rl_select_country_bar);

        llDeleteAccountForm = (LinearLayout) view.findViewById(R.id.ll_delete_account_form);

        tvSelectedCountryName = (TextView) view.findViewById(R.id.tv_selected_country_name);
        tvCountryCodeDigit = (TextView) view.findViewById(R.id.tv_country_code_digit);

        etPhoneNumber = (EditText) view.findViewById(R.id.et_phone_number);
        etPassword = (EditText) view.findViewById(R.id.et_password);

        btnDeleteAccount = (Button) view.findViewById(R.id.btn_delete_account);

        ivProfilePic = (ImageView) view.findViewById(R.id.iv_profile_pic);
        homeIcon = (ImageView) view.findViewById(R.id.home_icon);

        rlDeleteAccountBar.setOnClickListener(onClickListener);
        rlSelectCountryBar.setOnClickListener(onClickListener);
        btnDeleteAccount.setOnClickListener(onClickListener);
        ivProfilePic.setOnClickListener(onClickListener);
        homeIcon.setOnClickListener(onClickListener);

        userManagerImpl = new UserManagerImpl(getCenesActivity().getCenesApplication());
        eventManagerImpl = new EventManagerImpl(getCenesActivity().getCenesApplication());
        meTimeManagerImpl =  new MeTimeManagerImpl(getCenesActivity().getCenesApplication());
        notificationManagerImpl = new NotificationManagerImpl(getCenesActivity().getCenesApplication());

        cenesApplication = getCenesActivity().getCenesApplication();
        CoreManager coreManager = cenesApplication.getCoreManager();
        UserManager userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();

        authenticateService = new AuthenticateService();

        if (isCountrySelected == false) {
            isCountrySelected = true;
            System.out.println("Country Code Selected : "+isCountrySelected);
            countryCodeStr = CenesUtils.getDeviceCountryCode(getContext());
            countryCodeDigit = "+"+authenticateService.getPhoneCodeByCountryCode(countryCodeStr.toUpperCase());
            String countryName = authenticateService.getCountryNameFromCountryCode(countryCodeStr.toUpperCase());

            tvCountryCodeDigit.setText(countryCodeDigit);
            tvSelectedCountryName.setText(countryName);
        }


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.circleCrop();
        requestOptions.placeholder(R.drawable.profile_pic_no_image);
        Glide.with(getContext()).load(loggedInUser.getPicture()).apply(requestOptions).into(ivProfilePic);

        llDeleteAccountForm.setVisibility(View.GONE);
        if (CenesUtils.isEmpty(loggedInUser.getPassword())) {
            etPassword.setVisibility(View.GONE);
        }

        return view;
    }

    public void deleteAccountButtonPressed() {

        boolean isFormValid = true;
        if (etPhoneNumber.getText().toString().equals("")) {

            showAlert("Alert", "Phone number cannot be empty.");
            isFormValid = false;

        } else if (!CenesUtils.isEmpty(loggedInUser.getPassword())) {

            if (etPassword.getText().toString().equals("")) {
                showAlert("Alert", "Password cannot be empty.");
                isFormValid = false;
            }
        }

        if (isFormValid) {

            try {
                JSONObject postData = new JSONObject();
                postData.put("phone", countryCodeDigit+etPhoneNumber.getText().toString());
                postData.put("userId", loggedInUser.getUserId());
                if (!CenesUtils.isEmpty(loggedInUser.getPassword())) {
                    postData.put("password", etPassword.getText().toString());
                }

                new ProfileAsyncTask(getCenesActivity().getCenesApplication(), (CenesBaseActivity)getActivity());
                new ProfileAsyncTask.DeleteAccountTask(new ProfileAsyncTask.DeleteAccountTask.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject response) {

                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {

                                userManagerImpl.deleteAll();
                                eventManagerImpl.deleteAllEvents();
                                meTimeManagerImpl.deleteAllMeTimeRecurringEvents();
                                notificationManagerImpl.deleteAllNotifications();

                                ((CenesBaseActivity)getActivity()).startActivity(new Intent((CenesBaseActivity)getActivity(), GuestActivity.class));
                                ((CenesBaseActivity)getActivity()).finish();

                            } else {
                                showAlert("Alert", response.getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute(postData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case  R.id.rl_delete_account_bar:

                    rlDeleteAccountBar.setVisibility(View.GONE);
                    llDeleteAccountForm.setVisibility(View.VISIBLE);

                    break;

                case R.id.rl_select_country_bar:
                    SignupCountryListFragment scls = new SignupCountryListFragment();
                    scls.setTargetFragment(DeleteAccountFragment.this, SELECT_COUNTRY_REQUEST);
                    ((CenesBaseActivity) getActivity()).replaceFragment(scls, DeleteAccountFragment.TAG);
                    break;

                case R.id.btn_delete_account:
                    deleteAccountButtonPressed();
                    break;

                case R.id.home_icon:
                    getActivity().onBackPressed();
                    break;

                case R.id.iv_profile_pic:
                    if (getActivity() instanceof CenesBaseActivity) {
                        ((CenesBaseActivity)getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_COUNTRY_REQUEST) {

            final String cc = data.getStringExtra("countryCode");
            countryCodeDigit = "+"+cc;
            countryCodeStr = authenticateService.getCountryFromCountryCode(cc.replaceAll("\\+", ""));
            countryCodeDigit = "+"+authenticateService.getPhoneCodeByCountryCode(countryCodeStr.toUpperCase());
            String countryName = authenticateService.getCountryNameFromCountryCode(countryCodeStr.toUpperCase());

            tvCountryCodeDigit.setText(countryCodeDigit);
            tvSelectedCountryName.setText(countryName);
        }
    }
}
