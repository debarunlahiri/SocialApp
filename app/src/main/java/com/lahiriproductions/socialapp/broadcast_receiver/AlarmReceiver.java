package com.lahiriproductions.socialapp.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.music_services.PlayMusicService;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConstantData.sharedpreferences.getString(ConstantData.strSleepTimer, "false").equals("true")) {
            if (ConstantData.isServiceRunning(PlayMusicService.class.getName(), context)) {
                context.sendBroadcast(new Intent(ConstantData.strTimeUp));
            }

        }
    }
}
