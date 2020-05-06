package com.cenesbeta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.fragment.gathering.GatheringGuestListFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesUtils;

import java.util.List;

public class GatheringGuestListItemAdapter extends BaseAdapter {

    private GatheringGuestListFragment gatheringGuestListFragment;
    private List<EventMember> eventMembers;
    private LayoutInflater inflter;

    public GatheringGuestListItemAdapter(GatheringGuestListFragment gatheringGuestListFragment, List<EventMember> eventMembers) {

        this.gatheringGuestListFragment = gatheringGuestListFragment;
        this.eventMembers = eventMembers;
        this.inflter = (LayoutInflater.from(gatheringGuestListFragment.getContext()));

    }
    @Override
    public int getCount() {
        return eventMembers.size();
    }

    @Override
    public EventMember getItem(int position) {
        return eventMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflter.inflate(R.layout.adapter_gathering_guest_list_item, null);

            viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.iv_profile_pic);
            viewHolder.tvCenesName = (TextView) convertView.findViewById(R.id.tv_cenes_name);
            viewHolder.tvPhonebookName = (TextView) convertView.findViewById(R.id.tv_phonebook_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final EventMember eventMember = getItem(position);

        System.out.println(eventMember.toString());
        String host = "";
        if (eventMember.getUserId() != null && eventMember.getUserId().equals(gatheringGuestListFragment.event.getCreatedById())) {
            host = " (Host)";
        }
        if (eventMember.getUser() != null && !CenesUtils.isEmpty(eventMember.getUser().getName())) {
            viewHolder.tvCenesName.setText(eventMember.getUser().getName()+" "+host);
        } else if (!CenesUtils.isEmpty(eventMember.getName())) {
            viewHolder.tvCenesName.setText(eventMember.getName()+" "+host);
        } else if (eventMember.getUserContact() != null && eventMember.getUserContact().getName() != null) {
            viewHolder.tvCenesName.setText(eventMember.getUserContact().getName()+" "+host);
        } else {
            viewHolder.tvCenesName.setText("  "+host);
        }

        //if (this.gatheringGuestListFragment.event.getCreatedById().equals(this.gatheringGuestListFragment.loggedInUser.getUserId())) {

           // if (eventMember.getUserId() != null && eventMember.getUserId() != 0 && eventMember.getUser() != null && !CenesUtils.isEmpty(eventMember.getUser().getName())) {
                if (eventMember.getUserId() != null && eventMember.getUserId() != 0 && !eventMember.getUserId().equals(gatheringGuestListFragment.loggedInUser.getUserId()) && eventMember.getUserContact() != null && eventMember.getUserContact().getName() != null) {
                    viewHolder.tvPhonebookName.setText(eventMember.getUserContact().getName());
                } else if (eventMember.getUserId() != null && eventMember.getUserId() != 0 && eventMember.getUserId().equals(gatheringGuestListFragment.loggedInUser.getUserId())) {
                    viewHolder.tvPhonebookName.setText("You");
                } //else if (eventMember.getName() != null && !gatheringGuestListFragment.loggedInUser.getUserId().equals(gatheringGuestListFragment.event.getCreatedById())) {
                 //   viewHolder.tvPhonebookName.setText(eventMember.getName());
                //}
                else {
                    viewHolder.tvPhonebookName.setText("");
                }
            //}
        //} else {
        //    viewHolder.tvPhonebookName.setText("");
        //}


        String profilePic = "";
        viewHolder.ivProfilePic.setImageDrawable(gatheringGuestListFragment.getResources().getDrawable(R.drawable.profile_pic_no_image));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_pic_no_image);
        requestOptions.circleCrop();
        if (eventMember.getUser() != null && !CenesUtils.isEmpty(eventMember.getUser().getPicture())) {

            profilePic = eventMember.getUser().getPicture();
            Glide.with(gatheringGuestListFragment.getContext()).load(eventMember.getUser().getPicture()).apply(requestOptions).into(viewHolder.ivProfilePic);

        } else if (!CenesUtils.isEmpty(eventMember.getPicture())) {

            profilePic = eventMember.getPicture();
            Glide.with(gatheringGuestListFragment.getContext()).load(eventMember.getPicture()).apply(requestOptions).into(viewHolder.ivProfilePic);

        }  else {

            viewHolder.ivProfilePic.setImageDrawable(gatheringGuestListFragment.getResources().getDrawable(R.drawable.profile_pic_no_image));
        }

        final String profilePicTemp = profilePic;
        viewHolder.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CenesBaseActivity)gatheringGuestListFragment.getActivity()).zoomImageFromThumb(viewHolder.ivProfilePic, profilePicTemp);

            }
        });


        return convertView;
    }

    class ViewHolder {

        private ImageView ivProfilePic;
        private TextView tvCenesName, tvPhonebookName;

    }
}
