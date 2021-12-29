package com.lahiriproductions.socialapp.utils;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.yashovardhan99.timeit.Stopwatch;

public class MyBroadcastService extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "your_package_name.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;

    private com.yashovardhan99.timeit.Stopwatch stopwatch;

    private long lapMilLi = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        stopwatch = new com.yashovardhan99.timeit.Stopwatch();
        Log.i(TAG, "Starting timer...");
        stopwatch.setOnTickListener(new Stopwatch.OnTickListener() {
            @Override
            public void onTick(Stopwatch stopwatch) {
                if (lapMilLi != 0 && stopwatch.getElapsedTime() >= lapMilLi) {
                    stopwatch.stop();
                    stopwatch.start();

                }
            }
        });
        stopwatch.start();

    }

    @Override
    public void onDestroy() {

        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}