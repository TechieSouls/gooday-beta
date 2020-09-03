package com.cenesbeta.fragment.gathering;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.EventCategoriesAdapter;
import com.cenesbeta.bo.EventCategory;
import com.cenesbeta.fragment.CenesFragment;

import java.util.List;

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

        ((CenesBaseActivity)getActivity()).hideFooter();
        lvEventCategories = (ListView) view.findViewById(R.id.lv_event_categories);
        List<EventCategory> eventCategories = ((CenesBaseActivity)getActivity()).eventCategories;
        eventCategoriesAdapter = new EventCategoriesAdapter(this, eventCategories);
        lvEventCategories.setAdapter(eventCategoriesAdapter);
        return view;
    }
}
