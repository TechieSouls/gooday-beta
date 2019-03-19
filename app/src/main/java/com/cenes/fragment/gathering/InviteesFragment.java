package com.cenes.fragment.gathering;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.R;
import com.cenes.application.CenesApplication;
import com.cenes.bo.Event;
import com.cenes.bo.EventMember;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.fragment.CenesFragment;
import com.cenes.util.CenesUtils;
import com.cenes.util.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InviteesFragment extends CenesFragment {

    public static String TAG = "InviteesFragment";

    private CenesApplication cenesApplication;

    private CoreManager coreManager;
    private UserManager userManager;
    private User loggedInUser;
    private Event event;

    private RelativeLayout rvAttendingList, rvPendingList, rvDeclineList;
    private LinearLayout llPendingSection, llDeclineSection, llAttendingSection;
    private TextView tvGuestsCount, tvAttendingCount, tvDeclineCount;
    private ImageView ivInviteeBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_gathering_invitees, container, false);

        rvAttendingList = (RelativeLayout) view.findViewById(R.id.rv_attending_list);
        rvPendingList = (RelativeLayout) view.findViewById(R.id.rv_pending_list);
        rvDeclineList = (RelativeLayout) view.findViewById(R.id.rv_decline_list);

        llPendingSection = (LinearLayout) view.findViewById(R.id.ll_pending_section);
        llAttendingSection = (LinearLayout) view.findViewById(R.id.ll_attending_section);
        llDeclineSection = (LinearLayout) view.findViewById(R.id.ll_decline_section);

        tvGuestsCount = (TextView) view.findViewById(R.id.tv_guests_count);
        tvAttendingCount = (TextView) view.findViewById(R.id.tv_attending_count);
        tvDeclineCount = (TextView) view.findViewById(R.id.tv_decline_count);

        ivInviteeBack = (ImageView) view.findViewById(R.id.iv_invitee_back);
        cenesApplication = getCenesActivity().getCenesApplication();


        ivInviteeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Bundle bundle = getArguments();
        String membersStr = bundle.getString("members");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<EventMember>>() {}.getType();
        List<EventMember> eventMembers = new Gson().fromJson(membersStr, listType);

        List<EventMember> attendings = new ArrayList<>();
        List<EventMember> declines = new ArrayList<>();
        List<EventMember> pendings = new ArrayList<>();

        if (eventMembers != null && eventMembers.size() > 0) {
            for (EventMember member: eventMembers) {

                if (member.getStatus() == null || member.getStatus().equals("null")) {
                    pendings.add(member);
                } else if ("Going".equals(member.getStatus())) {
                    attendings.add(member);
                } else if ("NotGoing".equals(member.getStatus())) {
                    declines.add(member);
                }
            }



            tvGuestsCount.setText(eventMembers.size()+"");
            tvAttendingCount.setText(attendings.size()+"");
            tvDeclineCount.setText(declines.size()+"");

            if (pendings.size() == 0) {
                llPendingSection.setVisibility(View.GONE);
            }
            if (attendings.size() == 0) {
                llAttendingSection.setVisibility(View.GONE);
            }
            if (declines.size() == 0) {
                llDeclineSection.setVisibility(View.GONE);
            }

            rvAttendingList.removeAllViews();
            createAttendeeUserList(attendings);
            createPendingUserList(pendings);
            createDeclinedUserList(declines);
        }
        return view;
    }

    public void createAttendeeUserList(List<EventMember> members) {

        RelativeLayout.LayoutParams guestLinearLayoutRowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout guestLinearLayoutRow = new LinearLayout(getActivity());
        guestLinearLayoutRow.setLayoutParams(guestLinearLayoutRowParams);
        guestLinearLayoutRow.setOrientation(LinearLayout.VERTICAL);

        for (EventMember eventMember: members) {

            RelativeLayout.LayoutParams guestLinearLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout guestLinearLayout = new LinearLayout(getActivity());
            guestLinearLayout.setLayoutParams(guestLinearLayoutParams);
            guestLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            guestLinearLayout.setPadding(CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10));

            System.out.println(eventMember.toString());

            if (eventMember.getUserId() == null) {
                String imageName = "";
                String[] titleArr = eventMember.getName().split(" ");
                int i=0;
                for (String str: titleArr) {
                    if (i > 1) {
                        break;
                    }
                    imageName += str.substring(0,1).toUpperCase();
                    i++;
                }
                TextView circleText = new TextView(getActivity());
                LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(45), CenesUtils.dpToPx(45));
                circleText.setLayoutParams(imageViewParams);
                circleText.setText(imageName);
                circleText.setGravity(Gravity.CENTER);
                circleText.setTextColor(getActivity().getResources().getColor(R.color.white));
                circleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                circleText.setBackground(getActivity().getResources().getDrawable(R.drawable.xml_circle_noncenes_grey));
                guestLinearLayout.addView(circleText);
            } else {
                LinearLayout.LayoutParams roundedImageViewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(45), CenesUtils.dpToPx(45));
                RoundedImageView profileImage = new RoundedImageView(getActivity());
                profileImage.setLayoutParams(roundedImageViewParams);

                Glide.with(this).load(eventMember.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.cenes_user_no_image)).into(profileImage);
                guestLinearLayout.addView(profileImage);
            }
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewParams.gravity = Gravity.CENTER_VERTICAL;
            textViewParams.setMargins(CenesUtils.dpToPx(20), 0, 0, 0);
            TextView nameTextView = new TextView(getActivity());
            nameTextView.setText(eventMember.getName());
            //nameTextView.setTextSize(CenesUtils.spToPx(16));
            nameTextView.setTextColor(getResources().getColor(R.color.cenes_textfield_color));
            nameTextView.setLayoutParams(textViewParams);

            guestLinearLayout.addView(nameTextView);
            guestLinearLayoutRow.addView(guestLinearLayout);

        }
        rvAttendingList.addView(guestLinearLayoutRow);

    }

    public void createDeclinedUserList(List<EventMember> members) {

        RelativeLayout.LayoutParams guestLinearLayoutRowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout guestLinearLayoutRow = new LinearLayout(getActivity());
        guestLinearLayoutRow.setLayoutParams(guestLinearLayoutRowParams);
        guestLinearLayoutRow.setOrientation(LinearLayout.VERTICAL);

        for (EventMember eventMember: members) {
            RelativeLayout.LayoutParams guestLinearLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout guestLinearLayout = new LinearLayout(getActivity());
            guestLinearLayout.setLayoutParams(guestLinearLayoutParams);
            guestLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            guestLinearLayout.setPadding(CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10));


            System.out.println(eventMember.toString());

            if (eventMember.getUserId() == null) {
                String imageName = "";
                String[] titleArr = eventMember.getName().split(" ");
                int i=0;
                for (String str: titleArr) {
                    if (i > 1) {
                        break;
                    }
                    imageName += str.substring(0,1).toUpperCase();
                    i++;
                }
                TextView circleText = new TextView(getActivity());
                LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(45), CenesUtils.dpToPx(45));
                circleText.setLayoutParams(imageViewParams);
                circleText.setText(imageName);
                circleText.setGravity(Gravity.CENTER);
                circleText.setTextColor(getActivity().getResources().getColor(R.color.white));
                circleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                circleText.setBackground(getActivity().getResources().getDrawable(R.drawable.xml_circle_noncenes_grey));
                guestLinearLayout.addView(circleText);
            } else {
                LinearLayout.LayoutParams roundedImageViewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(45), CenesUtils.dpToPx(45));
                RoundedImageView profileImage = new RoundedImageView(getActivity());
                profileImage.setLayoutParams(roundedImageViewParams);

                Glide.with(this).load(eventMember.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.cenes_user_no_image)).into(profileImage);
                guestLinearLayout.addView(profileImage);
            }
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewParams.gravity = Gravity.CENTER_VERTICAL;
            textViewParams.setMargins(CenesUtils.dpToPx(20), 0, 0, 0);
            TextView nameTextView = new TextView(getActivity());
            nameTextView.setText(eventMember.getName());
            //nameTextView.setTextSize(CenesUtils.spToPx(16));
            nameTextView.setTextColor(getResources().getColor(R.color.cenes_textfield_color));
            nameTextView.setLayoutParams(textViewParams);

            guestLinearLayout.addView(nameTextView);
            guestLinearLayoutRow.addView(guestLinearLayout);
        }
        rvDeclineList.addView(guestLinearLayoutRow);

    }

    public void createPendingUserList(List<EventMember> members) {

        RelativeLayout.LayoutParams guestLinearLayoutRowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout guestLinearLayoutRow = new LinearLayout(getActivity());
        guestLinearLayoutRow.setLayoutParams(guestLinearLayoutRowParams);
        guestLinearLayoutRow.setOrientation(LinearLayout.VERTICAL);

        for (EventMember eventMember: members) {

             RelativeLayout.LayoutParams guestLinearLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
             LinearLayout guestLinearLayout = new LinearLayout(getActivity());
             guestLinearLayout.setLayoutParams(guestLinearLayoutParams);
             guestLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
             guestLinearLayout.setPadding(CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10));


             System.out.println(eventMember.toString());

            if (eventMember.getUserId() == null) {
                String imageName = "";
                String[] titleArr = eventMember.getName().split(" ");
                int i=0;
                for (String str: titleArr) {
                    if (i > 1) {
                        break;
                    }
                    imageName += str.substring(0,1).toUpperCase();
                    i++;
                }
                TextView circleText = new TextView(getActivity());
                LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(45), CenesUtils.dpToPx(45));
                circleText.setLayoutParams(imageViewParams);
                circleText.setText(imageName);
                circleText.setGravity(Gravity.CENTER);
                circleText.setTextColor(getActivity().getResources().getColor(R.color.white));
                circleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                circleText.setBackground(getActivity().getResources().getDrawable(R.drawable.xml_circle_noncenes_grey));
                guestLinearLayout.addView(circleText);
            } else {
                LinearLayout.LayoutParams roundedImageViewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(45), CenesUtils.dpToPx(45));
                RoundedImageView profileImage = new RoundedImageView(getActivity());
                profileImage.setLayoutParams(roundedImageViewParams);

                Glide.with(this).load(eventMember.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.cenes_user_no_image)).into(profileImage);
                guestLinearLayout.addView(profileImage);
            }
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewParams.gravity = Gravity.CENTER_VERTICAL;
            textViewParams.setMargins(CenesUtils.dpToPx(20), 0, 0, 0);
            TextView nameTextView = new TextView(getActivity());
            nameTextView.setText(eventMember.getName());
            //nameTextView.setTextSize(CenesUtils.spToPx(16));
            nameTextView.setTextColor(getResources().getColor(R.color.cenes_textfield_color));
            nameTextView.setLayoutParams(textViewParams);

            guestLinearLayout.addView(nameTextView);
            guestLinearLayoutRow.addView(guestLinearLayout);
         }
        rvPendingList.addView(guestLinearLayoutRow);

    }

}
