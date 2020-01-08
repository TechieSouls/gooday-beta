package com.cenesbeta.fragment.guest;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.AlertManager;
import com.cenesbeta.Manager.ValidationManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.UserApiManager;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.ImageUtils;
import com.cenesbeta.util.RoundedDrawable;
import com.cenesbeta.util.RoundedImageView;
import com.google.gson.Gson;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mandeep on 25/9/18.
 */

public class SignupStepSuccessFragment extends CenesFragment {

    public final static String TAG = "SignupStepSuccessFragment";
    // Storage Permissions
    private static final int PICK_IMAGE = 12;
    private static final int CLICK_IMAGE = 13;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1001, CAMERA_PERMISSION_CODE = 1002, UPLOAD_PERMISSION_CODE = 1003;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private AlertManager alertManager;
    private UserApiManager userApiManager;
    private UserManager userManager;
    private ValidationManager validationManager;

    private String isTakeOrUpload = "take_picture";
    private  File file;
    private Uri cameraFileUri;
    private User loggedInUser = null;

    private EditText etSignupSuccessName;
    private TextView etSignupSuccessBirthday, tvSignupSuccessGender, tvGenderMale, tvGenderFemale, tvGenderOther, tvGenderCancel;
    private TextView tvTakePhoto, tvUploadGallery, tvPhotoCancel;
    private RelativeLayout rlGenderActionSheet, rlPhotoActionSheet;
    private View avatar;
    private ImageView ivProfileForwardGrey, ivReportInstabug;
    private RoundedImageView rivProfileRoundedImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_signup_success, container, false);

        initilizeComponents();

        loggedInUser = userManager.getUser();
        if (loggedInUser == null) {
            loggedInUser = new User();
        }

        initializeLayoutComponents(v);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        etSignupSuccessName.setNextFocusDownId(R.id.et_signup_success_birthday);
        etSignupSuccessBirthday.setNextFocusDownId(R.id.tv_signup_success_gender);
        return v;
    }

    public void initilizeComponents() {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        alertManager = coreManager.getAlertManager();
        userApiManager = coreManager.getUserAppiManager();
        userManager = coreManager.getUserManager();
        validationManager = coreManager.getValidatioManager();

        new ProfileAsyncTask(cenesApplication, getActivity());
    }

    public void initializeLayoutComponents(View v) {

        etSignupSuccessName = (EditText) v.findViewById(R.id.et_signup_success_name);
        etSignupSuccessBirthday = (TextView) v.findViewById(R.id.et_signup_success_birthday);
        tvSignupSuccessGender = (TextView) v.findViewById(R.id.tv_signup_success_gender);

        rlGenderActionSheet = (RelativeLayout) v.findViewById(R.id.rl_gender_action_sheet);
        rlPhotoActionSheet = (RelativeLayout) v.findViewById(R.id.rl_photo_action_sheet);

        tvGenderMale = (TextView) v.findViewById(R.id.tv_gender_male);
        tvGenderFemale = (TextView) v.findViewById(R.id.tv_gender_female);
        tvGenderOther = (TextView) v.findViewById(R.id.tv_gender_other);
        tvGenderCancel = (TextView) v.findViewById(R.id.tv_gender_cancel);

        tvTakePhoto = (TextView) v.findViewById(R.id.tv_take_photo);
        tvUploadGallery = (TextView) v.findViewById(R.id.tv_choose_library);
        tvPhotoCancel = (TextView) v.findViewById(R.id.tv_photo_cancel);

        rivProfileRoundedImg = (RoundedImageView) v.findViewById(R.id.riv_profile_pic);
        ivProfileForwardGrey = (ImageView) v.findViewById(R.id.iv_profile_forward_grey);
        ivReportInstabug = (ImageView) v.findViewById(R.id.iv_report_instabug);

        rivProfileRoundedImg.setOnClickListener(onClickListener);
        tvGenderMale.setOnClickListener(onClickListener);
        tvGenderFemale.setOnClickListener(onClickListener);
        tvGenderOther.setOnClickListener(onClickListener);
        tvGenderCancel.setOnClickListener(onClickListener);
        tvTakePhoto.setOnClickListener(onClickListener);
        tvUploadGallery.setOnClickListener(onClickListener);
        tvPhotoCancel.setOnClickListener(onClickListener);

        etSignupSuccessBirthday.setOnClickListener(onClickListener);
        tvSignupSuccessGender.setOnClickListener(onClickListener);
        ivProfileForwardGrey.setOnClickListener(onClickListener);
        ivReportInstabug.setOnClickListener(onClickListener);

        if (loggedInUser != null) {

            if (!CenesUtils.isEmpty(loggedInUser.getName())) {
                etSignupSuccessName.setText(loggedInUser.getName());
            }

            if (loggedInUser.getPicture() != null) {

                Glide.with(getContext()).load(loggedInUser.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(rivProfileRoundedImg);
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_profile_forward_grey:

                    if (isValid()) {

                        JSONObject jsonObject = new JSONObject();
                        try {

                            loggedInUser.setName(etSignupSuccessName.getText().toString());

                            Gson gson = new Gson();
                            jsonObject = new JSONObject(gson.toJson(loggedInUser));
                            jsonObject.put("username", loggedInUser.getName());
                            jsonObject.put("birthDayStr", loggedInUser.getBirthDateStr());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new ProfileAsyncTask.SignupProfileUpdateTask(new ProfileAsyncTask.SignupProfileUpdateTask.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject response) {
                                        try {

                                            boolean success = response.getBoolean("success");
                                            if (success) {
                                                userManager.updateUser(loggedInUser);

                                                if (file != null) {
                                                    new ProfileAsyncTask.UploadProfilePhoto(new ProfileAsyncTask.UploadProfilePhoto.AsyncResponse() {
                                                        @Override
                                                        public void processFinish(JSONObject response) {
                                                              try {
                                                                if (response != null && response.getInt("errorCode") == 0) {
                                                                    if (response.has("photo")) {
                                                                        loggedInUser.setPicture(response.getString("photo"));
                                                                        userManager.updateProfilePic(loggedInUser);
                                                                    }
                                                                } else {
                                                                    getCenesActivity().showRequestTimeoutDialog();
                                                                }

                                                                getContacts();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).execute(file);
                                                } else {
                                                    getContacts();
                                                }
                                            } else {

                                                //String message = response.getString("message");
                                                //showAlert("Alert", message);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                }
                        }).execute(jsonObject);
                    }
                break;
                case R.id.riv_profile_pic:

                    rlPhotoActionSheet.setVisibility(View.VISIBLE);
                    break;

                case R.id.et_signup_success_birthday:
                    Calendar cal = Calendar.getInstance();

                    Calendar eligibleCal = Calendar.getInstance();
                    eligibleCal.add(Calendar.YEAR, -13);
                    DatePickerDialog birthDatePicker = new DatePickerDialog(getActivity(), datePickerListener, eligibleCal
                            .get(Calendar.YEAR), eligibleCal.get(Calendar.MONTH),
                            eligibleCal.get(Calendar.DAY_OF_MONTH));
                    birthDatePicker.getDatePicker().setMaxDate(eligibleCal.getTime().getTime());
                    birthDatePicker.show();
                    break;

                case R.id.tv_signup_success_gender:

                    rlGenderActionSheet.setVisibility(View.VISIBLE);
                    break;
                case R.id.tv_gender_male:

                    rlGenderActionSheet.setVisibility(View.GONE);
                    loggedInUser.setGender("Male");
                    tvSignupSuccessGender.setText(loggedInUser.getGender());
                    break;
                case R.id.tv_gender_female:

                    rlGenderActionSheet.setVisibility(View.GONE);
                    loggedInUser.setGender("Female");
                    tvSignupSuccessGender.setText(loggedInUser.getGender());
                    break;

                case R.id.tv_gender_other:

                    rlGenderActionSheet.setVisibility(View.GONE);
                    loggedInUser.setGender("Other");
                    tvSignupSuccessGender.setText(loggedInUser.getGender());
                    break;

                case R.id.tv_gender_cancel:
                    rlGenderActionSheet.setVisibility(View.GONE);
                    break;

                case R.id.tv_take_photo:
                    isTakeOrUpload = "take_picture";
                    /*if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                    } else {
                        firePictureIntent();
                    }*/
                    checkCameraPermissiosn();
                    rlPhotoActionSheet.setVisibility(View.GONE);
                    break;

                case R.id.tv_choose_library:
                    isTakeOrUpload = "upload_picture";
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, UPLOAD_PERMISSION_CODE);
                    } else {
                        firePictureIntent();
                    }
                    rlPhotoActionSheet.setVisibility(View.GONE);
                    break;

                case R.id.tv_photo_cancel:
                    rlPhotoActionSheet.setVisibility(View.GONE);
                    break;

                case R.id.iv_report_instabug:
                    break;

            }
        }
    };

    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {

            System.out.println(dayOfMonth+", "+monthOfYear+", "+year);
            Calendar yesCalendar = Calendar.getInstance();
            yesCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            yesCalendar.set(Calendar.MONTH, monthOfYear);
            yesCalendar.set(Calendar.YEAR, year);

            loggedInUser.setBirthDateStr(CenesUtils.ddMMMYYYY.format(yesCalendar.getTime()));
            etSignupSuccessBirthday.setText(loggedInUser.getBirthDateStr());
        }
    };

    public boolean isValid() {

        StringBuffer missingFields = new StringBuffer();
        if (etSignupSuccessName.getText().toString().length() == 0) {
            missingFields.append("Name");
        }

        if (CenesUtils.isEmpty(loggedInUser.getGender())) {
            if (missingFields.length() > 0) {
                missingFields.append(", ");
            }
            missingFields.append("Gender");
        }


        if (CenesUtils.isEmpty(loggedInUser.getBirthDateStr())) {
            if (missingFields.length() > 0) {
                missingFields.append(", ");
            }
            missingFields.append("BirthDay");
        }

        if (missingFields.length() > 0) {
            alertManager.getAlert((GuestActivity)getActivity(), missingFields.toString(), "Following Information is Required", null, false, "OK");
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

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkReadWritePermissiosn();
            }
        } else if (requestCode == UPLOAD_PERMISSION_CODE) {
            try  {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    firePictureIntent();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContacts();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot show your friendList", Toast.LENGTH_SHORT).show();
                //((GuestActivity)getActivity()).replaceFragment(new HolidaySyncFragment(), null);
                startActivity(new Intent((GuestActivity)getActivity(), CenesBaseActivity.class));
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

                    ExifInterface ei = new ExifInterface(filePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch(orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(imageBitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(imageBitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(imageBitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = imageBitmap;
                    }

                    RoundedDrawable drawable = new RoundedDrawable(ImageUtils.getRotatedBitmap(rotatedBitmap, filePath));

                    rivProfileRoundedImg.setImageBitmap(rotatedBitmap  );
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            if(loggedInUser.getUserId() != null) {
                userContact.put("userId", loggedInUser.getUserId());
            }
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

    class DeviceTokenSync extends AsyncTask<JSONObject,Object,Object>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(JSONObject... objects) {
            JSONObject deviceTokenInfo = objects[0];
            return userApiManager.syncDeviceToken(deviceTokenInfo, loggedInUser.getAuthToken());
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void checkCameraPermissiosn() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            checkReadWritePermissiosn();
        }
    }

    public void checkReadWritePermissiosn() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, UPLOAD_PERMISSION_CODE);
        } else {
            firePictureIntent();
        }
    }

}
