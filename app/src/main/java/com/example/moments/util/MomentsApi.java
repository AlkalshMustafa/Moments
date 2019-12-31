package com.example.moments.util;

import android.net.Uri;

public class MomentsApi {

    private String userId;
    private String profileImgUrl;
    private String userName;

    private static MomentsApi instance;

    public static MomentsApi getInstance() {
        if (instance == null)
            instance = new MomentsApi();
        return instance;

    }

    public MomentsApi() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
