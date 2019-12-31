package com.example.moments.Model;

public class UserInfor {
    public String fullUserName;
    public String profilePhotoUrl;


    public UserInfor() {
    }

    public UserInfor(String fullUserName, String profilePhotoUrl) {
        this.fullUserName = fullUserName;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getFullUserName() {
        return fullUserName;
    }

    public void setFullUserName(String fullUserName) {
        this.fullUserName = fullUserName;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
}
