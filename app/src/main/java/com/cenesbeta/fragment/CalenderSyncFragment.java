package com.cenesbeta.fragment;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.Manager.AlertManager;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.DeviceManager;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.Manager.ValidationManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesActivity;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.livesdk.com.microsoft.live.LiveAuthClient;
import com.cenesbeta.livesdk.com.microsoft.live.LiveAuthException;
import com.cenesbeta.livesdk.com.microsoft.live.LiveAuthListener;
import com.cenesbeta.livesdk.com.microsoft.live.LiveConnectClient;
import com.cenesbeta.livesdk.com.microsoft.live.LiveConnectSession;
import com.cenesbeta.livesdk.com.microsoft.live.LiveStatus;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by rohan on 10/10/17.
 */

public class CalenderSyncFragment extends CenesFragment implements GoogleApiClient.OnConnectionFailedListener, LiveAuthListener {

    public final static String TAG = "CalenderSyncFragment";

    ImageView googleCalImg;
    ImageView ivGoogleCheckmark, ivOutlookCheckmark;
    LinearLayout llOutlook;

    private SignInButton btnSignIn;
    //private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;


    private LiveAuthClient mAuthClient;
    private LiveConnectClient mLiveConnectClient;

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    ValidationManager validationManager;
    InternetManager internetManager;
    UrlManager urlManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    String googleAccessToken, googleEmail;
    private User loggedInUser;
    private JSONArray syncStatusJson;


    RelativeLayout rlHeader, rlSignupCalsyncHeader;
    ImageView ivProfile, ivCalsyncInstabug, homeIcon;
    Button btnFinishCalSync;
    public static boolean isFirstLogin;

    private static final int RC_SIGN_IN = 007;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_calender_sync, container, false);

        isFirstLogin = ((CenesActivity) getActivity()).sharedPrefs.getBoolean("isFirstLogin", true);

        //if (getActivity() instanceof ChoiceActivity) {
          //  isFirstLogin = ((ChoiceActivity) getActivity()).sharedPrefs.getBoolean("isFirstLogin", true);
        //} else
            if (getActivity() instanceof CenesBaseActivity) {
            isFirstLogin = ((CenesBaseActivity) getActivity()).sharedPrefs.getBoolean("isFirstLogin", true);
        } else if (getActivity() instanceof GuestActivity) {
            isFirstLogin = ((GuestActivity) getActivity()).sharedPrefs.getBoolean("isFirstLogin", true);
        }

        init(v);


        mAuthClient = new LiveAuthClient(getCenesActivity(), CenesConstants.OutlookClientId);

        btnSignIn.setOnClickListener(onClickListener);
        //googleCalImg.setOnClickListener(onClickListener);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void nextClickListener() {
        //CenesUtils.logEntries(loggedInUser, "Calendar Sync Screen Next Clicked", getCenesActivity().getApplicationContext());
        startActivity(new Intent(getCenesActivity(), CenesBaseActivity.class));
        getActivity().finish();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_sign_in:
                    //showProgressDialog();
                    proceedWithGoogleSync();

                    break;
                case R.id.google_cal_img:
                    //btnSignIn.performClick();
                    break;
                case R.id.ll_outlook:
                    outlookAuth();
                    //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://login.microsoftonline.com/common/oauth2/v2.0/authorize?client_id=0b228193-26f2-4837-b791-ffd7eab7441e&redirect_uri=http://localhost:8181/api/guest/outlookAuthCode&response_type=code&scope=Calendars.Read"));
                    //startActivity(browserIntent);
                    break;
            }
        }
    };

    public void proceedWithGoogleSync() {
        /*if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getCenesActivity());
            mGoogleApiClient.disconnect();
        }*/


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().
                        requestServerAuthCode(CenesConstants.GoogleWebClientid, true).
                        requestScopes(new Scope("https://www.googleapis.com/auth/calendar"), new Scope("https://www.googleapis.com/auth/calendar.readonly")).build();

//        mGoogleApiClient = new GoogleApiClient.Builder(getCenesActivity())
//                .enableAutoManage(getCenesActivity(), CalenderSyncFragment.this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        try {
            mGoogleSignInClient = GoogleSignIn.getClient(getCenesActivity(), gso);
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getCenesActivity());
            if (account != null) {
                signOutGoogleAccount();
            } else {
                signInGoogleAccount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        //onStart1();

        //hideProgressDialog();

    }


    public void init(View v) {

        //----------------------------------
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        alertManager = coreManager.getAlertManager();
        validationManager = coreManager.getValidatioManager();
        internetManager = coreManager.getInternetManager();
        urlManager = coreManager.getUrlManager();
        deviceManager = coreManager.getDeviceManager();
        apiManager = coreManager.getApiManager();

        loggedInUser = userManager.getUser();

        //googleCalImg = (ImageView) v.findViewById(R.id.google_cal_img);
        btnSignIn = (SignInButton) v.findViewById(R.id.btn_sign_in);
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        googleCalImg = (ImageView) v.findViewById(R.id.google_cal_img);
        llOutlook = (LinearLayout) v.findViewById(R.id.ll_outlook);

        ivGoogleCheckmark = (ImageView) v.findViewById(R.id.iv_google_checkmark);
        ivOutlookCheckmark = (ImageView) v.findViewById(R.id.iv_outlook_checkmark);

        llOutlook.setOnClickListener(onClickListener);

        rlHeader = (RelativeLayout) v.findViewById(R.id.rl_header);
        rlSignupCalsyncHeader = (RelativeLayout) v.findViewById(R.id.rl_signup_calsync_header);
        homeIcon = (ImageView) v.findViewById(R.id.home_icon);

        ivProfile = (ImageView) v.findViewById(R.id.home_profile_pic);
        ivCalsyncInstabug = (ImageView) v.findViewById(R.id.iv_calsync_instabug);
        btnFinishCalSync = (Button) v.findViewById(R.id.btn_finish_cal_sync);

        if (isFirstLogin) {
            rlHeader.setVisibility(View.GONE);
            btnFinishCalSync.setVisibility(View.VISIBLE);
            rlSignupCalsyncHeader.setVisibility(View.VISIBLE);
        } else {

            ((CenesBaseActivity)getActivity()).hideFooter();
            rlHeader.setVisibility(View.VISIBLE);
            rlSignupCalsyncHeader.setVisibility(View.GONE);
            btnFinishCalSync.setVisibility(View.GONE);

            if (loggedInUser != null && loggedInUser.getPicture() != null && loggedInUser.getPicture() != "null") {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.circleCrop();
                requestOptions.placeholder(R.drawable.profile_pic_no_image);
                Glide.with(getActivity()).load(loggedInUser.getPicture()).apply(requestOptions).into(ivProfile);
            }

            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof CenesBaseActivity) {
                        ((CenesBaseActivity)getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            homeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //getActivity().onBackPressed();

                    ((CenesBaseActivity)getActivity()).clearAllFragmentsInBackstack();
                    HomeFragment homeFragment = new HomeFragment();
                    ((CenesBaseActivity)getActivity()).replaceFragment(homeFragment, null);
                }
            });

            if (internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {
                new SyncStatusTask().execute();
            }
        }

        btnFinishCalSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CenesBaseActivity.class));
                getActivity().finish();
            }
        });

    }

    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
        if (status == LiveStatus.CONNECTED) {
            //resultTextView.setText(R.string.auth_yes);

            String refreshToken = session.getRefreshToken();
            String authCode = session.getAuthenticationToken();
            String outlookAccessToken = session.getAccessToken();
            mLiveConnectClient = new LiveConnectClient(session);

            JSONObject postJson = new JSONObject();
            try {
                postJson.put("accessToken", outlookAccessToken);
                postJson.put("refreshToken", refreshToken);
            } catch (Exception e) {
                e.printStackTrace();
            }

            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
            try {
                JSONObject props = new JSONObject();
                props.put("CalendarType","Outlook");
                props.put("Action","Sync Begins");
                props.put("UserEmail",loggedInUser.getEmail());
                props.put("UserName",loggedInUser.getName());
                mixpanel.track("SyncCalendar", props);

            } catch (Exception e) {
                e.printStackTrace();
            }
            new OutlookEventsTask().execute(postJson);
        } else {
            //resultTextView.setText(R.string.auth_no);
            Toast.makeText(getCenesActivity(), "Could Not Authenticate", Toast.LENGTH_LONG).show();
            mLiveConnectClient = null;
        }
    }

    public void onAuthError(LiveAuthException exception, Object userState) {
        //resultTextView.setText(getResources().getString(R.string.auth_err) + exception.getMessage());
        //Toast.makeText(getCenesActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
        mLiveConnectClient = null;
    }

    /**
     * Called when the user clicks the Authenticate button. Requests the office.onenote_create scope.
     */
    public void outlookAuth() {
        //super.onStart();
        try {

            Iterable<String> scopes = Arrays.asList("wl.calendars,wl.offline_access ");
            mAuthClient.login(getActivity(), scopes, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void googleSigningDialog() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }*/

    private void signInGoogleAccount() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            //Log.e(TAG, "display name: " + acct.getDisplayName());
            //String personName = acct.getDisplayName();
            //String personPhotoUrl = acct.getPhotoUrl().toString();
            googleEmail = acct.getEmail();

           Account account = new Account(googleEmail, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
           String mScope="oauth2:server:client_id:"+CenesConstants.GoogleWebClientid+":api_scope:"+"https://www.googleapis.com/auth/userinfo.email";

           try {
               String refreshToken =  GoogleAuthUtil.getToken(getActivity(), account, mScope);
                Log.e(TAG, refreshToken);
           } catch (Exception e) {
               e.printStackTrace();
           }


            try {

                //googleAccessToken = acct.getIdToken();
                new CalenderSyncTask().execute();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String refToken = account.getIdToken();
            String authCode = account.getServerAuthCode();
            // Signed in successfully, show authenticated UI.
            googleEmail = account.getEmail();

            try {
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                try {
                    JSONObject props = new JSONObject();
                    props.put("CalendarType","Google");
                    props.put("Action","Sync Begins");
                    props.put("UserEmail",loggedInUser.getEmail());
                    props.put("UserName",loggedInUser.getName());
                    mixpanel.track("SyncCalendar", props);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //googleAccessToken = acct.getIdToken();
                new CalenderSyncTask().execute(authCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /*private void signOutGoogleAccount() {

        if (mGoogleApiClient != null && (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected())) {
            if(mGoogleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                signInGoogleAccount();
                            }
                        });
            } else {
                signOutGoogleAccount();
            }
        } else {
            signInGoogleAccount();
        }
    }*/

    private void signOutGoogleAccount() {
        mGoogleSignInClient.signOut().addOnCompleteListener(getCenesActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                signInGoogleAccount();
            }
        });
    }

  /*  //@Override
    public void onStart1() {
        //super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("Google Signin", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }*/

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("Connection", "onConnectionFailed:" + connectionResult);
    }

    ProgressDialog mProgressDialog;

    private void showProgressDialog() {

        mProgressDialog = new ProgressDialog(getCenesActivity());
        mProgressDialog.setMessage("Logging...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.hide();
        mProgressDialog.dismiss();
        mProgressDialog = null;

    }


    class CalenderSyncTask extends AsyncTask<String, String, String> {
        private Exception exception;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(getCenesActivity());
            progressDialog.setMessage("Processing..");
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {

                String authCode = urls[0];
                googleAccessToken = GoogleAuthUtil.getToken(
                        getCenesActivity().getApplicationContext(),
                        googleEmail, "oauth2:https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.readonly");
                Log.e("Google Access Token  ", googleAccessToken);
                User user = userManager.getUser();
                user.setApiUrl(urlManager.getApiUrl("dev"));
                String queryStr = "?access_token=" + googleAccessToken + "&user_id=" + user.getUserId()+"&serverAuthCode="+authCode;
                JSONArray events = apiManager.googleEvents(user, queryStr, getCenesActivity());

            } catch (Exception e) {
                this.exception = e;
                return null;
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String param) {
            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;

            if (param != null) {
                Toast.makeText(getCenesActivity(), "Google Calendar Synced", Toast.LENGTH_LONG).show();
                ivGoogleCheckmark.setImageResource(R.drawable.circle_selected);

                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                try {
                    JSONObject props = new JSONObject();
                    props.put("CalendarType","Google");
                    props.put("Action","Sync Success");
                    props.put("UserEmail",loggedInUser.getEmail());
                    props.put("UserName",loggedInUser.getName());
                    mixpanel.track("SyncCalendar", props);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                getCenesActivity().showRequestTimeoutDialog();
            }
            /*if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.stopAutoManage(getCenesActivity());
                mGoogleApiClient.disconnect();
            }*/
        }
    }

    class OutlookEventsTask extends AsyncTask<JSONObject, String, String> {
        private ProgressDialog mpProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mpProgressDialog = new ProgressDialog(getCenesActivity());
            mpProgressDialog.setMessage("Syncing...");
            mpProgressDialog.setIndeterminate(false);
            mpProgressDialog.setCanceledOnTouchOutside(false);
            mpProgressDialog.setCancelable(false);
            mpProgressDialog.show();
        }

        @Override
        protected String doInBackground(JSONObject... voids) {
            //CenesUtils.logEntries(loggedInUser, "Outlook Sync Processing STARTS", getCenesActivity().getApplicationContext());

            try {
                JSONObject postJson = voids[0];
                String postParams = "user_id=" + loggedInUser.getUserId();
                try {
                    postParams += "&access_token="+postJson.getString("accessToken");
                    postParams += "&refreshToken="+postJson.getString("refreshToken");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                User user = userManager.getUser();
                user.setApiUrl(urlManager.getApiUrl("dev"));
                String queryStr = "?"+postParams;
                JSONArray events = apiManager.syncOutlookEvents(user, queryStr, getCenesActivity());

            } catch (Exception e) {
                e.printStackTrace();
            }

           // CenesUtils.logEntries(loggedInUser, "Outlook Sync Processing ENDS", getCenesActivity().getApplicationContext());

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            mpProgressDialog.hide();
            mpProgressDialog.dismiss();
            mpProgressDialog = null;

            Toast.makeText(getCenesActivity(), "Outlook Calendar Synced", Toast.LENGTH_LONG).show();
            ivOutlookCheckmark.setImageResource(R.drawable.circle_selected);

            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
            try {
                JSONObject props = new JSONObject();
                props.put("CalendarType","Outlook");
                props.put("Action","Sync Success");
                props.put("UserEmail",loggedInUser.getEmail());
                props.put("UserName",loggedInUser.getName());
                mixpanel.track("SyncCalendar", props);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class SyncStatusTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getCenesActivity());
            pDialog.setMessage("Fetching..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {
            try {
                loggedInUser.setApiUrl(urlManager.getApiUrl("dev"));
                JSONObject response = apiManager.getUserCalendarSyncStatus(loggedInUser, loggedInUser.getUserId(), getCenesActivity());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog = null;
            }

            System.out.println("Calendar Sync Status : "+response.toString());
            try {
                if (response != null && response.getBoolean("success")) {
                    ///Toast.makeText(getContext(),"Information Update Successfully",Toast.LENGTH_SHORT).show();
                    syncStatusJson = response.getJSONArray("data");

                    if (syncStatusJson != null && syncStatusJson.length() > 0) {
                        for (int i = 0; i < syncStatusJson.length(); i++) {
                            JSONObject syncStatusObj = syncStatusJson.getJSONObject(i);
                            JSONObject propertyObj = syncStatusObj.getJSONObject("cenesProperty");
                            if ("outlook_calendar".equals(propertyObj.getString("name")) && syncStatusObj.getString("value").equals("true")) {
                                ivOutlookCheckmark.setImageResource(R.drawable.circle_selected);
                            }
                            if ("google_calendar".equals(propertyObj.getString("name")) && syncStatusObj.getString("value").equals("true")) {
                                ivGoogleCheckmark.setImageResource(R.drawable.circle_selected);
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Error in updating Info", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
