package com.cenesbeta.fragment.gathering;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.EventCardExpandableAdapter;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.InvitationFragment;
import com.cenesbeta.fragment.NavigationFragment;
import com.cenesbeta.util.RoundedImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 2/11/17.
 */

public class GatheringsFragment extends CenesFragment {

    public final static String TAG = "GatheringsFragment";

    private int CREATE_GATHERING_RESULT_CODE = 1001;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    InternetManager internetManager;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private View fragmentView;

    //GatheringsAdapter listAdapter;
    EventCardExpandableAdapter listAdapter;
    ExpandableListView gatheringsEventsList;

    private TextView homeNoEvents, gatheringsText, tvNotificationCount;
    private RoundedImageView homePageProfilePic;
    private TextView confirmedBtn, maybeBtn, declinedBtn;
    private ImageView createGatheringBtn;

    private GatheringAsyncTask gatheringAsyncTasks;
    private GatheringAsyncTask gatheringsTask;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_gatherings, container, false);
        fragmentView = view;
        init(view);
        User user = userManager.getUser();
        if (user != null && user.getPicture() != null && user.getPicture() != "null") {
            // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
            Glide.with(this).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(homePageProfilePic);
        }

        ((CenesBaseActivity)getActivity()).rlLoadingBlock.setVisibility(View.GONE);
        Bundle bundle_ = this.getArguments();
        if (bundle_ != null && "push".equals(bundle_.getString("dataFrom"))) {
            new FetchGatheringTask().execute(bundle_);
        } else if (bundle_ != null && "list".equals(bundle_.getString("dataFrom"))) {

            //Lets open the Gathering Preview First
            Bundle bundle = new Bundle();
            bundle.putString("dataFrom", "list");
            bundle.putLong("eventId", bundle_.getLong("eventId"));
            this.getArguments().clear();
            //CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
            //createGatheringFragment.setArguments(bundle);
            //((GatheringScreenActivity) getActivity()).replaceFragment(createGatheringFragment, "CreateGatheringFragment");
            GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
            gatheringPreviewFragment.setArguments(bundle);
            ((CenesBaseActivity) getActivity()).replaceFragment(gatheringPreviewFragment, "GatheringPreviewFragment");

        } else {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //TODO your background code
                    //gatheringsTask = new GatheringsTask();
                    //gatheringsTask.execute("Going");

                    new GatheringAsyncTask.GatheringsTask(new GatheringAsyncTask.GatheringsTask.AsyncResponse() {
                        @Override
                        public void processFinish(Map<String, Object> response) {
                            updateUIAfterGatheringAsyncTask(response);
                        }
                    }).execute("Going");


                }
            });
        }

        /*AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code

                new GatheringAsyncTask.NotificationCountTask(new GatheringAsyncTask.NotificationCountTask.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject response) {
                        try {
                            tvNotificationCount.setText(String.valueOf(response.getInt("data")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute();

            }
        });*/

        return view;
    }

    public void init(View view) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        internetManager = coreManager.getInternetManager();

        gatheringAsyncTasks = new GatheringAsyncTask(cenesApplication, (CenesBaseActivity) getActivity());

        gatheringsEventsList = (ExpandableListView) view.findViewById(R.id.home_events_list_view);

        homeNoEvents = (TextView) view.findViewById(R.id.home_no_events);
        homePageProfilePic = (RoundedImageView) view.findViewById(R.id.home_profile_pic);

        gatheringsText = (TextView) view.findViewById(R.id.gatherings_text);
        gatheringsText.setText("Your Gatherings");

        confirmedBtn = (TextView) view.findViewById(R.id.confirmed_btn);
        maybeBtn = (TextView) view.findViewById(R.id.maybe_btn);
        declinedBtn = (TextView) view.findViewById(R.id.declined_btn);

        createGatheringBtn = (ImageView) view.findViewById(R.id.create_gath_btn);
        tvNotificationCount = (TextView) view.findViewById(R.id.tv_notification_count_pic);
        //fab = (FloatingActionButton) findViewById(R.id.fab);

        //Listenerss
        confirmedBtn.setOnClickListener(onClickListener);
        maybeBtn.setOnClickListener(onClickListener);
        declinedBtn.setOnClickListener(onClickListener);
        createGatheringBtn.setOnClickListener(onClickListener);
        homePageProfilePic.setOnClickListener(onClickListener);

        //fab.setOnClickListener(onClickListener);
    }

    public void updateUIAfterGatheringAsyncTask(Map<String, Object> response) {
        if (getActivity() == null) {
            return;
        }
        if (response != null) {

            List<String> headers = (List<String>) response.get("headers");
            Map<String, List<Event>> eventMap = (Map<String, List<Event>>) response.get("eventMap");
            Boolean isInvitation = (Boolean)  response.get("isInvitation");
            listAdapter = new EventCardExpandableAdapter((CenesBaseActivity)getActivity(), fragmentManager,  headers, eventMap, isInvitation);

            gatheringsEventsList.setVisibility(View.VISIBLE);
            gatheringsEventsList.setAdapter(listAdapter);
            homeNoEvents.setVisibility(View.GONE);
        } else {
            gatheringsEventsList.setVisibility(View.GONE);
            homeNoEvents.setVisibility(View.VISIBLE);
            homeNoEvents.setText("You have no gatherings");
        }

    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_GATHERING_RESULT_CODE && resultCode == RESULT_OK) {
            new GatheringsTask().execute("Going");
        } else if (requestCode == CREATE_GATHERING_RESULT_CODE && resultCode == RESULT_CANCELED) {
            //Do Nothing
        }
    }*/

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.home_profile_pic:
                    ((CenesBaseActivity)getActivity()).fragmentManager.beginTransaction().add(R.id.settings_container, new NavigationFragment(), null).commit();
                    ((CenesBaseActivity)getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
                    break;
                case R.id.confirmed_btn:
                    selectTab(confirmedBtn);
                    gatheringsText.setText("Your Gatherings");
                    //new GatheringsTask().execute("Going");
                    new GatheringAsyncTask.GatheringsTask(new GatheringAsyncTask.GatheringsTask.AsyncResponse() {
                        @Override
                        public void processFinish(Map<String, Object> response) {
                            updateUIAfterGatheringAsyncTask(response);
                        }
                    }).execute("Going");
                    break;
                case R.id.maybe_btn:
                    selectTab(maybeBtn);
                    gatheringsText.setText("Your Invitations");
                    //new GatheringsTask().execute("pending");
                    new GatheringAsyncTask.GatheringsTask(new GatheringAsyncTask.GatheringsTask.AsyncResponse() {
                        @Override
                        public void processFinish(Map<String, Object> response) {
                            updateUIAfterGatheringAsyncTask(response);
                        }
                    }).execute("pending");
                    break;
                case R.id.declined_btn:
                    selectTab(declinedBtn);
                    gatheringsText.setText("Your Invitations");
                    //new GatheringsTask().execute("NotGoing");
                    new GatheringAsyncTask.GatheringsTask(new GatheringAsyncTask.GatheringsTask.AsyncResponse() {
                        @Override
                        public void processFinish(Map<String, Object> response) {
                            updateUIAfterGatheringAsyncTask(response);
                        }
                    }).execute("NotGoing");
                    break;

                case R.id.create_gath_btn:
                    //startActivityForResult(new Intent(getActivity(), CreateGatheringActivity.class), CREATE_GATHERING_RESULT_CODE);
                    //break;
                    ((CenesBaseActivity) getActivity()).parentEvent = null;
                    fragmentManager = getActivity().getSupportFragmentManager();
                    ((CenesBaseActivity) getActivity()).replaceFragment(new CreateGatheringFragment(), CreateGatheringFragment.TAG);
//                    replaceFragment(new ProfileFragment(), "cgFragment");
                    break;
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        //if (gatheringAsyncTasks != null) {
            //gatheringAsyncTasks.cancel(true);
        //}
       /* if (notificationCountTask != null) {
            notificationCountTask.cancel(true);
        }*/
    }

    public void selectTab(TextView selection) {
        confirmedBtn.setBackground(getResources().getDrawable(R.drawable.border_bottom_gray));
        confirmedBtn.setTextColor(getResources().getColor(R.color.font_grey_color));
        confirmedBtn.setTypeface(Typeface.DEFAULT);

        maybeBtn.setBackground(getResources().getDrawable(R.drawable.border_bottom_gray));
        maybeBtn.setTextColor(getResources().getColor(R.color.font_grey_color));
        maybeBtn.setTypeface(Typeface.DEFAULT);

        declinedBtn.setBackground(getResources().getDrawable(R.drawable.border_bottom_gray));
        declinedBtn.setTextColor(getResources().getColor(R.color.font_grey_color));
        declinedBtn.setTypeface(Typeface.DEFAULT);

        selection.setBackground(getResources().getDrawable(R.drawable.border_bottom_orange));
        selection.setTextColor(getResources().getColor(R.color.cenes_new_orange));
        selection.setTypeface(Typeface.DEFAULT_BOLD);
    }


    class FetchGatheringTask extends AsyncTask<Bundle, Map<String,Object>, Map<String,Object>> {
        ProgressDialog fetchingGathDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fetchingGathDialog = new ProgressDialog(getActivity());
            fetchingGathDialog.setMessage("Fetching..");
            fetchingGathDialog.setIndeterminate(false);
            fetchingGathDialog.setCanceledOnTouchOutside(false);
            fetchingGathDialog.setCancelable(false);
            fetchingGathDialog.show();
        }

        @Override
        protected Map<String,Object> doInBackground(Bundle... bundles) {

            Bundle bundle = bundles[0];

            User user = userManager.getUser();

            Long eventId = bundle.getLong("eventId");
            user.setApiUrl(urlManager.getApiUrl("dev"));

            Map<String,Object> gatheringMap = new HashMap<>();
            JSONObject response = apiManager.getEventById(user, eventId, getCenesActivity());
            gatheringMap.put("response",response);
            gatheringMap.put("bundle",bundle);
            return gatheringMap;
        }

        @Override
        protected void onPostExecute(Map<String,Object> responseMap) {
            super.onPostExecute(responseMap);
            fetchingGathDialog.dismiss();

            fetchingGathDialog = null;
            try {

                JSONObject apiResponse = (JSONObject) responseMap.get("response");

                if (apiResponse.getBoolean("success")) {
                    //Toast.makeText(getActivity(), "Gathering ", Toast.LENGTH_SHORT).show();
                    if (apiResponse.getString("data") == "null" || apiResponse.get("data") == null) {
                        Toast.makeText(getActivity(), "Gathering Not Available", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle bundle_ = (Bundle)responseMap.get("bundle");
                        Bundle bundle = new Bundle();
                        bundle.putString("dataFrom", "push");
                        bundle.putLong("eventId", bundle_.getLong("eventId"));
                        bundle.putString("message", bundle_.getString("message"));
                        bundle.putString("title", bundle_.getString("title"));
                        (GatheringsFragment.this).getArguments().clear();
                        InvitationFragment invitationFragment = new InvitationFragment();
                        invitationFragment.setArguments(bundle);
                        ((CenesBaseActivity) getActivity()).replaceFragment(invitationFragment, "InvitationFragment");
                    }
                } else {
                    Toast.makeText(getActivity(), "Gathering Not Available", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CenesBaseActivity) getActivity()).showFooter();
        ((CenesBaseActivity) getActivity()).activateFooterIcon(GatheringsFragment.TAG);
    }
}
