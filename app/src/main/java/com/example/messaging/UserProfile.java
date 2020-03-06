package com.example.messaging;

public class UserProfile {
    private String name, email, tag, mobile, password, uid, imageURL;

    public UserProfile(String name, String email, String tag, String mobile, String password, String uid, String imageURL) {
        this.name = name;
        this.email = email;
        this.tag = tag;
        this.mobile = mobile;
        this.password = password;
        this.uid = uid;
        this.imageURL = imageURL;
    }

    public UserProfile() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTag() {
        return tag;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPassword() {
        return password;
    }

    public String getUid() {
        return uid;
    }

    public String getImageURL() {
        return imageURL;
    }
}
