package com.cenesbeta.Manager;

/**
 * Created by puneet on 11/8/17.
 */

public interface ValidationManager {
    public boolean isFieldBlank(String field);

    public boolean isSpace(String field);

    public boolean isValidEmail(String email);

    public boolean isValidLength(String field, int length);

    public boolean isValidPhoneLength(String field, int length);

}
