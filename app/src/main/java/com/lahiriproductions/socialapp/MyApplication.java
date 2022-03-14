package com.lahiriproductions.socialapp;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.lahiriproductions.socialapp.models.SongFolderData;
import com.lahiriproductions.socialapp.models.VideoFolderData;
import com.lahiriproductions.socialapp.models.VideosDataType;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends Application {
    static Context context;
    private MyApplication instance;
    ArrayList<VideosDataType> videolist;
    List<VideoFolderData> videoFolderData;
    List<SongFolderData> songFolderSongs;

    @Override
    public void onCreate() {

        MultiDex.install(this);
        super.onCreate();
        instance = this;
        context = getApplicationContext();

    }


    public static Context getContext() {
        return context;
    }

    public MyApplication getInstance() {
        if (instance == null) {
            instance = this;
        }

        return instance;
    }

    public ArrayList<VideosDataType> getVideolist() {
        return videolist;
    }

    public void setVideolist(ArrayList<VideosDataType> videolist) {
        this.videolist = videolist;
    }

    public List<VideoFolderData> getFolderVideos() {
        return videoFolderData;
    }

    public void setFolderVideos(List<VideoFolderData> videoFolderData) {
        this.videoFolderData = videoFolderData;
    }

    public List<SongFolderData> getFolderSongs() {
        return songFolderSongs;
    }

    public void setFolderSongs(List<SongFolderData> songFolderSongs) {
        this.songFolderSongs = songFolderSongs;
    }

}
