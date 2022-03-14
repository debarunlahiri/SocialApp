package com.lahiriproductions.socialapp.models;

import android.net.Uri;

import com.lahiriproductions.socialapp.main_functions.ConstantData;


public class AlbumModelData {
    public final long artistId;
    public final String artistName;
    public final long id;
    public final int songCount;
    public final String title;
    public final Uri img_uri;
    public final int year;

    public AlbumModelData() {
        this.id = -1;
        this.title = "";
        this.artistName = "";
        this.artistId = -1;
        this.songCount = -1;
        this.year = -1;
        this.img_uri = null;
    }

    public AlbumModelData(long _id, String _title, String _artistName, long _artistId, int _songCount, int _year) {
        this.id = _id;
        this.title = _title;
        this.artistName = _artistName;
        this.artistId = _artistId;
        this.songCount = _songCount;
        this.year = _year;
        this.img_uri = ConstantData.getImgUri(_id);
    }
}
