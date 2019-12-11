package com.cenesbeta.bo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by puneet on 11/8/17.
 */

public class User {

    public enum AuthenticateType{
        email,facebook, google
    }
    private String name;
    private  String email;
    private String password;
    private String apiUrl;

    @SerializedName("token")
    public String authToken;
    private Integer userId;
    private String username;
    private String facebookId;

    @SerializedName("photo")
    private String picture;

    private AuthenticateType authType;
    private String gender;
    private String phone;
    private String birthDateStr;
    private String googleId;
    private String country;
    private boolean isNew;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
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

    public AuthenticateType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthenticateType authType) {
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

    public String getBirthDateStr() {
        return birthDateStr;
    }

    public void setBirthDateStr(String birthDateStr) {
        this.birthDateStr = birthDateStr;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
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
                ", facebookId='" + facebookId + '\'' +
                ", picture='" + picture + '\'' +
                ", authType='" + authType + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                ", birthDateStr='" + birthDateStr + '\'' +
                ", facebookAuthToken='" + facebookAuthToken + '\'' +
                '}';
    }
}
