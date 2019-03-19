package com.cenes.fragment.profile;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.AsyncTasks.ProfileAsyncTask;
import com.cenes.Manager.AlertManager;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.DeviceManager;
import com.cenes.Manager.UrlManager;
import com.cenes.Manager.ValidationManager;
import com.cenes.R;
import com.cenes.activity.AlarmActivity;
import com.cenes.activity.CenesBaseActivity;
import com.cenes.activity.DiaryActivity;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.ReminderActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.fragment.CenesFragment;
import com.cenes.fragment.NavigationFragment;
import com.cenes.util.CenesEditText;
import com.cenes.util.CenesUtils;
import com.cenes.util.ImageUtils;
import com.cenes.util.RoundedDrawable;
import com.cenes.util.RoundedImageView;
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
import com.google.gson.Gson;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mandeep on 8/11/17.
 */

public class ProfileFragment extends CenesFragment {

    public final static String TAG = "ProfileFragment";

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private DeviceManager deviceManager;

    private LoginButton buttonJoinFB;

    private static final int PICK_IMAGE_GALLERY = 12;
    private static final int PICK_IMAGE_CAMERA = 13;

    String birthDayStr;
    ImageView ivProfile, ivProfileBack;
    TextView tvName, tvEmail, tvPhone, tvBirthday;
    EditText etName;
    Button btnProfileDone;
    private LinearLayout llChangePassword, llFacebookSync;
    private User loggedInUser;
    private CallbackManager callbackManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        View v = inflater.inflate(R.layout.profile, container, false);

        init(v);

        ((CenesBaseActivity)getActivity()).hideFooter();

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

    public void init(View v) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        deviceManager = coreManager.getDeviceManager();

        loggedInUser = userManager.getUser();
        System.out.println("User Date : "+loggedInUser.toString());

        new ProfileAsyncTask(cenesApplication, getActivity());

        llChangePassword = (LinearLayout) v.findViewById(R.id.ll_change_password);
        llFacebookSync = (LinearLayout) v.findViewById(R.id.ll_facebook_sync);
        ivProfileBack = (ImageView) v.findViewById(R.id.iv_profile_back);

        ivProfile = (RoundedImageView) v.findViewById(R.id.ivProfile);
        tvName = (TextView) v.findViewById(R.id.tv_name);
        tvPhone = (TextView) v.findViewById(R.id.tv_phone);
        tvEmail = (TextView) v.findViewById(R.id.tv_email);
        tvBirthday = (TextView) v.findViewById(R.id.tv_birthday);

        etName = (EditText) v.findViewById(R.id.et_name);
        btnProfileDone = (Button) v.findViewById(R.id.btn_profile_done);

        if (loggedInUser != null && !CenesUtils.isEmpty(loggedInUser.getPicture())) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.default_profile_icon);
            requestOptions.circleCrop();

            Glide.with(getActivity()).load(loggedInUser.getPicture()).apply(requestOptions).into(ivProfile);
        }

        tvName.setText(loggedInUser.getName());
        tvEmail.setText(loggedInUser.getEmail());
        tvPhone.setText(loggedInUser.getPhone());
        if (loggedInUser.getBirthDate() ==  null || loggedInUser.getBirthDate() == 0l) {
            tvBirthday.setText("Choose BirthDate");
        } else {
            Calendar yesCalendar = Calendar.getInstance();
            yesCalendar.setTimeInMillis(loggedInUser.getBirthDate());
            String birthDateStr = CenesUtils.ddMMMYYYY.format(yesCalendar.getTime());
            tvBirthday.setText(birthDateStr);

        }

        buttonJoinFB = (LoginButton) v.findViewById(R.id.bt_fb_join);
        buttonJoinFB.setFragment(this);
        buttonJoinFB.setReadPermissions(Arrays.asList(CenesUtils.facebookPermissions));

        birthDayStr = null;
        //Set Click Listeners
        ivProfile.setOnClickListener(imageClickListener);
        tvName.setOnClickListener(clickListener);
        llChangePassword.setOnClickListener(clickListener);
        llFacebookSync.setOnClickListener(clickListener);
        buttonJoinFB.setOnClickListener(clickListener);
        tvBirthday.setOnClickListener(clickListener);
        ivProfileBack.setOnClickListener(clickListener);
        btnProfileDone.setOnClickListener(clickListener);
        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Do something after 100ms
                    if (tvName.getVisibility() == View.GONE) {
                        tvName.setText(etName.getText().toString());
                        etName.setVisibility(View.GONE);
                        tvName.setVisibility(View.VISIBLE);

                        loggedInUser.setName(etName.getText().toString());
                    }
                    deviceManager.hideKeyBoard(etName, (CenesBaseActivity)getActivity());
                    return true;
                }

                return false;
            }
        });
    }

    private static String isTakeOrUpload = "take_picture";

    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.ll_change_password:
                    ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                    if (getActivity() instanceof HomeScreenActivity) {
                        ((HomeScreenActivity) getActivity()).replaceFragment(changePasswordFragment, ChangePasswordFragment.TAG);
                    } else if (getActivity() instanceof ReminderActivity) {
                        ((ReminderActivity) getActivity()).replaceFragment(changePasswordFragment, ChangePasswordFragment.TAG);
                    } else if (getActivity() instanceof GatheringScreenActivity) {
                        ((GatheringScreenActivity) getActivity()).replaceFragment(changePasswordFragment, ChangePasswordFragment.TAG);
                    } else if (getActivity() instanceof DiaryActivity) {
                        ((DiaryActivity) getActivity()).replaceFragment(changePasswordFragment, ChangePasswordFragment.TAG);
                    } else if (getActivity() instanceof AlarmActivity) {
                        ((AlarmActivity) getActivity()).replaceFragment(changePasswordFragment, ChangePasswordFragment.TAG);
                    }
                break;
                case R.id.iv_profile_back:
                    getActivity().onBackPressed();
                    break;
                case R.id.ll_facebook_sync:
                    disconnectFromFacebook();
                    buttonJoinFB.performClick();
                    break;

                case R.id.bt_fb_join:

                    buttonJoinFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.e("Fb status : ", "Facebook Id : " + loginResult.getAccessToken().getUserId() + ",Access Token : " + loginResult.getAccessToken().getToken());
                            loggedInUser.setFacebookID(loginResult.getAccessToken().getUserId());
                            loggedInUser.setFacebookAuthToken(loginResult.getAccessToken().getToken());

                            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        System.out.println(object.toString());
                                        String id = object.getString("id");
                                        String first_name = object.getString("first_name");
                                        String last_name = object.getString("last_name");

                                        tvName.setText(first_name+" "+last_name);
                                        loggedInUser.setName(first_name+" "+last_name);
                                        //gender = object.getString("gender");
                                        //String birthday = object.getString("birthday");
                                        if (object.has("picture")) {
                                            JSONObject dataObj = object.getJSONObject("picture");
                                            loggedInUser.setPicture(dataObj.getJSONObject("data").getString("url"));
                                        }

                                        /*String email;
                                        if (object.has("email")) {
                                            email = object.getString("email");
                                            tvEmail.setText(email);
                                        }*/

                                        if (object.has("gender")) {
                                            loggedInUser.setGender(object.getString("gender"));
                                        }

                                        if (loggedInUser.getPicture() != null) {
                                            new ProfileAsyncTask.DownloadFacebookImage(new ProfileAsyncTask.DownloadFacebookImage.AsyncResponse() {
                                                @Override
                                                public void processFinish(Bitmap response) {
                                                    ivProfile.setImageBitmap(response);
                                                }
                                            }).execute(loggedInUser.getPicture());
                                        }

                                        if (btnProfileDone.getVisibility() == View.GONE) {
                                            btnProfileDone.setVisibility(View.VISIBLE);
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

                    case R.id.tv_birthday:

                        // TODO Auto-generated method stub
                        Calendar birthCal = Calendar.getInstance();
                        if (loggedInUser.getBirthDate() != null) {
                            birthCal.setTimeInMillis(loggedInUser.getBirthDate());
                        }

                        new DatePickerDialog(getActivity(), datePickerListener, birthCal
                                .get(Calendar.YEAR), birthCal.get(Calendar.MONTH),
                                birthCal.get(Calendar.DAY_OF_MONTH)).show();
                        break;

                case R.id.tv_name:
                    tvName.setVisibility(View.GONE);
                    etName.setVisibility(View.VISIBLE);
                    etName.setText(tvName.getText().toString());
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            etName.requestFocus();
                            etName.setSingleLine(true);
                            deviceManager.showKeyBoard(etName, (CenesBaseActivity)getActivity());

                            if (btnProfileDone.getVisibility() == View.GONE) {
                                btnProfileDone.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 500);
                    break;

                case R.id.btn_profile_done:

                    if (file != null) {
                        new ProfileAsyncTask.UploadProfilePhoto(new ProfileAsyncTask.UploadProfilePhoto.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject response) {

                                if (response != null) {

                                    try {
                                        loggedInUser.setPicture(response.getString("photo"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        if (etName.getVisibility() == View.VISIBLE) {
                                            loggedInUser.setName(etName.getText().toString());
                                        }
                                        JSONObject userObj = new JSONObject(new Gson().toJson(loggedInUser));
                                        new ProfileAsyncTask.UpdateUserTask(new ProfileAsyncTask.UpdateUserTask.AsyncResponse() {
                                            @Override
                                            public void processFinish(JSONObject response) {

                                                if (response != null) {
                                                    try {
                                                        if (response != null && response.getBoolean("success")) {

                                                            btnProfileDone.setVisibility(View.GONE);

                                                            JSONObject userObj = response.getJSONObject("data");
                                                            loggedInUser.setName(userObj.getString("name"));
                                                            loggedInUser.setGender(userObj.getString("gender"));
                                                            loggedInUser.setPicture(userObj.getString("photo"));
                                                            userManager.updateUser(loggedInUser);

                                                            Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getContext(), "Error in updating Info", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }).execute(userObj);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }).execute(file);
                    } else {
                        try {

                            if (etName.getVisibility() == View.VISIBLE) {
                                loggedInUser.setName(etName.getText().toString());
                            }
                            JSONObject userObj = new JSONObject(new Gson().toJson(loggedInUser));
                            new ProfileAsyncTask.UpdateUserTask(new ProfileAsyncTask.UpdateUserTask.AsyncResponse() {
                                @Override
                                public void processFinish(JSONObject response) {

                                    if (response != null) {
                                        try {
                                            if (response != null && response.getBoolean("success")) {

                                                btnProfileDone.setVisibility(View.GONE);

                                                JSONObject userObj = response.getJSONObject("data");
                                                loggedInUser.setName(userObj.getString("name"));
                                                loggedInUser.setGender(userObj.getString("gender"));
                                                loggedInUser.setPicture(userObj.getString("photo"));
                                                userManager.updateUser(loggedInUser);

                                                Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Error in updating Info", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).execute(userObj);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

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
            loggedInUser.setBirthDate(yesCalendar.getTimeInMillis());
            String birthDateStr = CenesUtils.ddMMMYYYY.format(yesCalendar.getTime());
            tvBirthday.setText(birthDateStr);

            if (btnProfileDone.getVisibility() == View.GONE) {
                btnProfileDone.setVisibility(View.VISIBLE);
            }
            //Do something after 100ms
            if (tvName.getVisibility() == View.GONE) {
                tvName.setText(etName.getText().toString());
                etName.setVisibility(View.GONE);
                tvName.setVisibility(View.VISIBLE);
            }
            deviceManager.hideKeyBoard(etName, (CenesBaseActivity)getActivity());

        }
    };
    public View.OnClickListener imageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("Select");
            alert.setCancelable(true);
            final CharSequence[] options = {"Take Picture", "Choose From Gallery"};
            alert.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();

                    if (options[item].equals("Take Picture")) {
                        isTakeOrUpload = "take_picture";
                    } else if (options[item].equals("Choose From Gallery")) {
                        isTakeOrUpload = "upload_picture";
                    }

                    if (ContextCompat.checkSelfPermission(getCenesActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        firePictureIntent();
                    }
                }
            });
            alert.show();
        }
    };

    Uri cameraFileUri;
    File file;

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
            startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA);
        } else if (isTakeOrUpload == "upload_picture") {
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            startActivityForResult(browseIntent, PICK_IMAGE_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            firePictureIntent();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_CAMERA || requestCode == PICK_IMAGE_GALLERY) {
                try {
                    if (isTakeOrUpload == "take_picture") {
                        ImageUtils.cropImageWithAspect(cameraFileUri, this, 512, 512);
                    } else if (isTakeOrUpload == "upload_picture") {

                        String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), data.getData());

                        Uri imageUri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), imageUri);
                        ExifInterface ei = new ExifInterface(filePath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotatedBitmap = null;
                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }


                        ImageUtils.cropImageWithAspect(getImageUri(getContext().getApplicationContext(), rotatedBitmap), this, 200, 200);
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

                    persistImage(rotatedBitmap, filePath.substring(filePath.lastIndexOf("/"), filePath.length()));
                    RoundedDrawable drawable = new RoundedDrawable(ImageUtils.getRotatedBitmap(rotatedBitmap, filePath));

                    ivProfile.setImageBitmap(rotatedBitmap);
                    if (btnProfileDone.getVisibility() == View.GONE) {
                        btnProfileDone.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        LoginManager.getInstance().logOut();
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void persistImage(Bitmap bitmap, String name) {
        File filesDir = getActivity().getApplicationContext().getFilesDir();
        file = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
