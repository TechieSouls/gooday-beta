package com.cenesbeta.fragment.guest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.cenesbeta.R;
import com.cenesbeta.adapter.CountryCodeAdapter;
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
    private LinearLayout llSignupCcBack;
    private EditText etSearchCc;

    private List<String> headers = null;
    private Map<String, List<CountryCode>> countryListItem = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup_country_list, container, false);

        exCountryCodeList = (ExpandableListView) v.findViewById(R.id.ex_country_code_list);
        llSignupCcBack = (LinearLayout) v.findViewById(R.id.ll_signup_cc_back);
        etSearchCc = (EditText) v.findViewById(R.id.et_search_cc);

        prepareListData(null);

        llSignupCcBack.setOnClickListener(onClickListener);
        etSearchCc.addTextChangedListener(searchTextWatcher);

        expandableListAdapter = new CountryCodeAdapter(SignupCountryListFragment.this, this.headers, this.countryListItem);
        exCountryCodeList.setAdapter(expandableListAdapter);

        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_signup_cc_back:
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
            prepareListData(editable.toString());
            expandableListAdapter = new CountryCodeAdapter(SignupCountryListFragment.this, headers, countryListItem);
            exCountryCodeList.setAdapter(expandableListAdapter);
        }
    };

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
