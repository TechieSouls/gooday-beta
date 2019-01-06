package com.deploy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.R;
import com.deploy.activity.SearchFriendActivity;
import com.deploy.activity.SearchLocationActivity;
import com.deploy.bo.Location;

import java.util.List;

/**
 * Created by mandeep on 4/1/19.
 */

public class RecentLocationAdapter extends RecyclerView.Adapter<RecentLocationAdapter.RecentLocationHolder> {

        private List<Location> locations;
        private SearchLocationActivity context;

        public class RecentLocationHolder extends RecyclerView.ViewHolder {
            ImageView ivLocationPhoto;
            TextView tvLocation;
            Button btnUseLocation;

            public RecentLocationHolder(View view) {
                super(view);
                ivLocationPhoto = (ImageView) view.findViewById(R.id.iv_image_thumbnail);
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
                    Glide.with(context).load(locationObj.getPhoto()).apply(RequestOptions.bitmapTransform(new RoundedCorners(14))).apply(RequestOptions.placeholderOf(R.drawable.party_image)).into(holder.ivLocationPhoto);
                }
                holder.tvLocation.setText(locationObj.getLocation());

                holder.btnUseLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("selection", "scroll");
                        intent.putExtra("title", locationObj.getLocation());
                        intent.putExtra("photo", locationObj.getPhoto());
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
