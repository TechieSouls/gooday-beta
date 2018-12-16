package com.cenes.fragment;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.UrlManager;
import com.cenes.R;
import com.cenes.activity.AlarmActivity;
import com.cenes.activity.ChoiceActivity;
import com.cenes.activity.DiaryActivity;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.activity.GuestActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.ReminderActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.Alarm;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.AlarmManager;
import com.cenes.database.manager.UserManager;
import com.cenes.service.AlarmReceiver;
import com.cenes.util.CenesTextView;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

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

    ImageView ivProfile;
    CenesTextView tvUsername, tvNotifications, tvProfile, tvMeTime, tvCalendarSync, tvHolidayCalendar,
            tvHelpAndFeedback, tvAbout, tvLogout;
    TextView tvNotificationCount, tvNotificationCountPic;

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

        ivProfile = (ImageView) v.findViewById(R.id.ivProfile);
        tvUsername = (CenesTextView) v.findViewById(R.id.tvUsername);
        tvNotifications = (CenesTextView) v.findViewById(R.id.tvNotifications);
        tvProfile = (CenesTextView) v.findViewById(R.id.tvProfile);
        tvMeTime = (CenesTextView) v.findViewById(R.id.tvMeTime);
        tvCalendarSync = (CenesTextView) v.findViewById(R.id.tvCalendarSync);
        tvHolidayCalendar = (CenesTextView) v.findViewById(R.id.tvHolidayCalendar);
        tvHelpAndFeedback = (CenesTextView) v.findViewById(R.id.tvHelpAndFeedback);
        tvAbout = (CenesTextView) v.findViewById(R.id.tvAbout);
        tvLogout = (CenesTextView) v.findViewById(R.id.tvLogout);
        tvNotificationCount = (TextView) v.findViewById(R.id.tv_notification_count);
        tvNotificationCountPic = (TextView) v.findViewById(R.id.tv_notification_count_pic);


        ivProfile.setOnClickListener(itemClickListener);
        tvUsername.setOnClickListener(itemClickListener);
        tvNotifications.setOnClickListener(itemClickListener);
        tvProfile.setOnClickListener(itemClickListener);
        tvMeTime.setOnClickListener(itemClickListener);
        tvCalendarSync.setOnClickListener(itemClickListener);
        tvHolidayCalendar.setOnClickListener(itemClickListener);
        tvHelpAndFeedback.setOnClickListener(itemClickListener);
        tvAbout.setOnClickListener(itemClickListener);
        tvLogout.setOnClickListener(itemClickListener);

        refreshUserInfo(user);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                new NotificationCountTask().execute();
            }
        });
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

    public void closeDrawer() {
        if (getActivity() instanceof HomeScreenActivity) {
            HomeScreenActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (getActivity() instanceof ReminderActivity) {
            ReminderActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (getActivity() instanceof GatheringScreenActivity) {
            GatheringScreenActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (getActivity() instanceof DiaryActivity) {
            DiaryActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (getActivity() instanceof AlarmActivity) {
            AlarmActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void replaceFragment(Fragment fragment, String tag) {
        if (getActivity() instanceof HomeScreenActivity) {
            ((HomeScreenActivity) getActivity()).replaceFragment(fragment, tag);
        } else if (getActivity() instanceof ReminderActivity) {
            ((ReminderActivity) getActivity()).replaceFragment(fragment, tag);
        } else if (getActivity() instanceof GatheringScreenActivity) {
            ((GatheringScreenActivity) getActivity()).replaceFragment(fragment, tag);
        } else if (getActivity() instanceof DiaryActivity) {
            ((DiaryActivity) getActivity()).replaceFragment(fragment, tag);
        } else if (getActivity() instanceof AlarmActivity) {
            ((AlarmActivity) getActivity()).replaceFragment(fragment, tag);
        }
    }

    public void refreshUserInfo(User user) {
        if (user != null && user.getPicture() != null && user.getPicture() != "null") {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.circleCrop();
            requestOptions.placeholder(R.drawable.default_profile_icon);
            Glide.with(getActivity()).load(user.getPicture()).apply(requestOptions).into(ivProfile);
        }
        tvUsername.setText(user.getName());
    }

    public View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeDrawer();
            switch (v.getId()) {
                case R.id.tvNotifications:
                    checkFragmentBeforeOpening(new NotificationFragment(), NotificationFragment.TAG);
                    break;
                case R.id.tvProfile:
                    checkFragmentBeforeOpening(new ProfileFragment(), ProfileFragment.TAG);
                    break;
                case R.id.tvMeTime:
                    checkFragmentBeforeOpening(new MeTimeFragment(), MeTimeFragment.TAG);
                    break;
                case R.id.tvCalendarSync:
                    checkFragmentBeforeOpening(new CalenderSyncFragment(), CalenderSyncFragment.TAG);
                    break;
                case R.id.tvHolidayCalendar:
                    checkFragmentBeforeOpening(new HolidaySyncFragment(), HolidaySyncFragment.TAG);
                    break;
                case R.id.tvHelpAndFeedback:
                    checkFragmentBeforeOpening(new HelpFeedbackFragment(), HelpFeedbackFragment.TAG);
                    break;
                case R.id.tvAbout:
                    checkFragmentBeforeOpening(new AboutUsFragment(), AboutUsFragment.TAG);
                    break;
                case R.id.tvLogout:
                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                    alert.setTitle("Log Out");
                    alert.setMessage("Are you sure you want to Log Out?");
                    alert.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String queryStr = "?userId=" + user.getUserId() + "&deviceType=android";
                                new LogoutTask().execute(queryStr);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
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

    class NotificationCountTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(getActivity());
 //           progressDialog.setCancelable(false);
  //          progressDialog.setMessage("Loading...");
            //progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... strings) {
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?userId="+user.getUserId();
            return apiManager.getNotificationCounts(user,queryStr,getCenesActivity());
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);

            try {
                tvNotificationCount.setText(String.valueOf(s.getInt("data")));
                tvNotificationCountPic.setText(String.valueOf(s.getInt("data")));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //progressDialog.hide();
            //progressDialog.dismiss();
            //progressDialog = null;
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
