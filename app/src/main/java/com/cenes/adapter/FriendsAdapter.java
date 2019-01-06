package com.deploy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.R;
import com.deploy.activity.SearchFriendActivity;
import com.deploy.util.CenesUtils;
import com.deploy.util.RoundedImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 27/12/18.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {

    private List<Map<String, String>> jsonObjectArrayList;
    private SearchFriendActivity context;
    private RecyclerView recyclerView;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView ivFriend;
        TextView tvName;
        RelativeLayout container;
        ImageButton ibDelete;

        public MyViewHolder(View view) {
            super(view);
            ivFriend = (RoundedImageView) view.findViewById(R.id.iv_friend_image);
            tvName = (TextView) view.findViewById(R.id.tv_friend_name);
            container = (RelativeLayout) view.findViewById(R.id.container);
            ibDelete = (ImageButton) view.findViewById(R.id.ib_delete);
        }
    }

    public FriendsAdapter(SearchFriendActivity context, List<Map<String, String>> jsonObjectArrayList, RecyclerView recyclerView) {
        this.jsonObjectArrayList = jsonObjectArrayList;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public FriendsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gathering_friend_list_item, parent, false);
        return new FriendsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FriendsAdapter.MyViewHolder holder, final int position) {
        try {
            final Map<String, String> invFrn = jsonObjectArrayList.get(position);
            String userStr = invFrn.get("user");
            String photo = null;
            if (!CenesUtils.isEmpty(userStr)) {
                Map<String, String> userMap = CenesUtils.getMapFromJson(new JSONObject(userStr));
                photo = userMap.get("photo");
            }
            holder.tvName.setText(invFrn.get("name"));
            holder.ibDelete.setVisibility(View.VISIBLE);
            holder.ivFriend.setImageResource(R.drawable.default_profile_icon);
            if (photo != null ) {
                Glide.with(context).load(photo).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.ivFriend);
            } else {
                holder.ivFriend.setImageResource(R.drawable.default_profile_icon);
            }

            holder.ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Map<String, String> objectToRemove = jsonObjectArrayList.get(position);
                    context.checkboxStateHolder.remove(Integer.valueOf(objectToRemove.get("userContactId")));
                    context.checkboxObjectHolder.remove(Integer.valueOf(objectToRemove.get("userContactId")));

                    CheckBox checkBoxToUnSelect = context.checkboxButtonHolder.get(Integer.valueOf(objectToRemove.get("userContactId")));
                    checkBoxToUnSelect.setSelected(false);
                    jsonObjectArrayList.remove(position);
                    recyclerView.removeViewAt(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, jsonObjectArrayList.size());
                    context.getSearchFriendAdapter().notifyDataSetChanged();

                    if (context.checkboxObjectHolder.size() == 0) {
                        context.findViewById(R.id.rl_selected_friends_recycler_view).setVisibility(View.GONE);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonObjectArrayList.size();
    }
}
