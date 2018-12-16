package com.cenes.fragment.guest;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.ExtractedTextRequest;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.cenes.Manager.AlertManager;
import com.cenes.Manager.ValidationManager;
import com.cenes.R;
import com.cenes.activity.CompleteYourProfileActivity;
import com.cenes.activity.GuestActivity;
import com.cenes.activity.SignUpActivity;
import com.cenes.application.CenesApplication;
import com.cenes.backendManager.UserApiManager;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.fragment.CenesFragment;
import com.cenes.util.ImageUtils;
import com.cenes.util.RoundedDrawable;
import com.cenes.util.RoundedImageView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.soundcloud.android.crop.Crop;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mandeep on 25/9/18.
 */

public class SignupStepSuccessFragment extends CenesFragment {

    public final static String TAG = "SignupStepSuccessFragment";
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE = 12;
    private static final int CLICK_IMAGE = 13;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private AlertManager alertManager;
    private UserApiManager userApiManager;
    private UserManager userManager;
    private ValidationManager validationManager;
    private CallbackManager callbackManager;

    private String phoneNumber, facebookId, facebookToken, gender, photo;
    private String isTakeOrUpload = "take_picture";
    private  File file;
    private Uri cameraFileUri;
    private User user = null;
    private Long userId = null;

    private EditText etSignupSuccessName, etSignupSuccessEmail, etSignupSuccessPassword;
    private TextView signupSuccessFormNextStep, tvSyncWithFb;
    private View avatar;
    private LoginButton buttonJoinFB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        View v = inflater.inflate(R.layout.fragment_signup_success, container, false);

        initializeLayoutComponents(v);
        initilizeComponents();

        return v;
    }

    public void initilizeComponents() {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        alertManager = coreManager.getAlertManager();
        userApiManager = coreManager.getUserAppiManager();
        userManager = coreManager.getUserManager();
        validationManager = coreManager.getValidatioManager();

        phoneNumber = getArguments().getString("phoneNumber");

        user = new User();
        facebookId = "";
        facebookToken = "";
    }

    public void initializeLayoutComponents(View v) {

        etSignupSuccessName = (EditText) v.findViewById(R.id.et_signup_success_name);
        etSignupSuccessEmail = (EditText) v.findViewById(R.id.et_signup_success_email);
        etSignupSuccessPassword = (EditText) v.findViewById(R.id.et_signup_success_password);

        signupSuccessFormNextStep = (TextView) v.findViewById(R.id.signup_success_form_next_step);
        tvSyncWithFb = (TextView) v.findViewById(R.id.tv_sync_with_fb);

        buttonJoinFB = (LoginButton) v.findViewById(R.id.bt_fb_join);
        buttonJoinFB.setFragment(this);
        buttonJoinFB.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_friends"));

        avatar = (View) v.findViewById(R.id.avatar);

        signupSuccessFormNextStep.setOnClickListener(onClickListener);
        avatar.setOnClickListener(onClickListener);
        buttonJoinFB.setOnClickListener(onClickListener);
        tvSyncWithFb.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.signup_success_form_next_step:

                    if (isValid()) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("name", etSignupSuccessName.getText().toString());
                            jsonObject.put("email", etSignupSuccessEmail.getText().toString());
                            jsonObject.put("password", etSignupSuccessPassword.getText().toString());
                            jsonObject.put("phone", phoneNumber);
                            if (gender != null) {
                                jsonObject.put("gender", gender);
                            }
                            if (photo != null) {
                                jsonObject.put("photo", photo);
                            }
                            if (facebookId.length() != 0) {
                                jsonObject.put("facebookID", facebookId);
                            }
                            if (facebookToken.length() != 0) {
                                jsonObject.put("facebookAuthToken", facebookToken);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new SignupStepSuccessTask().execute(jsonObject);
                    }
                break;
                case R.id.avatar:
                    ActionSheet.createBuilder((GuestActivity)getActivity(), getActivity().getSupportFragmentManager())
                            .setCancelButtonTitle("Cancel")
                            .setOtherButtonTitles("Take Photo", "Upload Photo")
                            .setCancelableOnTouchOutside(true)
                            .setListener(sheetListener).show();
                    break;
                case R.id.avatar_uploaded:
                    ActionSheet.createBuilder((GuestActivity)getActivity(), getActivity().getSupportFragmentManager())
                            .setCancelButtonTitle("Cancel")
                            .setOtherButtonTitles("Take Photo", "Upload Photo")
                            .setCancelableOnTouchOutside(true)
                            .setListener(sheetListener).show();
                    break;
                case R.id.bt_fb_join:
                    buttonJoinFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.e("Fb status : ", "Facebook Id : " + loginResult.getAccessToken().getUserId() + ",Access Token : " + loginResult.getAccessToken().getToken());
                            facebookId = loginResult.getAccessToken().getUserId();
                            facebookToken = loginResult.getAccessToken().getToken();

                            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        String id = object.getString("id");
                                        String first_name = object.getString("first_name");
                                        String last_name = object.getString("last_name");

                                        etSignupSuccessName.setText(first_name+" "+last_name);

                                        //gender = object.getString("gender");
                                        //String birthday = object.getString("birthday");
                                        if (object.has("picture")) {
                                            JSONObject dataObj = object.getJSONObject("picture");
                                            photo = dataObj.getJSONObject("data").getString("url");
                                        }

                                        String email;
                                        if (object.has("email")) {
                                            email = object.getString("email");
                                            etSignupSuccessEmail.setText(email);
                                        }

                                        if (object.has("gender")) {
                                            gender = object.getString("gender");
                                        }

                                        if (photo != null) {
                                            new DownloadFacebookImage().execute(photo);
                                        }

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

                    });
                    break;
                case R.id.tv_sync_with_fb:
                    buttonJoinFB.performClick();
                    break;

            }
        }
    };


    ActionSheet.ActionSheetListener sheetListener = new ActionSheet.ActionSheetListener() {
        @Override
        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
            //Toast.makeText(getContext(),"Cancel", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
            //Toast.makeText(getContext(),index+"", Toast.LENGTH_LONG).show();
            if (index == 0) {
                isTakeOrUpload = "take_picture";
                if (ContextCompat.checkSelfPermission(getCenesActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    firePictureIntent();
                }
            } else if (index == 1) {
                isTakeOrUpload = "upload_picture";
                if (ContextCompat.checkSelfPermission(getCenesActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    firePictureIntent();
                }
            }
        }
    };

    public boolean isValid() {

        if (etSignupSuccessName.getText().toString().length() == 0) {

            alertManager.getAlert((GuestActivity)getActivity(), "Name cannot be empty", "Alert", null, false, "OK");
            return false;
        }
        if (etSignupSuccessEmail.getText().toString().length() == 0) {
            alertManager.getAlert((GuestActivity)getActivity(), "Email cannot be empty", "Alert", null, false, "OK");
            return false;
        }
        if (!validationManager.isValidEmail(etSignupSuccessEmail.getText().toString())) {
            alertManager.getAlert((GuestActivity)getActivity(), "Invalid Email", "Alert", null, false, "OK");
            return false;
        }
        if (etSignupSuccessPassword.getText().toString().length() == 0) {

            alertManager.getAlert((GuestActivity)getActivity(), "Password cannot be empty", "Alert", null, false, "OK");
            return false;
        }

        return true;
    }

    public void firePictureIntent() {
        if (isTakeOrUpload == "take_picture") {
            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Cenes";
            File newdir = new File(dir);
            newdir.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            file = new File(dir + File.separator + "IMG_" + timeStamp + ".jpg");

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            cameraFileUri = null;
            cameraFileUri = Uri.fromFile(file);

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
            startActivityForResult(takePictureIntent, CLICK_IMAGE);
        } else if (isTakeOrUpload == "upload_picture") {
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            startActivityForResult(browseIntent, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            firePictureIntent();
        } else if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContacts();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot show your friendList", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), CompleteYourProfileActivity.class));
                getActivity().finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CLICK_IMAGE || requestCode == PICK_IMAGE) {
                try {
                    if (isTakeOrUpload == "take_picture") {
                        ImageUtils.cropImageWithAspect(cameraFileUri, this, 512, 512);
                    } else if (isTakeOrUpload == "upload_picture") {
                        ImageUtils.cropImageWithAspect(Uri.parse(data.getDataString()), this, 512, 512);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == Crop.REQUEST_CROP) {
                try {
                    String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), Crop.getOutput(data));
                    file = new File(filePath);

                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), Crop.getOutput(data));
                    RoundedDrawable drawable = new RoundedDrawable(ImageUtils.getRotatedBitmap(imageBitmap, filePath));

                    //avatar.setVisibility(View.GONE);
                    //rImageView.setVisibility(View.VISIBLE);
                    avatar.setBackground(drawable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
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

                        Log.e("phoneNo : "+phoneNo , "Name : "+name);

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
            userContact.put("userId",user.getUserId());
            userContact.put("contacts",contactsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new PhoneCalendarSync().execute(userContact);
    }

    class SignupStepSuccessTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        ProgressDialog scanSuccessDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            scanSuccessDialog = new ProgressDialog((GuestActivity)getActivity());
            scanSuccessDialog.setMessage("Signing Up");
            scanSuccessDialog.setIndeterminate(false);
            scanSuccessDialog.setCanceledOnTouchOutside(false);
            scanSuccessDialog.setCancelable(false);
            scanSuccessDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            JSONObject postSignupData = jsonObjects[0];
            return userApiManager.emailSignup(postSignupData);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            scanSuccessDialog.dismiss();
            scanSuccessDialog  = null;

            if (jsonObject!=null){
                if(jsonObject.has("errorCode") ){
                    try{
                        if(jsonObject.getInt("errorCode") == 0){
                            if(jsonObject.has(user.USERID)){
                                user.setUserId(jsonObject.getInt(user.USERID));
                            }
                            if(jsonObject.has(user.USERNAME)){
                                user.setUsername(jsonObject.getString(user.USERNAME));
                            }
                            if(jsonObject.has(user.TOKEN)){
                                user.setAuthToken(jsonObject.getString(user.TOKEN));
                            }
                            if(jsonObject.has(user.NAME)){
                                user.setName(jsonObject.getString(user.NAME));
                            }
                            if(jsonObject.has(user.EMAIL)){
                                user.setEmail(jsonObject.getString(user.EMAIL));
                            }
                            if(jsonObject.has(user.PHOTO)){
                                user.setPicture(jsonObject.getString(user.PHOTO));
                            }
                            userManager.addUser(user);

                            SharedPreferences prefs = getActivity().getSharedPreferences("CenesPrefs", Context.MODE_PRIVATE);
                            String token = prefs.getString("FcmToken", null);

                            if (token != null) {
                                JSONObject registerDeviceObj = new JSONObject();
                                registerDeviceObj.put("deviceToken",token);
                                registerDeviceObj.put("deviceType","android");
                                registerDeviceObj.put("userId",user.getUserId());
                                new DeviceTokenSync().execute(registerDeviceObj);
                            }

                            if (file != null) {
                                new DoFileUpload().execute();
                            }
                            userId = Long.parseLong(user.getUserId()+"");
                            getContacts();

                        }
                        else{
                            if (jsonObject.has("errorDetail")) {
                                alertManager.getAlert((GuestActivity)getActivity(), jsonObject.getString("errorDetail"), "Error", null, false, "OK");
                                //startActivity(new Intent((GuestActivity)getActivity(), SignInActivity.class));
                                //finish();
                            }else{
                                alertManager.getAlert((GuestActivity)getActivity(), "Some thing is going wrong", "Error", null, false, "OK");
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    alertManager.getAlert((GuestActivity)getActivity(), "Server Error", "Error", null, false, "OK");
                }

            } else {
                getCenesActivity().showRequestTimeoutDialog();
            }
        }
    }

    class DoFileUpload extends AsyncTask<String, JSONObject, JSONObject> {

        ProgressDialog doFileUploadDialog;

        @Override
        protected void onPreExecute() {

            doFileUploadDialog = new ProgressDialog(getCenesActivity());
            doFileUploadDialog.setMessage("wait uploading Image..");
            doFileUploadDialog.setIndeterminate(false);
            doFileUploadDialog.setCanceledOnTouchOutside(false);
            doFileUploadDialog.setCancelable(false);
            doFileUploadDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                JSONObject response = userApiManager.uploadProfileImage(user, file);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (doFileUploadDialog.isShowing()) {
                doFileUploadDialog.dismiss();
            }
            doFileUploadDialog = null;

            try {
                if (response != null && response.getInt("errorCode") == 0) {
                    if (response.has("photo")) {
                        user.setPicture(response.getString("photo"));
                        userManager.updateProfilePic(user);
                    }
                } else {
                    getCenesActivity().showRequestTimeoutDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class DeviceTokenSync extends AsyncTask<JSONObject,Object,Object>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(JSONObject... objects) {
            JSONObject deviceTokenInfo = objects[0];
            return userApiManager.syncDeviceToken(deviceTokenInfo, user.getAuthToken());
        }
    }

    class PhoneCalendarSync extends AsyncTask<JSONObject,Object,Object>{

        ProgressDialog syncContactsDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            syncContactsDialog = new ProgressDialog(getCenesActivity());
            syncContactsDialog.setMessage("Syncing Contacts..");
            syncContactsDialog.setIndeterminate(false);
            syncContactsDialog.setCanceledOnTouchOutside(false);
            syncContactsDialog.setCancelable(false);
            syncContactsDialog.show();
        }

        @Override
        protected Object doInBackground(JSONObject... objects) {
            JSONObject deviceTokenInfo = objects[0];

            return userApiManager.syncDevicePhone(deviceTokenInfo, user.getAuthToken());
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (syncContactsDialog.isShowing()) {
                syncContactsDialog.dismiss();
            }
            syncContactsDialog = null;

            startActivity(new Intent((GuestActivity)getActivity(), CompleteYourProfileActivity.class));
            getActivity().finish();
        }
    }

    class DownloadFacebookImage extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String photo = strings[0];
            URL url;
            Bitmap bm = null;
            try {
                url = new URL(photo);
                URLConnection ucon = url.openConnection();
                InputStream is;
                if (ucon instanceof HttpURLConnection) {
                    HttpURLConnection httpConn = (HttpURLConnection) ucon;
                    int statusCode = httpConn.getResponseCode();
                    if (statusCode == 200) {
                        is = httpConn.getInputStream();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        BufferedInputStream bis = new BufferedInputStream(is, 8192);
                        ByteArrayBuffer baf = new ByteArrayBuffer(1024);
                        int current = 0;
                        while ((current = bis.read()) != -1) {
                            baf.append((byte) current);
                        }
                        byte[] rawImage = baf.toByteArray();
                        bm = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
                        bis.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap x) {
            super.onPostExecute(x);
            avatar.setBackground(new BitmapDrawable(x));
        }
    }

}