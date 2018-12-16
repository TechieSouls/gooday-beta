package com.cenes.Manager.Impl;

import com.cenes.Manager.ValidationManager;
import com.cenes.application.CenesApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by puneet on 11/8/17.
 */

public class ValidatioManagerImpl implements ValidationManager {
    CenesApplication cenesApplication;

    public ValidatioManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication= cenesApplication;
    }

    @Override
    public boolean isFieldBlank(String field) {
        if (field.trim().length() < 1)
            return true;
        return false;
    }

    @Override
    public boolean isSpace(String field) {
        boolean isSpaces = false;
        for (int i = 0; i < field.length(); i++) {
            if (field.charAt(i) == ' ') {
                isSpaces = true;
                i = field.length();
            }
        }

        return isSpaces;
    }

    @Override
    public boolean isValidEmail(String email) {
        boolean check;
        Pattern p;
        Matcher m;
        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        p = Pattern.compile(EMAIL_STRING);
        m = p.matcher(email);
        check = m.matches();
        return check;
    }

    @Override
    public boolean isValidLength(String field, int length) {
        if (field.length() >= length) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidPhoneLength(String field, int length) {
        if (field.length() >= length) {
            return true;
        }
        return false;
    }

}
