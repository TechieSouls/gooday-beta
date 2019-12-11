package com.cenesbeta.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.DiaryActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Section;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 6/11/17.
 */

public class DiariesFragment extends CenesFragment {

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    private InternetManager internetManager;

    private User user;
    private JSONArray diaries;

    DiariesAdapter diariesAdapter;
    ExpandableListView evListView;

    private TextView tvNoDiaries, tvNotificationCount;

    private ImageView ivAddButton;
    private RoundedImageView userProfilePic;
    RecyclerView mRecyclerView;
    SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_diaries, container, false);
        init(view);
        user = userManager.getUser();
        if (user != null && user.getPicture() != null && user.getPicture() != "null") {
            Glide.with(this).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(userProfilePic);
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(getActivity().getApplicationContext(), mRecyclerView, 2);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                new DiariesTask().execute();
            }
        });
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                new NotificationCountTask().execute();
            }
        });

        return view;
    }

    public void init(View view) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        internetManager = coreManager.getInternetManager();

        evListView = (ExpandableListView) view.findViewById(R.id.ev_list_view);

        tvNoDiaries = (TextView) view.findViewById(R.id.tv_no_diaries);
        userProfilePic = (RoundedImageView) view.findViewById(R.id.user_profile_pic);

        ivAddButton = (ImageView) view.findViewById(R.id.iv_add_btn);
        tvNotificationCount = (TextView) view.findViewById(R.id.tv_notification_count_pic);

        //Listenerss
        ivAddButton.setOnClickListener(onClickListener);
        userProfilePic.setOnClickListener(onClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DiaryActivity) getActivity()).showFooter();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.user_profile_pic:
                    DiaryActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                    break;
                case R.id.iv_add_btn:
                    ((DiaryActivity) getActivity()).replaceFragment(new AddDiaryFragment(), "AddDiaryFragment");
                    break;
            }
        }
    };

    class DiariesAdapter extends BaseExpandableListAdapter {
        DiariesFragment context;
        List<String> headers;
        Map<String, List<JSONObject>> eventsMap;
        LayoutInflater inflter;
        boolean isInvitation;

        public DiariesAdapter(DiariesFragment applicationContext, List<String> headers, Map<String, List<JSONObject>> eventsMap) {
            this.context = applicationContext;
            this.headers = headers;
            this.eventsMap = eventsMap;
            this.inflter = (LayoutInflater.from(applicationContext.getCenesActivity()));
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this.eventsMap.get(this.headers.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            //if (!isInvitation) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflter.inflate(R.layout.adapter_diary_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_item_title);
                viewHolder.description = (TextView) convertView.findViewById(R.id.tv_item_detail);
                viewHolder.diaryRowItem = (LinearLayout) convertView.findViewById(R.id.ll_dairy_list_item);
                viewHolder.diaryId = null;
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final JSONObject child = (JSONObject) getChild(groupPosition, childPosition);

            try {
                viewHolder.diaryId = child.getLong("diaryId");
                viewHolder.title.setText(child.getString("title"));
                if (!CenesUtils.isEmpty(child.getString("detail"))) {
                    viewHolder.description.setText(child.getString("detail"));
                } else {
                    viewHolder.description.setText("");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            /*viewHolder.diaryRowItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddDiaryFragment addDiaryFragment = new AddDiaryFragment();
                    Bundle diaryBundle = new Bundle();
                    diaryBundle.putString("diaryData", child.toString());
                    addDiaryFragment.setArguments(diaryBundle);
                    fragmentManager = getActivity().getSupportFragmentManager();
                    replaceFragment(addDiaryFragment, "AddDiaryFragment");
                }
            });*/

            viewHolder.diaryRowItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DiarySummaryFragment diarySummaryFragment = new DiarySummaryFragment();
                    Bundle diaryBundle = new Bundle();
                    diaryBundle.putString("diaryData", child.toString());
                    diarySummaryFragment.setArguments(diaryBundle);
                    ((DiaryActivity) getActivity()).replaceFragment(diarySummaryFragment, "DiarySummaryFragment");
                }
            });

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.eventsMap.get(this.headers.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.headers.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.headers.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            ExpandableListView mExpandableListView = (ExpandableListView) parent;
            mExpandableListView.expandGroup(groupPosition);
            if (convertView == null) {
                convertView = inflter.inflate(R.layout.adapter_home_data_headers, null);
            }
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            if (isInvitation) {
                lblListHeader.setVisibility(View.GONE);
            }
            String headerTitle = (String) getGroup(groupPosition);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public int convertPpToDp(int pp) {
            DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
            return (int) (pp / displayMetrics.density);
        }

        public int convertDpToPp(int dp) {
            DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
            return (int) (dp * displayMetrics.density);
        }

        class ViewHolder {
            private Long diaryId;
            private TextView title;
            private TextView location;
            private TextView description;
            private LinearLayout diaryRowItem;
        }

        class HeaderViewHolder {
            private ExpandableListView expandableListView;
            private TextView lblListHeader;
        }
    }

    public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder> {

        private static final int VIEW_TYPE_SECTION = R.layout.adapter_home_data_headers;
        private static final int VIEW_TYPE_ITEM = R.layout.adapter_diary_list_item;

        private final Context mContext;
        private ArrayList<Object> mDataArrayList;

        public SectionedExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList, final GridLayoutManager gridLayoutManager) {
            mContext = context;
            mDataArrayList = dataArrayList;

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return isSection(position) ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }

        private boolean isSection(int position) {
            return mDataArrayList.get(position) instanceof Section;
        }

        @Override
        public SectionedExpandableGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SectionedExpandableGridAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false), viewType);
        }

        @Override
        public void onBindViewHolder(SectionedExpandableGridAdapter.ViewHolder viewHolder, int position) {
            switch (viewHolder.viewType) {
                case VIEW_TYPE_ITEM:

                    final JSONObject child = (JSONObject) mDataArrayList.get(position);

                    try {
                        viewHolder.diaryId = child.getLong("diaryId");
                        viewHolder.title.setText(child.getString("title"));
                        if (child.getString("pictures") != null && !child.getString("pictures").isEmpty() && child.getString("pictures") != "null") {
                            String[] pics = child.getString("pictures").split(",");
                            Glide.with(getActivity()).load(pics[0]).apply(RequestOptions.placeholderOf(R.drawable.diary_default)).into(viewHolder.ivDiaryImage);
                        } else {
                            viewHolder.ivDiaryImage.setImageResource(R.drawable.diary_default);
                        }
                        if (!CenesUtils.isEmpty(child.getString("detail"))) {
                            viewHolder.description.setText(child.getString("detail"));
                        } else {
                            viewHolder.description.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                   /* viewHolder.diaryRowItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AddDiaryFragment addDiaryFragment = new AddDiaryFragment();
                            Bundle diaryBundle = new Bundle();
                            diaryBundle.putString("diaryData", child.toString());
                            addDiaryFragment.setArguments(diaryBundle);
                            fragmentManager = getActivity().getSupportFragmentManager();
                            replaceFragment(addDiaryFragment, "addDiaryFragment");
                        }
                    });*/

                    viewHolder.diaryRowItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DiarySummaryFragment diarySummaryFragment = new DiarySummaryFragment();
                            Bundle diaryBundle = new Bundle();
                            diaryBundle.putString("diaryData", child.toString());
                            diarySummaryFragment.setArguments(diaryBundle);
                            ((DiaryActivity) getActivity()).replaceFragment(diarySummaryFragment, "DiarySummaryFragment");
                        }
                    });
                    break;
                case VIEW_TYPE_SECTION:
                    final Section section = (Section) mDataArrayList.get(position);
                    viewHolder.sectionTextView.setText(section.getName());
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mDataArrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (isSection(position))
                return VIEW_TYPE_SECTION;
            else return VIEW_TYPE_ITEM;
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {

            View view;
            int viewType;

            TextView sectionTextView;

            Long diaryId;
            TextView title;
            TextView description;
            ImageView ivDiaryImage;
            LinearLayout diaryRowItem;

            public ViewHolder(View view, int viewType) {
                super(view);
                this.viewType = viewType;
                this.view = view;
                if (viewType == VIEW_TYPE_ITEM) {
                    title = (TextView) view.findViewById(R.id.tv_item_title);
                    description = (TextView) view.findViewById(R.id.tv_item_detail);
                    diaryRowItem = (LinearLayout) view.findViewById(R.id.ll_dairy_list_item);
                    ivDiaryImage = (ImageView) view.findViewById(R.id.ivDiaryImage);
                    diaryId = null;
                } else {
                    sectionTextView = (TextView) view.findViewById(R.id.lblListHeader);
                }
            }
        }
    }

    public class SectionedExpandableLayoutHelper {

        private LinkedHashMap<Section, List<JSONObject>> mSectionDataMap = new LinkedHashMap<Section, List<JSONObject>>();
        private ArrayList<Object> mDataArrayList = new ArrayList<Object>();
        private HashMap<String, Section> mSectionMap = new HashMap<String, Section>();
        private SectionedExpandableGridAdapter mSectionedExpandableGridAdapter;
        RecyclerView mRecyclerView;

        public SectionedExpandableLayoutHelper(Context context, RecyclerView recyclerView, int gridSpanCount) {

            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
            recyclerView.setLayoutManager(gridLayoutManager);
            mSectionedExpandableGridAdapter = new SectionedExpandableGridAdapter(context, mDataArrayList, gridLayoutManager);
            recyclerView.setAdapter(mSectionedExpandableGridAdapter);

            mRecyclerView = recyclerView;
        }

        public void notifyDataSetChanged() {
            //TODO : handle this condition such that these functions won't be called if the recycler view is on scroll
            generateDataList();
            mSectionedExpandableGridAdapter.notifyDataSetChanged();
        }

        public void addSection(String section, List<JSONObject> items) {
            Section newSection;
            mSectionMap.put(section, (newSection = new Section(section)));
            mSectionDataMap.put(newSection, items);
        }

        public void addSections(List<String> headers, Map<String, List<JSONObject>> diariesMap) {

            for (int i = 0; i < headers.size(); i++) {
                addSection(headers.get(i), diariesMap.get(headers.get(i)));
            }
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }

        public void addItem(String section, JSONObject item) {
            mSectionDataMap.get(mSectionMap.get(section)).add(item);
        }

        public void removeItem(String section, JSONObject item) {
            mSectionDataMap.get(mSectionMap.get(section)).remove(item);
        }

        public void removeSection(String section) {
            mSectionDataMap.remove(mSectionMap.get(section));
            mSectionMap.remove(section);
        }

        private void generateDataList() {
            mDataArrayList.clear();
            for (Map.Entry<Section, List<JSONObject>> entry : mSectionDataMap.entrySet()) {
                Section key;
                mDataArrayList.add((key = entry.getKey()));
                if (key.isExpanded)
                    mDataArrayList.addAll(entry.getValue());
            }
        }
    }

    class DiariesTask extends AsyncTask<String, JSONObject, JSONObject> {

//        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mProgressDialog = new ProgressDialog(getContext());
//            mProgressDialog.setMessage("Loading...");
//            mProgressDialog.setIndeterminate(false);
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?userId=" + user.getUserId();
            JSONObject response = apiManager.getAllUserDiaries(user, queryStr, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
//            if (mProgressDialog != null) {
//                mProgressDialog.dismiss();
//                mProgressDialog = null;
//            }
            try {
                if (!response.getBoolean("success")) {
                    Toast.makeText(getContext(), "Error in Getting Diary list", Toast.LENGTH_SHORT).show();
                } else {
                    diaries = response.getJSONArray("data");
                    if (diaries.length() == 0) {
                        tvNoDiaries.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        tvNoDiaries.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);

                        List<String> headers = new ArrayList<>();
                        Map<String, List<JSONObject>> diariesMap = new HashMap<>();
                        for (int i = 0; i < diaries.length(); i++) {
                            JSONObject diaryObj = diaries.getJSONObject(i);
                            if (diaryObj.has("diaryTime")) {
                                Date createdAt = new Date(diaryObj.getLong("diaryTime"));
                                String dateKey = CenesUtils.EEEEMMMdd.format(createdAt) + CenesUtils.getDateSuffix(createdAt.getDate());

                                if (!headers.contains(dateKey)) {
                                    headers.add(dateKey);
                                }
                                if (diariesMap.containsKey(dateKey)) {
                                    List<JSONObject> diariesList = diariesMap.get(dateKey);
                                    diariesList.add(diaryObj);
                                    diariesMap.put(dateKey, diariesList);
                                } else {
                                    List<JSONObject> diariesList = new ArrayList<>();
                                    diariesList.add(diaryObj);
                                    diariesMap.put(dateKey, diariesList);
                                }
                            }
                        }

                        //diariesAdapter = new DiariesAdapter(DiariesFragment.this, headers, diariesMap);
                        //evListView.setAdapter(diariesAdapter);
                        sectionedExpandableLayoutHelper.addSections(headers, diariesMap);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class NotificationCountTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(getActivity());
            //           progressDialog.setCancelable(false);
            //          progressDialog.setMessage("Loading...");
            //progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... strings) {
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?userId=" + user.getUserId();
            return apiManager.getNotificationCounts(user, queryStr, getCenesActivity());
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);

            try {
                tvNotificationCount.setText(String.valueOf(s.getInt("data")));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //progressDialog.hide();
            //progressDialog.dismiss();
            //progressDialog = null;
        }
    }
}
