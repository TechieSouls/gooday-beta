package com.cenes.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenes.R;
import com.cenes.activity.AlarmActivity;
import com.cenes.activity.DiaryActivity;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.ReminderActivity;

import java.util.List;

/**
 * Created by mandeep on 12/9/18.
 */

public class AboutUsFragment  extends CenesFragment {

    public final static String TAG = "AboutUsFragment";

    RelativeLayout rlVersionUpdateButton;
    ImageView closeAboutUsBtn;
    TextView tvVersionText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_about_us, container, false);

        setFragmentManager();


        rlVersionUpdateButton = (RelativeLayout) v.findViewById(R.id.rl_version_update_btn);
        closeAboutUsBtn = (ImageView) v.findViewById(R.id.iv_close_about_us_btn);
        tvVersionText = (TextView) v.findViewById(R.id.tv_version_text);

        rlVersionUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.about_us_version_update_link)));
                startActivity(intent);
            }
        });
        closeAboutUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

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

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (getActivity() instanceof HomeScreenActivity) {
                ((HomeScreenActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof ReminderActivity) {
                ((ReminderActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof GatheringScreenActivity) {
                ((GatheringScreenActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof DiaryActivity) {
                ((DiaryActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof AlarmActivity) {
                ((AlarmActivity) getActivity()).hideFooter();
            }
        } catch (Exception e) {

        }
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
        if (getActivity() instanceof HomeScreenActivity) {
            fragmentManager = ((HomeScreenActivity) getActivity()).fragmentManager;
        } else if (getActivity() instanceof ReminderActivity) {
            fragmentManager = ((ReminderActivity) getActivity()).fragmentManager;
        } else if (getActivity() instanceof GatheringScreenActivity) {
            fragmentManager = ((GatheringScreenActivity) getActivity()).fragmentManager;
        } else if (getActivity() instanceof DiaryActivity) {
            fragmentManager = ((DiaryActivity) getActivity()).fragmentManager;
        } else if (getActivity() instanceof AlarmActivity) {
            fragmentManager = ((AlarmActivity) getActivity()).fragmentManager;
        }
    }
}
