package com.cenesbeta.fragment.gathering;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cenesbeta.R;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class PublicGatheringPreviewFragment extends CenesFragment {

    private TextView tvEventTitle, tvEventDateTime, tvEventDescription, tvEventLocation, tvEventHostName;
    private ImageView ivBackButton, ivEventImage;
    private RoundedImageView ivEventHostImage;
    private RecyclerView recyclerviewLocationImages;
    private RelativeLayout rlPostPublicEvent;
    private Button btnViewEvent;

    public Event event;
    private EventMember eventHost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_public_gathering_preview, container, false);

        tvEventTitle = (TextView) view.findViewById(R.id.tv_event_title);
        tvEventDateTime = (TextView) view.findViewById(R.id.tv_event_date_time);
        tvEventDescription = (TextView) view.findViewById(R.id.tv_event_description);
        tvEventLocation = (TextView) view.findViewById(R.id.tv_event_location);
        tvEventHostName = (TextView) view.findViewById(R.id.tv_event_host_name);

        ivBackButton = (ImageView) view.findViewById(R.id.iv_back_button);
        ivEventImage = (ImageView) view.findViewById(R.id.iv_event_image);
        ivEventHostImage = (RoundedImageView) view.findViewById(R.id.iv_event_host_image);

        recyclerviewLocationImages = (RecyclerView) view.findViewById(R.id.recyclerview_location_images);

        rlPostPublicEvent = (RelativeLayout) view.findViewById(R.id.rl_post_public_event);
        btnViewEvent = (Button) view.findViewById(R.id.btn_view_event);

        ivBackButton.setOnClickListener(onClickListener);
        btnViewEvent.setOnClickListener(onClickListener);

        findEventHost();
        populateEventDetails();
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_back_button:
                    getActivity().onBackPressed();
                    break;

                case R.id.btn_view_event:

                    break;
            }
        }
    };

    public void populateEventDetails() {
        tvEventTitle.setText(event.getTitle());

        String eventDateTime = CenesUtils.EEEMMMMdd.format(new Date(event.getStartTime()))+", " +
                ""+CenesUtils.hmmaa.format(new Date(event.getStartTime())).toUpperCase()+"-"
                +CenesUtils.hmmaa.format(new Date(event.getEndTime())).toUpperCase();
        tvEventDateTime.setText(eventDateTime);

        //Event Image should be 119% of width of screen
        int widthOfScreen = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        ivEventImage.getLayoutParams().height = (widthOfScreen*119)/100;
        Glide.with(getContext()).load(event.getEventPicture()).into(ivEventImage);

        /**
         * lets set host details here
         */
        if (eventHost != null) {
            if (eventHost.getUser() != null) {
                if (eventHost.getUser().getPicture() != null) {
                    Glide.with(getContext()).load(eventHost.getUser().getPicture()).into(ivEventHostImage);
                }
                if (eventHost.getUser().getName() !=  null) {
                    tvEventHostName.setText(eventHost.getUser().getName());
                }
            } else if (eventHost.getName() != null) {
                tvEventHostName.setText(eventHost.getName());
            } else {
                tvEventHostName.setText("Host");
            }
        }

        tvEventDescription.setText(event.getDescription());
        tvEventLocation.setText(event.getLocation());
    }

    public void findEventHost() {
        for (EventMember eventMem: event.getEventMembers()) {
            if (eventMem.getUserId() != null && eventMem.getUserId().equals(event.getCreatedById())) {
                eventHost = eventMem;
                break;
            }
        }
    }
}
