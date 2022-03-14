package com.lahiriproductions.socialapp.load_song_data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;

import java.util.ArrayList;

public class LoadArtistSong {

    public static ArrayList<SongsModelData> getSongsForArtist(Context context, long artistID) {
        Cursor cursor = makeArtistSongCursor(context, artistID);
        ArrayList songsList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                long id = cursor.getLong(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                int duration = cursor.getInt(4);
                int trackNumber = cursor.getInt(5);
                long albumId = cursor.getInt(6);
                String data = cursor.getString(7);
                String size = cursor.getString(8);
                long artistId = artistID;

                songsList.add(new SongsModelData(id, albumId, artistID, title, artist, album, duration, trackNumber, data, size));
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return songsList;
    }


    public static Cursor makeArtistSongCursor(Context context, long artistID) {
        ContentResolver contentResolver = context.getContentResolver();
        final String artistSongSortOrder = PreferencesData.getInstance(context).getArtistSongSortOrder();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String string = "is_music=1 AND title != '' AND artist_id=" + artistID;
        return contentResolver.query(uri, new String[]{"_id", "title", "artist", "album", "duration", "track", "album_id", "_data", "_size"}, string, null, artistSongSortOrder);
    }

}
