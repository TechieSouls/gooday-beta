package com.cenesbeta.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cenesbeta.R;
import com.cenesbeta.bo.MeTimeItem;
import com.cenesbeta.fragment.metime.MeTimeCardFragment;
import com.cenesbeta.util.CenesUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class MeTimePagerAdapter extends PagerAdapter {

    private MeTimeCardFragment meTimeCardFragment;
    private LayoutInflater inflater;
    private Context context;

    public MeTimePagerAdapter(MeTimeCardFragment fragment) {
        this.meTimeCardFragment = fragment;
        inflater = LayoutInflater.from(fragment.getContext());
        context = fragment.getContext();
    }

    @Override
    public int getCount() {
        try {
            return 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        try {
            return view.equals(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = null;
        if (position == 0) {

            view = inflater.inflate(R.layout.layout_metime_days, container, false);

            meTimeCardFragment.sunday = (Button) view.findViewById(R.id.metime_sun_text);
            meTimeCardFragment.monday = (Button) view.findViewById(R.id.metime_mon_text);
            meTimeCardFragment.tuesday = (Button) view.findViewById(R.id.metime_tue_text);
            meTimeCardFragment.wednesday = (Button) view.findViewById(R.id.metime_wed_text);
            meTimeCardFragment.thursday = (Button) view.findViewById(R.id.metime_thu_text);
            meTimeCardFragment.friday = (Button) view.findViewById(R.id.metime_fri_text);
            meTimeCardFragment.saturday = (Button) view.findViewById(R.id.metime_sat_text);

            meTimeCardFragment.metimeStartTime = (LinearLayout) view.findViewById(R.id.metime_start_time);
            meTimeCardFragment.metimeEndTime = (LinearLayout) view.findViewById(R.id.metime_end_time);

            meTimeCardFragment.startTimeText = (TextView) view.findViewById(R.id.startTimeText);
            meTimeCardFragment.endTimeText = (TextView) view.findViewById(R.id.endTimeText);

            meTimeCardFragment.sunday.setOnClickListener(meTimeCardFragment.onClickListener);
            meTimeCardFragment.monday.setOnClickListener(meTimeCardFragment.onClickListener);
            meTimeCardFragment.tuesday.setOnClickListener(meTimeCardFragment.onClickListener);
            meTimeCardFragment. wednesday.setOnClickListener(meTimeCardFragment.onClickListener);
            meTimeCardFragment.thursday.setOnClickListener(meTimeCardFragment.onClickListener);
            meTimeCardFragment.friday.setOnClickListener(meTimeCardFragment.onClickListener);
            meTimeCardFragment.saturday.setOnClickListener(meTimeCardFragment.onClickListener);
            meTimeCardFragment.metimeStartTime.setOnClickListener(meTimeCardFragment.onClickListener);
            meTimeCardFragment.metimeEndTime.setOnClickListener(meTimeCardFragment.onClickListener);

            meTimeCardFragment.startTimeText.setText("00:00");
            meTimeCardFragment.startTimeMillis = null;

            meTimeCardFragment.endTimeText.setText("00:00");
            meTimeCardFragment.endTimeMillis = null;

            try {
                if (meTimeCardFragment.metime.getStartTime() != null && meTimeCardFragment.metime.getStartTime() != 0) {

                    meTimeCardFragment.startTimeText.setText(CenesUtils.hmm_aa.format(new Date(meTimeCardFragment.metime.getStartTime())).toUpperCase());
                    meTimeCardFragment.endTimeText.setText(CenesUtils.hmm_aa.format(new Date(meTimeCardFragment.metime.getEndTime())).toUpperCase());


                    for(MeTimeItem meTimeItem: meTimeCardFragment.metime.getItems()) {

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(meTimeItem.getDayOfWeekTimestamp());
                        //daysInStrList[j] = cal.get(Calendar.DAY_OF_WEEK);//recJson.getInt("dayOfWeek");
                        meTimeCardFragment.selectedDaysHolder.put(meTimeCardFragment.meTimeService.IndexDayMap().get(cal.get(Calendar.DAY_OF_WEEK)), true);
                    }

                    Iterator<String> daysItr = meTimeCardFragment.selectedDaysHolder.keys();
                    while (daysItr.hasNext()) {
                        String day = daysItr.next();
                        if ("Sunday".equals(day)) {
                            meTimeCardFragment.sunday.setBackground(meTimeCardFragment.getResources().getDrawable(R.drawable.round_button_red));
                            meTimeCardFragment.sunday.setTextColor(meTimeCardFragment.getResources().getColor(R.color.white));
                        } else if ("Monday".equals(day)) {
                            meTimeCardFragment.monday.setBackground(meTimeCardFragment.getResources().getDrawable(R.drawable.round_button_red));
                            meTimeCardFragment.monday.setTextColor(meTimeCardFragment.getResources().getColor(R.color.white));
                        } else if ("Tuesday".equals(day)) {
                            meTimeCardFragment.tuesday.setBackground(meTimeCardFragment.getResources().getDrawable(R.drawable.round_button_red));
                            meTimeCardFragment.tuesday.setTextColor(meTimeCardFragment.getResources().getColor(R.color.white));
                        } else if ("Wednesday".equals(day)) {
                            meTimeCardFragment.wednesday.setBackground(meTimeCardFragment.getResources().getDrawable(R.drawable.round_button_red));
                            meTimeCardFragment.wednesday.setTextColor(meTimeCardFragment.getResources().getColor(R.color.white));
                        } else if ("Thursday".equals(day)) {
                            meTimeCardFragment.thursday.setBackground(meTimeCardFragment.getResources().getDrawable(R.drawable.round_button_red));
                            meTimeCardFragment.thursday.setTextColor(meTimeCardFragment.getResources().getColor(R.color.white));
                        } else if ("Friday".equals(day)) {
                            meTimeCardFragment.friday.setBackground(meTimeCardFragment.getResources().getDrawable(R.drawable.round_button_red));
                            meTimeCardFragment.friday.setTextColor(meTimeCardFragment.getResources().getColor(R.color.white));
                        } else if ("Saturday".equals(day)) {
                            meTimeCardFragment.saturday.setBackground(meTimeCardFragment.getResources().getDrawable(R.drawable.round_button_red));
                            meTimeCardFragment.saturday.setTextColor(meTimeCardFragment.getResources().getColor(R.color.white));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            view = inflater.inflate(R.layout.layout_metime_friends, container, false);

            meTimeCardFragment.llFriendsCollectionView = view.findViewById(R.id.ll_friends_collection_view);
            meTimeCardFragment.ivAddMoreFriendsBtn = view.findViewById(R.id.iv_add_more_friends_btn);
            meTimeCardFragment.rvFriendsCollection = view.findViewById(R.id.rv_friends_collection);
            meTimeCardFragment.rlProfilePicPlaceholderView = view.findViewById(R.id.rl_profile_pic_placeholder_view);

            meTimeCardFragment.ivAddMoreFriendsBtn.setOnClickListener(meTimeCardFragment.onClickListener);
            meTimeCardFragment.rlProfilePicPlaceholderView.setOnClickListener(meTimeCardFragment.onClickListener);

            if (meTimeCardFragment.metime.getRecurringEventMembers() != null && meTimeCardFragment.metime.getRecurringEventMembers().size() > 0) {

                meTimeCardFragment.llFriendsCollectionView.setVisibility(View.VISIBLE);
                meTimeCardFragment.rlProfilePicPlaceholderView.setVisibility(View.GONE);

                meTimeCardFragment.getActivity().runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {


                        if (meTimeCardFragment.meTimeCollectionViewRecyclerAdapter == null) {
                            meTimeCardFragment.meTimeCollectionViewRecyclerAdapter = new MeTimeCollectionViewRecyclerAdapter(meTimeCardFragment, meTimeCardFragment.metime.getRecurringEventMembers());
                        }
                        meTimeCardFragment.meTimeCollectionViewRecyclerAdapter.notifyDataSetChanged();
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(meTimeCardFragment.getContext(), LinearLayoutManager.HORIZONTAL, false);
                        meTimeCardFragment.rvFriendsCollection.setLayoutManager(mLayoutManager);
                        meTimeCardFragment.rvFriendsCollection.setAdapter(meTimeCardFragment.meTimeCollectionViewRecyclerAdapter);
                        meTimeCardFragment.rvFriendsCollection.invalidate();
                    }
                }));

            } else {
                meTimeCardFragment.llFriendsCollectionView.setVisibility(View.GONE);
                meTimeCardFragment.rlProfilePicPlaceholderView.setVisibility(View.VISIBLE);
            }
        }
        try {

            container.addView(view, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}