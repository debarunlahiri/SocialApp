package com.lahiriproductions.socialapp.models;

import android.net.Uri;

import com.lahiriproductions.socialapp.main_functions.ConstantData;

public class SongsModelData {
    long song_id;  // songs id
    String album = "";
    long albumId;
    String artist;
    long duration;
    Uri img_uri;
    String path = "";
    String title = "";
    String history_date = "";
    int queue_id;
    String size;
    int trackNumber;
    long artistId;
    int date;

    public int getQueue_id() {
        return queue_id;
    }

    public void setQueue_id(int queue_id) {
        this.queue_id = queue_id;
    }

    public SongsModelData() {

    }

    public SongsModelData(long _id, long _albumId, long _artistId, String _title, String _artistName, String _albumName, int _duration, int _trackNumber, String _data, String size) {
        this.song_id = _id;
        this.albumId = _albumId;
        this.artistId = _artistId;
        this.title = _title;
        this.artist = _artistName;
        this.album = _albumName;
        this.duration = _duration;
        this.trackNumber = _trackNumber;
        this.path = _data;
        this.img_uri = ConstantData.getImgUri(_albumId);
        this.size = size;
    }


    public String toString() {
        return this.title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }


    public Uri getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(Uri img_uri) {
        this.img_uri = img_uri;
    }

    public long getSong_id() {
        return song_id;
    }

    public void setSong_id(long song_id) {
        this.song_id = song_id;
    }

    public String getHistory_date() {
        return history_date;
    }

    public void setHistory_date(String history_date) {
        this.history_date = history_date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }


}
