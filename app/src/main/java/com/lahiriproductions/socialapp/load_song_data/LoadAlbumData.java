package com.lahiriproductions.socialapp.load_song_data;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.lahiriproductions.socialapp.models.AlbumModelData;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;

import java.util.ArrayList;
import java.util.List;

public class LoadAlbumData {

    static Context context;

    public static AlbumModelData getAlbum(Cursor cursor) {
        AlbumModelData album = new AlbumModelData();
        if (cursor != null) {
            if (cursor.moveToFirst())
                album = new AlbumModelData(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getInt(4), cursor.getInt(5));
        }
        if (cursor != null)
            cursor.close();
        return album;
    }


    public static List<AlbumModelData> getAlbumsForCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        int native_count = 1;

        if ((cursor != null) && (cursor.moveToFirst()))
            do {

                arrayList.add(new AlbumModelData(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getInt(4), cursor.getInt(5)));

            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    public static List<AlbumModelData> getAllAlbums(Context context) {
        return getAlbumsForCursor(makeAlbumCursor(context, null, null));
    }

    public static Cursor makeAlbumCursor(Context ctx, String selection, String[] paramArrayOfString) {
        context = ctx;
        final String albumSortOrder = PreferencesData.getInstance(context).getAlbumSortOrder();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, new String[]{"_id", "album", "artist", "artist_id", "numsongs", "minyear"}, selection, paramArrayOfString, albumSortOrder);

        return cursor;
    }
}
