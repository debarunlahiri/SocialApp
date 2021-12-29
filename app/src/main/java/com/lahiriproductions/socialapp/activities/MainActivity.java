package com.lahiriproductions.socialapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.BottomViewPagerAdapter;
import com.lahiriproductions.socialapp.utils.MyBroadcastService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private ViewPager vpMain;

    private Toolbar tbMain;

    private BottomViewPagerAdapter bottomViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbMain = findViewById(R.id.tbMain);
        tbMain.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tbMain);

        startService(new Intent(this, MyBroadcastService.class));
        Log.i(TAG, "Started service");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        vpMain = findViewById(R.id.vpMain);

        bottomViewPagerAdapter = new BottomViewPagerAdapter(getSupportFragmentManager());
        vpMain.setAdapter(bottomViewPagerAdapter);
        vpMain.setOffscreenPageLimit(5);

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

//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.flMain, new StopWatchFragment(), "StopWatchFragment")
//                .disallowAddToBackStack()
//                .commit();

    }
}