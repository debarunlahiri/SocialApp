package com.lahiriproductions.socialapp.utils;

import static android.accounts.AccountManager.KEY_PASSWORD;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.Api;
import com.lahiriproductions.socialapp.AppInterface.ApiCalls;
import com.lahiriproductions.socialapp.AppInterface.ApiInterface;
import com.lahiriproductions.socialapp.activities.LoginActivity;
import com.lahiriproductions.socialapp.activities.MainActivity;
import com.lahiriproductions.socialapp.models.UserStatus.UserStatus;
import com.lahiriproductions.socialapp.models.UserStatus.UserStatusData;
import com.lahiriproductions.socialapp.models.UserStatusRequest;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller {

    private static final String TAG = Controller.class.getSimpleName();
    public static Activity mainActivity;
    public static Ringtone ringtone;
    public static MediaPlayer mediaPlayerRecordings;
    public static boolean isStatus = false;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


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

    public static String removeQuotesAndUnescape(String uncleanJson) {
        String noQuotes = uncleanJson.replaceAll("^\"|\"$", "");

        return StringEscapeUtils.unescapeJava(noQuotes);
    }

    public static boolean getUserStatus(Context mContext, String user_id) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user_status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiInterface.API_GET_USER_STATUS, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObjectUserStatus = jsonObject.getJSONObject("data");
                        if (jsonObjectUserStatus != null) {
                            Log.e(TAG, "onResponsedasda: " + jsonObjectUserStatus);
                            String status = jsonObjectUserStatus.getString("user_status");
                            if (status.equalsIgnoreCase("1")) {
                                isStatus = true;
                            } else {
                                isStatus = false;
                            }
                        } else {
                            isStatus = true;
                        }
                        editor.putBoolean("is_status", isStatus);
                        editor.apply();
                        Log.e(TAG, "onResponse: " + jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onFailure: ", error);
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
        if (sharedPreferences.contains("is_status")) {
            return sharedPreferences.getBoolean("is_status", true);
        } else {
            return true;
        }
    }

    public static void logout(Context mContext, FirebaseAuth mAuth) {
        mAuth.signOut();
        Intent loginIntent = new Intent(mContext, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(loginIntent);
    }
}
