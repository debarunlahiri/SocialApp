package com.lahiriproductions.socialapp.utils_data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

public final class PreferencesData {

    public static String isFirstTime = "is_first_time";
    public static final String strArtistSortOrder = "artist_sort_order";
    public static final String strArtistSongSortOrder = "artist_song_sort_order";
    public static final String strAlbumSortOrder = "album_sort_order";
    public static final String strAlbumSongSortOrder = "album_song_sort_order";
    public static final String strSongSortOrder = "song_sort_order";
    private static final String toggleArtistGrid = "toggle_artist_grid";
    private static final String toggleAlbumGrid = "toggle_album_grid";
    private static final String toggleGenerGrid = "toggle_gener_grid";
    private static final String lastFolder = "last_folder";
    private static final String toggleHeadPhonePause = "toggle_headphone_pause";
    private static final String themePrefernce = "theme_preference";
    public static String sortBY = "sortby";
    public static String sortColumn = "sort_column";
    public static String videoViewType = "video_view_type";
    private static PreferencesData sInstance;
    private static SharedPreferences mPreferences;


    public PreferencesData(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferencesData getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesData(context.getApplicationContext());
        }
        return sInstance;
    }

    public void setIsFirstTime(final Boolean is_shuffle) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(isFirstTime, is_shuffle);
        editor.apply();
    }

    public boolean isArtistsInGrid() {
        return mPreferences.getBoolean(toggleArtistGrid, true);
    }

    public void setArtistsInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(toggleArtistGrid, b);
        editor.apply();
    }

    public boolean isAlbumsInGrid() {
        return mPreferences.getBoolean(toggleAlbumGrid, true);
    }

    public void setAlbumsInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(toggleAlbumGrid, b);
        editor.apply();
    }

    public boolean isGenerInGrid() {
        return mPreferences.getBoolean(toggleGenerGrid, true);
    }

    public void setGenerInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(toggleGenerGrid, b);
        editor.apply();
    }

    public boolean pauseEnabledOnDetach() {
        return mPreferences.getBoolean(toggleHeadPhonePause, true);
    }

    public String getTheme() {
        return mPreferences.getString(themePrefernce, "light");
    }


    private void setSortOrder(final String key, final String value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public final String getArtistSortOrder() {
        return mPreferences.getString(strArtistSortOrder, SongSortOrder.ArtistSortOrder.strArtistAToZ);
    }

    public void setArtistSortOrder(final String value) {
        setSortOrder(strArtistSortOrder, value);
    }


    public final String getArtistSongSortOrder() {
        return mPreferences.getString(strArtistSongSortOrder,
                SongSortOrder.ArtistSongSortOrder.strArtistSongAtoZ);
    }


    public final String getAlbumSortOrder() {
        return mPreferences.getString(strAlbumSortOrder, SongSortOrder.AlbumSortOrder.strAlbumAtoZ);
    }

    public void setAlbumSortOrder(final String value) {
        setSortOrder(strAlbumSortOrder, value);
    }

    public final String getAlbumSongSortOrder() {
        return mPreferences.getString(strAlbumSongSortOrder,
                SongSortOrder.AlbumSongSortOrder.strSongTrackList);
    }

    public void setAlbumSongSortOrder(final String value) {
        setSortOrder(strAlbumSongSortOrder, value);
    }

    public final String getSongSortOrder() {
        return mPreferences.getString(strSongSortOrder, SongSortOrder.SongSortOrder1.strSongAtoZ);
    }

    public void setSongSortOrder(final String value) {
        setSortOrder(strSongSortOrder, value);
    }



    public void storeLastFolder(String path) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(lastFolder, path);
        editor.apply();
    }

    public String getLastFolder() {
        return mPreferences.getString(lastFolder, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath());
    }


    public void setSORTBY(final String key) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(sortBY, key);
        editor.apply();
    }

    public String getSORTBY() {
        return mPreferences.getString(sortBY, "DESC");
    }

    public void setSORTCOLUMN(final String key) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(sortColumn, key);
        editor.apply();
    }

    public String getSORTCOLUMN() {
        return mPreferences.getString(sortColumn, "duration"); //date_added
    }

    public void setViewType(final String key) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(videoViewType, key);
        editor.apply();
    }

    public String getViewType() {
        return mPreferences.getString(videoViewType, "files");
    }
}

