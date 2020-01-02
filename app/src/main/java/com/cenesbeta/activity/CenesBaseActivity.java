package com.cenesbeta.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.CenesCommonAsyncTask;
import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.NotificationCountData;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.fragment.NavigationFragment;
import com.cenesbeta.fragment.NotificationFragment;
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.fragment.gathering.GatheringsFragment;
import com.cenesbeta.fragment.metime.MeTimeCardFragment;
import com.cenesbeta.fragment.metime.MeTimeFragment;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

public class CenesBaseActivity extends CenesActivity {

    public DrawerLayout mDrawerLayout;

    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;
    public ImageView footerHomeIcon, footerGatheringIcon, footerMeTimeIcon;
    LinearLayout llFooter;
    public RelativeLayout rlLoadingBlock;
    public TextView tvLoadingMsg;
    public ImageView ivNotificationFloatingIcon;
    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private InternetManager internetManager;

    public Event parentEvent;
    private Fragment initialFragment;
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_cenes);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        internetManager = coreManager.getInternetManager();

        fragmentManager = getSupportFragmentManager();
        initialFragment = new HomeFragment();
        replaceFragment(initialFragment, null);

        llFooter = (LinearLayout) findViewById(R.id.rl_footer);
        footerHomeIcon = (ImageView) findViewById(R.id.footer_home_icon);
        footerGatheringIcon = (ImageView) findViewById(R.id.footer_gathering_icon);
        footerMeTimeIcon = (ImageView) findViewById(R.id.footer_metime_icon);
        ivNotificationFloatingIcon = (ImageView) findViewById(R.id.iv_notification_floating_icon);

        rlLoadingBlock = (RelativeLayout) findViewById(R.id.rl_loading_block);
        tvLoadingMsg = (TextView) findViewById(R.id.tv_loading_msg);

        //Click Listeners
        footerHomeIcon.setOnClickListener(onClickListener);
        footerGatheringIcon.setOnClickListener(onClickListener);
        footerMeTimeIcon.setOnClickListener(onClickListener);
        ivNotificationFloatingIcon.setOnClickListener(onClickListener);

        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        notificationCountCall();

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                fragmentManager.beginTransaction().add(R.id.settings_container, new NavigationFragment(), null).commit();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        String data = getIntent().getDataString();
        System.out.println(data);
        if (data != null) {

            try {
                    String params = "";
                    if (data.indexOf("https://betaweb.cenesgroup.com/event/invitation") != -1) {
                        params = data.substring(data.lastIndexOf("/"), data.length());
                    } else if (data.indexOf("https://dev.cenesgroup.com/app/signupOptions") != -1) {
                        String parameters = data.split("\\?")[1];
                        if (parameters != null && parameters.split("=").length > 1) {
                            params = parameters.split("=")[1];
                        }
                    }
                    if (params != null) {

                        new GatheringAsyncTask(cenesApplication, this);
                        new GatheringAsyncTask.GatheringByKeyTask(new GatheringAsyncTask.GatheringByKeyTask.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject response) {

                                try {

                                    boolean success = response.getBoolean("success");

                                    if (success == true) {

                                        Gson gson = new Gson();
                                        Event event = gson.fromJson(response.getJSONObject("data").toString(), Event.class);
                                        GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                                        gatheringPreviewFragment.event = event;
                                        replaceFragment(gatheringPreviewFragment, HomeFragment.TAG);

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(params);

                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            onNewIntent(getIntent());
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.footer_home_icon:
                    System.out.println("Home Clicked From Base");
                    notificationCountCall();
                    replaceFragment(new HomeFragment(), null);
                    break;

                case R.id.footer_gathering_icon:
                    System.out.println("Gathering Clicked From Base");
                    notificationCountCall();
                    replaceFragment(new GatheringsFragment(), null);
                    break;

                case R.id.footer_metime_icon:
                    System.out.println("MeTime Clicked From Base");
                    notificationCountCall();
                    replaceFragment(new MeTimeFragment(), null);
                    break;

                case R.id.iv_notification_floating_icon:
                    clearBackStackInclusive(null);
                    replaceFragment(new NotificationFragment(), null);
                    break;
            }
        }
    };

    public void hideFooter() {
        llFooter.setVisibility(View.GONE);
    }

    public void showFooter() {
        llFooter.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        for (Fragment fragment: fragmentManager.getFragments()) {

            System.out.println("Back Pressed");
            if (fragment instanceof MeTimeCardFragment) {
                showFooter();
                activateFooterIcon(MeTimeFragment.TAG);
            }
        }
        super.onBackPressed();
    }

    public void replaceFragment(Fragment fragment , String tag) {

        try {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (tag != null) {
                fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
                fragmentTransaction.addToBackStack(tag);
            } else {
                fragmentTransaction.replace(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hideAndreplaceFragment(Fragment currentFragment, Fragment fragment , String tag) {

        try {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (tag != null) {
                fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
                fragmentTransaction.addToBackStack(tag);
                fragmentTransaction.hide(currentFragment);
            } else {
                fragmentTransaction.replace(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void activateFooterIcon(String tag) {

        if (tag.equals(HomeFragment.TAG)) {
            footerHomeIcon.setImageResource(R.drawable.home_icon_selected);
        } else {
            footerHomeIcon.setImageResource(R.drawable.home_icon_unselected);
        }

        if (tag.equals(GatheringsFragment.TAG)) {
            footerGatheringIcon.setImageResource(R.drawable.gathering_icon_selected);
        } else {
            footerGatheringIcon.setImageResource(R.drawable.gathering_icon_unselected);
        }

        if (tag.equals(MeTimeFragment.TAG)) {
            footerMeTimeIcon.setImageResource(R.drawable.metime_icon_selected);
        } else {
            footerMeTimeIcon.setImageResource(R.drawable.metime_icon_unselected);
        }
    }

    public void clearFragmentsAndOpen(Fragment fragment) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(fragment, null);
    }

    public FragmentManager getHomeActivityFragmentManager() {
        return fragmentManager;
    }

    public Fragment getVisibleFragment() {
        fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void clearBackStackInclusive(String tag) {
        getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void clearAllFragmentsInBackstack() {
        FragmentManager fm = getSupportFragmentManager(); // or 'getSupportFragmentManager();'
        int count = fm.getBackStackEntryCount();
        for(int i = 0; i < count; ++i) {
            fm.popBackStack();
        }

    }
    public void notificationCountCall() {
        if (internetManager.isInternetConnection(CenesBaseActivity.this)) {
            new CenesCommonAsyncTask(getCenesApplication(), this);
            new CenesCommonAsyncTask.NotificationBadgeCountTask(new CenesCommonAsyncTask.NotificationBadgeCountTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {
                    //System.out.println("Notification Data "+response.toString());

                    try {
                        if (response != null && response.getBoolean("success") == true) {
                            Gson gson = new Gson();
                            NotificationCountData notificationCountData = gson.fromJson(response.getString("data"), NotificationCountData.class);
                            if (notificationCountData.getBadgeCount() > 0) {
                                ivNotificationFloatingIcon.setVisibility(View.VISIBLE);
                            } else {
                                ivNotificationFloatingIcon.setVisibility(View.GONE);
                            }
                        } else {
                            ivNotificationFloatingIcon.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).execute();
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG).show();
        }
    }


    public void zoomImageFromThumb(final ImageView thumbView, String imageUrl) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        //expandedImageView.setImageResource(imageResId);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_pic_no_image);
        Glide.with(this).load(imageUrl).apply(requestOptions).into(expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.drawer_layout)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        try {
            Bundle bundle = getIntent().getExtras();
            Long eventId = bundle.getLong("eventId");
            if (eventId != null) {

                new GatheringAsyncTask(cenesApplication, this);
                new GatheringAsyncTask.EventInfoTask(new GatheringAsyncTask.EventInfoTask.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject response) {

                        try {

                            boolean success = response.getBoolean("success");

                            if (success == true) {

                                Gson gson = new Gson();
                                Event event = gson.fromJson(response.getJSONObject("data").toString(), Event.class);
                                GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                                gatheringPreviewFragment.event = event;
                                gatheringPreviewFragment.sourceFragment = initialFragment;
                                replaceFragment(gatheringPreviewFragment, HomeFragment.TAG);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute(eventId);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
