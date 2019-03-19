package com.cenes.fragment.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cenes.AsyncTasks.ProfileAsyncTask;
import com.cenes.R;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.fragment.CenesFragment;
import com.cenes.util.CenesMessages;

import org.json.JSONObject;

import java.util.Map;

public class ChangePasswordFragment extends CenesFragment {

    public final static String TAG = "ChangePasswordFragment";

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private User loggedInUser;

    private EditText etCurrentPassword, etNewPassword, etRetypeNewPassword;
    private Button btnSaveChanges;
    private TextView tvErrorMsg;
    private ImageView ivCpBackBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_change_password, container, false);

        etCurrentPassword = (EditText) v.findViewById(R.id.et_current_password);
        etNewPassword = (EditText) v.findViewById(R.id.et_new_password);
        etRetypeNewPassword = (EditText) v.findViewById(R.id.et_retype_new_password);

        btnSaveChanges = (Button) v.findViewById(R.id.btn_save_changes);

        etRetypeNewPassword.addTextChangedListener(retypeTextWatcher);
        tvErrorMsg = (TextView) v.findViewById(R.id.tv_error_msg);

        ivCpBackBtn = (ImageView) v.findViewById(R.id.iv_cp_back_btn);

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();
        new ProfileAsyncTask(cenesApplication, getActivity());


        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                if (validateForm()) {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("currentPassword", etCurrentPassword.getText().toString());
                        jsonObject.put("newPassword", etNewPassword.getText().toString());
                        jsonObject.put("userId", loggedInUser.getUserId());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    new ProfileAsyncTask.ChangePasswordTask(new ProfileAsyncTask.ChangePasswordTask.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject response) {
                            try {
                                if (response != null && response.getBoolean("success")) {
                                    System.out.println(response.toString());
                                    Toast.makeText(getActivity(), CenesMessages.CHANGE_PASSWORD_SUCCESS_MSG, Toast.LENGTH_SHORT).show();
                                    getActivity().onBackPressed();
                                } else {
                                    Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).execute(jsonObject);
                }
            }
        });
        return v;
    }


    TextWatcher retypeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (!"".equals(s.toString()) && !s.toString().equals(etNewPassword.getText().toString())) {
                tvErrorMsg.setVisibility(View.VISIBLE);
                etNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                etRetypeNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                tvErrorMsg.setVisibility(View.GONE);
                btnSaveChanges.setBackgroundColor(getResources().getColor(R.color.cenes_blue));
                btnSaveChanges.setClickable(true);
                btnSaveChanges.setEnabled(true);

                if (!"".equals(s.toString())) {
                    etNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_selected, 0);
                    etRetypeNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_selected, 0);
                }

            }
        }
    };

    public boolean validateForm() {

        if (etCurrentPassword.getText().toString().equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), "Please Enter Current Password", Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }

}
