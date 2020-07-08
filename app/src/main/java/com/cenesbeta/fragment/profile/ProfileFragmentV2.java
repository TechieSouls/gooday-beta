package com.cenesbeta.fragment.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.ApiManagerImpl;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.ProfileListItemAdapter;
import com.cenesbeta.api.UserAPI;
import com.cenesbeta.bo.CalenadarSyncToken;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.CalendarSyncTokenManagerImpl;
import com.cenesbeta.database.impl.CenesUserManagerImpl;
import com.cenesbeta.database.impl.UserManagerImpl;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.dto.ProfileItemDto;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.ImageUtils;
import com.cenesbeta.util.RoundedDrawable;
import com.cenesbeta.util.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class ProfileFragmentV2  extends CenesFragment {

    public static String TAG = "ProfileFragmentV2";
    private static final int CAMERA_PERMISSION_CODE = 1001, UPLOAD_PERMISSION_CODE = 1002;
    private static final int OPEN_CAMERA_REQUEST_CODE = 1003, OPEN_GALLERY_REQUEST_CODE = 1004;


    private TextView tvProfileName, tvEventsAttaended, tvEventsHosted;
    private TextView tvRemovePhoto, tvTakePhoto, tvUploadPhoto, tvPhotoCancel;
    private TextView tvFaq, tvHelpAndFeedback, tvHandfCancel;
    private RoundedImageView ivProfilePic;
    private RelativeLayout rlPhotoActionSheet;
    public RelativeLayout rlHelpAndFeedbackActionSheet;
    private ListView lvProfileListItems;
    private ImageView ivIosSpinner;
    private View fragmentView;

    private CoreManager coreManager;
    private InternetManager internetManager;
    private UserManager userManager;
    private User loggedInUser;
    private Uri cameraFileUri;
    private File file;
    private String isTakeOrUpload = "take_picture";
    private ProfileListItemAdapter profileListItemAdapter;
    private CalendarSyncTokenManagerImpl calendarSyncTokenManagerImpl;
    private CenesUserManagerImpl cenesUserManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (fragmentView != null) {
            return fragmentView;
        }
        View view = inflater.inflate(R.layout.fragment_profile_v2, container, false);

        fragmentView = view;

        tvProfileName = (TextView) view.findViewById(R.id.tv_profile_name);
        tvEventsHosted = (TextView) view.findViewById(R.id.tv_events_hosted);
        tvEventsAttaended = (TextView) view.findViewById(R.id.tv_events_attended);
        tvRemovePhoto = (TextView) view.findViewById(R.id.tv_remove_photo);
        tvTakePhoto = (TextView) view.findViewById(R.id.tv_take_photo);
        tvUploadPhoto = (TextView) view.findViewById(R.id.tv_choose_library);
        tvPhotoCancel = (TextView) view.findViewById(R.id.tv_photo_cancel);

        tvFaq = (TextView) view.findViewById(R.id.tv_faq);
        tvHelpAndFeedback = (TextView) view.findViewById(R.id.tv_help_and_feedback);
        tvHandfCancel = (TextView) view.findViewById(R.id.tv_handf_cancel);

        ivProfilePic = (RoundedImageView) view.findViewById(R.id.iv_profile_pic);
        ivIosSpinner = (ImageView) view.findViewById(R.id.iv_ios_spinner);

        rlPhotoActionSheet = (RelativeLayout) view.findViewById(R.id.rl_photo_action_sheet);
        rlHelpAndFeedbackActionSheet = (RelativeLayout) view.findViewById(R.id.rl_helpAndFeedback_action_sheet);

        lvProfileListItems = (ListView) view.findViewById(R.id.lv_profile_list_items);

        coreManager = getCenesActivity().getCenesApplication().getCoreManager();
        internetManager = coreManager.getInternetManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();

        //Click Listeneres
        ivProfilePic.setOnClickListener(onClickListener);
        tvRemovePhoto.setOnClickListener(onClickListener);
        tvTakePhoto.setOnClickListener(onClickListener);
        tvUploadPhoto.setOnClickListener(onClickListener);
        tvPhotoCancel.setOnClickListener(onClickListener);
        tvFaq.setOnClickListener(onClickListener);
        tvHelpAndFeedback.setOnClickListener(onClickListener);
        tvHandfCancel.setOnClickListener(onClickListener);

        //Calling Functions
        ((CenesBaseActivity)getActivity()).showFooter();
        ((CenesBaseActivity)  getActivity()).activateFooterIcon(ProfileFragmentV2.TAG);
        populateProfileScreen();
        callMixPanel();
        new ProfileAsyncTask(getCenesActivity().getCenesApplication(), (CenesBaseActivity)getActivity());
        calendarSyncTokenManagerImpl = new CalendarSyncTokenManagerImpl(((CenesBaseActivity)getActivity()).getCenesApplication());
        cenesUserManager = new CenesUserManagerImpl(((CenesBaseActivity)getActivity()).getCenesApplication());
        loadAttenedHostedCounts();
        loadUserSyncTokens();
        Glide.with(getContext())
                .asGif()
                .load(R.drawable.ios_spinner)
                .into(ivIosSpinner);
        ivIosSpinner.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CenesBaseActivity)getActivity()).showFooter();
        ((CenesBaseActivity)  getActivity()).activateFooterIcon(ProfileFragmentV2.TAG);

        loggedInUser = userManager.getUser();
        populateProfileScreen();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.iv_profile_pic:
                    ((CenesBaseActivity)getActivity()).hideFooter();
                    rlPhotoActionSheet.setVisibility(View.VISIBLE);
                    break;

                case R.id.tv_remove_photo:
                    removePhotoPressed();
                    rlPhotoActionSheet.setVisibility(View.GONE);
                    ((CenesBaseActivity)getActivity()).showFooter();

                    break;

                case R.id.tv_take_photo:
                    isTakeOrUpload = "take_picture";
                    checkCameraPermissiosn();
                    rlPhotoActionSheet.setVisibility(View.GONE);
                    ((CenesBaseActivity)getActivity()).showFooter();


                    break;
                case R.id.tv_choose_library:
                    isTakeOrUpload = "upload_picture";
                    checkReadWritePermissiosn();
                    rlPhotoActionSheet.setVisibility(View.GONE);
                    ((CenesBaseActivity)getActivity()).showFooter();

                    break;

                case R.id.tv_photo_cancel:
                    rlPhotoActionSheet.setVisibility(View.GONE);
                    ((CenesBaseActivity)getActivity()).showFooter();
                    break;

                case R.id.tv_faq:
                    rlHelpAndFeedbackActionSheet.setVisibility(View.GONE);
                    ((CenesBaseActivity)getActivity()).showFooter();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CenesConstants.faqLink));
                    startActivity(browserIntent);
                    break;
                case R.id.tv_help_and_feedback:
                    rlHelpAndFeedbackActionSheet.setVisibility(View.GONE);
                    ((CenesBaseActivity)getActivity()).showFooter();

                    helpAndFeedbackPressed();
                    break;
                case R.id.tv_handf_cancel:
                    rlHelpAndFeedbackActionSheet.setVisibility(View.GONE);
                    ((CenesBaseActivity)getActivity()).showFooter();
                break;

            }
        }
    };

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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_CAMERA_REQUEST_CODE || requestCode == OPEN_GALLERY_REQUEST_CODE) {
                try {
                    if (isTakeOrUpload == "take_picture") {
                        try {
                            ImageUtils.cropImageWithAspect(cameraFileUri, this, 512, 512);
                        } catch (Exception e) {
                            showAlert("Error", e.getMessage());
                        }
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


                        ImageUtils.cropImageWithAspect(getImageUri(getContext().getApplicationContext(), rotatedBitmap), this, 512, 512);
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

                    final Bitmap bitImage = rotatedBitmap;

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            ivProfilePic.setImageBitmap(bitImage);
                            //ivProfilePic.invalidate();

                        }
                    }, 1000);


                    uploadPictureToServer();
                    //ivProfilePic.setImageBitmap(rotatedBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void populateProfileScreen() {
        try {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_pic_no_image);
            requestOptions.circleCrop();
            Glide.with(getActivity()).load(loggedInUser.getPicture()).apply(requestOptions).into(ivProfilePic);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            tvProfileName.setText(loggedInUser.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProfileItemDto> profileItemDtoList = new ArrayList<>();

        ProfileItemDto profileItemDto = new ProfileItemDto();
        profileItemDto.setTitle("Personal Details");
        profileItemDto.setDescription("Username, Email, Password, Phone, DOB");
        profileItemDto.setTAG(ProfileItemDto.ProfileItemDtoEnum.Profile);
        profileItemDtoList.add(profileItemDto);

        ProfileItemDto profileItemDto2 = new ProfileItemDto();
        profileItemDto2.setTitle("My Calendars");
        profileItemDto2.setDescription("Calendar Sync, Holiday Calendar");
        profileItemDto2.setTAG(ProfileItemDto.ProfileItemDtoEnum.Calendars);
        profileItemDtoList.add(profileItemDto2);

        ProfileItemDto profileItemDto3 = new ProfileItemDto();
        profileItemDto3.setTitle("App Settings");
        profileItemDto3.setDescription("Delete Account");
        profileItemDto3.setTAG(ProfileItemDto.ProfileItemDtoEnum.Settings);
        profileItemDtoList.add(profileItemDto3);

        ProfileItemDto profileItemDto4 = new ProfileItemDto();
        profileItemDto4.setTitle("Need Help?");
        profileItemDto4.setDescription("FAQ, Help & Feedback");
        profileItemDto4.setTAG(ProfileItemDto.ProfileItemDtoEnum.Help);
        profileItemDtoList.add(profileItemDto4);

        ProfileItemDto profileItemDto5 = new ProfileItemDto();
        profileItemDto5.setTitle("About");
        profileItemDto5.setDescription("Update to latest version");
        profileItemDto5.setTAG(ProfileItemDto.ProfileItemDtoEnum.About);
        profileItemDtoList.add(profileItemDto5);

        profileListItemAdapter = new ProfileListItemAdapter(ProfileFragmentV2.this, profileItemDtoList);
        lvProfileListItems.setAdapter(profileListItemAdapter);
    }

    public void callMixPanel() {
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
        try {
            JSONObject props = new JSONObject();
            props.put("Action","Profile Screen Opened");
            props.put("UserEmail",loggedInUser.getEmail());
            props.put("UserName",loggedInUser.getName());
            props.put("Device","Android");
            mixpanel.track("ProfileScreen", props);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void helpAndFeedbackPressed() {
        Intent intent=new Intent(Intent.ACTION_SEND);
        String[] recipients={"support@cenesgroup.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);

        String phoneDetails = "Device : "+CenesUtils.getDeviceManufacturer()+" "+CenesUtils.getDeviceModel()+" "+CenesUtils.getDeviceVersion()+"\n";

        intent.putExtra(Intent.EXTRA_TEXT,phoneDetails);
        intent.setType("text/html");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }
    public void loadAttenedHostedCounts() {

        try {
            if (internetManager.isInternetConnection(getCenesActivity())) {
                String queryStr = "userId="+loggedInUser.getUserId();
                String api = UrlManagerImpl.prodAPIUrl+UserAPI.get_user_stats;

                AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
                asyncTaskDto.setApiUrl(api);
                asyncTaskDto.setQueryStr(queryStr);
                asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

                new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject response) {

                        try {
                            boolean success = response.getBoolean("success");
                            if (success == true) {
                                JSONObject data = response.getJSONObject("data");

                                tvEventsHosted.setText(data.getInt("eventsHostedCounts")+"");
                                tvEventsAttaended.setText(data.getInt("eventsAttendedCounts")+"");

                            } else {

                                String message = response.getString("message");
                                JSONObject props = new JSONObject();
                                props.put("Error",message);
                                props.put("UserEmail",loggedInUser.getEmail());
                                props.put("UserName",loggedInUser.getName());
                                CenesUtils.postOnMixPanel(getContext(), "Profile", props);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).execute(asyncTaskDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePhotoPressed() {
        try {

            JSONObject postData = new JSONObject();
            postData.put("profilePic", "");
            postData.put("userId", loggedInUser.getUserId());

            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setPostData(postData);
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+UserAPI.post_userdetails);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            new ProfileAsyncTask.CommonPostRequestTask(new ProfileAsyncTask.CommonPostRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    ivProfilePic.setImageResource(R.drawable.profile_pic_no_image);

                    //Update Ptofile Pic in Local Database
                    loggedInUser.setPicture(null);
                    userManager.updateProfilePic(loggedInUser);
                    ((CenesBaseActivity)getActivity()).homeFragmentV2.loadHomeScreenData();
                }
            }).execute(asyncTaskDto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadPictureToServer() {
        try {
            ivIosSpinner.setVisibility(View.VISIBLE);

            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodImageApiDomain+UserAPI.post_upload_profile_pic_v2);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setFileToUpload(file);
            new ProfileAsyncTask.CommonMultipartUploadTask(new ProfileAsyncTask.CommonMultipartUploadTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {
                    ivIosSpinner.setVisibility(View.GONE);

                    try {
                        boolean success = response.getBoolean("success");
                        if (success == true) {

                            String profilePic = response.getString("data");
                            loggedInUser.setPicture(profilePic);
                            userManager.updateProfilePic(loggedInUser);
                            cenesUserManager.updateCenesUser(loggedInUser);

                            //Reload Home Screen
                            if (getActivity() != null) {
                                //((CenesBaseActivity)getActivity()).homeScreenReloadBroadcaster();
                                ((CenesBaseActivity)getActivity()).homeFragmentV2.loadHomeScreenData();
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
            this.startActivityForResult(takePictureIntent, OPEN_CAMERA_REQUEST_CODE);

        } else if (isTakeOrUpload == "upload_picture") {
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            this.startActivityForResult(browseIntent, OPEN_GALLERY_REQUEST_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("cameraMediaOutputUri", cameraFileUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState !=null && savedInstanceState.containsKey("cameraMediaOutputUri"))
            cameraFileUri = savedInstanceState.getParcelable("cameraMediaOutputUri");
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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

    public void loadUserSyncTokens() {
        try {
            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ UserAPI.get_user_sync_details);
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
                        boolean success = response.getBoolean("success");
                        if (success == true) {

                            Gson gson = new GsonBuilder().create();
                            Type listType = new TypeToken<List<CalenadarSyncToken>>(){}.getType();
                            List<CalenadarSyncToken> calenadarSyncTokens = gson.fromJson(response.getJSONArray("data").toString(), listType);
                            if (calenadarSyncTokens != null && calenadarSyncTokens.size() > 0) {

                                List<CalenadarSyncToken> localCalenadarSyncTokens = calendarSyncTokenManagerImpl.fetchCalendarAll();
                                if (localCalenadarSyncTokens != null && localCalenadarSyncTokens.size() > 0) {


                                    for (CalenadarSyncToken calenadarSyncToken: calenadarSyncTokens) {
                                        boolean syncTokenExists = false;
                                        for (CalenadarSyncToken localCalenadarSyncToken: localCalenadarSyncTokens) {
                                            if (localCalenadarSyncToken.getRefreshTokenId().equals(localCalenadarSyncToken.getRefreshTokenId())) {
                                                syncTokenExists = true;
                                                break;
                                            }
                                        }

                                        if (syncTokenExists == false) {
                                            calendarSyncTokenManagerImpl.addNewRow(calenadarSyncToken);
                                        }
                                    }
                                } else {
                                    for (CalenadarSyncToken calenadarSyncToken: calenadarSyncTokens) {
                                            calendarSyncTokenManagerImpl.addNewRow(calenadarSyncToken);

                                    }
                                }

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


}
