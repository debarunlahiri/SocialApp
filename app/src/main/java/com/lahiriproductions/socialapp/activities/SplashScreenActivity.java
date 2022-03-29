package com.lahiriproductions.socialapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.lahiriproductions.socialapp.AppService.MyService;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.data_helper.DataBaseHelper;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.utils.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {

    Context context;
    Activity activity;
    private static final int permissionRequestCode = 1;
    int storagePermission = -1, phoneStatePermission = -1;
    ArrayList<String> permissionsList;
    DataBaseHelper dataBaseHelper;
    boolean isFinish = true;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        MyService myService = new MyService();
        myService.onCreate();

        context = this;
        activity = this;

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Controller.getUserStatus(SplashScreenActivity.this, mAuth.getCurrentUser().getUid());
        }

        if (Build.VERSION.SDK_INT >= 23) {
            phoneStatePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (/*phone_state_permission == PackageManager.PERMISSION_DENIED ||*/ storagePermission == PackageManager.PERMISSION_DENIED) {
                permissionWrapper();
            } else {
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                new loadQueue().execute();
                            }
                        }, 100
                );
            }
        } else {
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            new loadQueue().execute();
                        }
                    }, 100
            );
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 3000);
    }

    private class loadQueue extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            dataBaseHelper = new DataBaseHelper(SplashScreenActivity.this);
            try {
                smallBackgroundImage();
                mainBackgroundImage();
                ConstantData.mediaItemsArrayList = dataBaseHelper.getQueueData(SplashScreenActivity.this);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            //  Intent intent = new Intent(context, SongsMainActivity.class);

            // mainAct();
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish();
            } else {
                Controller.getUserStatus(SplashScreenActivity.this, mAuth.getCurrentUser().getUid());
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }

        }

        @Override
        protected void onPreExecute() {
        }
    }

    private void permissionWrapper() {
        List<String> permissionsNeeded = new ArrayList<String>();

        permissionsList = new ArrayList<String>();

        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("External Storage");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                requestPermission();
                return;
            }
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), permissionRequestCode);
            return;
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);

            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return false;
        }
        return true;
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case permissionRequestCode: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (perms.get(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    new loadQueue().execute();
                    activity.finish();
                } else {
                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    new loadQueue().execute();
                                }
                            }, 50
                    );
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static void mainBackgroundImage() {
        ConstantData.integerArrayList.clear();
        ConstantData.integerArrayList.add(R.drawable.gradient1);

    }

    public static void smallBackgroundImage() {
        ConstantData.integerArrayList_small.clear();
        ConstantData.integerArrayList_small.add(R.drawable.gradient1);

    }

}