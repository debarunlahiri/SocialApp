package com.lahiriproductions.socialapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lahiriproductions.socialapp.R;

public class SoundRecordingsActivity extends AppCompatActivity {

    private Toolbar tbSoundRecording;

    private FloatingActionButton fabAddSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recordings);

        tbSoundRecording = findViewById(R.id.tbSoundRecording);
        tbSoundRecording.setTitle("Sound Recordings");
        tbSoundRecording.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tbSoundRecording);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tbSoundRecording.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_white_arrow_back_24));
        tbSoundRecording.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fabAddSound = findViewById(R.id.fabAddSound);

        fabAddSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SoundRecordingsActivity.this, SoundRecorderActivity.class));
            }
        });
    }
}