package com.lahiriproductions.socialapp.models;

public class Music {

    private String file_path;
    private String file_name;
    private String album_name;
    private String artist_name;

    public Music(String file_path, String file_name, String album_name, String artist_name) {
        this.file_path = file_path;
        this.file_name = file_name;
        this.album_name = album_name;
        this.artist_name = artist_name;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }
}
