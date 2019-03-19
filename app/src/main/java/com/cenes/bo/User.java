package com.cenes.bo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by puneet on 11/8/17.
 */

public class User {


    public final String AUTHTYPE = "authType";
    public final String EMAIL = "email";
    public final String NAME = "name";
    public final String PASSWORD = "password";
    public final String USERID = "userId";
    public final String USERNAME = "username";
    public final String FACEBOOKID = "facebookID";
    public final String FACEBOOKAUTHTOKEN = "facebookAuthToken";
    public final String TOKEN = "token";
    public final String PHOTO = "photo";
    public final String PHONE = "phone";
    public final String BIRTHDATE = "birthDate";



    private String name;
    private  String email;
    private String password;
    private String apiUrl;
    public String authToken;
    private int userId;
    private String username;
    private String facebookID;

    @SerializedName("photo")
    private String picture;
    private String authType;
    private String gender;
    private String phone;
    private Long birthDate;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public String getFacebookAuthToken() {
        return facebookAuthToken;
    }

    public void setFacebookAuthToken(String facebookAuthToken) {
        this.facebookAuthToken = facebookAuthToken;
    }

    private String facebookAuthToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Long birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "User{" +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", authToken='" + authToken + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", facebookID='" + facebookID + '\'' +
                ", picture='" + picture + '\'' +
                ", authType='" + authType + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", facebookAuthToken='" + facebookAuthToken + '\'' +
                '}';
    }
}
