package com.cenes.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.UrlManager;
import com.cenes.R;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.util.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mandeep on 2/11/17.
 */

public class FriendsFragment extends CenesFragment {

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.close_search_friends_btn:
                   getActivity().onBackPressed();
            }
        }
    };
    private ImageView closeSearchFriendsBtn;
    private EditText searchFriendEditText;
    private ListView gathSearchFriendListView;
    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UrlManager urlManager;
    private ApiManager apiManager;
    private UserManager userManager;
    private SearchFriendAdapter searchFriendAdapter;
    private JSONArray allfriends;
    private JSONArray searchedFriends;
    private View fragmentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_search_friends, container, false);
        fragmentView = view;
        initializeComponents();
        addClickListeners();
        new SearchLocationTask().execute();

        searchFriendEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //new SearchLocationTask().execute(editable.toString());
                searchedFriends = new JSONArray();
                if (allfriends != null) {
                    for (int i=0; i < allfriends.length(); i++) {
                        try {
                            JSONObject searchFriend = allfriends.getJSONObject(i);
                            if (searchFriend.getString("name").toLowerCase().contains(editable.toString().toLowerCase())) {
                                searchedFriends.put(searchFriend);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    searchFriendAdapter = new SearchFriendAdapter(FriendsFragment.this,searchedFriends);
                    gathSearchFriendListView.setAdapter(searchFriendAdapter);
                }
            }
        });
        return view;
    }

    public void initializeComponents() {
        closeSearchFriendsBtn = (ImageView) fragmentView.findViewById(R.id.close_search_friends_btn);
        searchFriendEditText = (EditText) fragmentView.findViewById(R.id.invite_friend_edit_text);
        gathSearchFriendListView = (ListView) fragmentView.findViewById(R.id.gath_search_friend_list_view);

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        urlManager = coreManager.getUrlManager();
        apiManager = coreManager.getApiManager();
        userManager = coreManager.getUserManager();
    }

    public void addClickListeners() {
        closeSearchFriendsBtn.setOnClickListener(onClickListener);
        searchFriendEditText.setOnClickListener(onClickListener);
    }

    class SearchLocationTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            //String keyword = strings[0];
            try {
                User user = userManager.getUser();
                user.setApiUrl(urlManager.getApiUrl("dev"));
                String queryStr = "?user_id="+ user.getUserId();
                allfriends = apiManager.searchFriends(user,queryStr,getCenesActivity());
                searchFriendAdapter = new SearchFriendAdapter(FriendsFragment.this,allfriends);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            gathSearchFriendListView.setAdapter(searchFriendAdapter);
        }
    }


    public class SearchFriendAdapter extends BaseAdapter {

        LayoutInflater inflter;
        FriendViewHolder holder;
        private FriendsFragment myActivity;
        private JSONArray friends;

        public SearchFriendAdapter(FriendsFragment context, JSONArray friends) {
            this.myActivity = context;
            this.friends = friends;
            this.inflter = (LayoutInflater.from(getContext()));
        }

        @Override
        public int getCount() {
            return friends.length();
        }

        @Override
        public Object getItem(int i) {
            try {
                return this.friends.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {

            if (view == null) {
                holder = new FriendViewHolder();
                view = this.inflter.inflate(R.layout.adapter_search_friends, null);
                holder.inviteFriendItem = (LinearLayout) view.findViewById(R.id.invite_friend_item);
                holder.inviteFriendName = (TextView) view.findViewById(R.id.invite_friend_name);
                holder.inviteFriendPicture = (RoundedImageView) view.findViewById(R.id.invite_friend_picture);
                holder.cenesMemberIcon = (TextView) view.findViewById(R.id.iv_cenes_member_icon);
                holder.inviteFriendNameCenesUserText = (TextView) view.findViewById(R.id.invite_friend_name_cenes_user_text);
                view.setTag(holder);
            } else {
                holder = (FriendViewHolder) view.getTag();
            }

            try {
                holder.inviteFriendName.setText(friends.getJSONObject(position).getString("name"));
                String photo = null;
                if (friends.getJSONObject(position).getJSONObject("user") != null) {
                    photo = friends.getJSONObject(position).getJSONObject("user").getString("photo");
                }

                //if (friends.getJSONObject(position).getString("photo") != null && friends.getJSONObject(position).getString("photo") != "null") {
                if (photo != null) {
                    Glide.with(myActivity).load(photo).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.inviteFriendPicture);
                } else {
                    holder.inviteFriendPicture.setImageResource(R.drawable.default_profile_icon);
                }
                System.out.println("cenesMember : "+ friends.getJSONObject(position).getInt("cenesMember"));
                if (friends.getJSONObject(position).getInt("cenesMember") == 1) {
                    holder.inviteFriendNameCenesUserText.setVisibility(View.GONE);
                    holder.cenesMemberIcon.setVisibility(View.VISIBLE);
                } else {
                    holder.inviteFriendNameCenesUserText.setVisibility(View.VISIBLE);
                    holder.cenesMemberIcon.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.inviteFriendItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyBoard(view);
                    Intent intent = new Intent();
                    try {
                        JSONObject friendObj = (JSONObject) friends.get(position);
                        String photo = friendObj.getJSONObject("user").getString("photo");
                        intent.putExtra("userId", friendObj.get("userId").toString());
                        //intent.putExtra("photo", friendObj.getString("photo"));
                        intent.putExtra("photo", photo);
                        intent.putExtra("name", friendObj.getString("name"));
                        getTargetFragment().onActivityResult(
                                getTargetRequestCode(),
                                Activity.RESULT_OK,
                                intent
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return view;
        }

        public void hideKeyBoard(View view) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        class FriendViewHolder {
            private LinearLayout inviteFriendItem;
            private TextView inviteFriendName,inviteFriendNameCenesUserText,cenesMemberIcon;
            private RoundedImageView inviteFriendPicture;
        }
    }
}
