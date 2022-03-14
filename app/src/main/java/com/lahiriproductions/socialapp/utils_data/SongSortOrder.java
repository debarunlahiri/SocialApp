package com.lahiriproductions.socialapp.utils_data;

import android.provider.MediaStore;


public final class SongSortOrder {

    public SongSortOrder() {
    }

    public interface ArtistSortOrder {

        String strArtistAToZ = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;
        String strArtistZToA = strArtistAToZ + " DESC";
        String noOfArtistSongs = MediaStore.Audio.Artists.NUMBER_OF_TRACKS
                + " DESC";

        String noOfArtistAlbums = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                + " DESC";
    }

    /**
     * AlbumModel sort order entries.
     */
    public interface AlbumSortOrder {


        String strAlbumAtoZ = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        String strAlbumZtoA = strAlbumAtoZ + " DESC";

        String noOfAlbumSong = MediaStore.Audio.Albums.NUMBER_OF_SONGS
                + " DESC";

        String strAlbumArtist = MediaStore.Audio.Albums.ARTIST;
        String strAlbumYear = MediaStore.Audio.Albums.FIRST_YEAR + " DESC";

    }

    /**
     * SongModel sort order entries.
     */
    public interface SongSortOrder1 {

        String strSongAtoZ = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        String strSongZtoA = strSongAtoZ + " DESC";
        String strSongArtist = MediaStore.Audio.Media.ARTIST;
        String strSongAlbum = MediaStore.Audio.Media.ALBUM;
        String strSongYear = MediaStore.Audio.Media.YEAR + " DESC";

        String strSongDuration = MediaStore.Audio.Media.DURATION + " DESC";

        String strSongFileName = MediaStore.Audio.Media.DATA;
    }

    /**
     * AlbumModel song sort order entries.
     */
    public interface AlbumSongSortOrder {

        String strAlbumSongAtoZ = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        String strSongTrackList = MediaStore.Audio.Media.TRACK + ", "
                + MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    }

    /**
     * ArtistModel song sort order entries.
     */
    public interface ArtistSongSortOrder {

        String strArtistSongAtoZ = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    }



}
