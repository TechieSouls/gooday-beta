package com.cenesbeta.fragment.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.api.UserAPI;
import com.cenesbeta.bo.CalenadarSyncToken;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.fragment.CalenderSyncFragment;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.livesdk.com.microsoft.live.LiveAuthClient;
import com.cenesbeta.livesdk.com.microsoft.live.LiveAuthException;
import com.cenesbeta.livesdk.com.microsoft.live.LiveAuthListener;
import com.cenesbeta.livesdk.com.microsoft.live.LiveConnectClient;
import com.cenesbeta.livesdk.com.microsoft.live.LiveConnectSession;
import com.cenesbeta.livesdk.com.microsoft.live.LiveStatus;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.CustomLoadingDialog;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileMyCalendarsSocialFragment extends CenesFragment {

    private static Integer GOOGLE_SIGNIN_REQUEST = 1001;


    public enum CalendarType {Google, Outlook};
    public enum MyCalendarActions {Load, Sync, Delete}

    private TextView tvCalendarName, tvCalendarGuideline;
    private Button btnCalendarActionDelete, btnCalendarActionSync;

    private CoreManager coreManager;
    private UserManager userManager;
    private User loggedInUser;
    public CalendarType calendarSelected;
    private CalenadarSyncToken selectedCalendarSyncToken;
    private Map<CalendarType, String> notSyncedMessageMap;
    private MyCalendarActions myCalendarActions;
    private GoogleSignInClient mGoogleSignInClient;
    private String googleWebPermissions = "oauth2:https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.readonly";
    private LiveAuthClient mAuthClient;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_my_calendars_social_item, container, false);

        tvCalendarName = (TextView) view.findViewById(R.id.tv_calendar_name);
        tvCalendarGuideline = (TextView) view.findViewById(R.id.tv_calendar_guideline);
        btnCalendarActionDelete = (Button) view.findViewById(R.id.btn_calendar_action_delete);
        btnCalendarActionSync = (Button) view.findViewById(R.id.btn_calendar_action_sync);

        btnCalendarActionDelete.setOnClickListener(onClickListener);
        btnCalendarActionSync.setOnClickListener(onClickListener);

        coreManager = getCenesActivity().getCenesApplication().getCoreManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();

        notSyncedMessageMap = new HashMap<>();
        notSyncedMessageMap.put(CalendarType.Google, "Not Synced to Google Calendar");
        notSyncedMessageMap.put(CalendarType.Outlook, "Not Synced to Outlook Calendar");

        myCalendarActions = MyCalendarActions.Load;
        mAuthClient = new LiveAuthClient(getCenesActivity(), CenesConstants.OutlookClientId);
        loadUserSyncTokens();
        return view;

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.btn_calendar_action_delete:
                    deleteAccontBySyncId(selectedCalendarSyncToken.getRefreshTokenId());
                    break;

                case R.id.btn_calendar_action_sync:

                    if (calendarSelected.equals(CalendarType.Google)) {
                        googleSyncBtnPressed();
                    } else if (calendarSelected.equals(CalendarType.Outlook)) {
                        outlookAuth();
                    }
                    break;
            }
        }
    };

    LiveAuthListener outlookAuthListener = new LiveAuthListener() {
        @Override
        public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
            if (status == LiveStatus.CONNECTED) {

                String refreshToken = session.getRefreshToken();
                String outlookAccessToken = session.getAccessToken();

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
                syncOutlookCalendar(outlookAccessToken, refreshToken);
            } else {
                //resultTextView.setText(R.string.auth_no);
                Toast.makeText(getCenesActivity(), "Could Not Authenticate", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onAuthError(LiveAuthException exception, Object userState) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGNIN_REQUEST) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void googleSyncBtnPressed() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().
                        requestServerAuthCode(CenesConstants.GoogleWebClientid, true).
                        requestScopes(new Scope("https://www.googleapis.com/auth/calendar"), new Scope("https://www.googleapis.com/auth/calendar.readonly")).build();


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
    }

    private void signOutGoogleAccount() {
        mGoogleSignInClient.signOut().addOnCompleteListener(getCenesActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                signInGoogleAccount();
            }
        });
    }
    private void signInGoogleAccount() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGNIN_REQUEST);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String refToken = account.getIdToken();
            String authCode = account.getServerAuthCode();
            // Signed in successfully, show authenticated UI.
            String googleEmail = account.getEmail();

            try {
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                try {
                    JSONObject props = new JSONObject();
                    props.put("CalendarType","Google");
                    props.put("Action","Sync Begins");
                    props.put("UserEmail",loggedInUser.getEmail());
                    props.put("UserName",loggedInUser.getName());
                    props.put("Device","Android");
                    mixpanel.track("SyncCalendar", props);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                syncCalendar(googleEmail, authCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace();
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
            try {
                JSONObject props = new JSONObject();
                props.put("CalendarType","Google");
                props.put("Action","Sync Begins");
                props.put("UserEmail",loggedInUser.getEmail());
                props.put("UserName",loggedInUser.getName());
                props.put("Device","Android");
                props.put("Error",e.getStatusMessage());
                mixpanel.track("SyncCalendar", props);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Called when the user clicks the Authenticate button. Requests the office.onenote_create scope.
     */
    public void outlookAuth() {
        //super.onStart();
        try {
            Iterable<String> scopes = Arrays.asList("wl.calendars,wl.offline_access ");
            mAuthClient.login(getActivity(), scopes, outlookAuthListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncOutlookCalendar(String accessToken, String refreshToken) {

        JSONObject postData = new JSONObject();
        try {
            postData.put("accessToken", accessToken);
            postData.put("userId", loggedInUser.getUserId());
            postData.put("refreshToken", refreshToken);

        } catch (Exception e) {
            e.printStackTrace();
        }

        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+UserAPI.post_sync_outlook_calendar);
        asyncTaskDto.setPostData(postData);
        postAsyncTaskCall(asyncTaskDto);

    }

    public void syncCalendar(String googleEmail, String authCode) {
        myCalendarActions = MyCalendarActions.Sync;
        try {
            String googleAccessToken = GoogleAuthUtil.getToken(
                    getCenesActivity().getApplicationContext(),
                    googleEmail, googleWebPermissions);

            JSONObject postData = new JSONObject();
            postData.put("accessToken", googleAccessToken);
            postData.put("userId", loggedInUser.getUserId());
            postData.put("serverAuthCode", authCode);

            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+UserAPI.post_sync_google_calendar);
            asyncTaskDto.setPostData(postData);

            postAsyncTaskCall(asyncTaskDto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAccontBySyncId(Integer syncId) {

        try {
            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setQueryStr("calendarSyncTokenId="+syncId);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+UserAPI.delete_sync_token);
            deleteAsyncTaskCall(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadUserSyncTokens() {
        try {
            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ UserAPI.delete_sync_token);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setQueryStr("userId="+loggedInUser.getUserId());
            getAsyncTaskCall(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAsyncTaskCall(AsyncTaskDto asyncTaskDto) {
        try {

            new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        if (myCalendarActions.equals(MyCalendarActions.Load)) {
                            boolean success = response.getBoolean("success");
                            if (success == true) {

                                Gson gson = new GsonBuilder().create();
                                Type listType = new TypeToken<List<CalenadarSyncToken>>(){}.getType();
                                List<CalenadarSyncToken> calenadarSyncTokens = gson.fromJson(response.getJSONArray("data").toString(), listType);
                                for (CalenadarSyncToken calenadarSyncToken: calenadarSyncTokens) {
                                    if (calenadarSyncToken.getAccountType().equals(calendarSelected.toString())) {
                                        selectedCalendarSyncToken = calenadarSyncToken;
                                        break;
                                    }
                                }
                                if (selectedCalendarSyncToken != null) {

                                    tvCalendarName.setText("Account: "+ selectedCalendarSyncToken.getEmailId());
                                    btnCalendarActionDelete.setVisibility(View.VISIBLE);
                                    btnCalendarActionSync.setVisibility(View.GONE);

                                } else {
                                    btnCalendarActionDelete.setVisibility(View.GONE);
                                    btnCalendarActionSync.setVisibility(View.VISIBLE);

                                    if (calendarSelected.equals(CalendarType.Google)) {
                                        tvCalendarName.setText(notSyncedMessageMap.get(CalendarType.Google));
                                    } else if (calendarSelected.equals(CalendarType.Outlook)) {
                                        tvCalendarName.setText(notSyncedMessageMap.get(CalendarType.Outlook));
                                    }
                                }
                            } else {
                                showAlert("Error", response.getString("message"));
                            }
                        } else if (myCalendarActions.equals(MyCalendarActions.Sync)) {

                            //Refresh Home Page
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAsyncTaskCall(AsyncTaskDto asyncTaskDto) {
        try {

            new ProfileAsyncTask.CommonDeleteRequestTask(new ProfileAsyncTask.CommonDeleteRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        boolean success = response.getBoolean("success");
                        if (success == true) {

                            if (calendarSelected.equals(CalendarType.Google)) {
                                tvCalendarName.setText(notSyncedMessageMap.get(CalendarType.Google).toString());
                            } else if (calendarSelected.equals(CalendarType.Outlook)) {
                                tvCalendarName.setText(notSyncedMessageMap.get(CalendarType.Outlook).toString());
                            }
                            //Reresh Home Screen

                            btnCalendarActionDelete.setVisibility(View.GONE);
                            btnCalendarActionSync.setVisibility(View.VISIBLE);

                            //Mix Panel Tracking
                            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                            try {
                                JSONObject props = new JSONObject();
                                props.put("CalendarType","Google");
                                props.put("Action","Delete Calendar");
                                props.put("UserEmail",loggedInUser.getEmail());
                                props.put("UserName",loggedInUser.getName());
                                props.put("Device","Android");
                                mixpanel.track("SyncCalendar", props);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            showAlert("Error", response.getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postAsyncTaskCall(AsyncTaskDto asyncTaskDto) {

        try {
            final CustomLoadingDialog customLoadingDialog = new CustomLoadingDialog((CenesBaseActivity)getActivity());
            customLoadingDialog.showDialog();

            new ProfileAsyncTask.CommonPostRequestTask(new ProfileAsyncTask.CommonPostRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        customLoadingDialog.hideDialog();

                        if (myCalendarActions.equals(MyCalendarActions.Sync)) {
                            //Reload Home Screen

                            boolean success = response.getBoolean("success");
                            if (success == true) {

                                JSONObject data = response.getJSONObject("data");
                                selectedCalendarSyncToken = new Gson().fromJson(data.toString(), CalenadarSyncToken.class);

                                tvCalendarName.setText("Account: "+selectedCalendarSyncToken.getEmailId());
                                btnCalendarActionDelete.setVisibility(View.VISIBLE);
                                btnCalendarActionSync.setVisibility(View.GONE);

                                //Mix Panel Tracking
                                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                                try {
                                    JSONObject props = new JSONObject();
                                    if (calendarSelected.equals(CalendarType.Google)) {
                                        props.put("CalendarType","Google");
                                    } else {
                                        props.put("CalendarType","Outlook");
                                    }
                                    props.put("Action","Sync Ends");
                                    props.put("UserEmail",loggedInUser.getEmail());
                                    props.put("UserName",loggedInUser.getName());
                                    props.put("Device","Android");
                                    mixpanel.track("SyncCalendar", props);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            boolean success = response.getBoolean("success");
                            if (success == true) {

                            } else {
                                showAlert("Error", response.getString("message"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAsyncArrayResponseCall(AsyncTaskDto asyncTaskDto) {
        try {
            final CustomLoadingDialog customLoadingDialog = new CustomLoadingDialog((CenesBaseActivity)getActivity());
            customLoadingDialog.showDialog();
            new ProfileAsyncTask.CommonGetRequestArrayResponseTask(new ProfileAsyncTask.CommonGetRequestArrayResponseTask.AsyncResponse() {
                @Override
                public void processFinish(JSONArray response) {

                    //Refresh Home Screen
                    customLoadingDialog.hideDialog();

                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                    try {
                        JSONObject props = new JSONObject();
                        props.put("CalendarType","Google");
                        props.put("Action","Sync Ends");
                        props.put("UserEmail",loggedInUser.getEmail());
                        props.put("UserName",loggedInUser.getName());
                        props.put("Device","Android");
                        mixpanel.track("SyncCalendar", props);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).equals(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
