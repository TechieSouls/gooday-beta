package com.cenesbeta.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.bo.CountryCode;
import com.cenesbeta.fragment.guest.SignupCountryListFragment;

import java.util.List;

public class CountryListItemAdapter extends BaseAdapter {

    private SignupCountryListFragment signupCountryListFragment;
    private List<CountryCode> countries;
    private LayoutInflater inflter;

    public CountryListItemAdapter(SignupCountryListFragment signupCountryListFragment, List<CountryCode> countries) {
        this.signupCountryListFragment = signupCountryListFragment;
        this.countries = countries;
        this.inflter = (LayoutInflater.from(this.signupCountryListFragment.getContext()));
    }
    @Override
    public int getCount() {
        return this.countries.size();
    }

    @Override
    public CountryCode getItem(int position) {
        return this.countries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = this.inflter.inflate(R.layout.adapter_country_list_item, null);

            viewHolder.llCountryListItemBar = (LinearLayout) convertView.findViewById(R.id.ll_country_list_item_bar);
            viewHolder.tvCountryPhoneCode = (TextView) convertView.findViewById(R.id.tv_country_phone_code);
            viewHolder.tvCountryName = (TextView) convertView.findViewById(R.id.tv_country_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final CountryCode countryCode = getItem(position);
        viewHolder.tvCountryPhoneCode.setText(countryCode.getPhoneCode());
        viewHolder.tvCountryName.setText(countryCode.getName());
        viewHolder.llCountryListItemBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupCountryListFragment.getTargetFragment().onActivityResult(
                        signupCountryListFragment.getTargetRequestCode(),
                        Activity.RESULT_OK,
                        new Intent().putExtra("countryCode", countryCode.getPhoneCode()));
                signupCountryListFragment.getFragmentManager().popBackStack();

            }
        });
        return convertView;
    }

    class ViewHolder {

        private LinearLayout llCountryListItemBar;
        private TextView tvCountryPhoneCode, tvCountryName;
    }
}
