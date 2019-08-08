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
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.activity.SearchLocationActivity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mandeep on 6/9/17.
 */

public class SearchLocationAdapter extends BaseAdapter {

    JSONArray locations;
    LayoutInflater inflter;
    SearchLocationActivity mActivity;
    View screenView;

    LinearLayout linerLayout;

    public SearchLocationAdapter(SearchLocationActivity context, JSONArray locations) {
        this.locations = locations;
        this.mActivity = context;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return locations.length();
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
            holder.placeId = "";
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        try {
            JSONObject locObj = this.locations.getJSONObject(i);
            JSONObject joStructuredFormatting = locObj.getJSONObject("structured_formatting");

            System.out.println(joStructuredFormatting.toString());

            holder.locTitle.setText(joStructuredFormatting.getString("main_text"));
            holder.locAddress.setText(joStructuredFormatting.getString("main_text"));
            holder.placeId = locObj.getString("place_id");

            holder.linerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyBoard(holder.linerLayout);
                    Intent intent = new Intent();
                    intent.putExtra("selection", "list");
                    intent.putExtra("title", holder.locTitle.getText().toString());
                    intent.putExtra("address", holder.locAddress.getText().toString());
                    intent.putExtra("placeId", holder.placeId);
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
    }

    public void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
