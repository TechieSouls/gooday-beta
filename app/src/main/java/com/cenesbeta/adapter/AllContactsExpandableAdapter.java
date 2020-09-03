package com.cenesbeta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.fragment.friend.FriendListFragment;
import com.cenesbeta.util.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllContactsExpandableAdapter extends BaseExpandableListAdapter {


    private FriendListFragment friendListFragment;
    private LayoutInflater inflter;
    private List<String> headers;
    private Map<String, List<EventMember>> eventMembersMap;
    private FriendsCollectionViewAdapter mFriendsCollectionViewAdapter;
    private RecyclerView recyclerView;
    private boolean isCenesFriends;

    public AllContactsExpandableAdapter(FriendListFragment friendListFragment, List<String> headers, Map<String, List<EventMember>> eventMembersMap, boolean isCenesFriends) {

            this.friendListFragment = friendListFragment;
            this.inflter = (LayoutInflater.from(friendListFragment.getContext()));
            this.headers = headers;
            this.isCenesFriends = isCenesFriends;
            Collections.sort(this.headers, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            this.eventMembersMap = eventMembersMap;
            recyclerView = (RecyclerView) this.friendListFragment.fragmentView.findViewById(R.id.recycler_view);
            recyclerView.setVisibility(View.VISIBLE);
    }
    @Override
    public int getGroupCount() {
        return headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return eventMembersMap.get(headers.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return headers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return eventMembersMap.get(headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_friends_data_headers, null);
            holder = new HeaderViewHolder();
            holder.lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        String headerLetter = getGroup(groupPosition);
        /*if (friendListFragment.cenesFriendsVisible == true) {
            holder.lblListHeader.setVisibility(View.GONE);
        } else {
            holder.lblListHeader.setVisibility(View.VISIBLE);*/
        holder.lblListHeader.setText(headerLetter);
        //}
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_search_friends, null);
            viewHolder = new ViewHolder();
            viewHolder.inviteFriendName = (TextView) convertView.findViewById(R.id.invite_friend_name);
            viewHolder.inviteFriendPicture = (RoundedImageView) convertView.findViewById(R.id.invite_friend_picture);
            viewHolder.tvFriendPhone = (TextView) convertView.findViewById(R.id.tv_friend_phone);
            viewHolder.rvNonCenesLayout = (RelativeLayout) convertView.findViewById(R.id.rv_non_cenes_layout);
            viewHolder.llInviteFrndBar = (LinearLayout) convertView.findViewById(R.id.ll_invite_frnd_bar);
            viewHolder.ivHostCircle = (ImageView) convertView.findViewById(R.id.iv_host_circle);
            viewHolder.ivHostCircleMember = (ImageView) convertView.findViewById(R.id.iv_host_circle_member);
            viewHolder.tvNonCenesLabel = (TextView) convertView.findViewById(R.id.tv_non_cenes_label);
            viewHolder.rvCenesLayout = (RelativeLayout) convertView.findViewById(R.id.rv_cenes_layout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final EventMember child = (EventMember) getChild(groupPosition, childPosition);

        if (isCenesFriends) {
            try {
                viewHolder.inviteFriendName.setText(child.getUser().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.inviteFriendName.setText(child.getName());
        }

        if (isCenesFriends) {
            try {
                viewHolder.tvFriendPhone.setText(child.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.tvFriendPhone.setText(child.getPhone());
        }

        if (child.getCenesMember().equals("no")) {

            String imageName = "";
            String[] titleArr = child.getName().split(" ");
            int i=0;
            for (String str: titleArr) {
                if (i > 1) {
                    break;
                }
                imageName += str.substring(0,1).toUpperCase();
                i++;
            }

            viewHolder.tvNonCenesLabel.setText(imageName);
            viewHolder.rvCenesLayout.setVisibility(View.GONE);
            viewHolder.rvNonCenesLayout.setVisibility(View.VISIBLE);

            if (friendListFragment.checkboxStateHolder.containsKey(child.getUserContactId())) {

                viewHolder.ivHostCircle.setVisibility(View.VISIBLE);

            } else {

                viewHolder.ivHostCircle.setVisibility(View.GONE);
            }

        } else {

            viewHolder.rvCenesLayout.setVisibility(View.VISIBLE);
            viewHolder.rvNonCenesLayout.setVisibility(View.GONE);

            if (child.getUser() != null && child.getUser().getPicture() !=  null) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.circleCrop();
                requestOptions.placeholder(R.drawable.profile_pic_no_image);
                Glide.with(this.friendListFragment.getContext()).load(child.getUser().getPicture()).apply(requestOptions).into(viewHolder.inviteFriendPicture);

            } else {
                viewHolder.inviteFriendPicture.setImageResource(R.drawable.profile_pic_no_image);
            }

            if (friendListFragment.checkboxStateHolder.containsKey(child.getUserContactId())) {
                viewHolder.ivHostCircleMember.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivHostCircleMember.setVisibility(View.GONE);
            }
        }

        viewHolder.llInviteFrndBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    System.out.println("User Contact Id : "+child.toString());
                    friendListFragment.hideKeyboard();
                    if (friendListFragment.checkboxStateHolder.containsKey(child.getUserContactId())) {
                        friendListFragment.selectedEventMembers.remove(child);
                        friendListFragment.checkboxStateHolder.remove(child.getUserContactId());
                        friendListFragment.checkboxObjectHolder.remove(child.getUserContactId());
                        if (child.getCenesMember().equals("no")) {
                            viewHolder.ivHostCircle.setVisibility(View.GONE);
                        } else {
                            viewHolder.ivHostCircleMember.setVisibility(View.GONE);
                        }

                    } else {
                        friendListFragment.checkboxStateHolder.put(child.getUserContactId(), true);
                        friendListFragment.checkboxObjectHolder.put(child.getUserContactId(), child);
                        if (child.getCenesMember().equals("no")) {
                            viewHolder.ivHostCircle.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.ivHostCircleMember.setVisibility(View.VISIBLE);
                        }
                        friendListFragment.selectedEventMembers.add(child);

                    }
                    friendListFragment.getAllContactsExpandableAdapter().notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //friendListFragment.searchFriendEditText.setText("");
                if (friendListFragment.checkboxObjectHolder.size() > 0) {
                    friendListFragment.getView().findViewById(R.id.rl_selected_friends_recycler_view).setVisibility(View.VISIBLE);
                    friendListFragment.getActivity().runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {

                            /*List<EventMember> members = new ArrayList<>();
                            for (Map.Entry<Integer, EventMember> checkboxHolderSet: friendListFragment.checkboxObjectHolder.entrySet()) {
                                members.add(checkboxHolderSet.getValue());
                            }*/
                            List<EventMember> members = new LinkedList<>();
                            for (int i = friendListFragment.selectedEventMembers.size() - 1; i > -1; i--) {
                                members.add(friendListFragment.selectedEventMembers.get(i));
                            }

                            //Collections.reverse(members);
                            mFriendsCollectionViewAdapter = new FriendsCollectionViewAdapter(friendListFragment, members, recyclerView);
                            mFriendsCollectionViewAdapter.notifyDataSetChanged();
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(friendListFragment.getContext(), LinearLayoutManager.HORIZONTAL, false);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setHasFixedSize(true);

                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mFriendsCollectionViewAdapter);
                            recyclerView.invalidate();
                        }
                    }));

                } else {
                    friendListFragment.getView().findViewById(R.id.rl_selected_friends_recycler_view).setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    class HeaderViewHolder {
        TextView lblListHeader;
    }

    class ViewHolder {
        private TextView inviteFriendName,tvFriendPhone;
        private RoundedImageView inviteFriendPicture;
        private RelativeLayout rvNonCenesLayout, rvCenesLayout;
        private LinearLayout llInviteFrndBar;
        private ImageView ivHostCircle, ivHostCircleMember;
        private TextView tvNonCenesLabel;

    }
}
