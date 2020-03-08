package com.example.messaging;

public class UserProfile {
    private String name, email, tag, mobile, password, uid, imageURL, status;

    public UserProfile(String name, String email, String tag, String mobile, String password, String uid, String imageURL, String status) {
        this.name = name;
        this.email = email;
        this.tag = tag;
        this.mobile = mobile;
        this.password = password;
        this.uid = uid;
        this.imageURL = imageURL;
        this.status = status;
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

    public String getStatus() {
        return status;
    }
}
