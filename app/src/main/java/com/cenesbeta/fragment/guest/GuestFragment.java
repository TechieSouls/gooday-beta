package com.cenesbeta.fragment.guest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cenesbeta.R;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.activity.SignInActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.fragment.CenesFragment;

/**
 * Created by mandeep on 18/9/18.
 */

public class GuestFragment extends CenesFragment {

    public final static String TAG = "GuestFragment";

    Button btSignupMobile, btAlreadyLogin;
    RelativeLayout rlAlreadyLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Obtain the shared Tracker instance.
        CenesApplication application = (CenesApplication) getActivity().getApplication();

        View v = inflater.inflate(R.layout.fragment_guest, container, false);

        btSignupMobile = (Button) v.findViewById(R.id.bt_signup_mobile);
        btAlreadyLogin = (Button) v.findViewById(R.id.bt_already_login);
        rlAlreadyLogin = (RelativeLayout) v.findViewById(R.id.rl_already_login);

        btSignupMobile.setOnClickListener(onClickListener);
        btAlreadyLogin.setOnClickListener(onClickListener);
        rlAlreadyLogin.setOnClickListener(onClickListener);

        btAlreadyLogin.setText(Html.fromHtml("Already Have an Account? <b>Log In</b>"));

        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.bt_signup_mobile:
                    ((GuestActivity) getActivity()).replaceFragment(new PhoneVerificationStep1Fragment(), GuestFragment.TAG);
                    //((GuestActivity) getActivity()).replaceFragment(new SignupStepSuccessFragment(), null);
                   // ((GuestActivity) getActivity()).replaceFragment(new CalenderSyncFragment(), HolidaySyncFragment.TAG);
                    break;

                case R.id.bt_already_login:
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                    getActivity().finish();
                    break;


                case R.id.rl_already_login:
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                    getActivity().finish();
                    break;
            }
        }
    };
}
