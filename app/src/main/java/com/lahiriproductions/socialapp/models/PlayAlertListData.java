package com.lahiriproductions.socialapp.models;

public class PlayAlertListData {
    String img_url;
    String title;
    String id;
    int songCount;

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

        this.id = id;
    }


    public PlayAlertListData() {
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}