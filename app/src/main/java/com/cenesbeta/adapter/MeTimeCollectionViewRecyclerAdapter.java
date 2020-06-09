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
import com.cenesbeta.bo.RecurringEventMember;
import com.cenesbeta.fragment.metime.MeTimeCardFragment;
import com.cenesbeta.util.RoundedImageView;

import java.util.List;

public class MeTimeCollectionViewRecyclerAdapter extends RecyclerView.Adapter<MeTimeCollectionViewRecyclerAdapter.MyViewHolder> {

    private MeTimeCardFragment meTimeCardFragment;
    private List<RecurringEventMember> recurringEventMembers;

    public MeTimeCollectionViewRecyclerAdapter(MeTimeCardFragment meTimeCardFragment, List<RecurringEventMember> recurringEventMembers) {

        this.meTimeCardFragment = meTimeCardFragment;
        this.recurringEventMembers = recurringEventMembers;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(meTimeCardFragment.getContext()).inflate(R.layout.adapter_metime_collectionview_recycler_item, viewGroup, false);
        return new MeTimeCollectionViewRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        RecurringEventMember recurringEventMember = recurringEventMembers.get(position);

        if (recurringEventMember.getUser() != null && recurringEventMember.getUser().getPicture() != null && recurringEventMember.getUser().getPicture().length() > 0) {

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.drawable.profile_pic_no_image);
            Glide.with(meTimeCardFragment.getContext()).load(recurringEventMember.getUser().getPicture()).apply(requestOptions).into(myViewHolder.rvProfileImage);

        } else {

            myViewHolder.rvProfileImage.setImageResource(R.drawable.profile_pic_no_image);

        }
        if (recurringEventMember.getUser() != null) {
            myViewHolder.tvProfileName.setText(recurringEventMember.getUser().getName());
        }
    }

    @Override
    public int getItemCount() {
        return recurringEventMembers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvProfileName;
        private RoundedImageView rvProfileImage;

        public MyViewHolder(View view) {
            super(view);
            tvProfileName = (TextView) view.findViewById(R.id.tv_profile_name);
            rvProfileImage = (RoundedImageView) view.findViewById(R.id.rv_profile_image);
        }
    }

}
