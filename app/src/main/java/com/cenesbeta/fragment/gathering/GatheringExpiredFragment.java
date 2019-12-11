package com.cenesbeta.fragment.gathering;

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
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.HelpFeedbackFragment;
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.util.RoundedImageView;

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
            Glide.with(this).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(homeProfilePic);
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

                    ((CenesBaseActivity)getActivity()).parentEvent = null;
                    CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
                    ((CenesBaseActivity)getActivity()).replaceFragment(createGatheringFragment, CreateGatheringFragment.TAG);
                    break;
            }
        }
    };
}
