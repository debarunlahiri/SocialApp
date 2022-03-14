package com.lahiriproductions.socialapp.data_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.lahiriproductions.socialapp.models.HistoryModelData;
import com.lahiriproductions.socialapp.models.PlayAlertListData;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.main_functions.AllFunctions;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static DataBaseHelper oh;
    Boolean flag = false;
    public static final String tableId = "ID";
    //Cursor cursor;
    // ================= likes table ===============================================================
    public static final String databaseName = "MUSICPLAYER";


    public static final String tableAddFavorite = "ADD_TO_FAVOURITE";
    public static final String addFavoriteSongId = "ADD_TO_FAVOURITE_SONG_ID";
    public static final String addFavoriteLink = "ADD_TO_FAVOURITE_LINK";

    public static final String tableAddHistory = "ADD_TO_HISTORY";
    public static final String addHistorySongId = "ADD_TO_HISTORY_SONG_ID";
    public static final String addHistoryDate = "ADD_TO_HISTORY_DATE";

    // =================Add to Queue table==========================================================
    public static final String tableAddQueue = "ADD_TO_QUEUE";
    public static final String addQueueSongId = "songid";
    public static final String addQueueImage = "image";
    public static final String addQueueLink = "songpath";
    public static final String addQueueSinger = "singer";
    public static final String addQueueSongName = "songname";
    public static final String addQueueSongSize = "songsize";


    // ==============Playlist table===================================

    private final String tablePlayList = "PLAYLIST";
    private final String platListId = "playlistid";
    private final String playListName = "playlistname";
    private final String playListImage = "playlistimage";


    // ==============Add to Playlist Songs table======================

    private final String addNameToPlayList = "ADD_TO_PLAYLIST";
    private final String addQueuePlayListSongId = "songid";
    private final String queuePlayListLink = "songpath";


    String createFavoriteTable = "create table " + tableAddFavorite + " (" + tableId + " integer primary key autoincrement," + addFavoriteSongId + " text" + "," + addFavoriteLink + " text" + ")";
    String CreateHistoryTable = "create table " + tableAddHistory + " (" + tableId + " integer primary key autoincrement," + addHistorySongId + " text" + "," + addHistoryDate + " text" + ")";
    String createQueueTable = "create table " + tableAddQueue + " (" + tableId + " integer primary key autoincrement,"
            + addQueueSongId + " text" + ","
            + addQueueSongName + " text" + ","
            + addQueueImage + " text" + ","
            + addQueueLink + " text" + ","
            + addQueueSongSize + " text" + ","
            + addQueueSinger + " text)";

    String createAddSongPlayListTable = "create table " + addNameToPlayList + " ("
            + tableId + " integer primary key autoincrement,"
            + platListId + " text,"
            + addQueuePlayListSongId + " text,"
            + queuePlayListLink + " text)";

    String createPlayListTable = "create table " + tablePlayList + " (" + platListId + " integer primary key autoincrement," + playListName + " text " + "," + playListImage + " text" + ")";


    public DataBaseHelper(Context context) {
        super(context, databaseName, null, 4);
    }

    public static DataBaseHelper sharedInstance(Context context) {
        if (oh == null) {
            oh = new DataBaseHelper(context);
        }
        return oh;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createFavoriteTable);
        db.execSQL(CreateHistoryTable);
        db.execSQL(createQueueTable);
        db.execSQL(createAddSongPlayListTable);
        db.execSQL(createPlayListTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {

            if (!isFieldExist(db, "ADD_TO_QUEUE", "songsize")) {
                db.execSQL("ALTER TABLE  `ADD_TO_QUEUE` ADD  `songsize` TEXT NOT NULL DEFAULT  '0'");
            }

        }
    }


    public String insertHistory(long songId, String historyDate) {
        //Cursor res;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(addHistorySongId, songId);
        contentValues.put(addHistoryDate, historyDate);
        long id = db.insert(tableAddHistory, null, contentValues);


        return id + "";
    }

    //====================================queue data=============================================

    public boolean insertQueue(long songId, String songUri, String songName, String songPath, String songArtist, String songSize) {
        //Cursor res;

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + tableAddQueue + " WHERE " + addQueueSongId + "='" + songId + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            return false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(addQueueSongId, songId);
            contentValues.put(addQueueSongName, songName);
            contentValues.put(addQueueImage, songUri);
            contentValues.put(addQueueLink, songPath);
            contentValues.put(addQueueSinger, songArtist);
            contentValues.put(addQueueSongSize, songSize);
            long id = db.insert(tableAddQueue, null, contentValues);
            return true;
        }

    }

    public void clearQueue() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(tableAddQueue, null, null);
        db.close();
    }

    public ArrayList<SongsModelData> getQueueData(Context context) {
        String selectQuery = "SELECT * FROM " + tableAddQueue;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<SongsModelData> mediaItemses = new ArrayList<SongsModelData>();

        try {
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        SongsModelData data = new SongsModelData();
                        data.setQueue_id(cursor.getInt(cursor.getColumnIndex(tableId)));
                        data.setSong_id(cursor.getLong(cursor.getColumnIndex(addQueueSongId)));
                        data.setArtist(cursor.getString(cursor.getColumnIndex(addQueueSinger)));
                        data.setTitle(cursor.getString(cursor.getColumnIndex(addQueueSongName)));
                        data.setPath(cursor.getString(cursor.getColumnIndex(addQueueLink)));
                        data.setImg_uri(Uri.parse(cursor.getString(cursor.getColumnIndex(addQueueImage))));
                        data.setSize(cursor.getString(cursor.getColumnIndex(addQueueSongSize)));
                        mediaItemses.add(data);

                    } while (cursor.moveToNext());
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return mediaItemses;
    }


    public void deleteQueueSong(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(tableAddQueue, addQueueSongId + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public boolean isQueuelist(long id) {
        String selectQuery = "SELECT * FROM " + tableAddQueue + " WHERE " + addQueueSongId + "=" + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    return true;
                }

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }


    public int getNext(int queu_id) {
        int id = 0;
        String selectQuery = " SELECT " + tableId + " FROM " + tableAddQueue + " WHERE " + tableId + ">" + queu_id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndex(tableId));
            } else {
                id = 0;
            }
            cursor.close();
        } else {
            id = 0;
        }

        return id;
    }

    public int getCurrent_song(int queu_id) {
        int id = 0;
        String selectQuery = " SELECT " + tableId + " FROM " + tableAddQueue + " WHERE " + tableId + "=" + queu_id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(cursor.getColumnIndex(tableId));
                } else {
                    id = 0;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return id;
    }

    //------------------------playlist----------------------------------------------------------

    public String insertPlaylist(String playlist_name, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(playListName, playlist_name);
        contentValues.put(playListImage, image);
        long id = db.insert(tablePlayList, null, contentValues);
        return id + "";
    }


    public boolean AddToPlaylistData(String playlistid, long songid, String link) {
        String selectQuery = "SELECT * FROM " + addNameToPlayList + " WHERE " + platListId + "='" + playlistid + "' and " + queuePlayListLink + "='" + link + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    return false;
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(platListId, playlistid);
                    contentValues.put(addQueuePlayListSongId, songid);
                    contentValues.put(queuePlayListLink, link);
                    long id = db.insert(addNameToPlayList, null, contentValues);
                    return true;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public ArrayList<SongsModelData> getPlayListData(String playlistid, Context context) {
        String selectQuery = "SELECT * FROM " + addNameToPlayList + " WHERE " + platListId + "='" + playlistid + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<SongsModelData> mediaItemses = new ArrayList<>();
        try {
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    {
                        do {
                            long song_id = cursor.getLong(cursor.getColumnIndex(addQueuePlayListSongId));
                            int queue_id = cursor.getInt(cursor.getColumnIndex(tableId));
                            SongsModelData mediaItemse = new AllFunctions().fatchSongsDetail(song_id, context, queue_id);
                            mediaItemses.add(mediaItemse);
                        }
                        while (cursor.moveToNext());
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        return mediaItemses;

    }

    public void deletePlayListSong(long songid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String del_query = "DELETE  FROM " + addNameToPlayList + " WHERE " + addQueuePlayListSongId + "='" + songid + "'";
        db.execSQL(del_query);
    }

    public void deletePlayListSongsData(String playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String del_query = "DELETE  FROM " + addNameToPlayList + " WHERE " + platListId + "='" + playlistId + "'";
        db.execSQL(del_query);
    }

    public void deletePlayList(String playlistid) {
        deletePlayListSongsData(playlistid);

        SQLiteDatabase db = this.getWritableDatabase();
        String del_query = "DELETE  FROM " + tablePlayList + " WHERE " + platListId + "='" + playlistid + "'";
        db.execSQL(del_query);
    }


    public ArrayList<PlayAlertListData> getPlaylist() {
        String selectQuery = "SELECT * FROM " + tablePlayList;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<PlayAlertListData> playListArrayAlertList = new ArrayList<>();
        try {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) // change 9/1 movetoNext
                    {
                        do {
                            PlayAlertListData playAlertListData = new PlayAlertListData();
                            playAlertListData.setTitle(cursor.getString(cursor.getColumnIndex(playListName)));
                            playAlertListData.setImg_url(cursor.getString(cursor.getColumnIndex(playListImage)));
                            playAlertListData.setId(cursor.getInt(cursor.getColumnIndex(platListId)) + "");

                            String query = "SELECT * FROM " + addNameToPlayList + " WHERE " + platListId + "='" + cursor.getInt(cursor.getColumnIndex(platListId)) + "'";

                            Cursor c = db.rawQuery(query, null);
                            if (c != null) {
                                playAlertListData.setSongCount(c.getCount());
                            }

                            playListArrayAlertList.add(playAlertListData);
                        }
                        while (cursor.moveToNext());
                    }

                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        return playListArrayAlertList;
    }

    public boolean checkPlaylistExistOrNot(String playlist_name) {
        String selectQuery = "SELECT * FROM " + tablePlayList + " WHERE " + playListName + "='" + playlist_name + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public boolean isPlaylist(long id) {
        String selectQuery = "SELECT * FROM " + addNameToPlayList + " WHERE " + addQueuePlayListSongId + "=" + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                return true;
            }
            cursor.close();
        }

        return false;
    }

//--------------------------------------------------------------------------------------------------


    public void deleteFavoriteSong(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(tableAddFavorite, addFavoriteSongId + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteHistorySong(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(tableAddHistory, addHistorySongId + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(tableAddHistory, null, null);
        db.close();
    }


    public ArrayList<HistoryModelData> getHistory() {
        String selectQuery = "SELECT * FROM " + tableAddHistory + " ORDER BY " + addHistoryDate + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<HistoryModelData> historyArrayList = new ArrayList<HistoryModelData>();
        //Log.e ("Mediaitem", cursor.getCount () + "");
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        String songId = cursor.getString(cursor.getColumnIndex(addHistorySongId));
                        String historyDate = cursor.getString(cursor.getColumnIndex(addHistoryDate));

                        HistoryModelData history = new HistoryModelData();
                        history.setSongId(songId);
                        history.setHistoryDate(historyDate);
                        historyArrayList.add(history);
                    } while (cursor.moveToNext());
                }

            }
            cursor.close();
        }
        return historyArrayList;
    }

    public void updateHistory(long id, String date) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(addHistoryDate, date);

        db.update(tableAddHistory,
                args,
                addHistorySongId + " =? ",
                new String[]{id + ""});

    }

    public int getTotalHistorySongs() {
        String selectQuery = "SELECT * FROM " + tableAddHistory;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int numRows = 0;
        try {
            if (cursor != null)
                numRows = cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return numRows;
    }

    public String firstQueueRecod() {
        String selectQuery = "SELECT * FROM " + tableAddHistory + " LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String id = "0";
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    id = cursor.getString(cursor.getColumnIndex(addHistorySongId));
                }
            }
            cursor.close();
        }
        return id;
    }

    public boolean isHistory(long id) {

        String selectQuery = "SELECT * FROM " + tableAddHistory + " WHERE " + addHistorySongId + "=" + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    return true;
                }

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        return false;
    }


    //-----------------for show db-----------------------------------------------------

   /* public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.d("printing exception", ex.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }*/


    public static boolean isFieldExist(SQLiteDatabase db, String tableName, String fieldName) {
        boolean isExist = false;

        Cursor res = null;

        try {

            res = db.rawQuery("Select * from " + tableName + " limit 1", null);

            int colIndex = res.getColumnIndex(fieldName);
            if (colIndex != -1) {
                isExist = true;
            }

        } catch (Exception e) {
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
            } catch (Exception e1) {
            }
        }

        return isExist;
    }
}
