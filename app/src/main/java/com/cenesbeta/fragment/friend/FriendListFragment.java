package com.cenesbeta.fragment.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.AsyncTasks.FriendAsyncTask;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.AllContactsExpandableAdapter;
import com.cenesbeta.adapter.FriendListAdapter;
import com.cenesbeta.adapter.FriendsCollectionViewAdapter;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.EventManagerImpl;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.gathering.CreateGatheringFragment;
import com.cenesbeta.service.SearchFriendService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FriendListFragment  extends CenesFragment {


    public static String TAG = "FriendListFragment";

    private Button closeSearchFriendsBtn;
    public EditText searchFriendEditText;
    private ListView gathSearchFriendListView;
    private ExpandableListView expandableFriendListView;
    private Button btnDoneInviteFriend;
    private RelativeLayout cenesNoncenesSelectBar;
    private TextView tvSelectBarTitle;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private InternetManager internetManager;

    private AllContactsExpandableAdapter allContactsExpandableAdapter;
    private List<EventMember> allFriends;
    private List<EventMember> cenesFriends;
    private List<String> headers;
    private Map<String, List<EventMember>> headerFriendsMap;

    private List<EventMember> searchedFriends;
    public User loggedInUser;
    public Boolean cenesFriendsVisible = false;

    public List<EventMember> selectedEventMembers;
    public static Map<Integer, CheckBox> checkboxButtonHolder;
    public static Map<Integer, Boolean> checkboxStateHolder;
    public static Map<Integer, EventMember> checkboxObjectHolder;
    public RecyclerView recyclerView;
    public boolean isEditMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_search_friends, container, false);

        closeSearchFriendsBtn = (Button) view.findViewById(R.id.close_search_friends_btn);
        searchFriendEditText = (EditText) view.findViewById(R.id.invite_friend_edit_text);
        gathSearchFriendListView = (ListView) view.findViewById(R.id.gath_search_friend_list_view);
        btnDoneInviteFriend = (Button) view.findViewById(R.id.btn_done_invite_friend);
        cenesNoncenesSelectBar = (RelativeLayout) view.findViewById(R.id.cenes_noncenes_select_bar);
        tvSelectBarTitle = (TextView) view.findViewById(R.id.tv_select_bar_title);
        expandableFriendListView = (ExpandableListView) view.findViewById(R.id.elv_friend_list);

        cenesApplication = ((CenesBaseActivity) getActivity()).getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        internetManager = coreManager.getInternetManager();

        loggedInUser = userManager.getUser();

        closeSearchFriendsBtn.setOnClickListener(onClickListener);
        searchFriendEditText.setOnClickListener(onClickListener);
        btnDoneInviteFriend.setOnClickListener(onClickListener);
        cenesNoncenesSelectBar.setOnClickListener(onClickListener);

        ((CenesBaseActivity)getActivity()).hideFooter();

        searchFriendEditText.setOnEditorActionListener(onEditorActionListener);
        searchFriendEditText.addTextChangedListener(onTextChangeListener);

        checkboxStateHolder = new LinkedHashMap<>();
        checkboxObjectHolder = new LinkedHashMap<>();
        checkboxButtonHolder = new LinkedHashMap<>();
        allFriends = new ArrayList<>();
        cenesFriends = new ArrayList<>();

        expandableFriendListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        addLoggedInUserAsMember(view);
        loadFriends();
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.close_search_friends_btn:
                    ((CenesBaseActivity)getActivity()).onBackPressed();
                    break;
                case R.id.btn_done_invite_friend:
                    Intent intent = new Intent();
                    try {
                        List<EventMember> selectedFriends = new ArrayList<>();
                        if (checkboxObjectHolder.size() > 0) {
                            for (Map.Entry<Integer, EventMember> selectedFriendsEntryMap: checkboxObjectHolder.entrySet()) {
                                selectedFriends.add(selectedFriendsEntryMap.getValue());
                            }
                        }

                        Gson gson = new Gson();
                        String friendsArrayStr = gson.toJson(selectedFriends);
                        intent.putExtra("selectedFriendJsonArray", friendsArrayStr);

                        if (isEditMode == true) {

                            getTargetFragment().onActivityResult(getTargetRequestCode(), getActivity().RESULT_OK, intent);
                            getFragmentManager().popBackStack();

                        } else {

                            CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
                            createGatheringFragment.membersSelected = selectedFriends;
                            ((CenesBaseActivity) getActivity()).replaceFragment(createGatheringFragment, FriendListFragment.TAG);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.cenes_noncenes_select_bar:

                    if (cenesFriendsVisible == false) {
                        if (allFriends.size() > 0) {

                            cenesFriendsVisible = true;
                            //gathSearchFriendListView.setVisibility(View.VISIBLE);
                            //expandableFriendListView.setVisibility(View.GONE);
                            tvSelectBarTitle.setText("All Contacts (" + allFriends.size() + ")");
                            //searchFriendAdapter = new FriendListAdapter(FriendListFragment.this, searchedFriends);

                            List<String> cenesContactHeaders = prepareListHeadersForCenesContacts();
                            allContactsExpandableAdapter = new AllContactsExpandableAdapter(FriendListFragment.this, cenesContactHeaders, headerFriendsMap, true);
                            expandableFriendListView.setAdapter(allContactsExpandableAdapter);
                        }
                    } else {
                        if (cenesFriends.size() > 0) {

                            cenesFriendsVisible = false;
                            //gathSearchFriendListView.setVisibility(View.GONE);
                            //expandableFriendListView.setVisibility(View.VISIBLE);
                            tvSelectBarTitle.setText("Cenes Contacts ("+cenesFriends.size()+")");

                            List<String> allContactHeaders = prepareListHeadersForAllContacts();
                            allContactsExpandableAdapter = new AllContactsExpandableAdapter(FriendListFragment.this, allContactHeaders, headerFriendsMap, false);
                            expandableFriendListView.setAdapter(allContactsExpandableAdapter);
                        }
                    }
                    break;
            }
        }
    };

    TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
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
    };

    TextWatcher onTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            //new SearchLocationTask().execute(editable.toString());
            searchedFriends = new ArrayList<>();
            if (cenesFriends != null && cenesFriends.size() > 0 && cenesFriendsVisible) {
                headers = SearchFriendService.allAphabets();
                List<EventMember> searchedAllFriends = new ArrayList<>();
                headerFriendsMap = new HashMap<>();
                for (EventMember eventMember : cenesFriends) {
                    try {
                        if (eventMember.getName().toLowerCase().contains(editable.toString().toLowerCase())) {
                            searchedAllFriends.add(eventMember);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (EventMember friend: searchedAllFriends) {
                    String initialAlphabet = friend.getName().substring(0, 1).toUpperCase();

                    System.out.println("Inital ALPHABET "+initialAlphabet);
                    if (!headers.contains(initialAlphabet)) {
                        initialAlphabet = "#";
                    }

                    List<EventMember> members = null;
                    if (headerFriendsMap.containsKey(initialAlphabet)) {
                        members = headerFriendsMap.get(initialAlphabet);
                    } else {
                        members = new ArrayList<>();
                    }
                    members.add(friend);
                    headerFriendsMap.put(initialAlphabet, members);
                }
                List<String> filteredHeader = new ArrayList<>();
                for (String header: headerFriendsMap.keySet()){
                    filteredHeader.add(header);
                }
                headers = filteredHeader;
                //searchFriendAdapter = new FriendListAdapter(FriendListFragment.this, searchedFriends);
                //gathSearchFriendListView.setAdapter(searchFriendAdapter);

                allContactsExpandableAdapter = new AllContactsExpandableAdapter(FriendListFragment.this, headers, headerFriendsMap, true);
                expandableFriendListView.setAdapter(allContactsExpandableAdapter);

            } else {
                headers = SearchFriendService.allAphabets();
                List<EventMember> searchedAllFriends = new ArrayList<>();
                if (allFriends != null && allFriends.size() > 0) {
                    headerFriendsMap = new HashMap<>();
                    for (EventMember eventMember : allFriends) {
                        try {
                            if (eventMember.getName().toLowerCase().contains(editable.toString().toLowerCase())) {
                                searchedAllFriends.add(eventMember);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    for (EventMember friend: searchedAllFriends) {
                        String initialAlphabet = friend.getName().substring(0, 1).toUpperCase();

                        System.out.println("Inital ALPHABET "+initialAlphabet);
                        if (!headers.contains(initialAlphabet)) {
                            initialAlphabet = "#";
                        }

                        List<EventMember> members = null;
                        if (headerFriendsMap.containsKey(initialAlphabet)) {
                            members = headerFriendsMap.get(initialAlphabet);
                        } else {
                            members = new ArrayList<>();
                        }
                        members.add(friend);
                        headerFriendsMap.put(initialAlphabet, members);
                    }
                    List<String> filteredHeader = new ArrayList<>();
                    for (String header: headerFriendsMap.keySet()){
                        filteredHeader.add(header);
                    }
                    headers = filteredHeader;
                    allContactsExpandableAdapter = new AllContactsExpandableAdapter(FriendListFragment.this, headers, headerFriendsMap, false);
                    expandableFriendListView.setAdapter(allContactsExpandableAdapter);
                }
            }
        }
    };

    public void loadFriends() {
        new FriendAsyncTask(cenesApplication);
        new FriendAsyncTask.FriendListTask(new FriendAsyncTask.FriendListTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                try {
                    boolean success = response.getBoolean("success");
                    if (success == true) {

                        Gson gson = new GsonBuilder().create();
                        Type listType = new TypeToken<List<EventMember>>(){}.getType();
                        allFriends = gson.fromJson( response.getJSONArray("data").toString(), listType);

                        List<EventMember> allFriendsTemp = new ArrayList<>();
                        for (EventMember eventMember: allFriends) {
                            if (eventMember.getFriendId() != null && !eventMember.getFriendId().equals(0) && eventMember.getFriendId().equals(loggedInUser.getUserId())) {
                                continue;
                            }
                            allFriendsTemp.add(eventMember);
                        }
                        allFriends = allFriendsTemp;
                        cenesFriends = SearchFriendService.getCenesContacts(allFriends);

                        searchedFriends = cenesFriends;

                        List<String> filteredErrors = prepareListHeadersOnScreenLoad();
                        if (cenesFriends.size() > 0) {
                            cenesFriendsVisible = true;
                            //gathSearchFriendListView.setVisibility(View.VISIBLE);
                            //expandableFriendListView.setVisibility(View.VISIBLE);

                            tvSelectBarTitle.setText("All Contacts (" + allFriends.size() + ")");
                            //searchFriendAdapter = new FriendListAdapter(FriendListFragment.this, searchedFriends);
                            //gathSearchFriendListView.setAdapter(searchFriendAdapter);
                            allContactsExpandableAdapter = new AllContactsExpandableAdapter(FriendListFragment.this, filteredErrors, headerFriendsMap, true);
                            expandableFriendListView.setAdapter(allContactsExpandableAdapter);
                        } else {
                            cenesNoncenesSelectBar.setVisibility(View.GONE);
                            cenesFriendsVisible = false;

                            //gathSearchFriendListView.setVisibility(View.GONE);
                            //expandableFriendListView.setVisibility(View.VISIBLE);

                            tvSelectBarTitle.setText("Cenes Contacts (" + cenesFriends.size() + ")");
                            allContactsExpandableAdapter = new AllContactsExpandableAdapter(FriendListFragment.this, filteredErrors, headerFriendsMap, false);
                            expandableFriendListView.setAdapter(allContactsExpandableAdapter);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public void addLoggedInUserAsMember(View view) {
        try {


            if (selectedEventMembers != null && selectedEventMembers.size() > 0) {

                for (EventMember eventMember: selectedEventMembers) {
                    if (eventMember.getUserContactId() != null && eventMember.getUserContactId() != 0) {
                        checkboxStateHolder.put(eventMember.getUserContactId(), true);
                        checkboxObjectHolder.put(eventMember.getUserContactId(), eventMember);

                    } else {

                        if (eventMember.getUserId() != null) {
                            checkboxStateHolder.put(eventMember.getUserId(), true);
                            checkboxObjectHolder.put(eventMember.getUserId(), eventMember);

                        }
                    }
                }
            } else {

                EventMember friendObj = new EventMember();
                friendObj.setUser(loggedInUser);
                friendObj.setFriendId(loggedInUser.getUserId());
                friendObj.setUserId(loggedInUser.getUserId());
                friendObj.setUserContactId(loggedInUser.getUserId());
                friendObj.setName(loggedInUser.getName());
                friendObj.setCenesMember("yes");
                checkboxStateHolder.put(loggedInUser.getUserId(), true);
                checkboxObjectHolder.put(loggedInUser.getUserId(), friendObj);

            }

            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setVisibility(View.VISIBLE);

            view.findViewById(R.id.rl_selected_friends_recycler_view).setVisibility(View.VISIBLE);
            getActivity().runOnUiThread(new Thread(new Runnable() {
                @Override
                public void run() {

                    List<EventMember> members = new ArrayList<>(checkboxObjectHolder.values());
                    FriendsCollectionViewAdapter mFriendsCollectionViewAdapter = new FriendsCollectionViewAdapter(FriendListFragment.this, members, recyclerView);
                    mFriendsCollectionViewAdapter.notifyDataSetChanged();
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);
                    //recyclerView.setHasFixedSize(false);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mFriendsCollectionViewAdapter);
                    recyclerView.invalidate();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> prepareListHeadersOnScreenLoad() {

        headerFriendsMap = new HashMap<>();
        headers = SearchFriendService.allAphabets();
        List<String> filteredErrors = new ArrayList<>();

        if (cenesFriends.size() > 0) {

            for (EventMember friend : cenesFriends) {
                String initialAlphabet = friend.getName().substring(0, 1).toUpperCase();

                System.out.println("Inital ALPHABET " + initialAlphabet);
                if (!headers.contains(initialAlphabet)) {
                    initialAlphabet = "#";
                }

                List<EventMember> members = null;
                if (headerFriendsMap.containsKey(initialAlphabet)) {
                    members = headerFriendsMap.get(initialAlphabet);
                } else {
                    members = new ArrayList<>();
                }
                members.add(friend);
                headerFriendsMap.put(initialAlphabet, members);
                if (!filteredErrors.contains(initialAlphabet)) {
                    filteredErrors.add(initialAlphabet);
                }
            }
        } else {
            for (EventMember friend : allFriends) {
                String initialAlphabet = friend.getName().substring(0, 1).toUpperCase();

                System.out.println("Inital ALPHABET " + initialAlphabet);
                if (!headers.contains(initialAlphabet)) {
                    initialAlphabet = "#";
                }

                List<EventMember> members = null;
                if (headerFriendsMap.containsKey(initialAlphabet)) {
                    members = headerFriendsMap.get(initialAlphabet);
                } else {
                    members = new ArrayList<>();
                }
                members.add(friend);
                headerFriendsMap.put(initialAlphabet, members);
                if (!filteredErrors.contains(initialAlphabet)) {
                    filteredErrors.add(initialAlphabet);
                }
            }
        }

        return filteredErrors;
    }


    public List<String> prepareListHeadersForCenesContacts() {

        headerFriendsMap = new HashMap<>();
        headers = SearchFriendService.allAphabets();
        List<String> filteredErrors = new ArrayList<>();

        if (cenesFriends.size() > 0) {

            for (EventMember friend : cenesFriends) {
                String initialAlphabet = friend.getName().substring(0, 1).toUpperCase();

                System.out.println("Inital ALPHABET " + initialAlphabet);
                if (!headers.contains(initialAlphabet)) {
                    initialAlphabet = "#";
                }

                List<EventMember> members = null;
                if (headerFriendsMap.containsKey(initialAlphabet)) {
                    members = headerFriendsMap.get(initialAlphabet);
                } else {
                    members = new ArrayList<>();
                }
                members.add(friend);
                headerFriendsMap.put(initialAlphabet, members);
                if (!filteredErrors.contains(initialAlphabet)) {
                    filteredErrors.add(initialAlphabet);
                }

            }
        }
        return filteredErrors;
    }


    public List<String> prepareListHeadersForAllContacts() {

        headerFriendsMap = new HashMap<>();
        headers = SearchFriendService.allAphabets();
        List<String> filteredErrors = new ArrayList<>();

        if (allFriends.size() > 0) {

            for (EventMember friend : allFriends) {
                String initialAlphabet = friend.getName().substring(0, 1).toUpperCase();

                System.out.println("Inital ALPHABET " + initialAlphabet);
                if (!headers.contains(initialAlphabet)) {
                    initialAlphabet = "#";
                }

                List<EventMember> members = null;
                if (headerFriendsMap.containsKey(initialAlphabet)) {
                    members = headerFriendsMap.get(initialAlphabet);
                } else {
                    members = new ArrayList<>();
                }
                members.add(friend);
                headerFriendsMap.put(initialAlphabet, members);
                if (!filteredErrors.contains(initialAlphabet)) {
                    filteredErrors.add(initialAlphabet);
                }

            }
        }
        return filteredErrors;
    }

    public AllContactsExpandableAdapter getAllContactsExpandableAdapter() {
        return allContactsExpandableAdapter;
    }
}
