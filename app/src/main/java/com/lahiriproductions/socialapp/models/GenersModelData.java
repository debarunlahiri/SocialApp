package com.lahiriproductions.socialapp.models;

import android.net.Uri;

public class GenersModelData {
    String generName;
    Uri generUri;
    Long generId;
    Long albumId;
    int songCount;


    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public GenersModelData() {
    }

    public GenersModelData(String generName, Uri generUri, Long generId) {
        this.generName = generName;
        this.generUri = generUri;
        this.generId = generId;
    }

    public String getGenerName() {
        return generName;
    }

    public void setGenerName(String generName) {
        this.generName = generName;
    }

    public Uri getGenerUri() {
        return generUri;
    }

    public void setGenerUri(Uri generUri) {
        this.generUri = generUri;
    }

    public Long getGenerId() {
        return generId;
    }

    public void setGenerId(Long generId) {
        this.generId = generId;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }
}
