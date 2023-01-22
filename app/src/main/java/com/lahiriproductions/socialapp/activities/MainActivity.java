package com.lahiriproductions.socialapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lahiriproductions.socialapp.AppInterface.ApiInterface;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.BottomViewPagerAdapter;
import com.lahiriproductions.socialapp.utils.Constants;
import com.lahiriproductions.socialapp.utils.Controller;
import com.lahiriproductions.socialapp.utils.MyBroadcastService;
import com.lahiriproductions.socialapp.utils.NonSwipeableViewPager;
import com.lahiriproductions.socialapp.utils.NotificationEventReceiver;
import com.lahiriproductions.socialapp.utils.Variables;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CHANNEL_ID = "3";

    private Toolbar tbMain;


    private BottomNavigationView bottomNavigationView;
    private NonSwipeableViewPager vpMain;

    private BottomViewPagerAdapter bottomViewPagerAdapter;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private Context mContext;
    private Uri ringtone;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        sharedPreferences = getSharedPreferences(Constants.SELECTED_RINGTONE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        tbMain = findViewById(R.id.tbMain);
        tbMain.setTitleTextColor(Color.WHITE);
        tbMain.setTitle("Stop Watch");
        setSupportActionBar(tbMain);

        Controller.mainActivity = MainActivity.this;



        startService(new Intent(this, MyBroadcastService.class));
        Log.i(TAG, "Started service");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        vpMain = findViewById(R.id.vpMain);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl(getString(R.string.storage_reference_url));

        bottomViewPagerAdapter = new BottomViewPagerAdapter(getSupportFragmentManager());
        vpMain.setAdapter(bottomViewPagerAdapter);
        vpMain.setOffscreenPageLimit(5);

        if (currentUser == null) {
            sendToMain();
        } else {
            Log.e(TAG, "onCreate: " + Controller.getUserStatus(mContext, mAuth.getCurrentUser().getUid()));
            if (!Controller.getUserStatus(mContext, mAuth.getCurrentUser().getUid())) {
                Toast.makeText(MainActivity.this, "You have been blocked", Toast.LENGTH_SHORT).show();
                Controller.logout(mContext, mAuth);
            }
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        String age = snapshot.child("age").getValue().toString();
                        if (!snapshot.child("profile_image").exists()) {
                            insertUser(name, email, age, null);
                        } else {
                            String profile_image = snapshot.child("profile_image").getValue().toString();
                            insertUser(name, email, age, profile_image);
                        }
                        Log.e(TAG, "onDataChange: " + email);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            mDatabase.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        sendToProfile();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.stop_watch_menu_home_list_item:
                        tbMain.setTitle("Taki Tym");
                        vpMain.setCurrentItem(0, false);
                        return true;

                    case R.id.youtube_menu_home_list_item:
                        tbMain.setTitle("Kani-Kani Videos");
                        vpMain.setCurrentItem(1, false);
                        return true;


                    case R.id.radio_menu_home_list_item:
                        tbMain.setTitle("Talanoa Radio");
                        vpMain.setCurrentItem(2, false);
                        return true;


                    case R.id.group_chat_menu_home_list_item:
                        tbMain.setTitle("Taki Music");
                        vpMain.setCurrentItem(3, false);
                        return true;


                    case R.id.events_menu_home_list_item:
                        tbMain.setTitle("Magiti Events");
                        vpMain.setCurrentItem(4, false);
                        return true;

                }
                return false;
            }
        });

        mDatabase.child("notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String sender_user_id = snapshot.child("sender_user_id").getValue().toString();
                    String message = snapshot.child("message").getValue().toString();
                    long timestamp = (long) snapshot.child("timestamp").getValue();
                    String type = snapshot.child("type").getValue().toString();

                    if (currentUser.getUid() != sender_user_id) {
                        if (Math.abs(timestamp - System.currentTimeMillis()) <= 60000) {
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle(getResources().getString(R.string.app_name))
                                    .setContentText(message)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationEventReceiver.setupAlarm(getApplicationContext());

                            // Create the NotificationChannel, but only on API 26+ because
                            // the NotificationChannel class is new and not in the support library
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                CharSequence name = getString(R.string.channel_name);
                                String description = getString(R.string.channel_description);
                                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                                channel.setDescription(description);
                                // Register the channel with the system; you can't change the importance
                                // or other notification behaviors after this
                                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                notificationManager.createNotificationChannel(channel);
                                notificationManager.notify(12, builder.build());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void insertUser(String name, String email, String age, String profile_image) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiInterface.API_INSERT_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    Log.e(TAG, "onResponse: " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Internal server error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onErrorResponse: ", error);
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("phone", mAuth.getCurrentUser().getPhoneNumber());
                params.put("email", email);
                params.put("user_id", mAuth.getCurrentUser().getUid());
                params.put("age", age);
                params.put("image", profile_image);
                params.put("username", mAuth.getCurrentUser().getPhoneNumber());
                params.put("password", "password");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
//        if (vpMain.getCurrentItem() == 1) {
//            menu.findItem(R.id.baby_mix_stop_menu_list_item).setVisible(true);
//            menu.findItem(R.id.tabu_mix_stop_menu_list_item).setVisible(true);
//            menu.findItem(R.id.qito_mix_stop_menu_list_item).setVisible(true);
//            menu.findItem(R.id.kece_mix_stop_menu_list_item).setVisible(true);
//        } else {
//            menu.findItem(R.id.baby_mix_stop_menu_list_item).setVisible(false);
//            menu.findItem(R.id.tabu_mix_stop_menu_list_item).setVisible(false);
//            menu.findItem(R.id.qito_mix_stop_menu_list_item).setVisible(false);
//            menu.findItem(R.id.kece_mix_stop_menu_list_item).setVisible(false);
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent soundRecordingIntent = new Intent(MainActivity.this, SoundRecordingsActivity.class);
        switch (item.getItemId()) {
            case R.id.baby_mix_stop_menu_list_item:
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 5);
                Variables.isRingtoneOn = false;
                return true;

            case R.id.tabu_mix_stop_menu_list_item:
                soundRecordingIntent.putExtra("list_type", "tabu_mix");
                startActivity(soundRecordingIntent);
                Variables.isRingtoneOn = false;
                return true;

            case R.id.qito_mix_stop_menu_list_item:
                soundRecordingIntent.putExtra("list_type", "qito_mix");
                startActivity(soundRecordingIntent);
                Variables.isRingtoneOn = false;
                return true;

            case R.id.kece_mix_stop_menu_list_item:
//                soundRecordingIntent.putExtra("list_type", "kece_mix");
//                startActivity(soundRecordingIntent);
                Variables.isRingtoneOn = true;
                Toast.makeText(mContext, "Random ringtone enabled", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.normal_stop_menu_list_item:
                soundRecordingIntent.putExtra("list_type", "normal");
                startActivity(soundRecordingIntent);
                Variables.isRingtoneOn = false;
                return true;

            case R.id.adult_stop_menu_list_item:
                soundRecordingIntent.putExtra("list_type", "adult");
                startActivity(soundRecordingIntent);
                Variables.isRingtoneOn = false;
                return true;

            case R.id.sound_stop_menu_list_item:
                soundRecordingIntent.putExtra("list_type", "sound");
                startActivity(soundRecordingIntent);
                Variables.isRingtoneOn = false;
                return true;

            case R.id.edit_profile_stop_menu_list_item:
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                profileIntent.putExtra("isEdit", true);
                startActivity(profileIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 5:
                    ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    editor.putString(Constants.SELECTED_RINGTONE, String.valueOf(ringtone));
                    editor.apply();
                    // Toast.makeText(getBaseContext(),RingtoneManager.URI_COLUMN_INDEX,
                    // Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    }

    private void sendToProfile() {
        Intent mainIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(mainIntent);
        finishAffinity();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }
}

//{"status":1,"message":"successfully register;","data":[{"phone":"+919205225428","email":"debarunlahiri2011@gmail.com","username":"+919205225428","user_id":"dhPH352UKwg31IGExhvkq5qhbF72","image":"DataSnapshot { key = profile_image, value = https:\/\/firebasestorage.googleapis.com\/v0\/b\/takitym-2e39c.appspot.com\/o\/users%2Fprofiles%2Fprofile_images%2FdhPH352UKwg31IGExhvkq5qhbF72.jpg?alt=media&token=14fc2ebe-9def-43ca-a72e-3eea291d58a0 }","token":47232}]}