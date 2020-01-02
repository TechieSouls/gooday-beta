package com.cenesbeta.dto;

import org.json.JSONObject;

public class AsyncTaskDto {

    private String apiUrl;
    private JSONObject postData;
    private String queryStr;

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public JSONObject getPostData() {
        return postData;
    }

    public void setPostData(JSONObject postData) {
        this.postData = postData;
    }

    public String getQueryStr() {
        return queryStr;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }
}
