package com.lahiriproductions.socialapp.broadcast_receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;

import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.music_services.PlayMusicService;


public class NotificationBroadcast extends BroadcastReceiver {

    Intent playIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ConstantData.isServiceRunning(PlayMusicService.class.getName(), context)) {
            playIntent = new Intent(context, PlayMusicService.class);
            context.startService(playIntent);
        }

        if (ConstantData.sharedpreferences == null) {
            ConstantData.savePrefrence(context);
        }

        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent != null) {
                if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return;
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_HEADSETHOOK:
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:

                        context.sendBroadcast(new Intent(ConstantData.broadcastPlayerPause));

                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        break;
                    case KeyEvent.KEYCODE_MEDIA_STOP:

                        try {
                            SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                            editor.putBoolean(ConstantData.isShuffle, false);
                            editor.putBoolean(ConstantData.isRepeat, false);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (PlayMusicService.mNotifyManager == null) {
                            PlayMusicService.mNotifyManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                            PlayMusicService.mNotifyManager.cancel(PlayMusicService.notificationID);
                        }

                        PlayMusicService playMusicService = new PlayMusicService();
                        playMusicService.stopForeground(true);
                        playMusicService.stopSelf();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        context.sendBroadcast(new Intent(ConstantData.broadcastNext));
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        context.sendBroadcast(new Intent(ConstantData.broadcastPrev));
                        break;
                }
            } else {
                try {
                    SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                    editor.putBoolean(ConstantData.isShuffle, false);
                    editor.putBoolean(ConstantData.isRepeat, false);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (PlayMusicService.mNotifyManager == null) {
                    PlayMusicService.mNotifyManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    PlayMusicService.mNotifyManager.cancel(PlayMusicService.notificationID);
                }

                PlayMusicService playMusicService = new PlayMusicService();
                playMusicService.stopForeground(true);
                playMusicService.stopSelf();
            }
        } else {
            if (intent.getAction().equals(PlayMusicService.notifyPlay)) {
                context.sendBroadcast(new Intent(ConstantData.broadcastPlayerPause));
            } else if (intent.getAction().equals(PlayMusicService.notifyPause)) {
                context.sendBroadcast(new Intent(ConstantData.broadcastPause));
            } else if (intent.getAction().equals(PlayMusicService.notifyNext)) {
                context.sendBroadcast(new Intent(ConstantData.broadcastNext));

            } else if (intent.getAction().equals(PlayMusicService.notifyDelete)) {

                if (ConstantData.sharedpreferences == null) {
                    ConstantData.savePrefrence(context);
                }

                try {
                    SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                    editor.putBoolean(ConstantData.isShuffle, false);
                    editor.putBoolean(ConstantData.isRepeat, false);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (PlayMusicService.mNotifyManager == null) {
                    PlayMusicService.mNotifyManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                }
                Intent i = new Intent(context, PlayMusicService.class);
                context.stopService(i);

                PlayMusicService.stopService();

            } else if (intent.getAction().equals(PlayMusicService.openAudioPlayer)) {

            } else if (intent.getAction().equals(PlayMusicService.notifyPrevious)) {
                context.sendBroadcast(new Intent(ConstantData.broadcastPrev));
            }
        }
    }

    public String ComponentName() {
        return this.getClass().getName();
    }

}
