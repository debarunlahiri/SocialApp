package com.lahiriproductions.socialapp.activities;

import androidx.annotation.NonNull;
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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.BottomViewPagerAdapter;
import com.lahiriproductions.socialapp.utils.Controller;
import com.lahiriproductions.socialapp.utils.MyBroadcastService;
import com.lahiriproductions.socialapp.utils.NonSwipeableViewPager;
import com.lahiriproductions.socialapp.utils.NotificationEventReceiver;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

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
                        tbMain.setTitle("Stop Watch");
                        vpMain.setCurrentItem(0, false);
                        return true;

                    case R.id.youtube_menu_home_list_item:
                        tbMain.setTitle("Youtube");
                        vpMain.setCurrentItem(1, false);
                        return true;


                    case R.id.radio_menu_home_list_item:
                        tbMain.setTitle("Radio");
                        vpMain.setCurrentItem(2, false);
                        return true;


                    case R.id.group_chat_menu_home_list_item:
                        tbMain.setTitle("Group Chat");
                        vpMain.setCurrentItem(3, false);
                        return true;


                    case R.id.events_menu_home_list_item:
                        tbMain.setTitle("Events");
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
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile_menu_home_list_item:
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                profileIntent.putExtra("isEdit", true);
                startActivity(profileIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
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