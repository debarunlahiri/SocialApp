package com.lahiriproductions.socialapp.load_song_data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.lahiriproductions.socialapp.models.AlbumModelData;

import java.util.ArrayList;

public class LoadNormalAlbum {

    public static ArrayList<AlbumModelData> getAlbumsForGener(Context context, long generID) {

        ArrayList<AlbumModelData> albumList = new ArrayList();
        Cursor cursor = makeAlbumForGenerCursor(context, generID);
        int numsongs = 0;
        int minyear = 0;
        long temp_id = 0;

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {

                    Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                    String string = "_id=" + cursor.getLong(0);
                    Cursor cursor1 = context.getContentResolver().query(uri, new String[]{"numsongs", "minyear"}, string, null, null);

                    if (cursor1 != null) {
                        if (cursor1.moveToFirst()) {
                            numsongs = cursor1.getInt(0);
                            minyear = cursor1.getInt(1);
                        }
                    }

                    AlbumModelData album = new AlbumModelData(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), numsongs, minyear);

                    if (temp_id != cursor.getLong(0)) {
                        albumList.add(album);
                        temp_id = cursor.getLong(0);
                    }

                }
                while (cursor.moveToNext());

        }
        if (cursor != null)
            cursor.close();
        return albumList;
    }


    public static Cursor makeAlbumForGenerCursor(Context context, long generID) {

        if (generID == -1)
            return null;

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Genres.Members.getContentUri("external", generID), new String[]{"album_id", "album", "artist", "artist_id"}, null, null, null);

        return cursor;
    }


}
