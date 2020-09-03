package com.cenesbeta.fragment.friend;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
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
import android.widget.LinearLayout;
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
import com.cenesbeta.bo.Notification;
import com.cenesbeta.bo.User;
import com.cenesbeta.bo.UserContact;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.EventManagerImpl;
import com.cenesbeta.database.impl.UserContactManagerImpl;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.NotificationDto;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.gathering.CreateGatheringFragment;
import com.cenesbeta.fragment.metime.MeTimeCardFragment;
import com.cenesbeta.service.SearchFriendService;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FriendListFragment  extends CenesFragment {


    public static String TAG = "FriendListFragment";

    private Button closeSearchFriendsBtn, btnOpenSettings;
    public EditText searchFriendEditText;
    private ListView gathSearchFriendListView;
    private ExpandableListView expandableFriendListView;
    private Button btnDoneInviteFriend;
    private RelativeLayout cenesNoncenesSelectBar, gathSearchFriendSubHeader, rlSelectedFriendsRecyclerView;
    private RelativeLayout rlFriendsRelatedContent;
    private LinearLayout llMissingContactPermissions;
    private TextView tvSelectBarTitle, tvShareToFriends;
    private SwipeRefreshLayout swiperefreshFriends;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private InternetManager internetManager;
    private UserContactManagerImpl userContactManagerImpl;

    private AllContactsExpandableAdapter allContactsExpandableAdapter;
    private List<EventMember> allFriends;
    private List<EventMember> cenesFriends;
    private List<String> headers;
    private Map<String, List<EventMember>> headerFriendsMap;
    private ShimmerFrameLayout shimmerFrameLayout;

    private List<EventMember> searchedFriends;
    public User loggedInUser;
    public Boolean cenesFriendsVisible = false;
    public Fragment parentFragment;
    public View fragmentView;

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

        fragmentView = view;
        shimmerFrameLayout = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);

        closeSearchFriendsBtn = (Button) view.findViewById(R.id.close_search_friends_btn);
        btnOpenSettings = (Button) view.findViewById(R.id.btn_open_settings);
        searchFriendEditText = (EditText) view.findViewById(R.id.invite_friend_edit_text);
        gathSearchFriendListView = (ListView) view.findViewById(R.id.gath_search_friend_list_view);
        btnDoneInviteFriend = (Button) view.findViewById(R.id.btn_done_invite_friend);
        cenesNoncenesSelectBar = (RelativeLayout) view.findViewById(R.id.cenes_noncenes_select_bar);
        gathSearchFriendSubHeader = (RelativeLayout) view.findViewById(R.id.gath_search_friend_sub_header);
        rlFriendsRelatedContent = (RelativeLayout) view.findViewById(R.id.rl_friends_related_content);
        rlSelectedFriendsRecyclerView = (RelativeLayout) view.findViewById(R.id.rl_selected_friends_recycler_view);
        tvSelectBarTitle = (TextView) view.findViewById(R.id.tv_select_bar_title);
        tvShareToFriends = (TextView) view.findViewById(R.id.tv_share_to_friends);
        expandableFriendListView = (ExpandableListView) view.findViewById(R.id.elv_friend_list);
        swiperefreshFriends = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh_friends);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        llMissingContactPermissions = (LinearLayout) view.findViewById(R.id.ll_missing_contact_permissions);

        cenesApplication = ((CenesBaseActivity) getActivity()).getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        internetManager = coreManager.getInternetManager();
        userContactManagerImpl = new UserContactManagerImpl(cenesApplication);

        loggedInUser = userManager.getUser();

        closeSearchFriendsBtn.setOnClickListener(onClickListener);
        btnOpenSettings.setOnClickListener(onClickListener);
        searchFriendEditText.setOnClickListener(onClickListener);
        btnDoneInviteFriend.setOnClickListener(onClickListener);
        cenesNoncenesSelectBar.setOnClickListener(onClickListener);
        swiperefreshFriends.setOnRefreshListener(swipeDownListener);

        ((CenesBaseActivity)getActivity()).hideFooter();

        searchFriendEditText.setOnEditorActionListener(onEditorActionListener);
        searchFriendEditText.addTextChangedListener(onTextChangeListener);
        gathSearchFriendSubHeader.setOnTouchListener(layoutTouchListener);
        rlSelectedFriendsRecyclerView.setOnTouchListener(layoutTouchListener);
        recyclerView.setOnTouchListener(layoutTouchListener);

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
        if (parentFragment != null && parentFragment instanceof MeTimeCardFragment) {
            cenesNoncenesSelectBar.setVisibility(View.GONE);
            btnDoneInviteFriend.setVisibility(View.GONE);
        }
        addLoggedInUserAsMember(view);
        loadFriends();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFriends();
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

                        System.out.println("Is Edit Mode: "+isEditMode);
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
                    hideKeyboard();
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

                case R.id.btn_open_settings:

                    Intent settingIntent = new Intent();
                    settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    settingIntent.setData(uri);
                    getContext().startActivity(settingIntent);

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
                        if (eventMember.getUser().getName().toLowerCase().contains(editable.toString().toLowerCase())
                                || (eventMember.getName() != null && eventMember.getName().toLowerCase().contains(editable.toString().toLowerCase()))) {
                            searchedAllFriends.add(eventMember);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (EventMember friend: searchedAllFriends) {
                    String initialAlphabet = friend.getUser().getName().substring(0, 1).toUpperCase();

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

    SwipeRefreshLayout.OnRefreshListener swipeDownListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            ((CenesBaseActivity)getActivity()).getContacts();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swiperefreshFriends.setRefreshing(false);

                    allFriends = new ArrayList<>();
                    cenesFriends = new ArrayList<>();
                    loadFriends();
                }
            }, 6000);
        }
    };
    public void loadFriends() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            rlFriendsRelatedContent.setVisibility(View.GONE);
            btnDoneInviteFriend.setVisibility(View.VISIBLE);
            llMissingContactPermissions.setVisibility(View.VISIBLE);
        } else {
            rlFriendsRelatedContent.setVisibility(View.VISIBLE);
            btnDoneInviteFriend.setVisibility(View.VISIBLE);
            llMissingContactPermissions.setVisibility(View.GONE);

            //shimmerFrameLayout.setVisibility(View.VISIBLE);
            List<UserContact> uerContactList = userContactManagerImpl.fetchAllUserContacts();
            Log.e("UseContactList", (uerContactList.size())+"");
            if (uerContactList != null && uerContactList.size() > 0) {
                List<EventMember> eventMemberList = new ArrayList<>();
                for (UserContact userContactTmp: uerContactList) {
                    EventMember eventMember = new EventMember();
                    eventMember.setUserContactId(userContactTmp.getUserContactId());
                    eventMember.setUser(userContactTmp.getUser());
                    eventMember.setCenesMember(userContactTmp.getCenesMember());
                    eventMember.setUserId(userContactTmp.getUserId());
                    eventMember.setName(userContactTmp.getName());
                    eventMember.setPhone(userContactTmp.getPhone());
                    eventMember.setFriendId(userContactTmp.getFriendId());
                    eventMemberList.add(eventMember);
                }
                Log.e("UseContactListProcess", "Starts");
                processAllUserContacts(eventMemberList);
                Log.e("UseContactListProcess", "Ends");
            }
            /*new FriendAsyncTask(cenesApplication);
            new FriendAsyncTask.FriendListTask(new FriendAsyncTask.FriendListTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        shimmerFrameLayout.setVisibility(View.GONE);

                        boolean success = false;
                        if (response != null) {
                            success = response.getBoolean("success");
                        }
                        if (success == true) {
                            Gson gson = new GsonBuilder().create();
                            Type listType = new TypeToken<List<EventMember>>() {
                            }.getType();
                            List<EventMember> serverFriends = gson.fromJson(response.getJSONArray("data").toString(), listType);
                            processAllUserContacts(serverFriends);
                            userContactManagerImpl.deleteAllUserContacts();
                            Log.e("allFriends", (allFriends != null)+"");
                            for (EventMember eventMemberTmp: allFriends) {
                                UserContact userContact = new UserContact();
                                userContact.setUserContactId(eventMemberTmp.getUserContactId());
                                userContact.setCenesMember(eventMemberTmp.getCenesMember());
                                userContact.setFriendId(eventMemberTmp.getFriendId());
                                userContact.setUserId(eventMemberTmp.getUserId());
                                userContact.setPhone(eventMemberTmp.getPhone());
                                userContact.setName(eventMemberTmp.getName());
                                userContact.setUser(eventMemberTmp.getUser());
                                userContactManagerImpl.addUserContact(userContact);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute();*/
        }
    }

    public void processAllUserContacts(List<EventMember> serverFriends) {
        List<EventMember> uniqueFriends = new ArrayList<>();

        List<String> phoneTrackingList = new ArrayList<>();
        List<Integer> contactUserIdTrackingList = new ArrayList<>();

        for (EventMember serverEventMember : serverFriends) {

            if (phoneTrackingList.contains(serverEventMember.getPhone().replace("+",""))) {
                continue;
            }
            if (contactUserIdTrackingList.contains(serverEventMember.getUserContactId())) {
                continue;
            }

            contactUserIdTrackingList.add(serverEventMember.getUserContactId());
            phoneTrackingList.add(serverEventMember.getPhone());
            uniqueFriends.add(serverEventMember);
        }

        allFriends = uniqueFriends;
        List<EventMember> allFriendsTemp = new ArrayList<>();
        for (EventMember eventMember : allFriends) {
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
            cenesNoncenesSelectBar.setVisibility(View.VISIBLE);
            btnDoneInviteFriend.setVisibility(View.VISIBLE);

            cenesFriendsVisible = true;
            //gathSearchFriendListView.setVisibility(View.VISIBLE);
            //expandableFriendListView.setVisibility(View.VISIBLE);

            tvSelectBarTitle.setText("All Contacts (" + allFriends.size() + ")");
            //searchFriendAdapter = new FriendListAdapter(FriendListFragment.this, searchedFriends);
            //gathSearchFriendListView.setAdapter(searchFriendAdapter);
            allContactsExpandableAdapter = new AllContactsExpandableAdapter(FriendListFragment.this, filteredErrors, headerFriendsMap, true);
            expandableFriendListView.setAdapter(allContactsExpandableAdapter);
        } else {

            if (parentFragment != null && parentFragment instanceof MeTimeCardFragment) {
                expandableFriendListView.setVisibility(View.GONE);
                tvShareToFriends.setVisibility(View.VISIBLE);
                tvShareToFriends.setMovementMethod(LinkMovementMethod.getInstance());

            }
            cenesNoncenesSelectBar.setVisibility(View.GONE);
            cenesFriendsVisible = false;

            //gathSearchFriendListView.setVisibility(View.GONE);
            //expandableFriendListView.setVisibility(View.VISIBLE);

            tvSelectBarTitle.setText("Cenes Contacts (" + cenesFriends.size() + ")");
            allContactsExpandableAdapter = new AllContactsExpandableAdapter(FriendListFragment.this, filteredErrors, headerFriendsMap, false);
            expandableFriendListView.setAdapter(allContactsExpandableAdapter);
        }
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
                selectedEventMembers = new LinkedList<>();
                selectedEventMembers.add(friendObj);

            }

            recyclerView.setVisibility(View.VISIBLE);

            view.findViewById(R.id.rl_selected_friends_recycler_view).setVisibility(View.VISIBLE);
            getActivity().runOnUiThread(new Thread(new Runnable() {
                @Override
                public void run() {


                    //List<EventMember> members = new ArrayList<>(checkboxObjectHolder.values());
                    List<EventMember> members = new LinkedList<>();
                    for (int i = selectedEventMembers.size() - 1; i > -1; i--) {
                        members.add(selectedEventMembers.get(i));
                    }
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
                String initialAlphabet = friend.getName();
                if (friend.getUser() != null && friend.getUser().getName() != null) {
                    initialAlphabet = friend.getUser().getName().substring(0, 1).toUpperCase();
                }

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
