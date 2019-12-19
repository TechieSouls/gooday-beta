package com.cenesbeta.fragment;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.DiaryActivity;
import com.cenesbeta.activity.GatheringScreenActivity;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Alarm;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.AlarmManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.metime.MeTimeFragment;
import com.cenesbeta.fragment.profile.DeleteAccountFragment;
import com.cenesbeta.fragment.profile.ProfileFragment;
import com.cenesbeta.service.AlarmReceiver;
import com.cenesbeta.util.RoundedImageView;
import com.facebook.login.LoginManager;

import java.util.List;

/**
 * Created by rohan on 9/11/17.
 */

public class NavigationFragment extends CenesFragment {

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private UrlManager urlManager;
    private ApiManager apiManager;
    private AlarmManager alarmManager;

    RoundedImageView ivProfile;
    TextView tvUsername, tvNotifications, tvCalendarSync, tvHolidayCalendar,
            tvHelpAndFeedback, tvAbout, tvAppSettings;
    LinearLayout llProfileSection;

    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.navigation_fragment, container, false);

        init(v);

        return v;
    }

    public void init(View v) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        alarmManager = coreManager.getAlarmManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();

        user = userManager.getUser();

        setFragmentManager();

        ivProfile = (RoundedImageView) v.findViewById(R.id.ivProfile);
        tvUsername = (TextView) v.findViewById(R.id.tvUsername);
        tvNotifications = (TextView) v.findViewById(R.id.tvNotifications);
        llProfileSection = (LinearLayout) v.findViewById(R.id.ll_profile_section);
        tvCalendarSync = (TextView) v.findViewById(R.id.tvCalendarSync);
        tvHolidayCalendar = (TextView) v.findViewById(R.id.tvHolidayCalendar);
        tvHelpAndFeedback = (TextView) v.findViewById(R.id.tvHelpAndFeedback);
        tvAbout = (TextView) v.findViewById(R.id.tvAbout);
        tvAppSettings = (TextView) v.findViewById(R.id.tvAppSettings);

        tvNotifications.setOnClickListener(itemClickListener);
        llProfileSection.setOnClickListener(itemClickListener);
        tvCalendarSync.setOnClickListener(itemClickListener);
        tvHolidayCalendar.setOnClickListener(itemClickListener);
        tvHelpAndFeedback.setOnClickListener(itemClickListener);
        tvAbout.setOnClickListener(itemClickListener);
        tvAppSettings.setOnClickListener(itemClickListener);

        refreshUserInfo(user);

    }

    public void setFragmentManager() {
        if (getActivity() instanceof GatheringScreenActivity) {
            fragmentManager = ((GatheringScreenActivity) getActivity()).fragmentManager;
        } else if (getActivity() instanceof DiaryActivity) {
            fragmentManager = ((DiaryActivity) getActivity()).fragmentManager;
        }
    }

    public void closeDrawer() {
        if (getActivity() instanceof GatheringScreenActivity) {
            GatheringScreenActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (getActivity() instanceof DiaryActivity) {
            DiaryActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (getActivity() instanceof CenesBaseActivity) {
            ((CenesBaseActivity)getActivity()).mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void replaceFragment(Fragment fragment, String tag) {
        if (getActivity() instanceof GatheringScreenActivity) {
            ((GatheringScreenActivity) getActivity()).replaceFragment(fragment, tag);
        } else if (getActivity() instanceof DiaryActivity) {
            ((DiaryActivity) getActivity()).replaceFragment(fragment, tag);
        } else if (getActivity() instanceof CenesBaseActivity) {
            ((CenesBaseActivity) getActivity()).replaceFragment(fragment, tag);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("On Resume called");
        refreshUserInfo(user);
    }

    public void refreshUserInfo(User user) {
        //if (user != null && user.getPicture() != null && user.getPicture() != "null") {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.circleCrop();
            requestOptions.placeholder(R.drawable.profile_pic_no_image);
            Glide.with(getActivity()).load(user.getPicture()).apply(requestOptions).into(ivProfile);
        //}
        tvUsername.setText(user.getName());
    }

    public View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeDrawer();
            switch (v.getId()) {
                case R.id.tvNotifications:
                    //checkFragmentBeforeOpening(new NotificationFragment(), NotificationFragment.TAG);
                    getFragmentManager().popBackStack();
                    replaceFragment(new NotificationFragment(), NotificationFragment.TAG);
                    break;
                case R.id.ll_profile_section:
                    System.out.println("Profile Fragment Clicked...");
                    //checkFragmentBeforeOpening(new ProfileFragment(), ProfileFragment.TAG);
                    getFragmentManager().popBackStack();
                    replaceFragment(new ProfileFragment(), ProfileFragment.TAG);
                    break;
                case R.id.tvCalendarSync:
                    //checkFragmentBeforeOpening(new CalenderSyncFragment(), CalenderSyncFragment.TAG);
                    getFragmentManager().popBackStack();
                    replaceFragment(new CalenderSyncFragment(), CalenderSyncFragment.TAG);
                    break;
                case R.id.tvHolidayCalendar:
                    //checkFragmentBeforeOpening(new HolidaySyncFragment(), HolidaySyncFragment.TAG);
                    getFragmentManager().popBackStack();
                    replaceFragment(new HolidaySyncFragment(), HolidaySyncFragment.TAG);
                    break;
                case R.id.tvHelpAndFeedback:
                    //checkFragmentBeforeOpening(new HelpFeedbackFragment(), HelpFeedbackFragment.TAG);
                    getFragmentManager().popBackStack();
                    replaceFragment(new HelpFeedbackFragment(), HelpFeedbackFragment.TAG);
                    break;
                case R.id.tvAbout:
                   // checkFragmentBeforeOpening(new AboutUsFragment(), AboutUsFragment.TAG);
                    getFragmentManager().popBackStack();
                    replaceFragment(new AboutUsFragment(), AboutUsFragment.TAG);
                    break;
                case R.id.tvAppSettings:
                    getFragmentManager().popBackStack();
                    replaceFragment(new DeleteAccountFragment(), DeleteAccountFragment.TAG);
                    break;

            }
        }
    };

    class LogoutTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String queryStr = strings[0];
            apiManager.logout(user, queryStr, getCenesActivity());
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            List<Alarm> alarms = alarmManager.getAlarms();
            for (Alarm alarm : alarms) {
                int j = 0;
                while (j++ < 7) {
                    cancelAlarm(Integer.valueOf("" + alarm.getAlarmId() + 77 + j));
                }
            }
            alarmManager.deleteAll();
            userManager.deleteAll();
            getCenesActivity().getSharedPreferences("CenesPrefs", Context.MODE_PRIVATE).edit().clear().apply();
            try {
                LoginManager.getInstance().logOut();
            } catch (Exception e) {

            }

            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;
            //startActivity(new Intent(getActivity(), ChoiceActivity.class));
            startActivity(new Intent(getActivity(), GuestActivity.class));
            getActivity().finishAffinity();
        }
    }

    private void cancelAlarm(int RSQ) {
        System.out.println("\n\n***\nAlarm is cancelled for alarmId " + RSQ + "\n***\n");
        Intent intent = new Intent(getActivity().getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getBaseContext(), RSQ, intent, 0);
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void checkFragmentBeforeOpening(Fragment fragment, String tag) {
        Fragment fragmentFromBackStack = getFragmentPresentInBackStack();
        if (fragmentFromBackStack != null) {
            fragmentManager.popBackStack(fragmentFromBackStack.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        replaceFragment(fragment, tag);
    }

    FragmentManager fragmentManager;

    public Fragment getFragmentPresentInBackStack() {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && (fragment instanceof ProfileFragment || fragment instanceof MeTimeFragment || fragment instanceof CalenderSyncFragment || fragment instanceof HolidaySyncFragment || fragment instanceof NotificationFragment || fragment instanceof HelpFeedbackFragment))
                    return fragment;
            }
        }
        return null;
    }
}
