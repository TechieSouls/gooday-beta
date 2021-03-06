package com.cenesbeta.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by mandeep on 12/9/18.
 */

public class AboutUsFragment  extends CenesFragment {

    public final static String TAG = "AboutUsFragment";

    RelativeLayout rlVersionUpdateButton;
    TextView tvVersionText;
    private ImageView ivBackButtonImg;

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;

    private User user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_about_us, container, false);

        ivBackButtonImg = (ImageView) v.findViewById(R.id.iv_back_button_img);

        setFragmentManager();


        rlVersionUpdateButton = (RelativeLayout) v.findViewById(R.id.rl_version_update_btn);
        tvVersionText = (TextView) v.findViewById(R.id.tv_version_text);

        ((CenesBaseActivity)getActivity()).hideFooter();

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();

        user = userManager.getUser();

        ivBackButtonImg.setOnClickListener(onClickListener);
        rlVersionUpdateButton.setOnClickListener(onClickListener);

        PackageManager manager = getActivity().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            tvVersionText.setText("Version "+info.versionName);
            /*Toast.makeText(getActivity(),
                    "PackageName = " + info.packageName + "\nVersionCode = "
                            + info.versionCode + "\nVersionName = "
                            + info.versionName + "\nPermissions = " + info.permissions, Toast.LENGTH_SHORT).show();*/

        } catch (Exception e) {
            e.printStackTrace();
        }


        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_back_button_img:
                    getActivity().onBackPressed();
                    break;
                    case R.id.rl_version_update_btn:
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.about_us_version_update_link)));
                    startActivity(intent);
                    break;

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }


    FragmentManager fragmentManager;

    public Fragment getFragmentPresentInBackStack() {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && (fragment instanceof NavigationFragment))
                    return fragment;
            }
        }
        return null;
    }

    public void setFragmentManager() {
    }
}
