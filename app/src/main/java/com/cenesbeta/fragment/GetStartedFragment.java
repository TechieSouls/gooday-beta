package com.cenesbeta.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cenesbeta.R;

/**
 * Created by mandeep on 17/8/17.
 */

public class GetStartedFragment extends Fragment {

    public static final String IMAGE_ID = "IMAGE_ID";
    public static final String INSTRUCTION_ID = "INSTRUCTION_ID";
    public static final String TITLE_ID = "TITLE_ID";
    public static final String BUTTON_ID = "BUTTON_ID";

    public static final GetStartedFragment newInstance(Integer resourceId) {
        GetStartedFragment getStartedFragment = new GetStartedFragment();
        Bundle b = new Bundle(1);
        b.putInt(IMAGE_ID, resourceId);
        getStartedFragment.setArguments(b);
        return getStartedFragment;
    }

    public static final GetStartedFragment newInstance(Integer imageId, Integer titleId, Integer instructionId,Integer buttonId) {
        GetStartedFragment getStartedFragment = new GetStartedFragment();
        Bundle b = new Bundle(1);
        b.putInt(IMAGE_ID, imageId);
        b.putInt(TITLE_ID, titleId);
        b.putInt(INSTRUCTION_ID, instructionId);
        b.putInt(BUTTON_ID, instructionId);
        getStartedFragment.setArguments(b);
        return getStartedFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            Integer imageId = getArguments().getInt(IMAGE_ID);
            Integer titleId = getArguments().getInt(TITLE_ID);
            Integer instructionId = getArguments().getInt(INSTRUCTION_ID);
            Integer buttonId = getArguments().getInt(BUTTON_ID);

            View v = inflater.inflate(R.layout.getstarted, container, false);
            ImageView getStartedImage = (ImageView) v.findViewById(R.id.gs_image);
            TextView getStartedTitle = (TextView) v.findViewById(R.id.gs_heading);
            TextView getStartedInstructionsText = (TextView) v.findViewById(R.id.gs_instructions);
            Button getStartedButton = (Button) v.findViewById(R.id.gs_button);

            getStartedImage.setImageResource(imageId);
            getStartedTitle.setText(titleId);
            getStartedInstructionsText.setText(instructionId);
            getStartedButton.setText(buttonId);
            return v;
        } else {
            return null;
        }
    }
}