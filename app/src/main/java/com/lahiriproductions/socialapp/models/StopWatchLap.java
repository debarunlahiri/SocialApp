package com.lahiriproductions.socialapp.models;

public class StopWatchLap {
    private long milliseconds;

    public StopWatchLap(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }
}
