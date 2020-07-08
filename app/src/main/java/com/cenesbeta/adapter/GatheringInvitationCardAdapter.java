package com.cenesbeta.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class GatheringInvitationCardAdapter extends BaseAdapter {


    private Fragment gatheringPreviewFragment;
    private LayoutInflater inflter;
    private Event event;
    private EventMember eventOwner;

    public GatheringInvitationCardAdapter(Fragment gatheringPreviewFragment, Event event) {

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
            convertView = this.inflter.inflate(R.layout.adapter_gathering_invitation_card, null);

            holder.ivProfilePicView = (RoundedImageView) convertView.findViewById(R.id.iv_profile_pic);
            holder.ivEventPicture = (ImageView) convertView.findViewById(R.id.iv_event_picture);
            holder.tvEventTitle = (TextView) convertView.findViewById(R.id.tv_event_title);
            holder.tvEventDate = (TextView) convertView.findViewById(R.id.tv_event_date);
            holder. tvEventDescriptionDialogText = (TextView) convertView.findViewById(R.id.tv_event_description_dialog_text);

            holder.rlGuestListBubble = (RelativeLayout) convertView.findViewById(R.id.rl_guest_list_bubble);
            holder.rlLocationBubble = (RelativeLayout) convertView.findViewById(R.id.rl_location_bubble);
            holder.rlDescriptionBubble = (RelativeLayout) convertView.findViewById(R.id.rl_description_bubble);
            holder.rlShareBubble = (RelativeLayout) convertView.findViewById(R.id.rl_share_bubble);

            holder.rvEventDescriptionDialog = (RelativeLayout) convertView.findViewById(R.id.rv_event_description_dialog);
            holder.rlDescriptionBubbleBackground = (RelativeLayout) convertView.findViewById(R.id.rl_description_bubble_background);
            //holder.rlInvitationView = (RelativeLayout) convertView.findViewById(R.id.rl_invitation_view);
            holder.ivDescriptionBubbleIcon = (ImageView) convertView.findViewById(R.id.iv_description_bubble_icon);

            convertView.setTag(holder);
        } else {
            holder =  (ViewHolder) convertView.getTag();
        }


        if (event.getEventId() != null) {

            holder.tvEventTitle.setText(event.getTitle());

            String eventDate = CenesUtils.EEEMMMMdd.format(new Date(event.getStartTime()))+","+CenesUtils.hmm_aa.format(new Date(event.getStartTime()))+"-"+CenesUtils.hmm_aa.format(new Date(event.getEndTime()));
            holder.tvEventDate.setText(eventDate);
            if (!CenesUtils.isEmpty(event.getEventPicture())) {
                Glide.with(gatheringPreviewFragment.getContext()).load(event.getThumbnail()).into(holder.ivEventPicture);
                Glide.with(gatheringPreviewFragment.getContext()).load(event.getEventPicture()).into(holder.ivEventPicture);
            }

        }

        if (event.getEventMembers() != null && event.getEventMembers().size() > 0) {

            for (EventMember eventMember: event.getEventMembers()) {

                if (eventMember.getUserId() != null && eventMember.getUserId().equals(event.getCreatedById())) {
                    eventOwner = eventMember;
                    break;
                }
            }
        }

        if (eventOwner != null) {

            if (eventOwner.getUser() != null && !CenesUtils.isEmpty(eventOwner.getUser().getPicture())) {

                Glide.with(gatheringPreviewFragment.getContext()).load(eventOwner.getUser().getPicture()).apply(RequestOptions.centerCropTransform()).into(holder.ivProfilePicView);

            } else {

                holder.ivProfilePicView.setImageResource(R.drawable.profile_pic_no_image);
            }
        }


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.rl_guest_list_bubble:

                        GatheringGuestListFragment gatheringGuestListFragment = new GatheringGuestListFragment();
                        gatheringGuestListFragment.eventMembers = event.getEventMembers();
                        //((CenesBaseActivity)gatheringPreviewFragment.getActivity()).replaceFragment(gatheringGuestListFragment, GatheringPreviewFragment.TAG);
                        break;
                    case R.id.rl_location_bubble:

                        hideDescriptionMessage(holder);

                        if (!CenesUtils.isEmpty(event.getLatitude())) {
                            new AlertDialog.Builder(gatheringPreviewFragment.getActivity())
                                    .setTitle("")
                                    .setMessage(event.getLocation())
                                    .setPositiveButton("Get Directions", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Create a Uri from an intent string. Use the result to create an Intent.
                                            Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+event.getLongitude()+","+event.getLongitude()+"");

                                            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            // Make the Intent explicit by setting the Google Maps package
                                            mapIntent.setPackage("com.google.android.apps.maps");

                                            // Attempt to start an activity that can handle the Intent
                                            gatheringPreviewFragment.startActivity(mapIntent);

                                        }
                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                        } else  if (!CenesUtils.isEmpty(event.getLocation())) {

                            new AlertDialog.Builder(gatheringPreviewFragment.getActivity())
                                    .setTitle(event.getLocation())
                                    .setMessage("")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", null).show();

                        } else {

                            new AlertDialog.Builder(gatheringPreviewFragment.getActivity())
                                    .setTitle("No Location Selected")
                                    .setMessage("")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", null).show();

                        }
                        break;

                    case R.id.rl_description_bubble:

                        if (!CenesUtils.isEmpty(event.getDescription())) {

                            if (holder.rvEventDescriptionDialog.getVisibility() == View.GONE) {

                                holder.ivDescriptionBubbleIcon.setImageResource(R.drawable.message_on_icon);
                                holder.rlDescriptionBubbleBackground.setAlpha(1);
                                holder.rlDescriptionBubbleBackground.setBackground(gatheringPreviewFragment.getResources().getDrawable(R.drawable.xml_circle_white));
                                holder.tvEventDescriptionDialogText.setText(event.getDescription());
                                holder.rvEventDescriptionDialog.setVisibility(View.VISIBLE);
                            } else {
                                hideDescriptionMessage(holder);
                            }

                        } else {
                            new AlertDialog.Builder(gatheringPreviewFragment.getActivity())
                                    .setTitle("Description Not Available.")
                                    .setMessage("")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", null).show();
                        }
                        break;

                    case R.id.rl_share_bubble:

                        String name = "Your Friend";
                        if (eventOwner.getName() != null) {
                            name = eventOwner.getName();
                        } else if (eventOwner.getUser() != null && eventOwner.getUser().getName() != null) {
                            name = eventOwner.getUser().getName();
                        }
                        String shrareUrl = name+"invites you to "+event.getTitle()+". RSVP through the Cenes app. Link below: \n";
                        shrareUrl = shrareUrl + CenesConstants.webDomainEventUrl+""+event.getKey();


                        if (event.getEventId() != null) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, shrareUrl);
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            gatheringPreviewFragment.getActivity().startActivity(shareIntent);
                        } else {
                            new AlertDialog.Builder(gatheringPreviewFragment.getActivity())
                                    .setTitle("Event cannot be shared this time.")
                                    .setMessage("")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", null).show();

                        }

                        break;

                    default:
                        System.out.println("Heyyy you did it.");
                }
            }
        };


        return convertView;
    }

    class ViewHolder {

        private RoundedImageView ivProfilePicView;
        private ImageView ivEventPicture;
        private TextView tvEventTitle, tvEventDate, tvEventDescriptionDialogText;
        private RelativeLayout rlGuestListBubble, rlLocationBubble, rlDescriptionBubble, rlShareBubble;
        private RelativeLayout rvEventDescriptionDialog;
        private RelativeLayout rlDescriptionBubbleBackground;
        private ImageView ivDescriptionBubbleIcon;

    }

    public void hideDescriptionMessage(ViewHolder viewHolder) {
        viewHolder.rvEventDescriptionDialog.setVisibility(View.GONE);
        viewHolder.rlDescriptionBubbleBackground.setAlpha(0.3f);
        viewHolder.ivDescriptionBubbleIcon.setImageResource(R.drawable.message_off_icon);
        viewHolder.rlDescriptionBubbleBackground.setBackground(gatheringPreviewFragment.getResources().getDrawable(R.drawable.xml_circle_black_faded));
    }



}
