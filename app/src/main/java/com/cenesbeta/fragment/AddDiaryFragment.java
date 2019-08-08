package com.cenesbeta.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.Manager.AlertManager;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.DeviceManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.Manager.ValidationManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.DiaryActivity;
import com.cenesbeta.activity.SearchFriendActivity;
import com.cenesbeta.activity.SearchLocationActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.ImageUtils;
import com.cenesbeta.util.RoundedImageView;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mandeep on 6/11/17.
 */

public class AddDiaryFragment extends CenesFragment implements View.OnFocusChangeListener {

    private int SEACRH_LOCATION_RESULT_CODE = 1001, SEARCH_FRIEND_RESULT_CODE = 1002;
    private static final int PICK_IMAGE = 2;

    private View fragmentView;

    private FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    private AlertManager alertManager;
    private DeviceManager deviceManager;
    private ValidationManager validationManager;

    private boolean isEditMode;

    private User loggedInUser;
    private JSONObject diary;
    private Set<Map<String, String>> inviteFriendsImageList;
    private List<File> diaryFilesToUpload;
    private List<String> diaryGallery;
    private Long diaryId;
    private int createdById;
    private Calendar diaryCalendar;
    private EditText etTitle, etDesc;
    private TextView tvAddPhotos, tvInviteFrndsBtn, tvSearchLocationButton, tvTime;
    private TextView tvSaveBtn, tvCloseBtn;
    private RecyclerView photosRecyclerView;
    private TextView deleteBtnBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_create_diary, container, false);
        fragmentManager = ((DiaryActivity) getActivity()).fragmentManager;

        fragmentView = view;
        init();

        Bundle bundle = getArguments();
        if (bundle != null && bundle.getString("diaryData") != null) {
            try {
                deleteBtnBar.setVisibility(View.VISIBLE);
                isEditMode = true;

                diary = new JSONObject(bundle.getString("diaryData"));
                createdById = diary.getInt("createdById");
                diaryId = diary.getLong("diaryId");
                etTitle.setText(diary.getString("title"));
                tvTime.setText(CenesUtils.MMMMddy.format(diary.getLong("diaryTime")));
                diaryCalendar = Calendar.getInstance();
                diaryCalendar.setTimeInMillis(diary.getLong("diaryTime"));
                if (!CenesUtils.isEmpty(diary.getString("detail"))) {
                    etDesc.setText(diary.getString("detail"));
                }

                if (diary.has("location") && !diary.isNull("location") && !diary.getString("location").isEmpty()) {
                    tvSearchLocationButton.setText(diary.getString("location"));
                }

                if (diary.has("members") && !diary.isNull("members") && diary.getJSONArray("members").length() > 0) {
                    inviteFriendsImageList = new HashSet<>();
                    for (int i = 0; i < diary.getJSONArray("members").length(); i++) {
                        JSONObject frndObj = diary.getJSONArray("members").getJSONObject(i);
                        System.out.println(frndObj.toString());
                        Map<String, String> invFrnMap = new HashMap<>();
                        invFrnMap.put("name", frndObj.getString("name"));
                        invFrnMap.put("picture", frndObj.getString("picture"));
                        invFrnMap.put("userId", String.valueOf(frndObj.get("userId")));
                        inviteFriendsImageList.add(invFrnMap);
                    }

                    if (inviteFriendsImageList.size() > 0) {
                        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
                        llFriendsContainer = (HorizontalScrollView) fragmentView.findViewById(R.id.ll_friends_container);
                        ivAddMoreFriends = (ImageView) fragmentView.findViewById(R.id.iv_add_more_friends);
                        llFriendsContainer.setVisibility(View.VISIBLE);

                        ArrayList<Map<String, String>> jsonObjectArrayList = new ArrayList<>(inviteFriendsImageList);
                        mFriendsAdapter = new FriendsAdapter(jsonObjectArrayList);

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mFriendsAdapter);
                    }
                }
                if (diary.getString("pictures") != null && diary.getString("pictures") != "null" && !diary.getString("pictures").isEmpty()) {
                    photosRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.photos_recycler_view);
                    photosRecyclerView.setVisibility(View.VISIBLE);

                    String[] imageFileLinks = diary.getString("pictures").split(",");
                    for (String img : imageFileLinks) {
                        diaryGallery.add(img);
                    }
                    photosAdapter = new PhotosAdapter(null, diaryGallery);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    photosRecyclerView.setLayoutManager(mLayoutManager);
                    photosRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    photosRecyclerView.setAdapter(photosAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    public void init() {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        deviceManager = coreManager.getDeviceManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        validationManager = coreManager.getValidatioManager();
        alertManager = coreManager.getAlertManager();

        loggedInUser = userManager.getUser();
        diaryCalendar = null;

        etTitle = (EditText) fragmentView.findViewById(R.id.et_title);
        etDesc = (EditText) fragmentView.findViewById(R.id.et_desc);
        tvAddPhotos = (TextView) fragmentView.findViewById(R.id.tv_add_photos);
        tvInviteFrndsBtn = (TextView) fragmentView.findViewById(R.id.tv_invite_frnds_btn);
        ivAddMoreFriends = (ImageView) fragmentView.findViewById(R.id.iv_add_more_friends);
        tvSearchLocationButton = (TextView) fragmentView.findViewById(R.id.tv_search_location_button);
        tvTime = (TextView) fragmentView.findViewById(R.id.tv_time);

        tvSaveBtn = (TextView) fragmentView.findViewById(R.id.tv_save_btn);
        tvCloseBtn = (TextView) fragmentView.findViewById(R.id.tv_cancel);

        deleteBtnBar = (TextView) fragmentView.findViewById(R.id.delete_btn_bar);

        tvAddPhotos.setOnClickListener(onClickListener);
        tvInviteFrndsBtn.setOnClickListener(onClickListener);
        ivAddMoreFriends.setOnClickListener(onClickListener);
        tvSearchLocationButton.setOnClickListener(onClickListener);

        tvSaveBtn.setOnClickListener(onClickListener);
        tvCloseBtn.setOnClickListener(onClickListener);
        deleteBtnBar.setOnClickListener(onClickListener);
        tvTime.setOnClickListener(onClickListener);

        inviteFriendsImageList = new HashSet<>();
        diaryFilesToUpload = new ArrayList<>();
        diaryGallery = new ArrayList<>();

        etTitle.setOnTouchListener(clearTextTouchListener);
        etTitle.setOnFocusChangeListener(this);
        etDesc.setOnTouchListener(clearTextTouchListener);
        etDesc.setOnFocusChangeListener(this);
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

    @Override
    public void onResume() {
        super.onResume();
        ((DiaryActivity) getActivity()).hideFooter();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_add_photos:
                    if (ContextCompat.checkSelfPermission(getCenesActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        browseIntent.setType("image/*");
                        startActivityForResult(browseIntent, PICK_IMAGE);
                    }
                    break;

                case R.id.tv_invite_frnds_btn:
                case R.id.iv_add_more_friends:
                    startActivityForResult(new Intent(getActivity(), SearchFriendActivity.class), SEARCH_FRIEND_RESULT_CODE);
                    break;

                case R.id.tv_search_location_button:
                    startActivityForResult(new Intent(getActivity(), SearchLocationActivity.class), SEACRH_LOCATION_RESULT_CODE);
                    break;
                case R.id.tv_cancel:
                    getActivity().onBackPressed();
                    break;
                case R.id.tv_save_btn:
                    if (isValid()) {
                        if (diaryFilesToUpload.size() > 0) {
                            for (int i = 0; i < diaryFilesToUpload.size(); i++) {
                                try {
                                    File fileToUpload = diaryFilesToUpload.get(i);
                                    new DoFileUpload(i + 1).execute(fileToUpload);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            new AddDiaryTask().execute(populateDiaryData());
                        }
                    }
                    break;
                case R.id.delete_btn_bar:
                    try {
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                        alert.setTitle("Delete");
                        alert.setMessage("Are you sure you want to delete this Diary?");
                        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    new DeleteDiaryTask().execute(diary.getLong("diaryId"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.tv_time:

                    if (diaryCalendar == null) {
                        diaryCalendar = Calendar.getInstance();
                    }
                    final DatePickerDialog.OnDateSetListener diaryDatePickerListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                            diaryCalendar = Calendar.getInstance();
                            diaryCalendar.set(Calendar.YEAR, year);
                            diaryCalendar.set(Calendar.MONTH, month);
                            diaryCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                            System.out.println("DatePicker : " + diaryCalendar.getTime());
                            tvTime.setText(CenesUtils.MMMMddy.format(diaryCalendar.getTime()));

                        }
                    };
                    DatePickerDialog reminderDatePicker = new DatePickerDialog(getContext(), diaryDatePickerListener, diaryCalendar.get(Calendar.YEAR), diaryCalendar.get(Calendar.MONTH), diaryCalendar.get(Calendar.DAY_OF_MONTH));
                    reminderDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                    reminderDatePicker.show();
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            startActivityForResult(browseIntent, PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), data.getData());
                    Log.e("Action ******** ", data.getDataString());

                    Uri uri = Uri.parse(data.getDataString());

                    String s = ImageUtils.getPath(getCenesActivity().getApplicationContext(), uri);
                    File imageFile = new File(s);
                    diaryFilesToUpload.add(imageFile);

                    if (diaryFilesToUpload.size() > 0) {
                        photosRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.photos_recycler_view);
                        photosRecyclerView.setVisibility(View.VISIBLE);

                        ArrayList<File> jsonObjectArrayList = new ArrayList<>(diaryFilesToUpload);
                        photosAdapter = new PhotosAdapter(jsonObjectArrayList, diaryGallery);

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        photosRecyclerView.setLayoutManager(mLayoutManager);
                        photosRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        photosRecyclerView.setAdapter(photosAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }*/
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                ImageUtils.cropImageWithNewLocation(Uri.parse(data.getDataString()), this, 512, 512);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            try {
                String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), Crop.getOutput(data));
                File imageFile = new File(filePath);
                diaryFilesToUpload.add(imageFile);

                if (diaryFilesToUpload.size() > 0) {
                    photosRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.photos_recycler_view);
                    photosRecyclerView.setVisibility(View.VISIBLE);

                    ArrayList<File> jsonObjectArrayList = new ArrayList<>(diaryFilesToUpload);
                    photosAdapter = new PhotosAdapter(jsonObjectArrayList, diaryGallery);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    photosRecyclerView.setLayoutManager(mLayoutManager);
                    photosRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    photosRecyclerView.setAdapter(photosAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == SEACRH_LOCATION_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            String requiredValue = data.getStringExtra("title");
            tvSearchLocationButton.setText(requiredValue.toString());
        } else if (requestCode == SEACRH_LOCATION_RESULT_CODE && resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Location canceleld");
        } else if (requestCode == SEARCH_FRIEND_RESULT_CODE && resultCode == Activity.RESULT_OK) {

            String requiredValue = data.getExtras().getString("photo");
            String name = data.getExtras().getString("name");
            Map<String, String> invFrnMap = new HashMap<>();
            invFrnMap.put("name", name);
            invFrnMap.put("picture", requiredValue);
            invFrnMap.put("userId", data.getExtras().getString("userId"));

            Boolean isFriendExist = false;
            if (inviteFriendsImageList.size() > 0) {
                for (Map<String, String> friendMap : inviteFriendsImageList) {
                    System.out.println(friendMap.get("userId"));
                    System.out.println(invFrnMap.get("userId"));
                    if (friendMap.get("userId") == invFrnMap.get("userId")) {
                        isFriendExist = true;
                        break;
                    }
                }
            }
            if (!isFriendExist) {
                inviteFriendsImageList.add(invFrnMap);
            }

            if (inviteFriendsImageList.size() > 0) {
                recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
                llFriendsContainer = (HorizontalScrollView) fragmentView.findViewById(R.id.ll_friends_container);
                ivAddMoreFriends = (ImageView) fragmentView.findViewById(R.id.iv_add_more_friends);
                llFriendsContainer.setVisibility(View.VISIBLE);

                ArrayList<Map<String, String>> jsonObjectArrayList = new ArrayList<>(inviteFriendsImageList);
                mFriendsAdapter = new FriendsAdapter(jsonObjectArrayList);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mFriendsAdapter);
            }
        }
    }

    private RecyclerView recyclerView;
    private HorizontalScrollView llFriendsContainer;
    private ImageView ivAddMoreFriends;
    private FriendsAdapter mFriendsAdapter;
    private PhotosAdapter photosAdapter;

    public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.MyViewHolder> {

        private ArrayList<File> jsonObjectArrayList;
        private List<String> imageLinks;
        private Boolean isLinks = false;
        private List<Map<String, Object>> imagesToShow;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivDiaryGalleryImage;
            RelativeLayout container;
            ImageButton ibDelete;

            public MyViewHolder(View view) {
                super(view);
                ivDiaryGalleryImage = (ImageView) view.findViewById(R.id.iv_diary_gallery_image);
                container = (RelativeLayout) view.findViewById(R.id.container);
                ibDelete = (ImageButton) view.findViewById(R.id.ib_delete);
            }
        }

        public PhotosAdapter(ArrayList<File> jsonObjectArrayList, List<String> imageFileLinks) {
            this.imagesToShow = new ArrayList<>();
            if (jsonObjectArrayList != null && jsonObjectArrayList.size() > 0) {
                this.jsonObjectArrayList = jsonObjectArrayList;
                for (File file : this.jsonObjectArrayList) {
                    Map<String, Object> actualFileMap = new HashMap<>();
                    actualFileMap.put("file", file);
                    this.imagesToShow.add(actualFileMap);
                }
            }
            if (imageFileLinks != null && imageFileLinks.size() > 0) {
                this.imageLinks = imageFileLinks;
                for (String link : this.imageLinks) {
                    Map<String, Object> linkMap = new HashMap<>();
                    linkMap.put("link", link);
                    this.imagesToShow.add(linkMap);
                }
            }
        }

        @Override
        public PhotosAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_image_list_item, parent, false);
            return new PhotosAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final PhotosAdapter.MyViewHolder holder, final int position) {
            try {

                Map<String, Object> filesMap = this.imagesToShow.get(position);

                if (filesMap.containsKey("file")) {
                    final File gallImg = (File) filesMap.get("file");
                    String filePath = gallImg.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    holder.ivDiaryGalleryImage.setImageBitmap(ImageUtils.getRotatedBitmap(bitmap, filePath));
                }
                if (filesMap.containsKey("link")) {
                    String imgFileLink = (String) filesMap.get("link");
                    Glide.with(getActivity()).load(imgFileLink).into(holder.ivDiaryGalleryImage);
                }

                holder.container.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (holder.ibDelete.getVisibility() == View.VISIBLE) {
                            holder.ibDelete.setVisibility(View.GONE);
                        } else {
                            holder.ibDelete.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                });
                holder.ibDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> imagesToShowTemp = imagesToShow.get(position);
                        if (imagesToShowTemp.containsKey("link")) {
                            diaryGallery.remove((String) imagesToShowTemp.get("link"));
                        }
                        if (imagesToShowTemp.containsKey("file")) {
                            diaryFilesToUpload.remove((File) imagesToShowTemp.get("file"));
                        }

                        imagesToShow.remove(position);
                        photosRecyclerView.removeViewAt(position);
                        photosAdapter.notifyItemRemoved(position);
                        photosAdapter.notifyItemRangeChanged(position, imagesToShow.size());

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return imagesToShow.size();
        }
    }

    public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {

        private ArrayList<Map<String, String>> jsonObjectArrayList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public RoundedImageView ivFriend;
            TextView tvName;
            RelativeLayout container;
            ImageButton ibDelete;

            public MyViewHolder(View view) {
                super(view);
                ivFriend = (RoundedImageView) view.findViewById(R.id.iv_friend_image);
                tvName = (TextView) view.findViewById(R.id.tv_friend_name);
                container = (RelativeLayout) view.findViewById(R.id.container);
                ibDelete = (ImageButton) view.findViewById(R.id.ib_delete);
            }
        }

        public FriendsAdapter(ArrayList<Map<String, String>> jsonObjectArrayList) {
            this.jsonObjectArrayList = jsonObjectArrayList;
        }

        @Override
        public FriendsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gathering_friend_list_item, parent, false);
            return new FriendsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final FriendsAdapter.MyViewHolder holder, final int position) {
            try {
                final Map<String, String> invFrn = jsonObjectArrayList.get(position);
                holder.tvName.setText(invFrn.get("name"));
                holder.ivFriend.setImageResource(R.drawable.default_profile_icon);
                if (invFrn.get("picture") != null && invFrn.get("picture") != "null") {
                    Glide.with(getActivity()).load(invFrn.get("picture")).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.ivFriend);
                } else {
                    holder.ivFriend.setImageResource(R.drawable.default_profile_icon);
                }
                holder.container.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (holder.ibDelete.getVisibility() == View.VISIBLE) {
                            holder.ibDelete.setVisibility(View.GONE);
                        } else {
                            holder.ibDelete.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                });
                holder.ibDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inviteFriendsImageList.remove(invFrn);
                        jsonObjectArrayList.remove(position);
                        mFriendsAdapter.notifyItemRemoved(position);
                        mFriendsAdapter.notifyItemRangeChanged(position, jsonObjectArrayList.size());
                        if (jsonObjectArrayList.size() == 0) {
                            llFriendsContainer.setVisibility(View.GONE);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return jsonObjectArrayList.size();
        }
    }

    class AddDiaryTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Creating Diary...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... requests) {
            JSONObject diaryJson = requests[0];
            loggedInUser.setApiUrl(urlManager.getApiUrl("dev"));
            JSONObject response = apiManager.addDiary(loggedInUser, diaryJson, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            ImageUtils.cleanUpImages();
            try {
                if (!response.getBoolean("success")) {
                    Toast.makeText(getContext(), "Error in creating Diary", Toast.LENGTH_SHORT).show();
                } else {
                    diary = response.getJSONObject("data");
                    //TODO Show listings or summary screen
                    DiarySummaryFragment diarySummaryFragment = new DiarySummaryFragment();
                    Bundle diaryBundle = new Bundle();
                    diaryBundle.putString("diaryData", diary.toString());
                    diarySummaryFragment.setArguments(diaryBundle);
                    checkFragmentBeforeOpening(diarySummaryFragment, "DiarySummaryFragment");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void checkFragmentBeforeOpening(Fragment fragment, String tag) {
        Fragment fragmentFromBackStack = getFragmentPresentInBackStack();
        if (fragmentFromBackStack != null) {
            fragmentManager.popBackStack(fragmentFromBackStack.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (isEditMode)
            ((DiaryActivity) getActivity()).replaceFragment(fragment, tag);
        else {
            //((DiaryActivity) getActivity()).replaceFragment(fragment, null);
            getActivity().onBackPressed();
        }
    }

    public Fragment getFragmentPresentInBackStack() {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && (fragment instanceof DiarySummaryFragment))
                    return fragment;
            }
        }
        return null;
    }

    class DeleteDiaryTask extends AsyncTask<Long, JSONObject, JSONObject> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Deleting...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {
            Long diaryId = longs[0];
            loggedInUser.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?diaryId=" + diaryId;
            JSONObject response = apiManager.deleteDiaryById(loggedInUser, queryStr, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            try {
                if (!response.getBoolean("success")) {
                    Toast.makeText(getContext(), "Error in Deleting Diary", Toast.LENGTH_SHORT).show();
                } else {
                    DiarySummaryFragment.isDiaryDeleted = true;
                    getActivity().onBackPressed();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DoFileUpload extends AsyncTask<File, JSONObject, JSONObject> {
        ProgressDialog pDialog;
        int imageToProcess;

        public DoFileUpload(int imageToProcess) {
            this.imageToProcess = imageToProcess;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getCenesActivity());
            pDialog.setMessage("Uploading Photos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(File... files) {
            File image = files[0];
            try {
                User user = userManager.getUser();
                user.setApiUrl(urlManager.getApiUrl("dev"));
                JSONObject response = apiManager.postDiaryMedia(user, image, getCenesActivity());
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
                if (response.getBoolean("success")) {
                    diaryGallery.add(response.getString("data"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (imageToProcess == diaryFilesToUpload.size()) {
                if (isValid()) {
                    new AddDiaryTask().execute(populateDiaryData());
                }
            }
        }
    }

    public JSONObject populateDiaryData() {
        JSONObject diaryData = new JSONObject();
        try {
            diaryData.put("diaryId", diaryId);
            diaryData.put("title", etTitle.getText().toString().trim());
            diaryData.put("location", tvSearchLocationButton.getText().toString().trim());
            if (!CenesUtils.isEmpty(etDesc.getText().toString().trim())) {
                diaryData.put("detail", etDesc.getText().toString().trim());
            }
            diaryData.put("createdById", loggedInUser.getUserId());
            diaryData.put("diaryTime", diaryCalendar.getTimeInMillis());

            if (inviteFriendsImageList != null && inviteFriendsImageList.size() > 0) {
                JSONArray members = new JSONArray();
                for (Map<String, String> friendInfo : inviteFriendsImageList) {
                    JSONObject friendObj = new JSONObject();
                    friendObj.put("name", friendInfo.get("name"));
                    friendObj.put("userId", friendInfo.get("userId"));
                    friendObj.put("picture", friendInfo.get("picture"));
                    friendObj.put("type", "Diary");
                    members.put(friendObj);
                }
                diaryData.put("members", members);
            }
            if (diaryGallery.size() > 0) {
                String diaryGalleryStr = "";
                for (String img : diaryGallery) {
                    diaryGalleryStr += img + ",";
                }
                diaryData.put("pictures", diaryGalleryStr.substring(0, diaryGalleryStr.length() - 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return diaryData;
    }

    public boolean isValid() {
        Boolean isValid = true;
        if (validationManager.isFieldBlank(etTitle.getText().toString().trim())) {
            Toast.makeText(getContext(), "Please Enter Diary Title", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (diaryCalendar == null) {
            Toast.makeText(getContext(), "Please Select Diary Time", Toast.LENGTH_SHORT).show();
            isValid = false;
        } /*else if (diaryGallery == null || diaryGallery.size() == 0) {
            Toast.makeText(getContext(), "Please Select Images For Diary", Toast.LENGTH_SHORT).show();
            isValid = false;
        }*/

        return isValid;
    }
}
