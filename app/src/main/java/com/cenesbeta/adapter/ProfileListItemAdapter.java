package com.cenesbeta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.dto.ProfileItemDto;
import com.cenesbeta.fragment.AboutUsFragment;
import com.cenesbeta.fragment.profile.DeleteAccountFragment;
import com.cenesbeta.fragment.profile.ProfileMyCalendarsFragment;
import com.cenesbeta.fragment.profile.PersonalDetailsFragment;
import com.cenesbeta.fragment.profile.ProfileFragmentV2;

import java.util.List;

public class ProfileListItemAdapter extends BaseAdapter {

    private ProfileFragmentV2 profileFragmentV2;
    private List<ProfileItemDto> profileItemDto;
    private LayoutInflater inflter;

    public ProfileListItemAdapter(ProfileFragmentV2 profileFragmentV2, List<ProfileItemDto> profileItemDto) {
        this.profileFragmentV2 = profileFragmentV2;
        this.profileItemDto = profileItemDto;
        this.inflter = (LayoutInflater.from(profileFragmentV2.getContext()));
    }

    @Override
    public int getCount() {
        return profileItemDto.size();
    }

    @Override
    public ProfileItemDto getItem(int position) {
        return profileItemDto.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflter.inflate(R.layout.adapter_profile_list_item, null);

            holder.llProfileItemBar = convertView.findViewById(R.id.ll_profile_item_bar);
            holder.ivItemImage = convertView.findViewById(R.id.iv_item_image);
            holder.tvProfileTitile = convertView.findViewById(R.id.tv_item_title);
            holder.tvProfileDetail = convertView.findViewById(R.id.tv_item_detail);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        try {
            final ProfileItemDto profileItemDto = getItem(position);
            if (holder != null) {

                if (position == 0) {
                    holder.ivItemImage.setImageResource(R.drawable.personal_details_icon);

                }
                if (position == 1) {
                    holder.ivItemImage.setImageResource(R.drawable.my_calendar_icon);

                }
                if (position == 2) {
                    holder.ivItemImage.setImageResource(R.drawable.profile_app_settings_icon);

                }
                if (position == 3) {
                    holder.ivItemImage.setImageResource(R.drawable.need_help_icon);

                }
                if (position == 4) {
                    holder.ivItemImage.setImageResource(R.drawable.version_update_icon);
                }

                holder.tvProfileTitile.setText(profileItemDto.getTitle());
                holder.tvProfileDetail.setText(profileItemDto.getDescription());

                holder.llProfileItemBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (profileItemDto.getTAG().equals(ProfileItemDto.ProfileItemDtoEnum.Profile)) {

                            ((CenesBaseActivity)profileFragmentV2.getActivity()).replaceFragment(new PersonalDetailsFragment(), ProfileFragmentV2.TAG);

                        }
                        if (profileItemDto.getTAG().equals(ProfileItemDto.ProfileItemDtoEnum.Calendars)) {

                            ((CenesBaseActivity)profileFragmentV2.getActivity()).replaceFragment(new ProfileMyCalendarsFragment(), ProfileFragmentV2.TAG);

                        }
                        if (profileItemDto.getTAG().equals(ProfileItemDto.ProfileItemDtoEnum.Settings)) {

                            ((CenesBaseActivity)profileFragmentV2.getActivity()).replaceFragment(new DeleteAccountFragment(), ProfileFragmentV2.TAG);

                        }
                        if (profileItemDto.getTAG().equals(ProfileItemDto.ProfileItemDtoEnum.Help)) {

                            profileFragmentV2.rlHelpAndFeedbackActionSheet.setVisibility(View.VISIBLE);
                            ((CenesBaseActivity)profileFragmentV2.getActivity()).hideFooter();
                        }
                        if (profileItemDto.getTAG().equals(ProfileItemDto.ProfileItemDtoEnum.About)) {

                            ((CenesBaseActivity)profileFragmentV2.getActivity()).replaceFragment(new AboutUsFragment(), ProfileFragmentV2.TAG);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
    }

    class ViewHolder {

        private LinearLayout llProfileItemBar;
        private ImageView ivItemImage;
        private TextView tvProfileTitile, tvProfileDetail;
    }
}
