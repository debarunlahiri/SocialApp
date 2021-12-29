package com.lahiriproductions.socialapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.SoundRecordingsAdapter;
import com.lahiriproductions.socialapp.models.SoundRecordings;

import java.io.File;
import java.util.ArrayList;

public class SoundRecordingsActivity extends AppCompatActivity {

    private Context mContext;

    private Toolbar tbSoundRecording;

    private RecyclerView rvSoundRecording;
    private ArrayList<File> fileArrayList = new ArrayList<>();
    private SoundRecordingsAdapter soundRecordingsAdapter;

    private FloatingActionButton fabAddSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recordings);

        mContext = SoundRecordingsActivity.this;

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
        rvSoundRecording = findViewById(R.id.rvSoundRecording);

        File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            file = new File (this.getExternalFilesDir(null) + "/" + getResources().getString(R.string.app_name));
        } else {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
        }

        fileArrayList = findSong(file);

        soundRecordingsAdapter = new SoundRecordingsAdapter(mContext, fileArrayList);
        rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
        rvSoundRecording.setAdapter(soundRecordingsAdapter);

        fabAddSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SoundRecordingsActivity.this, SoundRecorderActivity.class));
            }
        });



    }

    public ArrayList<File> findSong (File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        if (files != null) {
            for (File singlefile : files) {
                if (singlefile.isDirectory() && !singlefile.isHidden()) {
                    arrayList.addAll(findSong(singlefile));
                } else {
                    if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav") || singlefile.getName().endsWith(".m4a")) {
                        arrayList.add(singlefile);
                    }
                }
            }
        }
        return arrayList;
    }
}