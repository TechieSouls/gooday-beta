package com.cenesbeta.fragment.gathering;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cenesbeta.R;
import com.cenesbeta.adapter.EventCategoriesAdapter;
import com.cenesbeta.fragment.CenesFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EventCategoryFragment extends CenesFragment {

    private ListView lvEventCategories;

    private EventCategoriesAdapter eventCategoriesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_event_categories, container, false);

        return view;
    }
}
