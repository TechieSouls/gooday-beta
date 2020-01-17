package com.cenesbeta.adapter;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.bo.Event;
import com.cenesbeta.dto.HomeScreenDto;
import com.cenesbeta.fragment.dashboard.HomeFragment;

public class InvitationListItemAdapter extends BaseExpandableListAdapter {

    private HomeFragment homeFragment;
    private HomeScreenDto homeScreenDto;
    private LayoutInflater inflter;


    public InvitationListItemAdapter(HomeFragment homeFragment, HomeScreenDto homeScreenDto) {

        this.homeFragment = homeFragment;
        this.homeScreenDto = homeScreenDto;
        inflter = (LayoutInflater.from(homeFragment.getContext()));

    }

    @Override
    public int getGroupCount() {
        return homeScreenDto.getHomeDataHeaders().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return homeScreenDto.getHomeDataHeaders().get(groupPosition);
    }

    @Override
    public Event getChild(int groupPosition, int childPosition) {
        return homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_home_data_headers_v2, null);
            holder = new HeaderViewHolder();
            holder.tvHeader = (TextView) convertView.findViewById(R.id.tv_calendar_data_header);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        };

        String header = getGroup(groupPosition);
        holder.tvHeader.setText(header);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class HeaderViewHolder {
        private TextView tvHeader;
    }
}
