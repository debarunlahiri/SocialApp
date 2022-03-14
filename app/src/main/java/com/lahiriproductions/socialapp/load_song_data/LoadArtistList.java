package com.lahiriproductions.socialapp.load_song_data;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.lahiriproductions.socialapp.models.ArtistModelData;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;

import java.util.ArrayList;
import java.util.List;

public class LoadArtistList {
    static Context context;

    public static ArtistModelData getArtist(Cursor cursor) {
        ArtistModelData artist = new ArtistModelData();
        if (cursor != null) {
            if (cursor.moveToFirst())
                artist = new ArtistModelData(cursor.getLong(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3));
        }
        if (cursor != null)
            cursor.close();
        return artist;
    }

    public static List<ArtistModelData> getArtistsForCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();

        if ((cursor != null) && (cursor.moveToFirst()))
            do {

                arrayList.add(new ArtistModelData(cursor.getLong(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3)));

            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    public static List<ArtistModelData> getAllArtists(Context context) {
        return getArtistsForCursor(makeArtistCursor(context, null, null));
    }

    public static ArtistModelData getArtist(Context context, long id) {
        return getArtist(makeArtistCursor(context, "_id=?", new String[]{String.valueOf(id)}));
    }

    public static Cursor makeArtistCursor(Context ctx, String selection, String[] paramArrayOfString) {
        context = ctx;
        final String artistSortOrder = PreferencesData.getInstance(ctx).getArtistSortOrder();
        Cursor cursor = ctx.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, new String[]{"_id", "artist", "number_of_albums", "number_of_tracks"}, selection, paramArrayOfString, artistSortOrder);
        return cursor;
    }
}
