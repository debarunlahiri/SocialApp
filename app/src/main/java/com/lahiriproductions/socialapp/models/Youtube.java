package com.lahiriproductions.socialapp.models;

public class Youtube {

    private String youtube_id;
    private String youtube_url;
    private String youtube_video_id;
    private String user_id;
    private long posted_on;

    public Youtube() {

    }

    public Youtube(String youtube_id, String youtube_url, String youtube_video_id, String user_id, long posted_on) {
        this.youtube_id = youtube_id;
        this.youtube_url = youtube_url;
        this.youtube_video_id = youtube_video_id;
        this.user_id = user_id;
        this.posted_on = posted_on;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public String getYoutube_url() {
        return youtube_url;
    }

    public void setYoutube_url(String youtube_url) {
        this.youtube_url = youtube_url;
    }

    public String getYoutube_video_id() {
        return youtube_video_id;
    }

    public void setYoutube_video_id(String youtube_video_id) {
        this.youtube_video_id = youtube_video_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getPosted_on() {
        return posted_on;
    }

    public void setPosted_on(long posted_on) {
        this.posted_on = posted_on;
    }
}
