package com.cenesbeta.adapter;

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
import com.cenesbeta.dto.PredictiveData;
import com.cenesbeta.fragment.friend.FriendListFragment;
import com.cenesbeta.fragment.gathering.CreateGatheringFragment;
import com.cenesbeta.util.RoundedImageView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class FriendHorizontalScrollAdapter extends RecyclerView.Adapter<FriendHorizontalScrollAdapter.MyViewHolder> {

    private List<EventMember> jsonObjectArrayList;
    private CreateGatheringFragment context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView ivFriend;
        RelativeLayout rvNonCenesLayout;
        RelativeLayout rvCenesLayout, rlUserAvailabilityMark;
        TextView tvName, tvNonCenesLabel;
        RelativeLayout container;
        ImageView ibDeleteMember, ivDeleteNonCenesMember;
        ImageView ivHostCircle, ivHostCircleMmember;

        public MyViewHolder(View view) {
            super(view);
            ivFriend = (RoundedImageView) view.findViewById(R.id.iv_friend_image);
            tvName = (TextView) view.findViewById(R.id.tv_friend_name);
            tvNonCenesLabel = (TextView) view.findViewById(R.id.tv_non_cenes_label);
            container = (RelativeLayout) view.findViewById(R.id.container);
            ibDeleteMember = (ImageView) view.findViewById(R.id.ib_delete_member);
            ivDeleteNonCenesMember = (ImageView) view.findViewById(R.id.ib_delete_nonmember);

            rvNonCenesLayout = (RelativeLayout) view.findViewById(R.id.rv_non_cenes_layout);
            rvCenesLayout = (RelativeLayout) view.findViewById(R.id.rv_cenes_layout);
            rlUserAvailabilityMark = (RelativeLayout) view.findViewById(R.id.rl_user_availability_mark);

            ivHostCircle = (ImageView) view.findViewById(R.id.iv_host_circle);
            ivHostCircleMmember = (ImageView) view.findViewById(R.id.iv_host_circle_member);


            ibDeleteMember.setVisibility(View.GONE);
            ivDeleteNonCenesMember.setVisibility(View.GONE);
            ivHostCircle.setVisibility(View.GONE);
            ivHostCircleMmember.setVisibility(View.GONE);
        }
    }

    public FriendHorizontalScrollAdapter(CreateGatheringFragment context, List<EventMember> jsonObjectArrayList) {
        this.jsonObjectArrayList = jsonObjectArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gathering_friend_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            final EventMember invFrn = jsonObjectArrayList.get(position);

            if (invFrn.getUserId() == null || invFrn.getUserId() == 0 || (invFrn.getCenesMember() != null && invFrn.getCenesMember().equals("no"))) {

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


            } else {

                holder.rvNonCenesLayout.setVisibility(View.GONE);
                holder.rvCenesLayout.setVisibility(View.VISIBLE);

                if (invFrn.getUser() != null && invFrn.getUser().getName() != null) {
                    holder.tvName.setText(invFrn.getUser().getName());
                } else if (invFrn.getName() != null) {
                    holder.tvName.setText(invFrn.getName());
                }
                if (invFrn.getUser() != null && invFrn.getUser().getPicture() != null) {
                    Glide.with(context.getCenesActivity()).load(invFrn.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(holder.ivFriend);
                } else {
                    holder.ivFriend.setImageResource(R.drawable.profile_pic_no_image);
                }
            }


            if (context.predictiveDataList != null) {
                if (invFrn.getUserId() != null && invFrn.getUserId() != 0) {

                    holder.rlUserAvailabilityMark.setVisibility(View.VISIBLE);

                    boolean isUserAvailable = false;
                    if (context.predictiveDataForDate.getAttendingFriendsList() != null && context.predictiveDataForDate.getAttendingFriendsList().length() > 0) {
                        for (String userId : context.predictiveDataForDate.getAttendingFriendsList().split(",")) {
                            if (Integer.valueOf(userId).equals(invFrn.getUserId())) {
                                isUserAvailable = true;
                                break;
                            }
                        }

                    }
                    if (isUserAvailable) {
                        holder.rlUserAvailabilityMark.setBackgroundResource(R.drawable.xml_circle_green);
                    } else {
                        holder.rlUserAvailabilityMark.setBackgroundResource(R.drawable.xml_circle_red);
                    }
                }
            } else {
                holder.rlUserAvailabilityMark.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonObjectArrayList.size();
    }
}
