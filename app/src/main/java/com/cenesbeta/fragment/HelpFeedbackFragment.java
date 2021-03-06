package com.cenesbeta.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import java.util.List;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
                if (getActivity() instanceof CenesBaseActivity) {
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
                //new InstabugService().invokeBugReporting();

                Intent intent=new Intent(Intent.ACTION_SEND);
                String[] recipients={"support@cenesgroup.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);

                String phoneDetails = "Device : "+CenesUtils.getDeviceManufacturer()+" "+CenesUtils.getDeviceModel()+" "+CenesUtils.getDeviceVersion()+"\n";

                intent.putExtra(Intent.EXTRA_TEXT,phoneDetails);
                intent.setType("text/html");
                startActivity(Intent.createChooser(intent, "Send mail"));
            }
        });

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();

        loggedInUser = userManager.getUser();

        if (loggedInUser != null && loggedInUser.getPicture() != null && loggedInUser.getPicture() != "null") {
            // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
            Glide.with(getCenesActivity()).load(loggedInUser.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(homeProfilePic);
        }


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {

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

    }

}
