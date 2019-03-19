package com.cenes.fragment.gathering;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.R;
import com.cenes.activity.CenesBaseActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.fragment.CenesFragment;
import com.cenes.fragment.HelpFeedbackFragment;
import com.cenes.fragment.dashboard.HomeFragment;
import com.cenes.util.RoundedImageView;

public class GatheringExpiredFragment extends CenesFragment {

    public static String TAG = "GatheringExpiredFragment";
    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private User user;

    private RoundedImageView homeProfilePic;
    private ImageView homeIcon;
    private Button createGatheringBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gathering_expired, container, false);

        homeProfilePic = (RoundedImageView) v.findViewById(R.id.home_profile_pic);
        homeIcon = (ImageView) v.findViewById(R.id.home_icon);
        createGatheringBtn = (Button) v.findViewById(R.id.btn_create_gathering);
        ((CenesBaseActivity)getActivity()).hideFooter();

        cenesApplication = ((CenesBaseActivity) getActivity()).getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        user = userManager.getUser();

        if (user != null && user.getPicture() != null && user.getPicture() != "null") {
            Glide.with(this).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(homeProfilePic);
        }

        homeProfilePic.setOnClickListener(onClickListener);
        homeIcon.setOnClickListener(onClickListener);
        createGatheringBtn.setOnClickListener(onClickListener);
        return v;

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.home_profile_pic:
                    ((CenesBaseActivity)getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
                    break;

                case R.id.home_icon:
                    ((CenesBaseActivity)getActivity()).clearBackStackInclusive(null);
                    ((CenesBaseActivity)getActivity()).replaceFragment(new HomeFragment(), null);
                    break;

                case R.id.btn_create_gathering:

                    ((CenesBaseActivity)getActivity()).clearBackStackInclusive(null);
                    ((CenesBaseActivity)getActivity()).replaceFragment(new HomeFragment(), null);

                    CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
                    ((CenesBaseActivity)getActivity()).replaceFragment(createGatheringFragment, CreateGatheringFragment.TAG);
                    break;
            }
        }
    };
}
