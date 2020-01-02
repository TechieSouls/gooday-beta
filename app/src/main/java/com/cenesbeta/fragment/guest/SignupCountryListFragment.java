package com.cenesbeta.fragment.guest;

import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cenesbeta.R;
import com.cenesbeta.adapter.CountryCodeAdapter;
import com.cenesbeta.adapter.CountryListItemAdapter;
import com.cenesbeta.bo.CountryCode;
import com.cenesbeta.fragment.CenesFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 20/9/18.
 */

public class SignupCountryListFragment  extends CenesFragment {

    public final static String TAG = "SignupCountryListFragment";

    private ExpandableListView exCountryCodeList;
    private CountryCodeAdapter expandableListAdapter;
    private CountryListItemAdapter countryListItemAdapter;
    private EditText etSearchCc;
    private ListView lvCountries;
    private ImageView ivBackButtonImg;

    private List<CountryCode> countries;
    private List<String> headers = null;
    private Map<String, List<CountryCode>> countryListItem = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup_country_list, container, false);

        exCountryCodeList = (ExpandableListView) v.findViewById(R.id.ex_country_code_list);
        etSearchCc = (EditText) v.findViewById(R.id.et_search_cc);
        lvCountries = (ListView) v.findViewById(R.id.lv_countries);
        ivBackButtonImg = (ImageView) v.findViewById(R.id.iv_back_button_img);

        //prepareListData(null);
        onSearch(null);
        ivBackButtonImg.setOnClickListener(onClickListener);
        etSearchCc.addTextChangedListener(searchTextWatcher);

        countryListItemAdapter = new CountryListItemAdapter(SignupCountryListFragment.this, countries);
        lvCountries.setAdapter(countryListItemAdapter);
        //expandableListAdapter = new CountryCodeAdapter(SignupCountryListFragment.this, this.headers, this.countryListItem);
        //exCountryCodeList.setAdapter(expandableListAdapter);

        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_back_button_img:
                    getActivity().onBackPressed();
                    break;

            }
        }
    };

    TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            onSearch(editable.toString());
            countryListItemAdapter = new CountryListItemAdapter(SignupCountryListFragment.this, countries);
            lvCountries.setAdapter(countryListItemAdapter);
            //expandableListAdapter = new CountryCodeAdapter(SignupCountryListFragment.this, headers, countryListItem);
            //exCountryCodeList.setAdapter(expandableListAdapter);
        }
    };

    private void onSearch(String searchText) {

        if (searchText != null && searchText.length() > 0) {
            countries = new ArrayList<>();
            List<CountryCode> countriesTemp = CountryCode.getLibraryMasterCountriesEnglish();
            for (CountryCode countryCode : countriesTemp) {
                if (countryCode.getName().toLowerCase().startsWith(searchText.toLowerCase())) {
                    countries.add(countryCode);
                }
            }

        } else {
            countries = CountryCode.getLibraryMasterCountriesEnglish();
        }

    }


    private void prepareListData(String searchText) {
        headers = new ArrayList<>();
        countryListItem = new HashMap<>();

        List<CountryCode> tempCodeList = null;
        if (searchText == null || searchText.length() == 0) {
            tempCodeList = CountryCode.getLibraryMasterCountriesEnglish();
        } else {
            List<CountryCode> filteredCountryList = new ArrayList<>();
            for (CountryCode countryCode : CountryCode.getLibraryMasterCountriesEnglish()) {
                if (countryCode.getName().toLowerCase().startsWith(searchText.toLowerCase())) {
                    filteredCountryList.add(countryCode);
                }
            }
            tempCodeList = filteredCountryList;
        }

        for (CountryCode countryCode : tempCodeList) {

            if (!headers.contains(String.valueOf(countryCode.getName().charAt(0)).toUpperCase())) {
                headers.add(String.valueOf(countryCode.getName().charAt(0)).toUpperCase());
            }
        }
        Collections.sort(headers);
        for (CountryCode countryCode : tempCodeList) {
            String headerKey = String.valueOf(countryCode.getName().charAt(0)).toUpperCase();
            List<CountryCode> tempCountryItemList = null;
            if (countryListItem.containsKey(headerKey)) {
                tempCountryItemList = countryListItem.get(headerKey);
            } else {
                tempCountryItemList = new ArrayList<>();
            }
            tempCountryItemList.add(countryCode);
            Collections.sort(tempCountryItemList);
            countryListItem.put(headerKey, tempCountryItemList);
        }
    }

}
