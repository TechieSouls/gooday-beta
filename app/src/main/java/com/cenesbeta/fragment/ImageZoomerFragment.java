package com.cenesbeta.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.zoom.image.PhotoView;

public class ImageZoomerFragment extends CenesFragment {

    public String imageUrl;
    private PhotoView photoViewZoomer;
    private ImageView ivBackButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_image_zoomer, container, false);

        ivBackButton = (ImageView) view.findViewById(R.id.iv_back_button);
        photoViewZoomer = (PhotoView)view.findViewById(R.id.photo_view_zoomer);

        if (CenesUtils.isEmpty(imageUrl)) {
            photoViewZoomer.setImageResource(R.drawable.profile_pic_no_image);
        } else {
            Glide.with(getContext()).load(imageUrl).into(photoViewZoomer);
        }

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CenesBaseActivity)getActivity()).hideFooter();
    }
}
