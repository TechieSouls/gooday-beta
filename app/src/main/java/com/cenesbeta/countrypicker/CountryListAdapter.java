package com.cenesbeta.countrypicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cenesbeta.R;

import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 16/9/17.
 */

public class CountryListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<String> countriesCalList;
    private Map<String, String> countriesCalIdMap;
    private Map<String, String> countriesCodeMap;
    private Context context;

    CountryListAdapter(Context context, List<String> countriesCalList, Map<String, String> countriesCalIdMap, Map<String, String> countriesCodeMap) {
        this.layoutInflater = (LayoutInflater.from(context));
        this.countriesCalList = countriesCalList;
        this.countriesCalIdMap = countriesCalIdMap;
        this.countriesCodeMap = countriesCodeMap;
        this.context = context;
    }

    @Override
    public int getCount() {
        return countriesCalList.size();
    }

    @Override
    public Object getItem(int i) {
        return countriesCalList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.row, viewGroup, false);
            listViewHolder.calendarName = (TextView) convertView.findViewById(R.id.row_title);
            listViewHolder.flagImage = (ImageView) convertView.findViewById(R.id.row_icon);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }
        listViewHolder.calendarName.setText(countriesCalList.get(position));
        listViewHolder.flagImage.setImageResource(context.getResources()
                .getIdentifier("flag_" + countriesCodeMap.get(countriesCalList.get(position)), "drawable",
                        context.getPackageName()));

        return convertView;
    }

    class ViewHolder {
        ImageView flagImage;
        TextView calendarName;
    }
}