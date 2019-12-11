package com.cenesbeta.fragment.guest;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupOptionsFragment extends CenesFragment {

    private static String TAG = "SignupOptionsFragment";
    private static int  GOOGLE_SIGN_IN = 101;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    private RelativeLayout rlFacebookBtn, rlGoogleBtn, rlEmailBtn;
    private TextView tvTandCText;

    private LoginButton buttonJoinFB;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private User loggedInUser;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();

        View view = inflater.inflate(R.layout.fragment_guest_signup_options, container, false);

        rlFacebookBtn = (RelativeLayout) view.findViewById(R.id.rl_facebook_btn);
        rlGoogleBtn = (RelativeLayout) view.findViewById(R.id.rl_google_btn);
        rlEmailBtn = (RelativeLayout) view.findViewById(R.id.rl_email_btn);

        tvTandCText = (TextView) view.findViewById(R.id.tv_tandc_text);

        buttonJoinFB = (LoginButton) view.findViewById(R.id.bt_fb_join);
        //buttonJoinFB.setPermissions("email","public_profile");

        buttonJoinFB.registerCallback(callbackManager, facebookCallback);
        buttonJoinFB.setFragment(this);

        rlFacebookBtn.setOnClickListener(onClickListener);
        rlGoogleBtn.setOnClickListener(onClickListener);
        rlEmailBtn.setOnClickListener(onClickListener);

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();

        loggedInUser = userManager.getUser();
        if (loggedInUser == null) {
            loggedInUser = new User();
        }

        Spanned htmlAsSpanned = Html.fromHtml(getString(R.string.tandc_text)); // used by TextView
        tvTandCText.setText(htmlAsSpanned);
        tvTandCText.setMovementMethod(LinkMovementMethod.getInstance());

        return  view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.rl_facebook_btn:
                    disconnectFromFacebook();
                    buttonJoinFB.performClick();
                    break;

                case R.id.rl_google_btn:
                    googleSignInCall();
                    break;

                case R.id.rl_email_btn:

                    EmailSignupFragment emailSignupFragment = new EmailSignupFragment();
                    ((GuestActivity)getActivity()).replaceFragment(emailSignupFragment, SignupOptionsFragment.TAG);
                    break;

                    default:
                        System.out.println("Woww ji woww");
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == GOOGLE_SIGN_IN) {

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleGoogleSignInResult(task);

            }
        } else {
            System.out.println("Result False : "+data.getDataString());
        }
    }

    FacebookCallback facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e("Fb status : ", "Facebook Id : " + loginResult.getAccessToken().getUserId() + ",Access Token : " + loginResult.getAccessToken().getToken());
            String facebookId = loginResult.getAccessToken().getUserId();
            String facebookToken = loginResult.getAccessToken().getToken();

            loggedInUser.setAuthType(User.AuthenticateType.facebook);
            loggedInUser.setFacebookId(facebookId);
            loggedInUser.setFacebookAuthToken(facebookToken);
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        String id = object.getString("id");
                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");

                        loggedInUser.setName(first_name + " " +last_name);
                        //gender = object.getString("gender");
                        //String birthday = object.getString("birthday");
                        if (object.has("picture")) {
                            JSONObject dataObj = object.getJSONObject("picture");
                            loggedInUser.setPicture(dataObj.getJSONObject("data").getString("url"));
                        }

                        String email;
                        if (object.has("email")) {
                            loggedInUser.setEmail(object.getString("email"));
                        }

                        if (object.has("gender")) {
                            loggedInUser.setGender(object.getString("gender"));
                        }

                        /*if (photo != null) {
                            new ProfileAsyncTask.DownloadFacebookImage(new ProfileAsyncTask.DownloadFacebookImage.AsyncResponse() {
                                @Override
                                public void processFinish(Bitmap response) {
                                    rivProfileRoundedImg.setVisibility(View.VISIBLE);
                                    rivProfileRoundedImg.setImageBitmap(response);
                                }
                            }).execute(photo);
                        }*/

                        /*userManager.deleteAll();
                        userManager.addUser(loggedInUser);

                        SignupStepSuccessFragment signupStepSuccessFragment = new SignupStepSuccessFragment();
                        ((GuestActivity)getActivity()).replaceFragment(signupStepSuccessFragment,  SignupOptionsFragment.TAG);*/
                        socialSignupRequest(loggedInUser);
                        Log.i("RESULTS : ", object.getString("email"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,cover,picture.type(large)");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.e("Cancelled", "User cancelled dialog");
        }

        @Override
        public void onError(FacebookException e) {
            Log.e("Error : ", e.getMessage());
        }

    };


    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        LoginManager.getInstance().logOut();
    }

    public void socialSignupRequest(User user) {

        try {

            JSONObject postData = new JSONObject(new Gson().toJson(user));
            new ProfileAsyncTask(cenesApplication, getActivity());
            new ProfileAsyncTask.SignupStepOneSuccessTask(new ProfileAsyncTask.SignupStepOneSuccessTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {

                            User user = new Gson().fromJson(response.getString("data"), User.class);
                            userManager.deleteAll();
                            userManager.addUser(user);
                            loggedInUser = userManager.getUser();
                            SharedPreferences prefs = getActivity().getSharedPreferences("CenesPrefs", Context.MODE_PRIVATE);
                            String token = prefs.getString("FcmToken", null);

                            if (token != null) {
                                JSONObject registerDeviceObj = new JSONObject();
                                registerDeviceObj.put("deviceToken", token);
                                registerDeviceObj.put("deviceType", "android");
                                registerDeviceObj.put("model", CenesUtils.getDeviceModel());
                                registerDeviceObj.put("manufacturer", CenesUtils.getDeviceManufacturer());
                                registerDeviceObj.put("version", CenesUtils.getDeviceVersion());
                                registerDeviceObj.put("deviceType", "android");
                                registerDeviceObj.put("userId", loggedInUser.getUserId());
                                new ProfileAsyncTask.DeviceTokenSyncTask(new ProfileAsyncTask.DeviceTokenSyncTask.AsyncResponse() {
                                        @Override
                                    public void processFinish(JSONObject response) {

                                    }
                                }).execute(registerDeviceObj);
                            }

                            if (user.isNew()) {
                                SignupStepSuccessFragment signupStepSuccessFragment = new SignupStepSuccessFragment();
                                ((GuestActivity)getActivity()).clearFragmentsAndOpen(signupStepSuccessFragment);
                            } else {
                                getContacts();
                                startActivity(new Intent((GuestActivity)getActivity(), CenesBaseActivity.class));
                                getActivity().finish();
                            }

                        } else {

                            String message = response.getString("message");
                            showAlert("Alert", message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).execute(postData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signInGoogleAccount() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    public void googleSignInCall() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().
                        requestServerAuthCode(CenesConstants.GoogleWebClientid, true).build();

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

    public void getContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager
                .PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            fetchDeviceContactList();
        }
    }

    public void fetchDeviceContactList() {

        Map<String, String> contactsArrayMap = new HashMap<>();

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        //Log.e("phoneNo : "+phoneNo , "Name : "+name);

                        if (phoneNo.indexOf("\\*") != -1 || phoneNo.indexOf("\\#") != -1 || phoneNo.length() < 7) {
                            continue;
                        }
                        try {
                            String parsedPhone = phoneNo.replaceAll(" ","").replaceAll("-","").replaceAll("\\(","").replaceAll("\\)","");
                            if (parsedPhone.indexOf("+") == -1) {
                                parsedPhone = "+"+parsedPhone;
                            }
                            //contactObject.put(parsedPhone, name);
                            //contactsArray.put(contactObject);
                            if (!contactsArrayMap.containsKey(parsedPhone)) {
                                contactsArrayMap.put(parsedPhone, name);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        JSONArray contactsArray = new JSONArray();
        for (Map.Entry<String, String> entryMap: contactsArrayMap.entrySet()) {
            JSONObject contactObject = new JSONObject();
            try {
                contactObject.put(entryMap.getKey(), entryMap.getValue());
                contactsArray.put(contactObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONObject userContact = new JSONObject();
        try {
            userContact.put("userId",loggedInUser.getUserId());
            userContact.put("contacts",contactsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Making Phone Sync Call
        new ProfileAsyncTask.PhoneContactSync(new ProfileAsyncTask.PhoneContactSync.AsyncResponse() {
            @Override
            public void processFinish(Object response) {

                //((GuestActivity)getActivity()).replaceFragment(new HolidaySyncFragment(), null);
                startActivity(new Intent((GuestActivity)getActivity(), CenesBaseActivity.class));
                getActivity().finish();
            }
        }).execute(userContact);
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            System.out.println(account.getId());
            System.out.println(account.getIdToken());
            loggedInUser.setAuthType(User.AuthenticateType.google);
            loggedInUser.setName(account.getDisplayName());
            loggedInUser.setEmail(account.getEmail());
            loggedInUser.setGoogleId(account.getId());
            socialSignupRequest(loggedInUser);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
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

}
