package com.cenesbeta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.bo.EventCategory;
import com.cenesbeta.fragment.gathering.EventCategoryFragment;

import java.util.List;

public class EventCategoriesAdapter extends BaseAdapter {

    private EventCategoryFragment eventCategoryFragment;
    private List<EventCategory> eventCategories;
    private LayoutInflater layoutInflater;
    public EventCategoriesAdapter(EventCategoryFragment eventCategoryFragment, List<EventCategory> eventCategories) {
        this.eventCategoryFragment = eventCategoryFragment;
        this.eventCategories = eventCategories;
        this.layoutInflater = (LayoutInflater.from(this.eventCategoryFragment.getContext()));

    }

    @Override
    public int getCount() {
        return eventCategories.size();
    }

    @Override
    public EventCategory getItem(int position) {
        return eventCategories.get(position);
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
            convertView = this.layoutInflater.inflate(R.layout.adapter_event_category_item, null);

            viewHolder.rlCategoryItem = (RelativeLayout) convertView.findViewById(R.id.rl_category_item);
            viewHolder.tvCategoryTitle = (TextView) convertView.findViewById(R.id.tv_category_title);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        EventCategory eventCategory = getItem(position);

        viewHolder.tvCategoryTitle.setText(eventCategory.getTitle());

        viewHolder.rlCategoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }
}

class ViewHolder {

    TextView tvCategoryTitle;
    RelativeLayout rlCategoryItem;
}