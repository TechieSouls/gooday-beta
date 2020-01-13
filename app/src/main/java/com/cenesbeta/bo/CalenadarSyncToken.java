package com.cenesbeta.bo;

import com.google.gson.annotations.SerializedName;

public class CalenadarSyncToken {

    @SerializedName("refresh_token_id")
    private Integer refreshTokenId;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("account_type")
    private String accountType;

    @SerializedName("email_id")
    private String emailId;

    public Integer getRefreshTokenId() {
        return refreshTokenId;
    }

    public void setRefreshTokenId(Integer refreshTokenId) {
        this.refreshTokenId = refreshTokenId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
