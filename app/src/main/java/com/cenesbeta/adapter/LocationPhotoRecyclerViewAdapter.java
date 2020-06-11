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
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.LocationPhoto;
import com.cenesbeta.fragment.ImageViewSliderFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesConstants;

import java.util.ArrayList;
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
    public void onBindViewHolder(@NonNull LocationPhotoHolder viewHolder, final int position) {

        LocationPhoto locationPhoto = locationPhotos.get(position);
        try {

            RequestOptions requestOptions= new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.transform(new RoundedCorners(10));
            Glide.with(gatheringPreviewFragment.getContext())
                    .load(CenesConstants.GOOGLE_PLACE_THUMBNAIL_PHOTOS_API+locationPhoto.getPhotoReferences())
                    .apply(requestOptions)
                    .into(viewHolder.ivLocationPhoto);

            viewHolder.ivLocationPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    List<String> locPhotos = new ArrayList<>();
                    for (LocationPhoto locPhoto: locationPhotos) {
                        locPhotos.add(CenesConstants.GOOGLE_PLACE_SLIDER_PHOTOS_API+locPhoto.getPhotoReferences());
                    }
                    ImageViewSliderFragment imageViewSliderFragment = new ImageViewSliderFragment();
                    imageViewSliderFragment.images = locPhotos;
                    imageViewSliderFragment.pageIndex = position;
                    ((CenesBaseActivity)gatheringPreviewFragment.getActivity()).replaceFragment(imageViewSliderFragment, GatheringPreviewFragment.TAG);
                }
            });
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
