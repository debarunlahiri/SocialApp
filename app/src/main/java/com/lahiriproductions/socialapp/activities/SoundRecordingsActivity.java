package com.lahiriproductions.socialapp.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lahiriproductions.socialapp.AppInterface.ApiCalls;
import com.lahiriproductions.socialapp.AppInterface.ApiInterface;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.OtherSoundRecordingsAdapter;
import com.lahiriproductions.socialapp.adapter.QitoRingtoneAdapter;
import com.lahiriproductions.socialapp.adapter.SoundRecordingsAdapter;
import com.lahiriproductions.socialapp.models.QitoRingtone;
import com.lahiriproductions.socialapp.models.SoundRecordings;
import com.lahiriproductions.socialapp.utils.Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SoundRecordingsActivity extends AppCompatActivity implements SoundRecordingsAdapter.OnItemClickListener, OtherSoundRecordingsAdapter.OnItemClickListener {

    private static final String TAG = SoundRecordingsActivity.class.getSimpleName();
    private Context mContext;

    private Toolbar tbSoundRecording;

    private RecyclerView rvSoundRecording;
    private ArrayList<File> fileArrayList = new ArrayList<>();
    private List<SoundRecordings> soundRecordingsList = new ArrayList<>();
    private List<String> qitoRingtoneList = new ArrayList<>();
    private SoundRecordingsAdapter soundRecordingsAdapter;
    private OtherSoundRecordingsAdapter otherSoundRecordingsAdapter;
    private QitoRingtoneAdapter qitoRingtoneAdapter;

    private FloatingActionButton fabAddSound;

    private String list_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recordings);

        mContext = SoundRecordingsActivity.this;

        if (getIntent() != null) {
            list_type = getIntent().getStringExtra("list_type");
        }

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

        if (list_type == null) {
            fabAddSound.setVisibility(View.VISIBLE);
            soundRecordingsAdapter = new SoundRecordingsAdapter(mContext, fileArrayList, this::onItemClick);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(soundRecordingsAdapter);
            getFaltuSongsList("4");
        } else if (list_type.equalsIgnoreCase("qito_mix")) {
            fabAddSound.setVisibility(View.VISIBLE);
            soundRecordingsAdapter = new SoundRecordingsAdapter(mContext, fileArrayList, this::onItemClick);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(soundRecordingsAdapter);
        } else if (list_type.equalsIgnoreCase("tabu_mix")) {
            fabAddSound.setVisibility(View.GONE);
            qitoRingtoneAdapter = new QitoRingtoneAdapter(mContext, qitoRingtoneList);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(qitoRingtoneAdapter);
            getFaltuSongsList("");
        } else if (list_type.equalsIgnoreCase("normal")) {
            fabAddSound.setVisibility(View.GONE);
            qitoRingtoneAdapter = new QitoRingtoneAdapter(mContext, qitoRingtoneList);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(qitoRingtoneAdapter);
            getFaltuSongsList("1");
        } else if (list_type.equalsIgnoreCase("adult")) {
            fabAddSound.setVisibility(View.GONE);
            qitoRingtoneAdapter = new QitoRingtoneAdapter(mContext, qitoRingtoneList);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(qitoRingtoneAdapter);
            getFaltuSongsList("2");
        } else if (list_type.equalsIgnoreCase("sound")) {
            fabAddSound.setVisibility(View.GONE);
            qitoRingtoneAdapter = new QitoRingtoneAdapter(mContext, qitoRingtoneList);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(qitoRingtoneAdapter);
            getFaltuSongsList("3");
        } else {
            fabAddSound.setVisibility(View.GONE);
            otherSoundRecordingsAdapter = new OtherSoundRecordingsAdapter(mContext, soundRecordingsList);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(otherSoundRecordingsAdapter);
            listRingtones();
        }



        fabAddSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SoundRecordingsActivity.this, SoundRecorderActivity.class));
            }
        });


        loadLocalRingtonesUris();

    }

    private void getFaltuSongsList(String api_number) {
        try {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiInterface.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            ApiCalls apiCalls = retrofit.create(ApiCalls.class);
            Call<QitoRingtone> qitoRingtoneCall = apiCalls.getRingtones(api_number);
            qitoRingtoneCall.enqueue(new Callback<QitoRingtone>() {
                @Override
                public void onResponse(Call<QitoRingtone> call, Response<QitoRingtone> response) {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponse: " + response.body().getData());
                        qitoRingtoneList.addAll(response.body().getData());
                        qitoRingtoneAdapter.setQitoRingtoneList(qitoRingtoneList);
                    } else {
                        Log.e(TAG, "onResponseFailure: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<QitoRingtone> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + new Gson().toJson(t));

                    Toast.makeText(mContext, "Internal server error", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listRingtones() {
        soundRecordingsList.clear();
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        while (cursor.moveToNext()) {
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            Log.e(TAG, "listRingtones: " + title + " " + uri);
            // Do something with the title and the URI of ringtone
            soundRecordingsList.add(new SoundRecordings(title, uri));
        }
        otherSoundRecordingsAdapter.setSoundRecordingsList(soundRecordingsList);
    }

    private List<Uri> loadLocalRingtonesUris() {
        List<Uri> alarms = new ArrayList<>();
        try {
            RingtoneManager ringtoneMgr = new RingtoneManager(mContext);
            ringtoneMgr.setType(RingtoneManager.TYPE_RINGTONE);

            Cursor alarmsCursor = ringtoneMgr.getCursor();
            int alarmsCount = alarmsCursor.getCount();
            if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
                alarmsCursor.close();
                return null;
            }

            while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
                int currentPosition = alarmsCursor.getPosition();
                alarms.add(ringtoneMgr.getRingtoneUri(currentPosition));
            }
            Log.e(TAG, "loadLocalRingtonesUris: " + new Gson().toJson(alarms));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return alarms;
    }

    public ArrayList<File> findSong(File file) {
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

    @Override
    public void onItemClick(File soundRecordings) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            file = new File(this.getExternalFilesDir(null) + "/" + getResources().getString(R.string.app_name));
        } else {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
        }
        if (list_type == null) {
            fileArrayList.clear();
            soundRecordingsAdapter.notifyDataSetChanged();
            fileArrayList = findSong(file);
            soundRecordingsAdapter.setSoundRecordingsList(fileArrayList);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Controller.mediaPlayerRecordings != null && Controller.mediaPlayerRecordings.isPlaying()) {
            Controller.mediaPlayerRecordings.stop();
            Controller.mediaPlayerRecordings = null;
        }

        if (Controller.ringtone != null && Controller.ringtone.isPlaying()) {
            Controller.ringtone.stop();
            Controller.ringtone = null;
        }
    }
}