package com.cenes.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.InternetManager;
import com.cenes.Manager.UrlManager;
import com.cenes.R;
import com.cenes.activity.CenesBaseActivity;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.util.CenesUtils;
import com.cenes.util.RoundedImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by mandeep on 2/11/17.
 */

public class InvitationFragment extends CenesFragment{

    public static String TAG = "InvitationFragment";

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    InternetManager internetManager;

    private TextView tvMessage,tvTitle,tvInviteButton;

    private TextView tvInvitationTitle,tvInvitationDay,tvInvitationTime,tvInvitationLocation,tvInvitationDesc;
    private RoundedImageView userProfilePic;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private View fragmentView;

    private TextView tvAccept,tvDecline, tvSkip;

    private JSONObject gatheringData;
    private JSONObject gatheringMember;
    private User user;
    MapView mMapView;
    GoogleMap gMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_invitation, container, false);
        fragmentView = view;

        init();
        user = userManager.getUser();
        if (user != null && user.getPicture() != null && user.getPicture() != "null") {
            // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
            Glide.with(this).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(userProfilePic);
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tvMessage.setText(bundle.getString("message"));
            tvTitle.setText(bundle.getString("title"));
            tvInviteButton.setText("Open Gathering Invite!");
            if (bundle.get("dataFrom") != null && bundle.getString("dataFrom").equals("push")) {
                showInvitaiton();
            } else {
                showInvitaitonDetail();
            }
            Long eventId = bundle.getLong("eventId");
            new EventInfoTask().execute(eventId);
        }
        return view;
    }

    public void init() {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        internetManager = coreManager.getInternetManager();

        tvMessage = (TextView) fragmentView.findViewById(R.id.tv_message);
        tvTitle = (TextView) fragmentView.findViewById(R.id.tv_title);
        tvInviteButton = (TextView) fragmentView.findViewById(R.id.tv_invite_btn);

        tvInvitationTitle = (TextView) fragmentView.findViewById(R.id.tv_invitation_title);
        tvInvitationDay = (TextView) fragmentView.findViewById(R.id.tv_invitation_day);
        tvInvitationTime = (TextView) fragmentView.findViewById(R.id.tv_invitation_time);
        tvInvitationLocation = (TextView) fragmentView.findViewById(R.id.tv_invitation_location);
        tvInvitationDesc = (TextView) fragmentView.findViewById(R.id.tv_invitation_desc);

        tvAccept = (TextView) fragmentView.findViewById(R.id.tvAccept);
        tvDecline = (TextView) fragmentView.findViewById(R.id.tvDecline);
        tvSkip = (TextView) fragmentView.findViewById(R.id.tvSkip);

        userProfilePic = (RoundedImageView) fragmentView.findViewById(R.id.user_profile_pic);

        tvAccept.setOnClickListener(onClickListener);
        tvDecline.setOnClickListener(onClickListener);
        tvInviteButton.setOnClickListener(onClickListener);
        tvSkip.setOnClickListener(onClickListener);

        mMapView = (MapView) fragmentView.findViewById(R.id.loc_map);
        mMapView.onCreate(null);
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                gMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvAccept:
                    if (internetManager.isInternetConnection(getCenesActivity())) {
                        try {
                            String queryStr = "?event_member_id=" + gatheringMember.getLong("eventMemberId") + "&status=confirmed";
                            new ActionTask().execute(queryStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.tvDecline:
                    if (internetManager.isInternetConnection(getCenesActivity())) {
                        try {
                            String queryStr = "?event_member_id=" + gatheringMember.getLong("eventMemberId") + "&status=declined";
                            new ActionTask().execute(queryStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.tv_invite_btn:
                    showInvitaitonDetail();
                    break;
                case R.id.tvSkip:
                    getActivity().onBackPressed();
                    break;
            }
        }
    };


    class ActionTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... queryStrs) {
            String queryStr = "";
            if (queryStrs != null && queryStrs.length != 0) {
                queryStr = queryStrs[0];
            }

            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            JSONObject jObj = apiManager.updateInvitation(user, queryStr, getCenesActivity());

            try {
                System.out.println("blah: response: " + jObj.toString());
                return jObj.getBoolean("success");
            } catch (Exception e) {

            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            mProgressDialog = null;

            if (success == true) {
                getActivity().onBackPressed();
            }
        }
    }


    class EventInfoTask extends AsyncTask<Long, JSONObject, JSONObject> {
        ProgressDialog eventInfoDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventInfoDialog = new ProgressDialog(getContext());
            eventInfoDialog.setMessage("Loading");
            eventInfoDialog.setIndeterminate(false);
            eventInfoDialog.setCanceledOnTouchOutside(false);
            eventInfoDialog.setCancelable(false);
            eventInfoDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... eventIds) {
            Long evventId = eventIds[0];
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            JSONObject response = apiManager.getEventById(user, evventId, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject responseObj) {
            super.onPostExecute(responseObj);
            eventInfoDialog.dismiss();
            eventInfoDialog = null;
            try {
                if (responseObj.getBoolean("success")) {
                    gatheringData = responseObj.getJSONObject("data");

                    tvInvitationTitle.setText(gatheringData.getString("title"));

                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTimeInMillis(gatheringData.getLong("startTime"));
                    tvInvitationDay.setText(CenesUtils.EEEEMMMMdd.format(startCalendar.getTime())+CenesUtils.getDateSuffix(startCalendar.get(Calendar.DAY_OF_MONTH)));

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTimeInMillis(gatheringData.getLong("endTime"));
                    tvInvitationTime.setText(CenesUtils.hhmmaa.format(startCalendar.getTime())+" "+CenesUtils.hhmmaa.format(endCalendar.getTime()));

                    if (gatheringData.has("location") && !CenesUtils.isEmpty(gatheringData.getString("location"))) {
                        tvInvitationLocation.setText(gatheringData.getString("location"));
                    }

                    if (gatheringData.has("description") && !CenesUtils.isEmpty(gatheringData.getString("description"))) {
                        tvInvitationDesc.setText(gatheringData.getString("description"));
                    }

                    if (gatheringData.get("latitude") != null && gatheringData.getString("latitude") != "null"  && !gatheringData.getString("latitude").isEmpty()) {
                        mMapView.setVisibility(View.VISIBLE);
                        gMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(gatheringData.getString("latitude")), Double.valueOf(gatheringData.getString("longitude")))));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.valueOf(gatheringData.getString("latitude")), Double.valueOf(gatheringData.getString("longitude")))).zoom(15).build();
                        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        gMap.getUiSettings().setAllGesturesEnabled(false);
                    } else {
                        mMapView.setVisibility(View.GONE);
                    }

                    //Iterating parentEvent members from Event to get Logged In user as EventMember
                    if (gatheringData.has("eventMembers") && gatheringData.getJSONArray("eventMembers").length() > 0) {
                        for (int i=0; i<gatheringData.getJSONArray("eventMembers").length(); i++) {
                            JSONObject gatheringMemberObj = gatheringData.getJSONArray("eventMembers").getJSONObject(i);
                            if (gatheringMemberObj.getLong("userId") == user.getUserId()) {
                                gatheringMember = gatheringMemberObj;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showInvitaiton() {
        fragmentView.findViewById(R.id.rl_invitation).setVisibility(View.VISIBLE);
        fragmentView.findViewById(R.id.ll_invitation_detail).setVisibility(View.GONE);
    }
    public void showInvitaitonDetail() {
        fragmentView.findViewById(R.id.rl_invitation).setVisibility(View.GONE);
        fragmentView.findViewById(R.id.ll_invitation_detail).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((CenesBaseActivity) getActivity()).hideFooter();
    }
}
