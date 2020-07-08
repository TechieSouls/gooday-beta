package com.cenesbeta.fragment.gathering;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cenesbeta.R;
import com.cenesbeta.fragment.CenesFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GatheringScrollPreviewFragment extends CenesFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_gathering_scroll_preview, container, false);


        return view;
    }
}
