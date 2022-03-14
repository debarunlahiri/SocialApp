package com.lahiriproductions.socialapp.load_song_data;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.lahiriproductions.socialapp.models.AlbumModelData;

import java.util.ArrayList;

public class LoadArtistAlbumData {

    public static ArrayList<AlbumModelData> getAlbumsForArtist(Context context, long artistID) {

        ArrayList albumList = new ArrayList();
        Cursor cursor = makeAlbumForArtistCursor(context, artistID);

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    AlbumModelData album = new AlbumModelData(cursor.getLong(0), cursor.getString(1), cursor.getString(2), artistID, cursor.getInt(3), cursor.getInt(4));
                    albumList.add(album);
                }
                while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return albumList;
    }


    public static Cursor makeAlbumForArtistCursor(Context context, long artistID) {
        Cursor cursor;
        if (artistID == -1)
            return null;

       if(Build.VERSION.SDK_INT >= 29)
       {
            cursor = context.getContentResolver().query(MediaStore.Audio.Artists.Albums.getContentUri("external", artistID), new String[]{"album_id", "album", "artist", "numsongs", "minyear"}, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
       }
       else
       {
           cursor = context.getContentResolver().query(MediaStore.Audio.Artists.Albums.getContentUri("external", artistID), new String[]{BaseColumns._ID, "album", "artist", "numsongs", "minyear"}, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
       }

        return cursor;
    }
}
