package com.cenesbeta.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cenesbeta.Manager.AlertManager;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.DeviceManager;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.Manager.ValidationManager;
import com.cenesbeta.R;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by puneet on 11/8/17.
 */

public class SignUpActivity extends CenesActivity{

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    ProgressDialog progressDialog;

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    ValidationManager validationManager;
    InternetManager internetManager;
    UrlManager urlManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    User user = null;
    Long userId = null;
    EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    Button buttonGetStarted;
    ImageView signupBackArrow;

    String name,email,password, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();

    }

    public void init(){
        //----------------------------------
        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        alertManager = coreManager.getAlertManager();
        validationManager = coreManager.getValidatioManager();
        internetManager = coreManager.getInternetManager();
        urlManager = coreManager.getUrlManager();
        deviceManager = coreManager.getDeviceManager();
        apiManager = coreManager.getApiManager();
        //-----------------------------------------

        editTextName = (EditText) findViewById(R.id.et_name);
        editTextEmail = (EditText) findViewById(R.id.et_email);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        editTextPhone = (EditText) findViewById(R.id.et_phone);

        buttonGetStarted = (Button) findViewById(R.id.bt_get_started);
        buttonGetStarted.setOnClickListener(onClickListener);

        //signupBackArrow = (ImageView) findViewById(R.id.signup_back_arrow);
        signupBackArrow.setOnClickListener(onClickListener);

    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.bt_get_started){
                startSignUpProcess();
            }
            /*if(v.getId() == R.id.signup_back_arrow){
                startActivity(new Intent(SignUpActivity.this,ChoiceActivity.class));
                finish();
            }*/
        }
    };


    public void startSignUpProcess(){
        deviceManager.hideKeyBoard(editTextEmail, SignUpActivity.this);
        user = null;
        user = new User();
        name = editTextName.getText().toString();
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        phone = editTextPhone.getText().toString();

        validationProcess();
    }

    public void validationProcess(){

        boolean isValid = true;

        if (isValid && validationManager.isFieldBlank(name) && validationManager.isFieldBlank(password) && validationManager.isFieldBlank(email)) {
            alertManager.getAlert(SignUpActivity.this, "Please Enter the Username", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid && validationManager.isFieldBlank(name)) {
            alertManager.getAlert(SignUpActivity.this, "Please Enter the Username", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid && validationManager.isFieldBlank(email)) {
            alertManager.getAlert(SignUpActivity.this, "Please Enter the Email Address", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid && password.length() < 1) {
            alertManager.getAlert(SignUpActivity.this, "Please Enter the Password", "Info", null, false, "OK");
            isValid = false;
        }

        if (isValid && validationManager.isSpace(name)) {
            alertManager.getAlert(SignUpActivity.this, "Space is not allowed in username.", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid && validationManager.isSpace(email)) {
            alertManager.getAlert(SignUpActivity.this, "Space is not allowed in email.", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid && validationManager.isSpace(password)) {
            alertManager.getAlert(SignUpActivity.this, "Space is not allowed in password.", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid && !validationManager.isValidEmail(email)) {
            alertManager.getAlert(SignUpActivity.this, "Please Enter the Valid Email Format", "Info", null, false, "OK");
            isValid = false;
        }
        if (isValid && !validationManager.isValidPhoneLength(phone, 9)) {
            alertManager.getAlert(SignUpActivity.this, "Please Enter the Valid Phone Number", "Info", null, false, "OK");
            isValid = false;
        }

        if (isValid && !validationManager.isValidLength(password, 6)) {
            alertManager.getAlert(SignUpActivity.this, "Minimum Character Limit for Password is Six", "Info", null, false, "OK");
            isValid = false;
        }

        if(isValid){
            networkProcess();
        }
    }

    public void networkProcess(){
       if (internetManager.isInternetConnection(SignUpActivity.this)) {
            user.setEmail(email);
            user.setName(name);
            user.setPassword(password);
            user.setAuthType("email");
            user.setPhone(phone);
            user.setApiUrl(urlManager.getApiUrl(name));
            new SignUpProcess().execute();

       } else {
            alertManager.getAlert(SignUpActivity.this, "No Internet Connection!", "Info", null, false, "OK");
        }
    }

    public void getContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager
                .PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            fetchDeviceContactList();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot show your friendList", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, CompleteYourProfileActivity.class));
                finish();
            }
        }
    }


    public void fetchDeviceContactList() {

        JSONArray contactsArray = new JSONArray();

        ContentResolver cr = getContentResolver();
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

                            JSONObject contactObject = new JSONObject();
                            try {
                                contactObject.put(phoneNo,name);
                                contactsArray.put(contactObject);
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
            JSONObject userContact = new JSONObject();
            try {
                userContact.put("userId",userId);
                userContact.put("contacts",contactsArray);
            } catch (Exception e) {
                e.printStackTrace();
            }

            new PhoneCalendarSync().execute(userContact);
    }

    public class SignUpProcess extends AsyncTask<Object, Object, Object> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(SignUpActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub

            JSONObject jsonObject = apiManager.signUpByEmail(user, SignUpActivity.this);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;
            //{"createdAt":1503071853185,"updateAt":1503071853185,"errorCode":0,"errorDetail":null,"userId":5,"username":"abc1503071853181","email":"abc@abc.com","password":"cc4e7ba92ea0b1fc56e6ac67f682f3ea","facebookID":null,"authType":"email","facebookAuthToken":null,"name":"abc","photo":null,"token":"1503158253181eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmMxNTAzMDcxODUzMTgxIn0.HBBidMMaE3aiH7KLb3hiZuSNxIUElKcd1OFyRM0TTIg7BG1d9r7qYLntupwtqyEQCdsd8m1ZkNGs9oW9zXXw7g"}
            if (object!=null){
                JSONObject jsonObject = (JSONObject) object;
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
                            userManager.addUser(user);

                            SharedPreferences prefs = getSharedPreferences("deviceToken", MODE_PRIVATE);
                            String token = prefs.getString("token", null);

                            if (token != null) {
                                JSONObject registerDeviceObj = new JSONObject();
                                registerDeviceObj.put("deviceToken",token);
                                registerDeviceObj.put("deviceType","android");
                                registerDeviceObj.put("userId",user.getUserId());
                                new DeviceTokenSync().execute(registerDeviceObj);
                            }

                            userId = Long.parseLong(user.getUserId()+"");
                            getContacts();

                        }
                        else{
                            progressDialog.hide();
                            if (jsonObject.has("errorDetail")) {
                                alertManager.getAlert(SignUpActivity.this, jsonObject.getString("errorDetail"), "Error", null, false, "OK");
                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                finish();
                            }else{
                                alertManager.getAlert(SignUpActivity.this, "Some thing is going wrong", "Error", null, false, "OK");
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    progressDialog.hide();
                    alertManager.getAlert(SignUpActivity.this, "Server Error", "Error", null, false, "OK");
                }

            } else {
                showRequestTimeoutDialog();
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
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            apiManager.syncDeviceToekn(user,deviceTokenInfo,SignUpActivity.this);
            return null;
        }
    }

    class PhoneCalendarSync extends AsyncTask<JSONObject,Object,Object>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(JSONObject... objects) {
            JSONObject deviceTokenInfo = objects[0];
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            apiManager.syncDeviceConcats(user,deviceTokenInfo,SignUpActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            startActivity(new Intent(SignUpActivity.this, CompleteYourProfileActivity.class));
            finish();
        }
    }

}
