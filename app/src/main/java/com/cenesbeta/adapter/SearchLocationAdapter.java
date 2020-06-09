package com.cenesbeta.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.activity.SearchLocationActivity;
import com.cenesbeta.bo.Location;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by mandeep on 6/9/17.
 */

public class SearchLocationAdapter extends BaseAdapter {

    List<Location> locations;
    LayoutInflater inflter;
    SearchLocationActivity mActivity;
    View screenView;

    LinearLayout linerLayout;

    public SearchLocationAdapter(SearchLocationActivity context, List<Location> locations) {
        this.locations = locations;
        this.mActivity = context;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public Object getItem(int i) {
        try {
            return locations.get(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = inflter.inflate(R.layout.adapter_search_location, null);
            holder = new ViewHolder();
            holder.linerLayout = (LinearLayout) view.findViewById(R.id.ll_loc_title_add);
            holder.locTitle = (TextView) view.findViewById(R.id.loc_title);
            holder.locAddress = (TextView) view.findViewById(R.id.loc_add);
            holder.tvDistance = (TextView) view.findViewById(R.id.tv_distance);
            holder.rlLocationListItem = (RelativeLayout) view.findViewById(R.id.rl_location_list_item);
            holder.placeId = "";
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        try {
            final Location location = this.locations.get(i);
            //JSONObject joStructuredFormatting = locObj.getJSONObject("structured_formatting");

            //System.out.println(joStructuredFormatting.toString());

            holder.locTitle.setText(location.getLocation());
            holder.locAddress.setText(location.getAddress());
            holder.placeId = location.getPlaceId();

            if (location.getKilometers() == null) {
                holder.tvDistance.setVisibility(View.GONE);
            } else {
                holder.tvDistance.setVisibility(View.VISIBLE);
                holder.tvDistance.setText(location.getKilometers()+"Km");
            }

            holder.rlLocationListItem.setOnTouchListener(mActivity.layoutTouchListener);
            holder.linerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyBoard(holder.linerLayout);

                    Gson gson = new Gson();
                    String locationStr = gson.toJson(location);

                    Intent intent = new Intent();
                    intent.putExtra("selection", "list");
                    intent.putExtra("location", locationStr);
                    mActivity.setResult(Activity.RESULT_OK, intent);
                    mActivity.finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    class ViewHolder {
        TextView locTitle;
        TextView locAddress;
        LinearLayout linerLayout;
        String placeId;
        TextView tvDistance;
        RelativeLayout rlLocationListItem;
    }

    public void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
