package com.cenesbeta.countrypicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.cenesbeta.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mandeep on 16/9/17.
 */

public class CountryPicker extends DialogFragment {

    private EditText searchEditText;
    private ListView countryListView;
    private CountryListAdapter adapter;
    private List<String> countriesList = new ArrayList<>();
    private List<String> selectedCountriesList = new ArrayList<>();
    private CountryPickerListener listener;
    private Context context;


    /**
     * To support show as dialog
     */
    public static CountryPicker newInstance(String dialogTitle) {
        CountryPicker picker = new CountryPicker();
        Bundle bundle = new Bundle();
        bundle.putString("dialogTitle", dialogTitle);
        picker.setArguments(bundle);
        return picker;
    }

    public CountryPicker() {
        setCountriesList(CountryUtils.getCalendarCountries());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.country_picker, null);
        Bundle args = getArguments();
        if (args != null) {
            String dialogTitle = args.getString("dialogTitle");
            getDialog().setTitle(dialogTitle);

            int width = 200;
            int height = 300;
            getDialog().getWindow().setLayout(width, height);
        }
        searchEditText = (EditText) view.findViewById(R.id.country_code_picker_search);
        countryListView = (ListView) view.findViewById(R.id.country_code_picker_listview);

        selectedCountriesList = new ArrayList<>(countriesList.size());
        selectedCountriesList.addAll(countriesList);

        adapter = new CountryListAdapter(getActivity(), selectedCountriesList,CountryUtils.getCountryCalendarIdMap(),CountryUtils.getCountryCodeMap());
        countryListView.setAdapter(adapter);

        countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    String country = selectedCountriesList.get(position);
                    listener.onSelectCountry(country, CountryUtils.getCountryCodeMap().get(country), CountryUtils.getCountryCalendarIdMap().get(country));
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        return view;
    }

    public void setListener(CountryPickerListener listener) {
        this.listener = listener;
    }

    @SuppressLint("DefaultLocale")
    private void search(String text) {
        selectedCountriesList.clear();
        for (String country : countriesList) {
            if (country.toLowerCase().contains(text.toLowerCase())) {
                selectedCountriesList.add(country);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void setCountriesList(List<String> newCountries) {
        this.countriesList.clear();
        this.countriesList.addAll(newCountries);
    }
}
