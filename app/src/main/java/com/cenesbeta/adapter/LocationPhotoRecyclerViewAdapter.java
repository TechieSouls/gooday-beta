package com.cenesbeta.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.bo.LocationPhoto;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesConstants;

import java.util.List;

public class LocationPhotoRecyclerViewAdapter extends RecyclerView.Adapter<LocationPhotoRecyclerViewAdapter.LocationPhotoHolder> {

    private GatheringPreviewFragment gatheringPreviewFragment;
    private List<LocationPhoto> locationPhotos;

    public LocationPhotoRecyclerViewAdapter(GatheringPreviewFragment gatheringPreviewFragment, List<LocationPhoto> locationPhotos) {
        this.gatheringPreviewFragment = gatheringPreviewFragment;
        this.locationPhotos = locationPhotos;
    }

    @NonNull
    @Override
    public LocationPhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(gatheringPreviewFragment.getContext()).inflate(R.layout.adapter_recyclerview_location_place_photo_item, viewGroup, false);
        return new LocationPhotoRecyclerViewAdapter.LocationPhotoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationPhotoHolder viewHolder, int i) {

        LocationPhoto locationPhoto = locationPhotos.get(i);
        try {

            RequestOptions requestOptions= new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.transform(new RoundedCorners(10));
            Glide.with(gatheringPreviewFragment.getContext())
                    .load(CenesConstants.GOOGLE_PLACE_THUMBNAIL_PHOTOS_API+locationPhoto.getPhotoReferences())
                    .apply(requestOptions)
                    .into(viewHolder.ivLocationPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return locationPhotos.size();
    }

    class LocationPhotoHolder extends RecyclerView.ViewHolder {
        private ImageView ivLocationPhoto;

        public LocationPhotoHolder(View view) {
            super(view);
            ivLocationPhoto = (ImageView) view.findViewById(R.id.iv_location_photo);
        }
    }
}
