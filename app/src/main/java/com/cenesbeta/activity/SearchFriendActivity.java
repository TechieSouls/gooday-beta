package com.cenesbeta.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.adapter.SearchFriendAdapter;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mandeep on 6/9/17.
 */

public class SearchFriendActivity extends CenesActivity {

    private ImageView closeSearchFriendsBtn;
    public EditText searchFriendEditText;
    private ListView gathSearchFriendListView;
    private Button btnDoneInviteFriend;
    private RelativeLayout cenesNoncenesSelectBar;
    private TextView tvSelectBarTitle;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UrlManager urlManager;
    private ApiManager apiManager;
    private UserManager userManager;

    private SearchFriendAdapter searchFriendAdapter;
    private JSONArray allfriends;
    private JSONArray cenesFriends;
    private JSONArray searchedFriends;
    public Boolean cenesFriendsSelected = false;

    public static Map<Integer, CheckBox> checkboxButtonHolder;
    public static Map<Integer, Boolean> checkboxStateHolder;
    public static Map<Integer, JSONObject> checkboxObjectHolder;
    public static RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        initializeComponents();
        addClickListeners();

        new SearchLocationTask().execute();
        checkboxStateHolder = new HashMap<>();
        checkboxObjectHolder = new HashMap<>();
        checkboxButtonHolder = new HashMap<>();
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
                JSONArray friendListToSearch = null;
                if (cenesFriendsSelected == false) {

                    friendListToSearch = allfriends;

                } else {

                    friendListToSearch = cenesFriends;
                }
                gathSearchFriendListView.setAdapter(searchFriendAdapter);


                searchedFriends = new JSONArray();
                if (friendListToSearch != null) {
                    for (int i=0; i < friendListToSearch.length(); i++) {
                        try {
                            JSONObject searchFriend = friendListToSearch.getJSONObject(i);
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
                case R.id.btn_done_invite_friend:
                    Intent intent = new Intent();
                    try {
                        JSONArray selectedFriends = new JSONArray();
                        if (checkboxObjectHolder.size() > 0) {
                            for (Map.Entry selectedFriendsEntryMap: checkboxObjectHolder.entrySet()) {
                                selectedFriends.put(selectedFriendsEntryMap.getValue());
                            }
                        }

                        String friendsArrayStr = selectedFriends.toString();
                        intent.putExtra("selectedFriendJsonArray", friendsArrayStr);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.cenes_noncenes_select_bar:

                    if (cenesFriendsSelected == false) {
                        cenesFriendsSelected = true;
                        tvSelectBarTitle.setText("Cenes Contacts ("+cenesFriends.length()+")");
                        searchFriendAdapter = new SearchFriendAdapter(SearchFriendActivity.this,cenesFriends);
                    } else {
                        cenesFriendsSelected = false;
                        tvSelectBarTitle.setText("All Contacts ("+allfriends.length()+")");
                        searchFriendAdapter = new SearchFriendAdapter(SearchFriendActivity.this,allfriends);
                    }
                    gathSearchFriendListView.setAdapter(searchFriendAdapter);
                    break;
            }
        }
    };

    public void initializeComponents() {
        closeSearchFriendsBtn = (ImageView) findViewById(R.id.close_search_friends_btn);
        searchFriendEditText = (EditText) findViewById(R.id.invite_friend_edit_text);
        gathSearchFriendListView = (ListView) findViewById(R.id.gath_search_friend_list_view);
        btnDoneInviteFriend = (Button) findViewById(R.id.btn_done_invite_friend);
        cenesNoncenesSelectBar = (RelativeLayout) findViewById(R.id.cenes_noncenes_select_bar);
        tvSelectBarTitle = (TextView) findViewById(R.id.tv_select_bar_title);

        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        urlManager = coreManager.getUrlManager();
        apiManager = coreManager.getApiManager();
        userManager = coreManager.getUserManager();
    }

    public void addClickListeners() {
        closeSearchFriendsBtn.setOnClickListener(onClickListener);
        searchFriendEditText.setOnClickListener(onClickListener);
        btnDoneInviteFriend.setOnClickListener(onClickListener);
        cenesNoncenesSelectBar.setOnClickListener(onClickListener);

        searchFriendEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                } catch (Exception ce) {
                    ce.printStackTrace();
                }

                return false;
            }
        });
    }

    public SearchFriendAdapter getSearchFriendAdapter() {
        return searchFriendAdapter;
    }

    public JSONArray getCenesContacts(JSONArray allContacts) {
        JSONArray cenesContacts = new JSONArray();
        for (int i=0; i < allContacts.length(); i++) {
            try {

                JSONObject contactObj = allContacts.getJSONObject(i);
                if ("yes".equals(contactObj.getString("cenesMember"))) {
                    cenesContacts.put(contactObj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cenesContacts;
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

            cenesFriends = getCenesContacts(allfriends);
            if (cenesFriends.length() > 0) {
                cenesFriendsSelected = true;
                tvSelectBarTitle.setText("Cenes Contacts ("+cenesFriends.length()+")");
                searchFriendAdapter = new SearchFriendAdapter(SearchFriendActivity.this,cenesFriends);
            } else {
                cenesFriendsSelected = false;
                tvSelectBarTitle.setText("All Contacts ("+allfriends.length()+")");
                searchFriendAdapter = new SearchFriendAdapter(SearchFriendActivity.this,allfriends);
            }
            gathSearchFriendListView.setAdapter(searchFriendAdapter);
        }
    }
}
