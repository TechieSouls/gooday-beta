package com.cenesbeta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.bo.CountryCode;
import com.cenesbeta.fragment.profile.HolidayCalendarFragment;

import java.util.List;

public class HolidayCountryItemAdapter extends BaseAdapter {

    private HolidayCalendarFragment holidayCalendarFragment;
    private List<String> countries;
    private LayoutInflater inflter;

    public HolidayCountryItemAdapter(HolidayCalendarFragment holidayCalendarFragment, List<String> countries) {

        this.holidayCalendarFragment = holidayCalendarFragment;
        this.countries = countries;
        this.inflter = (LayoutInflater.from(holidayCalendarFragment.getContext()));

    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public String getItem(int position) {
        return countries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = this.inflter.inflate(R.layout.adapter_holiday_country_list_item, null);

            viewHolder.llHolidayListItemBar = (LinearLayout) convertView.findViewById(R.id.ll_holiday_list_item_bar);
            viewHolder.tvCountryName = (TextView) convertView.findViewById(R.id.tv_country_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String country = getItem(position);

        viewHolder.tvCountryName.setText(country);
        viewHolder.llHolidayListItemBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holidayCalendarFragment.holidayBarPresssed(country);
            }
        });

        return convertView;
    }

    class ViewHolder {

        private LinearLayout llHolidayListItemBar;
        private TextView tvCountryName;
    }
}
