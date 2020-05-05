package com.cenesbeta.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

/*public class ImageAdapter extends PagerAdapter {




/*
        private Fragment fragment;
        private List<PostImage> images;
        private LayoutInflater inflater;
        private Context context;
        private int imageCurrentPosition;

        public ImageAdapter(Fragment fragment, List<PostImage> images) {
            this.fragment = fragment;
            this.images = images;

            if(fragment instanceof HomeFragment){
                context = ((HomeFragment) fragment).getContext();
            } else if(fragment instanceof ArchieveFragment){
                context = ((ArchieveFragment) fragment).getContext();
            }

            inflater = LayoutInflater.from(fragment.getContext());
        }

        @Override
        public int getCount() {
            try {
                return this.images.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            try {
                return view.equals(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            // System.out.println("instantiateItem position" + position);

            View imageLayout = inflater.inflate(R.layout.image_slider_layout, container, false);

            try {
                assert imageLayout != null;
                final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);

                Glide.with(fragment).load(this.images.get(position).getImage()).into(imageView);
                //System.out.println("position : "+position);
                imageCurrentPosition = position;
                container.addView(imageLayout, 0);
                //imageView.setOnTouchListener(touchListener);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        View.OnTouchListener touchListener = new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // System.out.println("eeee");
                // Log.d("TEST Touch", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                try {
                    gestureDetector.onTouchEvent(event);

                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;

            }

            private GestureDetector gestureDetector  = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    //  System.out.println("5646456");
                    // Log.d("TEST Detector", "onDoubleTap");
                    try{
                        ImageViewerFragment imageViewerFragment = new ImageViewerFragment();
                        imageViewerFragment.images = images;
                        imageViewerFragment.imageCurrentPosition = imageCurrentPosition;
                        if (fragment instanceof HomeFragment) {
                            ((HomeFragment) fragment).addToBackStag(imageViewerFragment, HomeFragment.TAG);
                        } else if (fragment instanceof ArchieveFragment) {
                            ((ArchieveFragment) fragment).addToBackStag(imageViewerFragment, ArchieveFragment.TAG);
                        }
                    }catch (Exception ee){
                        ee.printStackTrace();
                    }
                    return super.onDoubleTap(e);
                }  // implement here other callback methods like onFling, onScroll as necessary
            });

        };

    } */


