package com.cenes.adapter;

import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.R;
import com.cenes.activity.CenesActivity;
import com.cenes.activity.CenesBaseActivity;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.bo.Event;
import com.cenes.bo.EventMember;
import com.cenes.fragment.InvitationFragment;
import com.cenes.fragment.gathering.CreateGatheringFragment;
import com.cenes.fragment.gathering.GatheringPreviewFragment;
import com.cenes.util.CenesUtils;
import com.cenes.util.RoundedImageView;

import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 5/1/19.
 */

public class EventCardExpandableAdapter  extends BaseExpandableListAdapter {

    private FragmentManager fragmentManager;
    CenesBaseActivity context;
    List<String> headers;
    Map<String, List<Event>> eventsMap;
    LayoutInflater inflter;
    boolean isInvitation;

    public EventCardExpandableAdapter(CenesBaseActivity applicationContext, FragmentManager fragmentManager, List<String> headers, Map<String, List<Event>> eventsMap, boolean isInvitation) {
        this.context = applicationContext;
        this.headers = headers;
        this.eventsMap = eventsMap;
        this.isInvitation = isInvitation;
        this.fragmentManager = fragmentManager;
        inflter = (LayoutInflater.from(this.context));
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.eventsMap.get(this.headers.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_gathering_card_rows, null);
            viewHolder = new ViewHolder();
            viewHolder.eventTitle = (TextView) convertView.findViewById(R.id.event_title);
            viewHolder.eventLocation = (TextView) convertView.findViewById(R.id.event_location);
            viewHolder.eventImage = (RoundedImageView) convertView.findViewById(R.id.event_image);
            viewHolder.eventRowItem = (LinearLayout) convertView.findViewById(R.id.event_row_item);
            viewHolder.homeEventMemberImages = (LinearLayout) convertView.findViewById(R.id.home_adapter_event_member_images);
            viewHolder.memberImagesContainer = (LinearLayout) convertView.findViewById(R.id.ll_member_images_container);
            viewHolder.homeEventMemberImagesCount = (TextView) convertView.findViewById(R.id.tv_event_member_images_count);
            viewHolder.homeAdapterHorizontalImageScrollView = (HorizontalScrollView) convertView.findViewById(R.id.home_adapter_horizontal_scroll_view);
            viewHolder.ivOwnerImage = (RoundedImageView) convertView.findViewById(R.id.iv_owner);
            viewHolder.startTime = (TextView) convertView.findViewById(R.id.tv_start_time);
            viewHolder.eventOwnerName = (TextView) convertView.findViewById(R.id.event_owner_name);
            viewHolder.eventId = null;
            viewHolder.trash = (TextView) convertView.findViewById(R.id.trash);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Event child = (Event) getChild(groupPosition, childPosition);

        viewHolder.eventId = child.getEventId();
        viewHolder.eventTitle.setText(child.getTitle());

        if (child.getLocation() == null || child.getLocation().length() == 0) {
            viewHolder.eventLocation.setVisibility(View.GONE);
        } else {
            viewHolder.eventLocation.setVisibility(View.VISIBLE);
            viewHolder.eventLocation.setText(child.getLocation());
        }

        EventMember owner = null;
        try {
            for (EventMember ownerMember: child.getEventMembers()) {
                if (ownerMember.getUserId() != null &&  ownerMember.getUserId().equals(child.getCreatedById())) {
                    owner = ownerMember;
                }
            }

            System.out.println(owner.toString());
            if (owner != null && owner.getUser() != null) {
                Glide.with(context).load(owner.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.cenes_user_no_image)).into(viewHolder.ivOwnerImage);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    viewHolder.eventOwnerName.setText(Html.fromHtml("<b>"+owner.getUser().getName()+"</b> is hosting", Html.FROM_HTML_MODE_COMPACT));
                } else {
                    viewHolder.eventOwnerName.setText(Html.fromHtml("<b>"+owner.getUser().getName()+"</b> is hosting"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (child.getIsFullDay() != null && child.getIsFullDay()) {
            viewHolder.startTime.setText("00:00AM");
        } else {
            viewHolder.startTime.setText(child.getStartTime());
        }

        //System.out.println(child.getTitle()+","+child.getEventMembers().size());
        if (child.getEventMembers() != null && child.getEventMembers().size() > 0) {
            viewHolder.homeEventMemberImages.removeAllViews();
            viewHolder.homeAdapterHorizontalImageScrollView.setVisibility(View.VISIBLE);

            for (int i = 0; i < child.getEventMembers().size(); i++) {
                EventMember em = child.getEventMembers().get(i);

                System.out.println(em.toString());
                if (i > 3) {
                    viewHolder.homeEventMemberImagesCount.setVisibility(View.VISIBLE);
                    viewHolder.homeEventMemberImagesCount.setText("+" + (child.getEventMembers().size() - 4));//3 Members and One owner
                } else {
                    viewHolder.homeEventMemberImagesCount.setVisibility(View.GONE);

                    if(em.isOwner()) {
                        continue;
                    }
                    RelativeLayout rlRoot = new RelativeLayout(context);
                    rlRoot.setLayoutParams(new RelativeLayout.LayoutParams(CenesUtils.dpToPx(60), CenesUtils.dpToPx(60)));

                    RelativeLayout.LayoutParams profileParams = new RelativeLayout.LayoutParams(CenesUtils.dpToPx(50), CenesUtils.dpToPx(50));
                    profileParams.setMargins(CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10));

                    RoundedImageView roundedImageView = new RoundedImageView(context, null);
                    roundedImageView.setLayoutParams(profileParams);
                    roundedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    //roundedImageView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.xml_gradient_border_transparentbg));

                    RelativeLayout.LayoutParams starParams = new RelativeLayout.LayoutParams(CenesUtils.dpToPx(30), CenesUtils.dpToPx(30));
                    starParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    starParams.addRule(RelativeLayout.ALIGN_PARENT_END);

                    ImageView ivStar = new ImageView(context);
                    ivStar.setLayoutParams(starParams);
                    ivStar.setPadding(CenesUtils.dpToPx(4), CenesUtils.dpToPx(4), CenesUtils.dpToPx(8), CenesUtils.dpToPx(8));
                    ivStar.setImageResource(R.drawable.star);

                    rlRoot.addView(roundedImageView);

                    try {
                        if(em.isOwner()) {
                            rlRoot.addView(ivStar);
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    try {
                            Glide.with(context).load(em.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.cenes_user_no_image)).into(roundedImageView);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    viewHolder.homeEventMemberImages.addView(rlRoot);
                }
            }
        } else {
            viewHolder.homeAdapterHorizontalImageScrollView.setVisibility(View.GONE);
        }

        //viewHolder.eventTime.setText(child.getStartTime());


        viewHolder.eventRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager = context.getSupportFragmentManager();

                if (isInvitation) {
                    /*context.hideFooter();
                    InvitationFragment ifFragment = new InvitationFragment();

                    Bundle bundle = new Bundle();
                    //bundle.putString("dataFrom", "GatheringsFragment");
                    bundle.putLong("eventId", viewHolder.eventId);
                    ifFragment.setArguments(bundle);
                    context.replaceFragment(ifFragment, "ifFragment");*/
                    Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "notification");
                    bundle.putLong("eventId", viewHolder.eventId);
                    GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                    gatheringPreviewFragment.setArguments(bundle);
                    context.replaceFragment(gatheringPreviewFragment, GatheringPreviewFragment.TAG);


                } else {
                    CreateGatheringFragment cgFragment = new CreateGatheringFragment();

                    /*Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "list");
                    bundle.putLong("eventId", viewHolder.eventId);
                    cgFragment.setArguments(bundle);
                    ((GatheringScreenActivity) context).replaceFragment(cgFragment, "cgFragment");*/
                    
                    Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "list");
                    bundle.putLong("eventId", viewHolder.eventId);
                    GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                    gatheringPreviewFragment.setArguments(bundle);
                    context.replaceFragment(gatheringPreviewFragment, GatheringPreviewFragment.TAG);

                }
            }
        });

        /*viewHolder.memberImagesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager = context.getSupportFragmentManager();
                if (isInvitation) {
                    InvitationFragment ifFragment = new InvitationFragment();

                    Bundle bundle = new Bundle();
                    //bundle.putString("dataFrom", "GatheringsFragment");
                    bundle.putLong("eventId", viewHolder.eventId);
                    ifFragment.setArguments(bundle);
                    context.replaceFragment(ifFragment, "ifFragment");
                } else {
                    context.hideFooter();
                    CreateGatheringFragment cgFragment = new CreateGatheringFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "list");
                    bundle.putLong("eventId", viewHolder.eventId);
                    cgFragment.setArguments(bundle);
                    context.replaceFragment(cgFragment, "cgFragment");
                }
            }
        });*/

        viewHolder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DeleteGatheringTask().execute(viewHolder.eventId);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.eventsMap.get(this.headers.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headers.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_home_data_headers, null);
            holder = new HeaderViewHolder();
            holder.lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        if (isInvitation) {
            holder.lblListHeader.setVisibility(View.GONE);
        }
        String headerTitle = (String) getGroup(groupPosition);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.lblListHeader.setText(Html.fromHtml(headerTitle, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.lblListHeader.setText(Html.fromHtml(headerTitle));
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public int convertPpToDp(int pp) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        return (int) (pp / displayMetrics.density);
    }

    public int convertDpToPp(int dp) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

    class ViewHolder {
        private Long eventId;
        private TextView eventTitle;
        private TextView eventLocation;
        private RoundedImageView eventImage;
        private LinearLayout eventRowItem;
        private LinearLayout homeEventMemberImages;
        private LinearLayout memberImagesContainer;
        private TextView homeEventMemberImagesCount;
        private HorizontalScrollView homeAdapterHorizontalImageScrollView;
        private TextView trash;
        private RoundedImageView ivOwnerImage;
        private TextView eventOwnerName;
        private TextView startTime;
    }

    class HeaderViewHolder {
        private ExpandableListView expandableListView;
        private TextView lblListHeader;
    }
}
