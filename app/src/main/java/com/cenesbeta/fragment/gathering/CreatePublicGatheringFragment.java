package com.cenesbeta.fragment.gathering;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.Manager.DeviceManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.util.CenesMessages;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.ImageUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.view.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

public class CreatePublicGatheringFragment extends CenesFragment {

    public static String TAG = "CreatePublicGatheringFragment";
    public enum PhotoOptions {Camera, Gallery};
    private int SEARCH_LOCATION_RESULT_CODE = 1001, SEARCH_FRIEND_RESULT_CODE = 1002,
            GATHERING_SUMMARY_RESULT_CODE = 1003, MESSAGE_FRAGMENT_CODE = 1004,
            CLICK_IMAGE_REQUEST_CODE = 1005, UPLOAD_IMAGE_REQUEST_CODE = 1006,
            UPLOAD_PERMISSION_CODE = 1007, CAMERA_PERMISSION_CODE = 2001;

    private ImageView ivEventImage, ivAbandonEvent, ivEventImagePlaceholder;
    private EditText gathEventTitleEt, etEventDescription;
    private TextView tvEditDescriptionLimit, tvChooseLibrary, tvTakePhoto, tvPhotoCancel, tvGatheringLocation;
    private RelativeLayout rlEventImageContainer, rlPreviewPublicEvent, rlPhotoActionSheet, rlGathLocationBar;
    private LinearLayout llPredictiveCalCell, llPredictiveCalInfo, llGatheringDateBars;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private DeviceManager deviceManager;
    private Event event;
    private PhotoOptions photoOptionSelected;
    private Uri cameraFileUri;
    private File eventImageFile;
    private User loggedInUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_create_public_gathering, container, false);

        gathEventTitleEt = (EditText) view.findViewById(R.id.gath_event_title_et);
        etEventDescription = (EditText)view.findViewById(R.id.et_event_description);
        tvEditDescriptionLimit = (TextView) view.findViewById(R.id.tv_event_descriptions_limit);

        rlEventImageContainer = (RelativeLayout) view.findViewById(R.id.rl_event_image_container);
        rlPreviewPublicEvent = (RelativeLayout) view.findViewById(R.id.rl_preview_public_event);
        rlPhotoActionSheet = (RelativeLayout) view.findViewById(R.id.rl_photo_action_sheet);
        rlGathLocationBar = (RelativeLayout) view.findViewById(R.id.rl_gath_location_bar);

        ivEventImage = (ImageView) view.findViewById(R.id.iv_event_image);
        ivAbandonEvent = (ImageView) view.findViewById(R.id.iv_abandon_event);
        ivEventImagePlaceholder = (ImageView) view.findViewById(R.id.iv_event_image_placeholder);

        tvChooseLibrary = (TextView) view.findViewById(R.id.tv_choose_library);
        tvTakePhoto = (TextView) view.findViewById(R.id.tv_take_photo);
        tvPhotoCancel = (TextView) view.findViewById(R.id.tv_photo_cancel);
        tvGatheringLocation = (TextView) view.findViewById(R.id.tv_gathering_location);

        ivEventImage.setOnClickListener(onClickListener);
        ivAbandonEvent.setOnClickListener(onClickListener);
        rlEventImageContainer.setOnClickListener(onClickListener);
        rlPreviewPublicEvent.setOnClickListener(onClickListener);
        rlGathLocationBar.setOnClickListener(onClickListener);
        tvChooseLibrary.setOnClickListener(onClickListener);
        tvTakePhoto.setOnClickListener(onClickListener);
        tvPhotoCancel.setOnClickListener(onClickListener);

        //Event Image should be 119% of width of screen
        int widthOfScreen = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        ivEventImage.getLayoutParams().height = (widthOfScreen*119)/100;


        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.iv_abandon_event:
                    new AlertDialog.Builder(getActivity())
                        .setTitle("Abandon Event?")
                        .setMessage("If you decide to leave this page, all progress will be lost.")
                        .setCancelable(false)
                        .setPositiveButton("Stay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                    break;
                case R.id.iv_event_image:

                    break;
                case R.id.rl_gath_location_bar:

                    break;
                case R.id.rl_event_image_container:
                    hideKeyboardAndClearFocus(gathEventTitleEt);
                    rlPhotoActionSheet.setVisibility(View.VISIBLE);
                    break;

                case R.id.rl_preview_public_event:
                    previewEvent();
                    break;

                case R.id.tv_choose_library:
                    hideKeyboardAndClearFocus(gathEventTitleEt);

                    photoOptionSelected = PhotoOptions.Gallery;
                    chooseFromGalleryPressed();
                    rlPreviewPublicEvent.setVisibility(View.VISIBLE);
                    break;

                case R.id.tv_take_photo:
                    hideKeyboardAndClearFocus(gathEventTitleEt);

                    photoOptionSelected = PhotoOptions.Camera;
                            checkCameraPermissiosn();
                    rlPreviewPublicEvent.setVisibility(View.VISIBLE);
                    break;
                case R.id.tv_photo_cancel:
                    hideKeyboardAndClearFocus(gathEventTitleEt);

                    rlPreviewPublicEvent.setVisibility(View.VISIBLE);
                    rlPhotoActionSheet.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public void previewEvent() {

        if (CenesUtils.isEmpty(gathEventTitleEt.getText().toString())) {
            showAlert("Alert", CenesMessages.ENTER_EVENT_TITLE);
            return;
        }

        event.setTitle(gathEventTitleEt.getText().toString());

        PublicGatheringPreviewFragment publicGatheringPreviewFragment = new PublicGatheringPreviewFragment();
        publicGatheringPreviewFragment.event = event;
        ((CenesBaseActivity)getActivity()).replaceFragment(publicGatheringPreviewFragment, CreatePublicGatheringFragment.TAG);
    }

    public void chooseFromGalleryPressed() {

        rlPhotoActionSheet.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, UPLOAD_PERMISSION_CODE);
        } else {
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            startActivityForResult(browseIntent, UPLOAD_IMAGE_REQUEST_CODE);
        }
    }

    public void checkCameraPermissiosn() {
        rlPhotoActionSheet.setVisibility(View.GONE);
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
            openCamera();
        }
    }

    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
        //startActivityForResult(takePictureIntent, CLICK_IMAGE_REQUEST_CODE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Cenes";
            File newdir = new File(dir);
            newdir.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File file = new File(dir + File.separator + "IMG_" + timeStamp + ".jpg");

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            cameraFileUri = null;
            cameraFileUri = Uri.fromFile(file);
            //takePictureIntent.putExtra( android.provider.MediaStore.EXTRA_SIZE_LIMIT, "720000");
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
            //takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, 180);
            this.startActivityForResult(takePictureIntent, CLICK_IMAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if  (resultCode == Activity.RESULT_OK ) {
            if (requestCode == UPLOAD_IMAGE_REQUEST_CODE) {
                try {

                    String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), data.getData());

                    Uri imageUri = data.getData();

                /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), imageUri);
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

                ImageUtils.cropImageWithAspect(getImageUri(getContext().getApplicationContext(), rotatedBitmap), this, 1280, 512);*/
                    Uri resultUri = Uri.fromFile(new File(ImageUtils.getDefaultFile()));
                    UCrop.Options options = new UCrop.Options();
                    options.setAspectRatioOptions(1,
                            new AspectRatio("1:2", 1, 2),
                            new AspectRatio("3:4", 3, 4),
                            new AspectRatio("Original", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
                            new AspectRatio("16:9", 16, 9),
                            new AspectRatio("1:1", 1, 1));
                    UCrop.of(imageUri, resultUri).withOptions(options)
                            //.withAspectRatio(3, 4)
                            .withMaxResultSize(1600, 1000)
                            .start(getContext(), CreatePublicGatheringFragment.this, UCrop.REQUEST_CROP);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CLICK_IMAGE_REQUEST_CODE) {

                try {
                    Uri resultUri = Uri.fromFile(new File(ImageUtils.getDefaultFile()));
                    UCrop.Options options = new UCrop.Options();
                    options.setAspectRatioOptions(1,
                            new AspectRatio("1:2", 1, 2),
                            new AspectRatio("3:4", 3, 4),
                            new AspectRatio("Original", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
                            new AspectRatio("16:9", 16, 9),
                            new AspectRatio("1:1", 1, 1));

                    UCrop.of(cameraFileUri, resultUri).withOptions(options)
                            //.withAspectRatio(3, 4)
                            .withMaxResultSize(1600, 1000)
                            .start(getContext(), CreatePublicGatheringFragment.this, UCrop.REQUEST_CROP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {

                try {
                    System.out.println("Back from UCrop.....");
                    Uri resultUri = UCrop.getOutput(data);
                    String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), resultUri);
                    eventImageFile = new File(filePath);
                    System.out.println("File Path String : " + filePath);
                    System.out.println("File Path : " + eventImageFile.getPath());
                    event.setEventImageURI(eventImageFile.getPath());

                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                    try {
                        JSONObject props = new JSONObject();
                        props.put("Action", "After Cropping Success");
                        props.put("Logs", "FilePath: " + filePath);
                        props.put("UserEmail", loggedInUser.getEmail());
                        props.put("UserName", loggedInUser.getName());
                        mixpanel.track("Gathering", props);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), resultUri);

                    /*ExifInterface ei = new ExifInterface(filePath);
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

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            tvCoverImageStatus.setText("Uplaoded");
                        }
                    }, 500);*/
                } catch (Exception e) {
                    e.printStackTrace();

                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                    try {
                        JSONObject props = new JSONObject();
                        props.put("Action", "After Cropping Exception");
                        props.put("Logs", e.getMessage());
                        props.put("UserEmail", loggedInUser.getEmail());
                        props.put("UserName", loggedInUser.getName());
                        mixpanel.track("Gathering", props);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                //progressBar.setVisibility(View.VISIBLE);
                //uploadEventPicture(eventImageFile);
            }
        }
    }
}
