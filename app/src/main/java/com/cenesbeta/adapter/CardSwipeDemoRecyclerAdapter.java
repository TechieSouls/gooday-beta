package com.cenesbeta.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.fragment.CardSwipeDemoFragment;
import com.cenesbeta.util.RoundedImageView;

public class CardSwipeDemoRecyclerAdapter extends RecyclerView.Adapter<CardSwipeDemoRecyclerAdapter.MyViewHolder> {

    CardSwipeDemoFragment cardSwipeDemoFragment;
    public CardSwipeDemoRecyclerAdapter(CardSwipeDemoFragment cardSwipeDemoFragment) {
        this.cardSwipeDemoFragment = cardSwipeDemoFragment;
    }


    @NonNull
    @Override
    public CardSwipeDemoRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(cardSwipeDemoFragment.getContext()).inflate(R.layout.adapter_cardswipedemo_list_recycler_item, viewGroup, false);
        return new CardSwipeDemoRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardSwipeDemoRecyclerAdapter.MyViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImg1, ivImg2, ivImg3;

        public MyViewHolder(View view) {
            super(view);

            ivImg1 = (ImageView) view.findViewById(R.id.iv_img1);
            ivImg2 = (ImageView) view.findViewById(R.id.iv_img2);
            ivImg3 = (ImageView) view.findViewById(R.id.iv_img3);

        }
    }
}