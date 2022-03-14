package com.lahiriproductions.socialapp.utils_data;

import android.app.Activity;
import android.os.Build.VERSION;
import android.view.Window;

public class CurrentVideoPlayer {
    VideoPlayerView view;

    public interface VideoPlayerView {
        void hideStatusBar();

        void showStatusBar();
    }

    public CurrentVideoPlayer(VideoPlayerView view) {
        this.view = view;
    }

    public void initSystembar(Activity act) {
        if (VERSION.SDK_INT >= 21) {
            Window window = act.getWindow();
            window.clearFlags(67108864);
            window.getDecorView().setSystemUiVisibility(1280);
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(0);
            this.view.showStatusBar();
        } else if (VERSION.SDK_INT >= 19) {
            act.getWindow().addFlags(67108864);
            this.view.showStatusBar();
        } else {
            this.view.hideStatusBar();
        }
    }
}
