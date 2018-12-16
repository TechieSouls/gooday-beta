package com.cenes.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.Manager.AlertManager;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.DeviceManager;
import com.cenes.Manager.UrlManager;
import com.cenes.Manager.ValidationManager;
import com.cenes.R;
import com.cenes.activity.AlarmActivity;
import com.cenes.activity.DiaryActivity;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.ReminderActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.util.CenesEditText;
import com.cenes.util.CenesUtils;
import com.cenes.util.ImageUtils;
import com.cenes.util.RoundedDrawable;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by mandeep on 8/11/17.
 */

public class ProfileFragment extends CenesFragment implements View.OnFocusChangeListener {

    public final static String TAG = "ProfileFragment";

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    private AlertManager alertManager;
    private DeviceManager deviceManager;
    private ValidationManager validationManager;


    private static final int PICK_IMAGE_GALLERY = 12;
    private static final int PICK_IMAGE_CAMERA = 13;

    ImageView ivProfile;
    CenesEditText etName, etEmail;
    Spinner spGender;

    RelativeLayout rlHeader;
    ImageView ivProfileHeader;
    TextView tvSave;

    String gender = "";

    private User loggedInUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile, container, false);

        init(v);

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
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        validationManager = coreManager.getValidatioManager();
        alertManager = coreManager.getAlertManager();

        loggedInUser = userManager.getUser();
        setFragmentManager();

        rlHeader = (RelativeLayout) v.findViewById(R.id.rl_header);
        ivProfileHeader = (ImageView) v.findViewById(R.id.ivProfileHeader);
        tvSave = (TextView) v.findViewById(R.id.tvSave);

        ivProfile = (ImageView) v.findViewById(R.id.ivProfile);
        etName = (CenesEditText) v.findViewById(R.id.etName);
        etEmail = (CenesEditText) v.findViewById(R.id.etEmail);
        spGender = (Spinner) v.findViewById(R.id.spGender);

        etName.setText(loggedInUser.getName());
        etEmail.setText(!CenesUtils.isEmpty(loggedInUser.getEmail()) ? loggedInUser.getEmail() : "");

        String arrGender[] = getResources().getStringArray(R.array.gender);

        for (int i = 0; i < arrGender.length; i++) {
            if (!CenesUtils.isEmpty(loggedInUser.getGender()) && loggedInUser.getGender().equalsIgnoreCase(arrGender[i])) {
                spGender.setSelection(i);
            }
        }

        etName.setOnTouchListener(clearTextTouchListener);
        etName.setOnFocusChangeListener(this);
        etEmail.setOnTouchListener(clearTextTouchListener);
        etEmail.setOnFocusChangeListener(this);

        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (loggedInUser != null && !CenesUtils.isEmpty(loggedInUser.getPicture())) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.default_profile_icon);
            requestOptions.circleCrop();

            Glide.with(getActivity()).load(loggedInUser.getPicture()).apply(requestOptions).into(ivProfile);
            Glide.with(getActivity()).load(loggedInUser.getPicture()).apply(requestOptions).into(ivProfileHeader);
        }
        ivProfile.setOnClickListener(imageClickListener);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Save user Data
                if (file == null) {
                    JSONObject userObj = new JSONObject();
                    try {
                        userObj.put("userId", loggedInUser.getUserId());
                        userObj.put("name", etName.getText().toString().trim());
                        userObj.put("email", !CenesUtils.isEmpty(etEmail.getText().toString().trim()) ? etEmail.getText().toString().trim() : "");
                        if (!CenesUtils.isEmpty(loggedInUser.getPicture())) {
                            userObj.put("photo", loggedInUser.getPicture());
                        }
                        if (!CenesUtils.isEmpty(gender) && !gender.equalsIgnoreCase("Select")) {
                            userObj.put("gender", gender);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new UpdateUserTask().execute(userObj);
                } else {
                    new DoFileUpload().execute();
                }

            }
        });

        ivProfileHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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

    private static String isTakeOrUpload = "take_picture";

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
                        ImageUtils.cropImageWithAspect(Uri.parse(data.getDataString()), this, 512, 512);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == Crop.REQUEST_CROP) {
                try {
                    String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), Crop.getOutput(data));
                    file = new File(filePath);

                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), Crop.getOutput(data));
                    RoundedDrawable drawable = new RoundedDrawable(ImageUtils.getRotatedBitmap(imageBitmap, filePath));

                    ivProfile.setImageDrawable(drawable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ((EditText) v).setCompoundDrawablesRelativeWithIntrinsicBounds(((EditText) v).getCompoundDrawables()[DRAWABLE_LEFT], null, getResources().getDrawable(R.drawable.close), null);
        } else {
            ((EditText) v).setCompoundDrawablesRelativeWithIntrinsicBounds(((EditText) v).getCompoundDrawables()[DRAWABLE_LEFT], null, null, null);
        }
    }

    public View.OnTouchListener clearTextTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            EditText et = (EditText) v;
            if (event.getAction() == MotionEvent.ACTION_UP && et.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                if (event.getRawX() >= (et.getRight() - et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) - 50) {
                    et.setText("");
                    return true;
                }
            }
            return false;
        }
    };

    class UpdateUserTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getCenesActivity());
            pDialog.setMessage("Updating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... objects) {
            JSONObject userData = objects[0];
            try {
                loggedInUser.setApiUrl(urlManager.getApiUrl("dev"));
                JSONObject response = apiManager.updateUserInfo(loggedInUser, userData, getCenesActivity());
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
            try {
                if (response != null && response.getBoolean("success")) {
                    JSONObject userObj = response.getJSONObject("data");
                    loggedInUser.setName(userObj.getString("name"));
                    loggedInUser.setEmail(userObj.getString("email"));
                    loggedInUser.setGender(userObj.getString("gender"));
                    loggedInUser.setPicture(userObj.getString("photo"));
                    userManager.updateUser(loggedInUser);

                    Fragment fragmentFromBackStack = getFragmentPresentInBackStack();
                    if (fragmentFromBackStack != null) {
                        ((NavigationFragment) fragmentFromBackStack).refreshUserInfo(loggedInUser);
                    }

                    Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Error in updating Info", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    FragmentManager fragmentManager;

    public Fragment getFragmentPresentInBackStack() {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && (fragment instanceof NavigationFragment))
                    return fragment;
            }
        }
        return null;
    }

    class DoFileUpload extends AsyncTask<String, JSONObject, JSONObject> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(getCenesActivity());
            pDialog.setMessage("wait uploading Image..");
            pDialog.setIndeterminate(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                User user = userManager.getUser();
                user.setApiUrl(urlManager.getApiUrl("dev"));
                JSONObject response = apiManager.postMultipart(user, file, getCenesActivity());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            try {
                if (response != null && response.getInt("errorCode") == 0) {
                    if (response.has("photo")) {
                        loggedInUser.setPicture(response.getString("photo"));
                        userManager.updateProfilePic(loggedInUser);

                        JSONObject userObj = new JSONObject();
                        try {
                            userObj.put("userId", loggedInUser.getUserId());
                            userObj.put("name", etName.getText().toString().trim());
                            userObj.put("email", !CenesUtils.isEmpty(etEmail.getText().toString().trim()) ? etEmail.getText().toString().trim() : "");
                            if (!CenesUtils.isEmpty(loggedInUser.getPicture())) {
                                userObj.put("photo", loggedInUser.getPicture());
                            }
                            if (!CenesUtils.isEmpty(gender) && !gender.equalsIgnoreCase("Select")) {
                                userObj.put("gender", gender);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new UpdateUserTask().execute(userObj);
                    }
                    pDialog.cancel();
                } else {
                    getCenesActivity().showRequestTimeoutDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
