package com.lahiriproductions.socialapp.music_services;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.broadcast_receiver.NotificationBroadcast;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.data_helper.DataBaseHelper;
import com.lahiriproductions.socialapp.fragments.MainSongsListFrag;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.music_controls.ControlMusicPlayer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class PlayMusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    public static final String notifyPrevious = "com.MUSIC.previous";
    public static final String notifyDelete = "com.MUSIC.delete";
    public static final String notifyPause = "com.MUSIC.pause";
    public static final String notifyPlay = "com.MUSIC.play";
    public static final String notifyNext = "com.MUSIC.next";
    public static final String openAudioPlayer = "com.MUSIC.audioplayer";

    public static final int notificationID = 1008;
    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    private static boolean currentVersionSupportLockScreenControls = false;
    public static NotificationManager mNotifyManager;
    public static MediaPlayer mediaPlayer;

    private final IBinder musicBind = new MusicBinder();
    public static boolean shuffle = false;
    private static boolean repeat = false;
    private static int repeatID;
    private Random random;
    public static Timer timer;
    private RemoteViews simpleContentView;
    private Notification notification;
    DataBaseHelper dataBaseHelper;
    static Context context;
    AudioManager audioManager = null;
    AudioManager.OnAudioFocusChangeListener changeListener;


    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
        context = this;
        timer = new Timer();
        dataBaseHelper = new DataBaseHelper(this);

        if (ConstantData.sharedpreferences == null) {
            ConstantData.savePrefrence(context);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        random = new Random();
        initMusicPlayer();
        checkPhoneCall();
        changeListener = this;

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus((AudioManager.OnAudioFocusChangeListener) this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            pausePlayer();
        }


        currentVersionSupportLockScreenControls = ConstantData.currentVersionSupportLockScreenControls();
        if (currentVersionSupportLockScreenControls) {
            RegisterRemoteClient();
        }

        registerReceiver(buttonplaypauseReceiver, new IntentFilter(ConstantData.strTimeUp));
        registerReceiver(widgetplaypauseReceiver, new IntentFilter(ConstantData.broadcastPlayerPause));
        registerReceiver(widgetnextReceiver, new IntentFilter(ConstantData.broadcastNext));
        registerReceiver(widgetprevReceiver, new IntentFilter(ConstantData.broadcastPrev));
        registerReceiver(widgetpauseReceiver, new IntentFilter(ConstantData.broadcastPause));

        return START_NOT_STICKY;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        context.sendBroadcast(new Intent(ConstantData.broadcastPause));
    }


    public static class MainTask extends TimerTask {
        public void run() {

            try {
                handler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        int progress = (mediaPlayer.getCurrentPosition() * 100) / mediaPlayer.getDuration();
                        Integer i[] = new Integer[3];
                        i[0] = mediaPlayer.getCurrentPosition();
                        i[1] = mediaPlayer.getDuration();
                        i[2] = progress;
                        ControlMusicPlayer.progressHandler.sendMessage(ControlMusicPlayer.progressHandler.obtainMessage(0, i));
                    }
                } catch (Exception e) {

                }
            }
        }
    };


    public void initMusicPlayer() {
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }


    public class MusicBinder extends Binder {
        public PlayMusicService getService() {
            return PlayMusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return false;
    }

    public void playerStart() {
        if (mediaPlayer != null) {
            go();
        } else {

            playSong();
        }
    }

    public static void playSong() {
        repeatID = ControlMusicPlayer.songNumber;

        if (MainSongsListFrag.playerSlidingUpPanelLayout != null && MainSongsListFrag.playerSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            MainSongsListFrag.playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        if (ConstantData.sharedpreferences == null) {
            ConstantData.savePrefrence(context);
        }
        SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
        editor1.putInt(ConstantData.strSongNumber, ControlMusicPlayer.songNumber);
        editor1.commit();

        MainSongsListFrag.changeUi(ControlMusicPlayer.songNumber);

        if (mediaPlayer != null) {
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();

        }

        SongsModelData playSong = null;
        try {
            playSong = ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber);
        } catch (Exception e) {
            playSong = new SongsModelData();
            ControlMusicPlayer.songNumber = 0;
            e.printStackTrace();
        }


        FileInputStream is = null;
        try {
            is = new FileInputStream(playSong.getPath());
            mediaPlayer.setDataSource(is.getFD());
//            mediaPlayer.setDataSource("http://streaming.radionomy.com/JamendoLounge?lang=en-US%2cen%3bq%3d0.9%2cgu-IN%3bq%3d0.8%2cgu%3bq%3d0.7%2chi-IN%3bq%3d0.6%2chi%3bq%3d0.5");
            mediaPlayer.prepareAsync();
            is.close();
        } catch (Exception e) {

            e.printStackTrace();
        }

        SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
        editor.putString(ConstantData.prefreanceLastSongKey, ControlMusicPlayer.songNumber + "");
        editor.putString("songId", playSong.getSong_id() + "");
        editor.commit();

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            if (repeat) {
                ControlMusicPlayer.songNumber = repeatID;
                playSong();
            } else if (mediaPlayer != null && mediaPlayer.getCurrentPosition() > 0) {
                mp.reset();
                playNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

//        musicPlayerNotification();
        timer.scheduleAtFixedRate(new MainTask(), 0, 1000);


        SimpleDateFormat currentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String history = currentdate.format(new Date());
        try {
            if (!dataBaseHelper.isHistory(ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber).getSong_id())) {
                if (dataBaseHelper.getTotalHistorySongs() > 50) {
                    String id = dataBaseHelper.firstQueueRecod();
                    dataBaseHelper.deleteHistorySong(id);
                }
                dataBaseHelper.insertHistory(ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber).getSong_id(), history);
            } else {
                try {
                    dataBaseHelper.updateHistory(ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber).getSong_id(), history);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MainSongsListFrag.changeButton();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int getDur() {
        return mediaPlayer.getDuration();
    }

    public static boolean isPng() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public void pausePlayer() {
        if (ConstantData.isServiceRunning(this.getClass().getName(), context)) {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                if (ConstantData.sharedpreferences == null) {
                    ConstantData.savePrefrence(context);
                }
//                musicPlayerNotification();
                MainSongsListFrag.changeButton();

            }
        }
    }

    public static void seek(int posn) {
        mediaPlayer.seekTo(posn);
    }

    public void go() {
        mediaPlayer.start();

        MainSongsListFrag.changeButton();
//        try {
//            musicPlayerNotification();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void playPrev() {
        if (shuffle) {
            int newSong = ControlMusicPlayer.songNumber;
            while (newSong == ControlMusicPlayer.songNumber) {
                if (ConstantData.mediaItemsArrayList != null)
                    newSong = random.nextInt(ConstantData.mediaItemsArrayList.size());
            }
            ControlMusicPlayer.songNumber = newSong;
        } else {
            ControlMusicPlayer.songNumber--;
            if (ControlMusicPlayer.songNumber < 0)
                ControlMusicPlayer.songNumber = ConstantData.mediaItemsArrayList.size() - 1;
        }
        playSong();
    }

    //skip to next
    public void playNext() {
        if (shuffle) {
            int newSong = ControlMusicPlayer.songNumber;
            while (newSong == ControlMusicPlayer.songNumber) {
                newSong = random.nextInt(ConstantData.mediaItemsArrayList.size());
            }
            ControlMusicPlayer.songNumber = newSong;
        } else {
            ControlMusicPlayer.songNumber++;
            if (ControlMusicPlayer.songNumber >= ConstantData.mediaItemsArrayList.size())
                ControlMusicPlayer.songNumber = 0;
        }
        playSong();
    }

    //===player notification
    @SuppressLint("NewApi")
    private void musicPlayerNotification() {
        if (ConstantData.mediaItemsArrayList != null && ConstantData.mediaItemsArrayList.size() > 0) {

            SongsModelData mediaItem = ConstantData.mediaItemsArrayList.get(ConstantData.sharedpreferences.getInt(ConstantData.strSongNumber, 0));
            String songName = mediaItem.getTitle();
            String artist = mediaItem.getArtist();
            Uri image = mediaItem.getImg_uri();

            Intent notIntent = new Intent(this, MainSongsListFrag.class);
            notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT | FLAG_MUTABLE);


            simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.small_notification);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = new NotificationCompat.Builder(getApplicationContext(), context.getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(songName)
                        .setContentIntent(pendInt)
                        .setCustomContentView(simpleContentView)
                        .build();
            } else {
                notification = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(songName)
                        .setContentIntent(pendInt)
                        .setCustomContentView(simpleContentView)
                        .build();
            }


            if (notification != null) {
                notification.contentView = simpleContentView;
                setListeners(simpleContentView, songName, artist, image);


                try {
                    if (!mediaPlayer.isPlaying()) {
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;
                        mNotifyManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);


                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            String channelId = context.getString(R.string.default_notification_channel_id);
                            NotificationChannel channel = new NotificationChannel(channelId, "MyMusic", NotificationManager.IMPORTANCE_NONE);
                            channel.enableLights(false);
                            channel.enableVibration(false);
                            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                            mNotifyManager.createNotificationChannel(channel);
                        }

                        mNotifyManager.notify(notificationID, notification);

                    } else {
                        notification.flags |= Notification.FLAG_NO_CLEAR;
                        mNotifyManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            String channelId = context.getString(R.string.default_notification_channel_id);
                            NotificationChannel channel = new NotificationChannel(channelId, "MyMusic", NotificationManager.IMPORTANCE_NONE);
                            channel.enableLights(false);
                            channel.enableVibration(false);
                            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                            mNotifyManager.createNotificationChannel(channel);
                        }

                        mNotifyManager.notify(notificationID, notification);
                    }


                    startForeground(notificationID, notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            stopService();
            this.stopSelf();
        }
    }

    @SuppressLint("NewApi")
    private void RegisterRemoteClient() {
        remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
        try {
            if (remoteControlClient == null) {
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
            }

        } catch (Exception ex) {

        }
    }

    public void setListeners(RemoteViews view, String songName, String artist, Uri image) {
        Intent previous = new Intent(notifyPrevious);
        previous.setClass(context, NotificationBroadcast.class);
        Intent delete = new Intent(notifyDelete);
        delete.setClass(context, NotificationBroadcast.class);
        Intent pause = new Intent(notifyPause);
        pause.setClass(context, NotificationBroadcast.class);
        Intent next = new Intent(notifyNext);
        next.setClass(context, NotificationBroadcast.class);
        Intent play = new Intent(notifyPlay);
        play.setClass(context, NotificationBroadcast.class);
        Intent openPlayer = new Intent(openAudioPlayer);
        openPlayer.setClass(context, NotificationBroadcast.class);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        view.setOnClickPendingIntent(R.id.img_previous_notificationbar_layout, pPrevious);

        PendingIntent pOpenplayer = PendingIntent.getBroadcast(getApplicationContext(), 0, openPlayer, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        view.setOnClickPendingIntent(R.id.img_album_notificationbar_layout, pOpenplayer);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        view.setOnClickPendingIntent(R.id.img_remove_notificationbar_layout, pDelete);

        view.setImageViewResource(R.id.img_previous_notificationbar_layout, R.drawable.ic_skip_previous);
        view.setImageViewResource(R.id.img_next_notificationbar_layout, R.drawable.ic_skip_next);
        view.setImageViewResource(R.id.img_remove_notificationbar_layout, R.drawable.ic_close);

        view.setTextViewText(R.id.txt_songname_notificationbar_layout, songName);
        view.setTextViewText(R.id.txt_singername_notificationbar_layout, artist);

        Picasso.get().load(image).error(R.drawable.splash_icon).into(view, R.id.img_album_notificationbar_layout, notificationID, notification);


        if (isPng()) {
            PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            view.setOnClickPendingIntent(R.id.img_play_notificationbar_layout, pPause);
            view.setImageViewResource(R.id.img_play_notificationbar_layout, R.drawable.ic_pause_circle_outline);
        } else {
            PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            view.setOnClickPendingIntent(R.id.img_play_notificationbar_layout, pPlay);
            view.setImageViewResource(R.id.img_play_notificationbar_layout, R.drawable.ic_play_circle_outline);
        }

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        view.setOnClickPendingIntent(R.id.img_next_notificationbar_layout, pNext);
    }


    public void checkPhoneCall() {
        if (ConstantData.isServiceRunning(PlayMusicService.class.getName(), getApplicationContext())) {
            PhoneStateListener phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        //pausePlayer();
                        context.sendBroadcast(new Intent(ConstantData.broadcastPlayerPause));
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {

                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        context.sendBroadcast(new Intent(ConstantData.broadcastPlayerPause));
                        //A call is dialing, active or on hold
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (mgr != null) {
//                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(buttonplaypauseReceiver);
        unregisterReceiver(widgetplaypauseReceiver);
        unregisterReceiver(widgetnextReceiver);
        unregisterReceiver(widgetprevReceiver);
        unregisterReceiver(widgetpauseReceiver);
        stopForeground(true);


        if (mediaPlayer != null) {
            pausePlayer();
            mediaPlayer.stop();
            mediaPlayer = null;
            if (mNotifyManager != null) {
                mNotifyManager.cancel(notificationID);
            }
            stopSelf();
        }

    }

    //toggle shuffle
    public static void setShuffle() {
        if (shuffle) {
            SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
            editor.putBoolean(ConstantData.isShuffle, false);
            editor.commit();
            shuffle = false;
        } else {
            SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
            editor.putBoolean(ConstantData.isShuffle, true);
            editor.commit();
            shuffle = true;
        }
    }

    public static void setRepeat() {
        if (repeat) {
            SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
            editor.putBoolean(ConstantData.isRepeat, false);
            editor.commit();
            repeat = false;
        } else {
            SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
            editor.putBoolean(ConstantData.isRepeat, true);
            editor.commit();
            repeat = true;
        }
    }

    public static boolean isShuffle() {
        return shuffle;
    }

    public static boolean isRepeat() {
        return repeat;
    }


    public static void stopService() {

        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }

        try {
            if (mNotifyManager != null) {
                mNotifyManager.cancel(notificationID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainSongsListFrag.changeButton();

    }


    //---------for sleep timer
    BroadcastReceiver buttonplaypauseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer.isPlaying()) {
                try {
                    //stopService();
                    context.sendBroadcast(new Intent(ConstantData.broadcastPlayerPause));
                    SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                    editor.putString(ConstantData.strSleepTimer, "false");
                    editor.commit();
                } catch (Exception e) {
                }
            }
        }
    };
    //-----------------------for app widget=------------------------------

    BroadcastReceiver widgetplaypauseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.requestAudioFocus(changeListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            if (mediaPlayer != null) {
                if (isPng()) {
                    try {
                        pausePlayer();
                    } catch (Exception e) {
                        stopService();
                    }
                } else {
                    try {
                        if (mediaPlayer != null && mediaPlayer.getDuration() > 0) {
                            playerStart();
                        } else {
                            playSong();
                        }
                    } catch (Exception e) {
                        playSong();
                    }
                }
            } else {

                playSong();
            }
        }
    };

    BroadcastReceiver widgetnextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mediaPlayer != null) {
                mediaPlayer.reset();
                playNext();
            }

        }
    };

    BroadcastReceiver widgetprevReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mediaPlayer != null) {
                playPrev();
            }

        }
    };

    BroadcastReceiver widgetpauseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mediaPlayer != null) {
                pausePlayer();
            }

        }
    };

}