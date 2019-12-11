package com.cenesbeta.adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
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
import java.util.List;
import java.util.Map;

public class FriendListAdapter extends BaseAdapter {

    private FriendListFragment friendListFragment;
    private List<EventMember> friends;
    LayoutInflater inflter;
    private FriendsCollectionViewAdapter mFriendsCollectionViewAdapter;
    private RecyclerView recyclerView;

    public FriendListAdapter(FriendListFragment friendListFragment, List<EventMember> friends) {
        this.friendListFragment = friendListFragment;
        this.friends = friends;
        this.inflter = (LayoutInflater.from(this.friendListFragment.getContext()));
        recyclerView = (RecyclerView) (this.friendListFragment).getView().findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public EventMember getItem(int i) {
        try {
            return this.friends.get(i);
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        final FriendViewHolder holder;

        if (view == null) {
            holder = new FriendViewHolder();
            view = this.inflter.inflate(R.layout.adapter_search_friends, null);
            holder.inviteFriendName = (TextView) view.findViewById(R.id.invite_friend_name);
            holder.inviteFriendPicture = (RoundedImageView) view.findViewById(R.id.invite_friend_picture);
            holder.tvFriendPhone = (TextView) view.findViewById(R.id.tv_friend_phone);
            holder.rvNonCenesLayout = (RelativeLayout) view.findViewById(R.id.rv_non_cenes_layout);
            holder.llInviteFrndBar = (LinearLayout) view.findViewById(R.id.ll_invite_frnd_bar);
            holder.ivHostCircleMember = (ImageView) view.findViewById(R.id.iv_host_circle_member);

            view.setTag(holder);

        } else {
            holder = (FriendViewHolder) view.getTag();
        }

         final EventMember friendObj = getItem(position);
        try {

            if (friendObj.getUser() != null && friendObj.getUser().getName() != null) {
                holder.inviteFriendName.setText(friendObj.getUser().getName());
            } else {
                holder.inviteFriendName.setText(friendObj.getName());
            }
            holder.tvFriendPhone.setText(friendObj.getName());
            if (friendObj.getUser() != null && friendObj.getUser().getPicture() != null) {
                Glide.with(this.friendListFragment.getContext()).load(friendObj.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(holder.inviteFriendPicture);
            } else {
                holder.inviteFriendPicture.setImageResource(R.drawable.profile_pic_no_image);
            }

            holder.inviteFriendPicture.setVisibility(View.VISIBLE);
            holder.rvNonCenesLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (friendListFragment.checkboxStateHolder.containsKey(friendObj.getUserContactId())) {
            holder.ivHostCircleMember.setVisibility(View.VISIBLE);
        } else {
            holder.ivHostCircleMember.setVisibility(View.GONE);
        }

        holder.llInviteFrndBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (friendListFragment.checkboxStateHolder.containsKey(friendObj.getUserContactId())) {
                        friendListFragment.checkboxStateHolder.remove(friendObj.getUserContactId());
                        friendListFragment.checkboxObjectHolder.remove(friendObj.getUserContactId());
                    } else {

                        System.out.println("User Info : "+friendObj.toString());
                        friendListFragment.checkboxStateHolder.put(friendObj.getUserContactId(), true);
                        friendListFragment.checkboxObjectHolder.put(friendObj.getUserContactId(), friendObj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                friendListFragment.searchFriendEditText.setText("");
                if (friendListFragment.checkboxObjectHolder.size() > 0) {
                    friendListFragment.getView().findViewById(R.id.rl_selected_friends_recycler_view).setVisibility(View.VISIBLE);
                    friendListFragment.getActivity().runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {

                            List<EventMember> members = new ArrayList<>();
                            for (Map.Entry<Integer, EventMember> checkboxHolderSet: friendListFragment.checkboxObjectHolder.entrySet()) {
                                members.add(checkboxHolderSet.getValue());
                            }

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
        return view;
    }

    class FriendViewHolder {
        private TextView inviteFriendName,tvFriendPhone;
        private RoundedImageView inviteFriendPicture;
        private RelativeLayout rvNonCenesLayout;
        private LinearLayout llInviteFrndBar;
        private ImageView ivHostCircleMember;

    }

    public void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) friendListFragment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
