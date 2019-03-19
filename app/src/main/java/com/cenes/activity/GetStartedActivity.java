package com.cenes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.view.View;
import android.widget.Button;


import com.cenes.R;
import com.cenes.fragment.GetStartedFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mandeep on 17/8/17.
 */

public class GetStartedActivity extends FragmentActivity {

    Button getStartedAtBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getstarted);
        getStartedAtBottom = (Button) findViewById(R.id.gs_button);
        List<Fragment> fragments = getFragments();

        getStartedAtBottom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),GuestActivity.class);
                startActivity(i);
            }
        });

    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        //fList.add(GetStartedFragment.newInstance(R.drawable.onboarding_4, R.string.gs_heading, R.string.gs_ins,R.string.get_started));
        return fList;
    }

}