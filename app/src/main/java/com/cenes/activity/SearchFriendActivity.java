package com.cenes.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.cenes.Manager.ApiManager;
import com.cenes.Manager.UrlManager;
import com.cenes.R;
import com.cenes.adapter.SearchFriendAdapter;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mandeep on 6/9/17.
 */

public class SearchFriendActivity extends CenesActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
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
                    searchFriendAdapter = new SearchFriendAdapter(SearchFriendActivity.this,searchedFriends);
                    gathSearchFriendListView.setAdapter(searchFriendAdapter);
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.close_search_friends_btn:
                   onBackPressed();
                    break;
            }
        }
    };

    public void initializeComponents() {
        closeSearchFriendsBtn = (ImageView) findViewById(R.id.close_search_friends_btn);
        searchFriendEditText = (EditText) findViewById(R.id.invite_friend_edit_text);
        gathSearchFriendListView = (ListView) findViewById(R.id.gath_search_friend_list_view);

        cenesApplication = getCenesApplication();
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
        ProgressDialog mProgressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(SearchFriendActivity.this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //String keyword = strings[0];
            try {
                User user = userManager.getUser();
                user.setApiUrl(urlManager.getApiUrl("dev"));
                String queryStr = "?user_id="+ user.getUserId();
                allfriends = apiManager.searchFriends(user,queryStr,SearchFriendActivity.this);
                searchFriendAdapter = new SearchFriendAdapter(SearchFriendActivity.this,allfriends);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            gathSearchFriendListView.setAdapter(searchFriendAdapter);
        }
    }
}
