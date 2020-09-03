package com.cenesbeta.fragment.guest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.fragment.CenesFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PreviousLoginAccountFragment extends CenesFragment {

    private TextView tvPreviousLoggedinAccount;
    private RelativeLayout rlEmailBtn, rlGoogleBtn, rlFacebookBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_previous_login_account, container, false);

        tvPreviousLoggedinAccount = (TextView) view.findViewById(R.id.tv_previous_loggedin_account);
        rlEmailBtn = (RelativeLayout) view.findViewById(R.id.rl_email_btn);
        rlGoogleBtn = (RelativeLayout) view.findViewById(R.id.rl_google_btn);
        rlFacebookBtn = (RelativeLayout) view.findViewById(R.id.rl_facebook_btn);

        rlEmailBtn.setOnClickListener(onClickListener);
        rlGoogleBtn.setOnClickListener(onClickListener);
        rlFacebookBtn.setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.rl_email_btn:

                break;

                case R.id.rl_facebook_btn:

                break;

                case  R.id.rl_google_btn:

                break;

                case  R.id.bt_fb_join:

                    break;
            }
        }
    };
}
