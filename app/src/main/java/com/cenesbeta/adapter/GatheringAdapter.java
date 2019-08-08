package com.cenesbeta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.bo.Friend;

import java.util.List;

/**
 * Created by mandeep on 25/8/17.
 */

public class GatheringAdapter extends BaseAdapter {

    List<Friend> friends;
    LayoutInflater inflter;
    Context context;


    public GatheringAdapter(Context context, List<Friend> friends) {
        this.friends = friends;
        this.context = context;
        inflter = (LayoutInflater.from(context));
    }
    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int i) {
        return friends.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_friends_list_view, null);
        TextView friendName = (TextView)view.findViewById(R.id.invite_friend_name);

        friendName.setText(friends.get(i).getName());
        return view;
    }
}
