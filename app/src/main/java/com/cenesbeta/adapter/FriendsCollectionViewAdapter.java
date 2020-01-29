package com.cenesbeta.adapter;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.fragment.friend.FriendListFragment;
import com.cenesbeta.util.RoundedImageView;

import java.util.List;

/**
 * Created by mandeep on 27/12/18.
 */

public class FriendsCollectionViewAdapter extends RecyclerView.Adapter<FriendsCollectionViewAdapter.MyViewHolder> {

    private List<EventMember> jsonObjectArrayList;
    private FriendListFragment context;
    private RecyclerView recyclerView;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView ivFriend;
        RelativeLayout rvNonCenesLayout;
        RelativeLayout rvCenesLayout;
        TextView tvName, tvNonCenesLabel;
        RelativeLayout container;
        ImageView ibDeleteMember, ivDeleteNonCenesMember;
        ImageView ivHostCircleMember;

        public MyViewHolder(View view) {
            super(view);
            ivFriend = (RoundedImageView) view.findViewById(R.id.iv_friend_image);
            tvName = (TextView) view.findViewById(R.id.tv_friend_name);
            tvNonCenesLabel = (TextView) view.findViewById(R.id.tv_non_cenes_label);
            container = (RelativeLayout) view.findViewById(R.id.container);
            ibDeleteMember = (ImageView) view.findViewById(R.id.ib_delete_member);
            ivDeleteNonCenesMember = (ImageView) view.findViewById(R.id.ib_delete_nonmember);
            ivHostCircleMember = (ImageView) view.findViewById(R.id.iv_host_circle_member);

            rvNonCenesLayout = (RelativeLayout) view.findViewById(R.id.rv_non_cenes_layout);
            rvCenesLayout = (RelativeLayout) view.findViewById(R.id.rv_cenes_layout);
        }
    }

    public FriendsCollectionViewAdapter(FriendListFragment context, List<EventMember> jsonObjectArrayList, RecyclerView recyclerView) {
        this.jsonObjectArrayList = jsonObjectArrayList;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public FriendsCollectionViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gathering_friend_list_item, parent, false);
        return new FriendsCollectionViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FriendsCollectionViewAdapter.MyViewHolder holder, final int position) {
        try {
            final EventMember invFrn = jsonObjectArrayList.get(position);

            System.out.println("REsult of condition : "+invFrn.getFriendId()+", asdfghj : "+(invFrn.getFriendId() != null && invFrn.getFriendId() == context.loggedInUser.getUserId()));
            if (invFrn.getFriendId() != null && invFrn.getFriendId().equals(context.loggedInUser.getUserId()) || (invFrn.getFriendId() == null && invFrn.getUserId() != null &&  invFrn.getUserId().equals(context.loggedInUser.getUserId()) && invFrn.getUser() != null) ) {
                holder.ibDeleteMember.setVisibility(View.GONE);
                holder.ivHostCircleMember.setVisibility(View.GONE);
            } else {
                holder.ibDeleteMember.setVisibility(View.VISIBLE);
                holder.ivHostCircleMember.setVisibility(View.VISIBLE);
            }

            //lets first check the condition for LoggediN User
            if (invFrn.getFriendId() != null && invFrn.getFriendId().equals(context.loggedInUser.getUserId()) || (invFrn.getFriendId() == null && invFrn.getUserId() != null &&  invFrn.getUserId().equals(context.loggedInUser.getUserId()) && invFrn.getUser() != null))  {
                holder.rvNonCenesLayout.setVisibility(View.GONE);
                holder.rvCenesLayout.setVisibility(View.VISIBLE);

                if (invFrn.getUser() != null && invFrn.getUser().getName() != null) {
                    holder.tvName.setText(invFrn.getUser().getName());
                } else if (invFrn.getName() != null) {
                    holder.tvName.setText(invFrn.getName());
                }
                if (invFrn.getUser() != null && invFrn.getUser().getPicture() != null) {
                    Glide.with(context).load(invFrn.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(holder.ivFriend);
                } else {
                    holder.ivFriend.setImageResource(R.drawable.profile_pic_no_image);
                }
            } else if ((invFrn.getFriendId() != null || invFrn.getUserId() != null) && invFrn.getUser() != null) {

                holder.rvNonCenesLayout.setVisibility(View.GONE);
                holder.rvCenesLayout.setVisibility(View.VISIBLE);
                if (invFrn.getUser() != null && invFrn.getUser().getName() != null) {
                    holder.tvName.setText(invFrn.getUser().getName());
                } else if (invFrn.getName() != null) {
                    holder.tvName.setText(invFrn.getName());
                }
                if (invFrn.getUser() != null && invFrn.getUser().getPicture() != null) {
                    Glide.with(context).load(invFrn.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(holder.ivFriend);
                } else {
                    holder.ivFriend.setImageResource(R.drawable.profile_pic_no_image);
                }

            } else {

                String imageName = "";
                String userName = "";
                if (invFrn.getUserContact() != null && invFrn.getUserContact().getName() != null) {
                    userName = invFrn.getUserContact().getName();
                } else if (invFrn.getName() != null) {
                    userName = invFrn.getName();
                }
                holder.tvName.setText(userName);
                String[] titleArr = userName.split(" ");
                int i=0;
                for (String str: titleArr) {
                    if (i > 1) {
                        break;
                    }
                    imageName += str.substring(0,1).toUpperCase();
                    i++;
                }

                holder.tvNonCenesLabel.setText(imageName);
                holder.rvCenesLayout.setVisibility(View.GONE);
                holder.rvNonCenesLayout.setVisibility(View.VISIBLE);
            }

            holder.ibDeleteMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (context != null) {

                        EventMember objectToRemove = jsonObjectArrayList.get(position);
                        context.checkboxStateHolder.remove(objectToRemove.getUserContactId());
                        context.checkboxObjectHolder.remove(objectToRemove.getUserContactId());
                        context.selectedEventMembers.remove(objectToRemove);
                        //CheckBox checkBoxToUnSelect = context.checkboxButtonHolder.get(objectToRemove.getUserContactId());
                        //checkBoxToUnSelect.setSelected(false);
                        jsonObjectArrayList.remove(position);
                        //recyclerView.removeViewAt(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, jsonObjectArrayList.size());

                        if (context.getAllContactsExpandableAdapter() != null) {
                            context.getAllContactsExpandableAdapter().notifyDataSetChanged();
                        }

                        if (context.checkboxObjectHolder.size() == 0) {
                            context.getView().findViewById(R.id.rl_selected_friends_recycler_view).setVisibility(View.GONE);
                        }
                    }
                }
            });

            holder.ivDeleteNonCenesMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (context != null) {

                        EventMember objectToRemove = jsonObjectArrayList.get(position);
                        context.checkboxStateHolder.remove(objectToRemove.getUserContactId());
                        context.checkboxObjectHolder.remove(objectToRemove.getUserContactId());
                        context.selectedEventMembers.remove(objectToRemove);

                        //CheckBox checkBoxToUnSelect = context.checkboxButtonHolder.get(objectToRemove.getUserContactId());
                        //checkBoxToUnSelect.setSelected(false);
                        jsonObjectArrayList.remove(position);
                        //recyclerView.removeViewAt(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, jsonObjectArrayList.size());
                        if (context.getAllContactsExpandableAdapter() != null) {
                            context.getAllContactsExpandableAdapter().notifyDataSetChanged();
                        }

                        if (context.checkboxObjectHolder.size() == 0) {
                            context.getView().findViewById(R.id.rl_selected_friends_recycler_view).setVisibility(View.GONE);
                        }
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
