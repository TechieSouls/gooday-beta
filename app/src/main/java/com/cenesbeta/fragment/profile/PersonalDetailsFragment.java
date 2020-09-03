package com.cenesbeta.fragment.profile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.api.UserAPI;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.util.CenesUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PersonalDetailsFragment extends CenesFragment {

    public static String TAG = "PersonalDetailsFragment";
    public enum GenderOption {Male, Female, Other, Cancel}

    private ImageView ivBackButtonImg;
    private TextView tvProfileName, tvPassword, tvProfileEmail, tvProfileNumber, tvProfileDOB, tvProfileGender;
    private TextView tvGenderMale, tvGenderFemale, tvGenderOther, tvGendeCancel;
    private RelativeLayout rlGenderActionSheet;

    private CoreManager coreManager;
    private UserManager userManager;
    private User loggedInUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_personal_details, container, false);

        ivBackButtonImg = (ImageView) view.findViewById(R.id.iv_back_button_img);
        tvProfileName = (TextView) view.findViewById(R.id.tv_profile_name);
        tvPassword = (TextView) view.findViewById(R.id.tv_password);
        tvProfileEmail = (TextView) view.findViewById(R.id.tv_profile_email);
        tvProfileNumber = (TextView) view.findViewById(R.id.tv_profile_number);
        tvProfileDOB = (TextView) view.findViewById(R.id.tv_profile_dob);
        tvProfileGender = (TextView) view.findViewById(R.id.tv_profile_gender);
        tvGenderMale = (TextView) view.findViewById(R.id.tv_gender_male);
        tvGenderFemale = (TextView) view.findViewById(R.id.tv_gender_female);
        tvGenderOther = (TextView) view.findViewById(R.id.tv_gender_other);
        tvGendeCancel = (TextView) view.findViewById(R.id.tv_gender_cancel);

        rlGenderActionSheet = (RelativeLayout) view.findViewById(R.id.rl_gender_action_sheet);

        coreManager = getCenesActivity().getCenesApplication().getCoreManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();

        //Set Click Listeners
        view.findViewById(R.id.rl_profile_name_bar).setOnClickListener(onClickListener);
        view.findViewById(R.id.rl_profile_password_bar).setOnClickListener(onClickListener);
        view.findViewById(R.id.rl_profile_dob_bar).setOnClickListener(onClickListener);
        view.findViewById(R.id.rl_profile_gender_bar).setOnClickListener(onClickListener);
        ivBackButtonImg.setOnClickListener(onClickListener);
        tvGenderMale.setOnClickListener(onClickListener);
        tvGenderFemale.setOnClickListener(onClickListener);
        tvGenderOther.setOnClickListener(onClickListener);
        tvGendeCancel.setOnClickListener(onClickListener);

        //Calling Functions
        ((CenesBaseActivity)getActivity()).hideFooter();
        new ProfileAsyncTask(getCenesActivity().getCenesApplication(), getCenesActivity());
        populatePersonalDetails();
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.iv_back_button_img:
                    if(getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                    break;

                case R.id.rl_profile_name_bar:

                    ProfileDetailsEditFragment profileDetailsEditFragment = new ProfileDetailsEditFragment();
                    profileDetailsEditFragment.loggedInUser = loggedInUser;
                    profileDetailsEditFragment.editRequest = ProfileDetailsEditFragment.ProfileDetailRequest.Name;
                    ((CenesBaseActivity)getActivity()).replaceFragment(profileDetailsEditFragment, PersonalDetailsFragment.TAG);
                    break;

                case R.id.rl_profile_password_bar:

                    ProfileDetailsEditFragment profileDetailsPasswordEditFragment = new ProfileDetailsEditFragment();
                    profileDetailsPasswordEditFragment.loggedInUser = loggedInUser;
                    profileDetailsPasswordEditFragment.editRequest = ProfileDetailsEditFragment.ProfileDetailRequest.Password;
                    ((CenesBaseActivity)getActivity()).replaceFragment(profileDetailsPasswordEditFragment, PersonalDetailsFragment.TAG);
                    break;

                case R.id.rl_profile_dob_bar:
                    birthDateBarPressed();
                    break;


                case R.id.rl_profile_gender_bar:
                    ((CenesBaseActivity)getActivity()).hideFooter();
                    genderBarPressed();
                    break;

                case R.id.tv_gender_male:
                    genderOptionPressed(GenderOption.Male);

                    break;
                case R.id.tv_gender_female:
                    genderOptionPressed(GenderOption.Female);

                    break;
                case R.id.tv_gender_other:
                    genderOptionPressed(GenderOption.Other);

                    break;
                case R.id.tv_gender_cancel:
                    genderOptionPressed(GenderOption.Cancel);

                    break;

            }
        }
    };

    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {

            System.out.println(dayOfMonth+", "+monthOfYear+", "+year);
            Calendar yesCalendar = Calendar.getInstance();
            yesCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            yesCalendar.set(Calendar.MONTH, monthOfYear);
            yesCalendar.set(Calendar.YEAR, year);

            String birthDateStr = CenesUtils.E_MMMM_d_yyyy.format(yesCalendar.getTime());
            loggedInUser.setBirthDayStr(birthDateStr);
            userManager.updateUser(loggedInUser);
            tvProfileDOB.setText(birthDateStr);

            //Making Call At API
            updateDateOfBirth();
        }
    };

    public void populatePersonalDetails() {

        try {
            tvProfileName.setText(loggedInUser.getName());
            tvProfileEmail.setText(loggedInUser.getEmail());
            tvProfileNumber.setText(loggedInUser.getPhone());
            if (!CenesUtils.isEmpty(loggedInUser.getBirthDayStr())) {
                tvProfileDOB.setText(loggedInUser.getBirthDayStr());
            }
            if (!CenesUtils.isEmpty(loggedInUser.getGender())) {
                tvProfileGender.setText(loggedInUser.getGender());
            }

            if (CenesUtils.isEmpty(loggedInUser.getPassword())) {
                tvPassword.setText("Set a Password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void birthDateBarPressed() {
        try {
            // TODO Auto-generated method stub
            Calendar eligibleCal = Calendar.getInstance();

            if (!CenesUtils.isEmpty(loggedInUser.getBirthDayStr())) {

                try {

                    Date birthDate = CenesUtils.E_MMMM_d_yyyy.parse(loggedInUser.getBirthDayStr());
                    eligibleCal.setTimeInMillis(birthDate.getTime());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                eligibleCal.add(Calendar.YEAR, -13);
            }

            DatePickerDialog birthDatePicker = new DatePickerDialog((CenesBaseActivity)getActivity(), datePickerListener, eligibleCal
                    .get(Calendar.YEAR), eligibleCal.get(Calendar.MONTH),
                    eligibleCal.get(Calendar.DAY_OF_MONTH));
            birthDatePicker.getDatePicker().setMaxDate(new Date().getTime());
            birthDatePicker.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void genderBarPressed() {
        rlGenderActionSheet.setVisibility(View.VISIBLE);
    }
    public void genderOptionPressed(GenderOption genderOption) {

        rlGenderActionSheet.setVisibility(View.GONE);
        ((CenesBaseActivity)getActivity()).hideFooter();

        if (!genderOption.equals(GenderOption.Cancel)) {
            tvProfileGender.setText(genderOption.toString());
            loggedInUser.setGender(genderOption.toString());
            userManager.updateUser(loggedInUser);

            //Making Call At API
            updateGender();
        }
    }
    public void updateGender() {

        try {
            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ UserAPI.post_userdetails);

            JSONObject postData = new JSONObject();
            postData.put("userId", loggedInUser.getUserId());
            postData.put("gender", loggedInUser.getGender());
            asyncTaskDto.setPostData(postData);

            performUpdateProfileItemsAsynctask(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDateOfBirth() {
        try {
            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ UserAPI.post_userdetails);

            JSONObject postData = new JSONObject();
            postData.put("userId", loggedInUser.getUserId());
            postData.put("birthDayStr", loggedInUser.getBirthDayStr());
            asyncTaskDto.setPostData(postData);

            performUpdateProfileItemsAsynctask(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performUpdateProfileItemsAsynctask(AsyncTaskDto asyncTaskDto) {

        new ProfileAsyncTask.CommonPostRequestTask(new ProfileAsyncTask.CommonPostRequestTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                try {
                    boolean success = response.getBoolean("success");
                    if (success == false) {
                        String message = response.getString("message");
                        JSONObject postData = new JSONObject();
                        postData.put("Action", "Update Gender");
                        postData.put("UserName", loggedInUser.getName());
                        postData.put("Email", loggedInUser.getEmail());
                        postData.put("Error", message);
                        CenesUtils.postOnMixPanel(getContext(), "PersonalDetails", postData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute(asyncTaskDto);
    }
}
