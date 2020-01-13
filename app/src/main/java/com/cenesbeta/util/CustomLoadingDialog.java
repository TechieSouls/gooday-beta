package com.cenesbeta.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.cenesbeta.R;

public class CustomLoadingDialog {
    Activity activity;
    Dialog dialog;
    //..we need the context else we can not create the dialog so get context in constructor
    public CustomLoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {

        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false);
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.custom_loader);

        //...initialize the imageView form infalted layout
        ImageView gifImageView = dialog.findViewById(R.id.custom_loading_imageView);

        /*
        it was never easy to load gif into an ImageView before Glide or Others library
        and for doing this we need DrawableImageViewTarget to that ImageView
        */
        DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(gifImageView);

        //...now load that gif which we put inside the drawble folder here with the help of Glide

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.placeholder(R.drawable.ios_spinner);
        Glide.with(activity.getApplicationContext()).load(R.drawable.ios_spinner).apply(options).into(imageViewTarget);

        //GlideApp.with(context).load("http://via.placeholder.com/300.png").placeholder(R.drawable.placeholder).error(R.drawable.imagenotfound).into(ivImg);

        //...finaly show it
        dialog.show();
    }

    //..also create a method which will hide the dialog when some work is done
    public void hideDialog(){
        dialog.dismiss();
    }
}
