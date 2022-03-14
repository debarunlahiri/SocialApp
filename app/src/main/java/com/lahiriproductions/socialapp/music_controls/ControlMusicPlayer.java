package com.lahiriproductions.socialapp.music_controls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.data_helper.DataBaseHelper;
import com.lahiriproductions.socialapp.fragments.MainSongsListFrag;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.music_services.PlayMusicService;

import java.util.ArrayList;


public class ControlMusicPlayer {

    //========= PLayer Control =======
    public static SongsModelData songItem = new SongsModelData();
    public static int songNumber = 0;

    public static Handler progressHandler;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static String strTypePast = "";
    public static DataBaseHelper dataBaseHelper;

    public static void startSongsWithQueue(final Context context, final ArrayList<SongsModelData> dataSet, int position, final String str_type) {

        ControlMusicPlayer.songNumber = position;

        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), 0);
        editor = sharedPreferences.edit();
        strTypePast = sharedPreferences.getString("str_type", str_type);

        SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
        editor1.putInt(ConstantData.strSongNumber, ControlMusicPlayer.songNumber);
        editor1.commit();


        dataBaseHelper = DataBaseHelper.sharedInstance(context);
        dataBaseHelper.clearQueue();


        ConstantData.mediaItemsArrayList = dataSet;

        if (!ConstantData.isServiceRunning(PlayMusicService.class.getName(), context)) {
            Intent musIntent = new Intent(context, PlayMusicService.class);
            context.startService(musIntent);
        }


        MainSongsListFrag.fillQueueAdapter();

        MainSongsListFrag.playerViewpager.setCurrentItem(position);

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    for (int i = 0; i < ConstantData.mediaItemsArrayList.size(); i++) {
                        SongsModelData mediaItems = ConstantData.mediaItemsArrayList.get(i);
                        dataBaseHelper.insertQueue(mediaItems.getSong_id(), mediaItems.getImg_uri() + "", mediaItems.getTitle(), mediaItems.getPath(), mediaItems.getArtist(), mediaItems.getSize());

                        if (ConstantData.breakInsertQueue) {
                            ConstantData.breakInsertQueue = false;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        editor.putString("str_type", str_type);
        editor.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlayMusicService.playSong();
            }
        }, 200);

    }
}
