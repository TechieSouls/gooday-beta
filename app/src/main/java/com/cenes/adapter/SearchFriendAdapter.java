package com.cenes.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.R;
import com.cenes.activity.SearchFriendActivity;
import com.cenes.util.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mandeep on 11/9/17.
 */

public class SearchFriendAdapter extends BaseAdapter {

    private SearchFriendActivity myActivity;
    private JSONArray friends;
    LayoutInflater inflter;

    public SearchFriendAdapter(SearchFriendActivity context, JSONArray friends) {
        this.myActivity = context;
        this.friends = friends;
        this.inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return friends.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return this.friends.getJSONObject(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    FriendViewHolder holder;

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            holder = new FriendViewHolder();
            view = this.inflter.inflate(R.layout.adapter_search_friends, null);
            holder.inviteFriendItem = (LinearLayout) view.findViewById(R.id.invite_friend_item);
            holder.inviteFriendName = (TextView) view.findViewById(R.id.invite_friend_name);
            holder.inviteFriendPicture = (RoundedImageView) view.findViewById(R.id.invite_friend_picture);
            holder.cenesMemberIcon = (TextView) view.findViewById(R.id.iv_cenes_member_icon);
            holder.inviteFriendNameCenesUserText = (TextView) view.findViewById(R.id.invite_friend_name_cenes_user_text);            view.setTag(holder);
        } else {
            holder = (SearchFriendAdapter.FriendViewHolder) view.getTag();
        }

        try {
            holder.inviteFriendName.setText(friends.getJSONObject(position).getString("name"));
           /* if (friends.getJSONObject(position).getString("photo") != null && friends.getJSONObject(position).getString("photo") != "null") {
                Glide.with(myActivity).load(friends.getJSONObject(position).getString("photo")).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.inviteFriendPicture);
            } else {
                holder.inviteFriendPicture.setImageResource(R.drawable.default_profile_icon);
            }*/
            String photo = null;
            if (friends.getJSONObject(position).getString("user") != "null") {
                photo = friends.getJSONObject(position).getJSONObject("user").getString("photo");
            }

            //if (friends.getJSONObject(position).getString("photo") != null && friends.getJSONObject(position).getString("photo") != "null") {
            if (photo != null) {
                Glide.with(myActivity).load(photo).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.inviteFriendPicture);
            } else {
                holder.inviteFriendPicture.setImageResource(R.drawable.default_profile_icon);
            }
            System.out.println("cenesMember : "+ friends.getJSONObject(position).getString("cenesMember"));
            if ("yes".equals(friends.getJSONObject(position).getString("cenesMember"))) {
                holder.inviteFriendNameCenesUserText.setVisibility(View.VISIBLE);
                holder.cenesMemberIcon.setVisibility(View.GONE);
            } else {
                holder.inviteFriendNameCenesUserText.setVisibility(View.GONE);
                holder.cenesMemberIcon.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.inviteFriendItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(view);
                Intent intent = new Intent();
                try {
                    JSONObject friendObj = (JSONObject) friends.get(position);
                    if ("no".equals(friendObj.getString("cenesMember"))) {
                        String inviteUserText = myActivity.getString(R.string.invite_user_text);
                        //SmsManager.getDefault().sendTextMessage(friendObj.getString("phone"), null,inviteUserText , null,null);
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.setData(Uri.parse("sms:"+friendObj.getString("phone")));
                            sendIntent.putExtra("sms_body", inviteUserText);
                        //myActivity.setResult(Activity.RESULT_OK, sendIntent);
                        //myActivity.finish();
                        myActivity.startActivity(sendIntent);
                    } else {
                        String photo = friendObj.getJSONObject("user").getString("photo");
                        intent.putExtra("userId", friendObj.get("friendId").toString());
                        intent.putExtra("photo", photo);
                        intent.putExtra("name", friendObj.getString("name"));
                        myActivity.setResult(Activity.RESULT_OK, intent);
                        myActivity.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        return view;
    }

    class FriendViewHolder {
        private LinearLayout inviteFriendItem;
        private TextView inviteFriendName,inviteFriendNameCenesUserText,cenesMemberIcon;
        private RoundedImageView inviteFriendPicture;
    }

    public void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) myActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
