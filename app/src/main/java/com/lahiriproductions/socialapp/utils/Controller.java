package com.lahiriproductions.socialapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.text.TextUtils;
import android.util.Patterns;
import android.webkit.MimeTypeMap;

import com.google.firebase.database.DatabaseReference;
import com.lahiriproductions.socialapp.activities.LoginActivity;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    public static Activity mainActivity;
    public static Ringtone ringtone;
    public static MediaPlayer mediaPlayerRecordings;

    public static String millisecondsToTime(long milliseconds) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String extractYTId(String ytUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);
        if(matcher.find()){
            return matcher.group();
        } else {
            return "error";
        }
    }

    public static String getMIMEType(String url) {
        String mType = null;
        String mExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (mExtension != null) {
            mType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExtension);
        }
        return mType;
    }

    public static void sendToLogin(Activity mActivity) {
        Intent loginIntent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivity(loginIntent);
        mActivity.finishAffinity();
    }

    public static void sendNotification(DatabaseReference mDatabase, String uid, String type) {
        HashMap<String, Object> mDataMap = new HashMap<>();
        mDataMap.put("sender_user_id", uid);
        mDataMap.put("message", "User has sent a message");
        mDataMap.put("type", type);
        mDataMap.put("timestamp", System.currentTimeMillis());
        mDatabase.child("notifications").setValue(mDataMap);
    }
}
