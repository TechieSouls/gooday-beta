package com.cenes.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cenes.R;
import com.cenes.activity.GuestActivity;
import com.cenes.bo.CountryCode;
import com.cenes.fragment.guest.SignupCountryListFragment;

import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 21/9/18.
 */

public class CountryCodeAdapter extends BaseExpandableListAdapter {

    SignupCountryListFragment activity;
    List<String> headers;
    LayoutInflater inflter;
    Map<String, List<CountryCode>> countryCodeMapList;

   public CountryCodeAdapter(SignupCountryListFragment activity, List<String> headers, Map<String, List<CountryCode>> countryCodeMapList) {
       this.activity = activity;
       this.headers = headers;
       this.countryCodeMapList = countryCodeMapList;
       this.inflter = (LayoutInflater.from(activity.getContext()));

   }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.countryCodeMapList.get(this.headers.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.headers.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.countryCodeMapList.get(this.headers.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int position, boolean b, View view, ViewGroup viewGroup) {
        ExpandableListView mExpandableListView = (ExpandableListView) viewGroup;
        mExpandableListView.expandGroup(position);
        HeaderViewHolder holder;
        if (view == null) {
            view = inflter.inflate(R.layout.adapter_country_code_list_group, null);
            holder = new HeaderViewHolder();
            holder.lblCountryListHeader = (TextView) view.findViewById(R.id.lblCountryListHeader);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }

        String headerTitle = (String) getGroup(position);
        holder.lblCountryListHeader.setText(headerTitle);

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        final ItemViewHolder viewHolder;
        if (view == null) {
            view = inflter.inflate(R.layout.adapter_country_code_list_item, null);
            viewHolder = new ItemViewHolder();
            viewHolder.ivCountryFlag = (ImageView) view.findViewById(R.id.iv_country_flag);
            viewHolder.tvCountryName = (TextView) view.findViewById(R.id.tv_country_name);
            viewHolder.tvCountryCode = (TextView) view.findViewById(R.id.tv_country_code);
            viewHolder.llCountryCodeItem = (LinearLayout) view.findViewById(R.id.ll_country_code_item);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) view.getTag();
        }

        final CountryCode child = (CountryCode) getChild(groupPosition, childPosition);
        viewHolder.tvCountryName.setText(child.getName());
        viewHolder.tvCountryCode.setText(child.getPhoneCode());
        viewHolder.ivCountryFlag.setImageResource(CountryCode.getFlagMasterResID(child));

        viewHolder.llCountryCodeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(activity.getContext(), SignupCountryListFragment.class);
                intent.putExtra("countryCode", child.getPhoneCode());
                activity.getTargetFragment().onActivityResult(activity.getTargetRequestCode(), Activity.RESULT_OK, intent);
                activity.getFragmentManager().popBackStack();*/

                activity.getTargetFragment().onActivityResult(
                        activity.getTargetRequestCode(),
                        Activity.RESULT_OK,
                        new Intent().putExtra("countryCode", child.getPhoneCode()));
                activity.getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    class HeaderViewHolder {
        TextView lblCountryListHeader;
    }

    class ItemViewHolder {
       LinearLayout llCountryCodeItem;
       ImageView ivCountryFlag;
       TextView tvCountryName, tvCountryCode;
    }
}
