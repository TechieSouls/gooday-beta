package com.cenesbeta.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.fragment.dashboard.HomeFragmentV2;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import java.util.List;

public class InvitationTabItemRecyclerAdapter extends RecyclerView.Adapter<InvitationTabItemRecyclerAdapter.MyViewHolder> {

    private HomeFragmentV2 homeFragmentV2;
    private List<EventMember> eventMembers;

    public InvitationTabItemRecyclerAdapter(HomeFragmentV2 homeFragmentV2, List<EventMember> eventMembers) {

        this.homeFragmentV2 = homeFragmentV2;
        this.eventMembers = eventMembers;

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(homeFragmentV2.getContext()).inflate(R.layout.adapter_invitation_list_recycler_item, viewGroup, false);
        return new InvitationTabItemRecyclerAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {

            final EventMember eventMember = eventMembers.get(position);

            System.out.println(eventMember.toString());

            if (eventMember.getUser() != null && eventMember.getUser().getUserId() != null && eventMember.getUser().getUserId().equals(homeFragmentV2.loggedInUser.getUserId())) {
                myViewHolder.tvProfileName.setText("Me");
            } else if (eventMember.getUser() != null && !CenesUtils.isEmpty(eventMember.getUser().getName())) {
                myViewHolder.tvProfileName.setText(eventMember.getUser().getName().split(" ")[0]);
            } else if (!CenesUtils.isEmpty(eventMember.getName())) {
                myViewHolder.tvProfileName.setText(eventMember.getName().split(" ")[0]);
            }
            if (eventMember.getUser() != null && !CenesUtils.isEmpty(eventMember.getUser().getPicture())) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.profile_pic_no_image);
                requestOptions.circleCrop();
                Glide.with(homeFragmentV2.getContext()).load(eventMember.getUser().getPicture()).apply(requestOptions).into(myViewHolder.rvProfileImage);
            } else {
                myViewHolder.rvProfileImage.setImageResource(R.drawable.profile_pic_no_image);
            }
            if (position != 0) {
                myViewHolder.tvGuestLabel.setText("");
            }

        myViewHolder.rvProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CenesBaseActivity)homeFragmentV2.getActivity()).zoomImageFromThumb(myViewHolder.rvProfileImage, eventMember.getUser().getPicture());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventMembers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvProfileName, tvGuestLabel;
        private RoundedImageView rvProfileImage;

        public MyViewHolder(View view) {
            super(view);

            tvProfileName = (TextView) view.findViewById(R.id.tv_profile_name);
            tvGuestLabel = (TextView) view.findViewById(R.id.tv_guest_label);
            rvProfileImage = (RoundedImageView) view.findViewById(R.id.rv_profile_image);

        }
    }
}
