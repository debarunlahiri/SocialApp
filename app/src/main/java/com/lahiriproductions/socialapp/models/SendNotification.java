package com.lahiriproductions.socialapp.models;

public class SendNotification {

    private String message;
    private int others;

    public SendNotification(String message, int others) {
        this.message = message;
        this.others = others;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getOthers() {
        return others;
    }

    public void setOthers(int others) {
        this.others = others;
    }
}
