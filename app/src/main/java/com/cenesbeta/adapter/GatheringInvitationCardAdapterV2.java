package com.cenesbeta.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.fragment.gathering.GatheringGuestListFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import java.util.Date;

public class GatheringInvitationCardAdapterV2 extends BaseAdapter {


    private GatheringPreviewFragment gatheringPreviewFragment;
    private LayoutInflater inflter;
    private Event event;
    private EventMember eventOwner;

    public GatheringInvitationCardAdapterV2(GatheringPreviewFragment gatheringPreviewFragment, Event event) {

        this.eventOwner = null;
        this.event = event;
        this.gatheringPreviewFragment = gatheringPreviewFragment;
        this.inflter = (LayoutInflater.from(this.gatheringPreviewFragment.getContext()));

    }
    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflter.inflate(R.layout.adapter_gathering_invitation_card_v2, null);

            holder.rl_invitation_view = (RelativeLayout) convertView.findViewById(R.id.rl_invitation_view);
            holder.rl_skip_bar = (RelativeLayout) convertView.findViewById(R.id.rl_skip_bar);

            convertView.setTag(holder);
        } else {
            holder =  (ViewHolder) convertView.getTag();
        }

        holder.rl_invitation_view.getLayoutParams().height = gatheringPreviewFragment.getActivity().getWindowManager().getDefaultDisplay().getHeight();
        return convertView;
    }

    class ViewHolder {
        RelativeLayout rl_invitation_view, rl_skip_bar;

    }
}
