package com.cenes.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.R;
import com.cenes.activity.AlarmActivity;
import com.cenes.activity.CenesBaseActivity;
import com.cenes.activity.DiaryActivity;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.MeTimeActivity;
import com.cenes.activity.ReminderActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.fragment.dashboard.HomeFragment;
import com.cenes.service.InstabugService;
import com.cenes.util.CenesConstants;
import com.cenes.util.RoundedImageView;
import com.instabug.bug.BugReporting;
import com.instabug.bug.PromptOption;
import com.instabug.library.Instabug;
import com.instabug.library.bugreporting.model.Bug;

import java.util.List;

/**
 * Created by mandeep on 27/7/18.
 */

public class HelpFeedbackFragment  extends CenesFragment {

    public final static String TAG = "HelpFeedbackFragment";

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private User loggedInUser;

    private RoundedImageView homeProfilePic;
    private ImageView homeIcon, ivFaq;
    private Button btnReportaProblem;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.help_feedback, container, false);

        setFragmentManager();

        homeProfilePic = (RoundedImageView) v.findViewById(R.id.home_profile_pic);
        homeIcon = (ImageView) v.findViewById(R.id.home_icon);
        ivFaq = (ImageView) v.findViewById(R.id.iv_faq);
        btnReportaProblem = (Button) v.findViewById(R.id.btn_report_a_problem);

        ((CenesBaseActivity)getActivity()).hideFooter();


        ivFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CenesConstants.faqLink));
                startActivity(browserIntent);
            }
        });
        homeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof HomeScreenActivity) {
                    HomeScreenActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                } else if (getActivity() instanceof ReminderActivity) {
                    ReminderActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                } else if (getActivity() instanceof GatheringScreenActivity) {
                    GatheringScreenActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                } else if (getActivity() instanceof MeTimeActivity) {
                    MeTimeActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                } else if (getActivity() instanceof AlarmActivity) {
                    ((AlarmActivity) getActivity()).hideFooter();
                } else if (getActivity() instanceof CenesBaseActivity) {
                    ((CenesBaseActivity)getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((CenesBaseActivity)getActivity()).clearBackStackInclusive(null);
                ((CenesBaseActivity)getActivity()).replaceFragment(new HomeFragment(), null);
            }
        });
        btnReportaProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Instabug.enable();
                BugReporting.setAttachmentTypesEnabled(false, true, true, true);
                BugReporting.setPromptOptionsEnabled(PromptOption.BUG,PromptOption.FEEDBACK);
                BugReporting.invoke();
            }
        });

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();

        loggedInUser = userManager.getUser();

        if (loggedInUser != null && loggedInUser.getPicture() != null && loggedInUser.getPicture() != "null") {
            // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
            Glide.with(HelpFeedbackFragment.this).load(loggedInUser.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(homeProfilePic);
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
