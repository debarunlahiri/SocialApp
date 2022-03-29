package com.lahiriproductions.socialapp.models;

public class UserStatusRequest {

    private String user_id;

    public UserStatusRequest(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
