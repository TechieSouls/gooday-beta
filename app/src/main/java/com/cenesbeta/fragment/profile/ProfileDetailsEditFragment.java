package com.cenesbeta.fragment.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProfileDetailsEditFragment extends CenesFragment {

    public static String TAG = "ProfileDetailsEditFragment";
    public enum ProfileDetailRequest{Name, Password};

    private Button btnCancel, btnSave;
    private TextView tvProfileEditTitle;
    private EditText etProfileName, etProfileCurrentPassword, etProfileChoosePassword, etProfileConfirmPassword;
    private RelativeLayout rlEditNameView;
    private LinearLayout rlEditPasswordView, llCurrentPasswordView;

    private CoreManager coreManager;
    private UserManager userManager;
    public ProfileDetailRequest editRequest;
    public User loggedInUser;
    private boolean isCurrentPasswordCorrect = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_profile_details_edit, container, false);

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnSave = (Button) view.findViewById(R.id.btn_save);
        tvProfileEditTitle = (TextView) view.findViewById(R.id.tv_profile_edit_title);

        etProfileName = (EditText) view.findViewById(R.id.et_profile_name);
        etProfileChoosePassword = (EditText) view.findViewById(R.id.et_profile_choose_password);
        etProfileConfirmPassword = (EditText) view.findViewById(R.id.et_profile_confirm_password);
        etProfileCurrentPassword = (EditText) view.findViewById(R.id.et_profile_current_password);


        rlEditNameView = (RelativeLayout) view.findViewById(R.id.rl_edit_name_view);

        rlEditPasswordView = (LinearLayout) view.findViewById(R.id.rl_edit_password_view);
        llCurrentPasswordView = (LinearLayout) view.findViewById(R.id.ll_current_password_view);

        btnCancel.setOnClickListener(onClickListener);
        btnSave.setOnClickListener(onClickListener);

        etProfileName.addTextChangedListener(onKeyTypeListener);
        etProfileCurrentPassword.addTextChangedListener(onKeyCurrentPasswordTypeListener);
        etProfileChoosePassword.addTextChangedListener(onKeyChoosePasswordTypeListener);
        etProfileConfirmPassword.addTextChangedListener(onKeyConfirmPasswordTypeListener);

        coreManager = getCenesActivity().getCenesApplication().getCoreManager();
        userManager = coreManager.getUserManager();

        //Calling Functions
        populateScreenData();
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.btn_cancel:
                    getActivity().onBackPressed();
                    break;
                case R.id.btn_save:

                    if (editRequest.equals(ProfileDetailRequest.Name)) {
                        prepareDataForNameUpdate();
                    } else if (editRequest.equals(ProfileDetailRequest.Password)) {
                        prepareDataForPasswordUpdate();
                    }
                    break;
            }
        }
    };

    TextWatcher onKeyTypeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.toString().length() == 0) {
                btnSave.setVisibility(View.GONE);
            } else {
                btnSave.setVisibility(View.VISIBLE);
            }
        }
    };

    TextWatcher onKeyCurrentPasswordTypeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.toString().length() == 0) {
                btnSave.setVisibility(View.GONE);
            } else {
                btnSave.setVisibility(View.VISIBLE);
            }
        }
    };
    TextWatcher onKeyChoosePasswordTypeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.toString().length() == 0) {
                btnSave.setVisibility(View.GONE);
            } else {
                btnSave.setVisibility(View.VISIBLE);
            }
        }
    };

    TextWatcher onKeyConfirmPasswordTypeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.toString().length() == 0) {
                btnSave.setVisibility(View.GONE);
            } else {
                btnSave.setVisibility(View.VISIBLE);
            }
        }
    };

    public void populateScreenData() {

        if (editRequest.equals(ProfileDetailRequest.Name)) {
            etProfileName.setText(loggedInUser.getName());
            etProfileName.requestFocus();
            tvProfileEditTitle.setText("Username");
            rlEditNameView.setVisibility(View.VISIBLE);
            rlEditPasswordView.setVisibility(View.GONE);
        } else {
            if (CenesUtils.isEmpty(loggedInUser.getPassword())) {
                llCurrentPasswordView.setVisibility(View.GONE);
            } else {
                llCurrentPasswordView.setVisibility(View.VISIBLE);
            }
            tvProfileEditTitle.setText("Password");
            rlEditNameView.setVisibility(View.GONE);
            rlEditPasswordView.setVisibility(View.VISIBLE);
        }
    }

    public void prepareDataForNameUpdate() {

        if (etProfileName.getText().toString().length() == 0) {
            showAlert("Alert", "Name cannot be left empty");
        } else {
            loggedInUser.setName(etProfileName.getText().toString());
            userManager.updateUser(loggedInUser);
            try {
                AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
                asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
                asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ UserAPI.post_userdetails);

                JSONObject postData = new JSONObject();
                postData.put("userId", loggedInUser.getUserId());
                postData.put("username", loggedInUser.getName());
                asyncTaskDto.setPostData(postData);

                performUpdateProfileItemsAsynctask(asyncTaskDto);
            } catch (Exception e) {
                e.printStackTrace();
            }
            getActivity().onBackPressed();
        }
    }

    public void prepareDataForPasswordUpdate() {

        if (!etProfileChoosePassword.getText().toString().equals(etProfileConfirmPassword.getText().toString())) {
            showAlert("", "Passwords Do not Match");
        } else if (etProfileChoosePassword.getText().toString().length() < 8 || etProfileChoosePassword.getText().toString().length() > 16) {
            showAlert("", "Check Password Requirements");
        } else {

            if (llCurrentPasswordView.getVisibility() == View.VISIBLE && isCurrentPasswordCorrect == false) {
                checkIfCurrentPasswordIsCorrect();
            } else {
                loggedInUser.setPassword(etProfileChoosePassword.getText().toString());
                userManager.updateUser(loggedInUser);
                try {
                    AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
                    asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
                    asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ UserAPI.post_userdetails);

                    JSONObject postData = new JSONObject();
                    postData.put("userId", loggedInUser.getUserId());
                    postData.put("newPassword", loggedInUser.getPassword());
                    asyncTaskDto.setPostData(postData);

                    performUpdateProfileItemsAsynctask(asyncTaskDto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void checkIfCurrentPasswordIsCorrect() {

        if (etProfileCurrentPassword.getText().toString().length() == 0) {
            showAlert("", "Current Password cannot be empty");
        } else {
            if (isCurrentPasswordCorrect == false) {
                try {
                    AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
                    asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
                    asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ UserAPI.post_validate_password);

                    JSONObject postData = new JSONObject();
                    postData.put("userId", loggedInUser.getUserId());
                    postData.put("currentPassword", etProfileCurrentPassword.getText().toString());
                    asyncTaskDto.setPostData(postData);

                    performUpdateProfileItemsAsynctask(asyncTaskDto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                prepareDataForPasswordUpdate();
            }
        }
    }

    public void performUpdateProfileItemsAsynctask(AsyncTaskDto asyncTaskDto) {

        new ProfileAsyncTask.CommonPostRequestTask(new ProfileAsyncTask.CommonPostRequestTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                try {
                    boolean success = response.getBoolean("success");
                    if (success == false) {
                        String message = "";
                        if (response.has("message")) {
                            message = response.getString("message");
                        }
                        JSONObject postData = new JSONObject();

                        if (editRequest.equals(ProfileDetailRequest.Name)) {
                            postData.put("Action", "Update Name");

                        } else {
                            if (isCurrentPasswordCorrect == false && llCurrentPasswordView.getVisibility() == View.VISIBLE) {
                                postData.put("Action", "Validate Current Password");
                                showAlert("", "Wrong Password");
                            } else {
                                postData.put("Action", "Update Password");

                            }
                        }
                        postData.put("UserName", loggedInUser.getName());
                        postData.put("Email", loggedInUser.getEmail());
                        postData.put("Error", message);
                        CenesUtils.postOnMixPanel(getContext(), "PersonalDetails", postData);
                    } else {

                        //Id the api request was for passwords
                        if (editRequest.equals(ProfileDetailRequest.Password)) {

                            //isCurrentPasswordCorrect == false, means it was request for validating current password
                            if (isCurrentPasswordCorrect == false && llCurrentPasswordView.getVisibility() == View.VISIBLE) {
                                isCurrentPasswordCorrect = true;
                                prepareDataForPasswordUpdate();
                            } else {

                                ((CenesBaseActivity)getActivity()).onBackPressed();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute(asyncTaskDto);
    }
}
