package com.cenesbeta.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cenesbeta.R;

import androidx.fragment.app.Fragment;

/**
 * Created by rohan on 9/8/17.
 */
public class WalkthroughFragment extends Fragment {

    public static final String IMAGE_ID = "IMAGE_ID";
    public static final String INSTRUCTION_ID = "INSTRUCTION_ID";
    public static final String TITLE_ID = "TITLE_ID";
    public static final String BUTTON_VISIBILITY = "BUTTON_VISIBILITY";

    /*public static final WalkthroughFragment newInstance(Integer resourceId) {
        WalkthroughFragment walkthroughFragment = new WalkthroughFragment();
        Bundle b = new Bundle(1);
        b.putInt(IMAGE_ID, resourceId);
        walkthroughFragment.setArguments(b);
        return walkthroughFragment;
    }*/

    public static final WalkthroughFragment newInstance(Integer imageId, Integer titleId, Integer instructionId, boolean buttonVisibilty) {
        WalkthroughFragment walkthroughFragment = new WalkthroughFragment();
        Bundle b = new Bundle();
        b.putInt(IMAGE_ID, imageId);
        b.putInt(TITLE_ID, titleId);
        b.putInt(INSTRUCTION_ID, instructionId);
        b.putBoolean(BUTTON_VISIBILITY, buttonVisibilty);
        walkthroughFragment.setArguments(b);
        return walkthroughFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            Integer imageId = getArguments().getInt(IMAGE_ID);
            Integer titleId = getArguments().getInt(TITLE_ID);
            Integer instructionId = getArguments().getInt(INSTRUCTION_ID);
            boolean buttonVisibility = getArguments().getBoolean(BUTTON_VISIBILITY);

            View v = inflater.inflate(R.layout.walkthrough_vp, container, false);
            ImageView walkthroughImage = (ImageView) v.findViewById(R.id.iv);
            TextView walkthroughTitle = (TextView) v.findViewById(R.id.tv_title);
            TextView walkthroughText = (TextView) v.findViewById(R.id.tv_instructions);
            Button buttonGetStarted = (Button) v.findViewById(R.id.gs_button);
            walkthroughImage.setImageResource(imageId);
            walkthroughTitle.setText(titleId);
            walkthroughText.setText(instructionId);
            if (buttonVisibility) {
                buttonGetStarted.setVisibility(View.VISIBLE);
            } else {
                buttonGetStarted.setVisibility(View.INVISIBLE);
            }
            buttonGetStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startActivity(new Intent(getActivity(), ChoiceActivity.class));
                    getActivity().finish();
                }
            });
            return v;
        } else {
            return null;
        }
    }

}
