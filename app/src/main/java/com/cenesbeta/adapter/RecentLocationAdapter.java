package com.cenesbeta.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.activity.SearchLocationActivity;
import com.cenesbeta.bo.Location;
import com.cenesbeta.util.CurvedImageView;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by mandeep on 4/1/19.
 */

public class RecentLocationAdapter extends RecyclerView.Adapter<RecentLocationAdapter.RecentLocationHolder> {

        private List<Location> locations;
        private SearchLocationActivity context;

        public class RecentLocationHolder extends RecyclerView.ViewHolder {
            CurvedImageView ivLocationPhoto;
            TextView tvLocation;
            Button btnUseLocation;

            public RecentLocationHolder(View view) {
                super(view);
                ivLocationPhoto = (CurvedImageView) view.findViewById(R.id.iv_image_thumbnail);
                tvLocation = (TextView) view.findViewById(R.id.tv_title);
                btnUseLocation = (Button) view.findViewById(R.id.btn_use_location);
            }
        }

        public RecentLocationAdapter(SearchLocationActivity context, List<Location> locations) {
            this.context = context;
            this.locations = locations;
        }

        @Override
        public RecentLocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_suggested_locations, parent, false);
            return new RecentLocationHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecentLocationAdapter.RecentLocationHolder holder, int position) {
            try {
                final Location locationObj = (Location) locations.get(position);

                holder.ivLocationPhoto.setImageResource(R.drawable.party_image);
                if (locationObj.getPhoto() != null) {
                    Glide.with(context).load(locationObj.getPhoto()).apply(RequestOptions.placeholderOf(R.drawable.party_image)).into(holder.ivLocationPhoto);
                }
                holder.tvLocation.setText(locationObj.getLocation().split(",")[0]);

                holder.btnUseLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("selection", "horizontalScroll");
                        intent.putExtra("locationObj", new Gson().toJson(locationObj));
                        context.setResult(Activity.RESULT_OK, intent);
                        context.finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return locations.size();
        }
}
