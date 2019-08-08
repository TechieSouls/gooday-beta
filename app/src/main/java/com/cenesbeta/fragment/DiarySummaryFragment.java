package com.cenesbeta.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.activity.DiaryActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.util.CenesUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rohan on 16/11/17.
 */

public class DiarySummaryFragment extends CenesFragment {

    private View fragmentView;
    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;

    private JSONObject diary;
    private List<String> diaryGallery;
    private Long diaryId;
    private int createdById;
    private TextView tvTitle, tvDesc;
    private TextView tvFriends, tvLocation, tvDate;
    private TextView tvBack, tvEdit;
    private RecyclerView photosRecyclerView;
    Bundle bundle;
    private User loggedInUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.diary_summary_fragment, container, false);
        fragmentView = view;
        init();

        bundle = getArguments();
        if (bundle != null && bundle.getString("diaryData") != null) {
            try {
                diary = new JSONObject(bundle.getString("diaryData"));
                createdById = diary.getInt("createdById");
                diaryId = diary.getLong("diaryId");

                if (loggedInUser.getUserId() == createdById) {
                    tvEdit.setVisibility(View.VISIBLE);
                }

                tvTitle.setText(diary.getString("title"));
                if (!CenesUtils.isEmpty(diary.getString("detail"))) {
                    tvDesc.setText(diary.getString("detail"));
                }
                tvDate.setText(CenesUtils.MMMdd.format(diary.getLong("diaryTime")));

                if (diary.has("location") && !diary.isNull("location") && !diary.getString("location").isEmpty()) {
                    tvLocation.setText(diary.getString("location"));
                }

                if (diary.has("members") && !diary.isNull("members") && diary.getJSONArray("members").length() > 0) {
                    String friends = "";
                    for (int i = 0; i < diary.getJSONArray("members").length(); i++) {
                        JSONObject frndObj = diary.getJSONArray("members").getJSONObject(i);
                        friends = friends.concat(frndObj.getString("name") + ", ");
                    }
                    if (friends.contains(", ")) {
                        friends = friends.substring(0, friends.lastIndexOf(", "));
                        tvFriends.setText(friends);
                    }
                }
                if (diary.getString("pictures") != null && diary.getString("pictures") != "null" && !diary.getString("pictures").isEmpty()) {
                    photosRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.photos_recycler_view);
                    photosRecyclerView.setVisibility(View.VISIBLE);

                    String[] imageFileLinks = diary.getString("pictures").split(",");
                    for (String img : imageFileLinks) {
                        diaryGallery.add(img);
                    }
                    photosAdapter = new PhotosAdapter(diaryGallery);

                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
                    photosRecyclerView.setLayoutManager(mLayoutManager);
                    photosRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    //int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
                    //photosRecyclerView.addItemDecoration(new SpacesItemDecoration(30));
                    photosRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 30, true));
                    photosRecyclerView.setAdapter(photosAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    public static boolean isDiaryDeleted;

    @Override
    public void onResume() {
        super.onResume();
        ((DiaryActivity) getActivity()).hideFooter();
        if (isDiaryDeleted) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    isDiaryDeleted = !isDiaryDeleted;
                    getActivity().onBackPressed();
                }
            });
        }
    }

    public void init() {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();

        tvTitle = (TextView) fragmentView.findViewById(R.id.tv_title);
        tvDate = (TextView) fragmentView.findViewById(R.id.tvDate);
        tvLocation = (TextView) fragmentView.findViewById(R.id.tv_location);
        tvDesc = (TextView) fragmentView.findViewById(R.id.tv_desc);
        tvFriends = (TextView) fragmentView.findViewById(R.id.tv_friends);

        tvEdit = (TextView) fragmentView.findViewById(R.id.tvEdit);
        tvBack = (TextView) fragmentView.findViewById(R.id.tvBack);

        tvEdit.setOnClickListener(onClickListener);
        tvBack.setOnClickListener(onClickListener);

        diaryGallery = new ArrayList<>();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvBack:
                    getActivity().onBackPressed();
                    break;
                case R.id.tvEdit:
                    AddDiaryFragment addDiaryFragment = new AddDiaryFragment();
                    addDiaryFragment.setArguments(bundle);
                    ((DiaryActivity) getActivity()).replaceFragment(addDiaryFragment, "AddDiaryFragment");
                    break;
            }
        }
    };

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private PhotosAdapter photosAdapter;

    public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.MyViewHolder> {

        private List<String> imageLinks;
        private List<Map<String, Object>> imagesToShow;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivDiaryGalleryImage;

            public MyViewHolder(View view) {
                super(view);
                ivDiaryGalleryImage = (ImageView) view.findViewById(R.id.iv_diary_gallery_image);
            }
        }

        public PhotosAdapter(List<String> imageFileLinks) {
            this.imagesToShow = new ArrayList<>();

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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_summary_list_item, parent, false);
            return new PhotosAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final PhotosAdapter.MyViewHolder holder, final int position) {
            try {
                Map<String, Object> filesMap = this.imagesToShow.get(position);

                if (filesMap.containsKey("link")) {
                    String imgFileLink = (String) filesMap.get("link");
                    Glide.with(getActivity()).load(imgFileLink).apply(RequestOptions.placeholderOf(R.drawable.party_image)).into(holder.ivDiaryGalleryImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return imagesToShow.size();
        }
    }

}
