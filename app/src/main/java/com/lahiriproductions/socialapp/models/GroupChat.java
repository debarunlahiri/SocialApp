package com.lahiriproductions.socialapp.models;

public class GroupChat {

    private String user_id;
    private String group_chat_id;
    private String group_message;
    private long timestamp;

    GroupChat() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getGroup_chat_id() {
        return group_chat_id;
    }

    public void setGroup_chat_id(String group_chat_id) {
        this.group_chat_id = group_chat_id;
    }

    public String getGroup_message() {
        return group_message;
    }

    public void setGroup_message(String group_message) {
        this.group_message = group_message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
