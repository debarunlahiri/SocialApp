package com.lahiriproductions.socialapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuckerteam.chucker.api.ChuckerInterceptor;
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

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SoundRecordingsActivity extends AppCompatActivity implements SoundRecordingsAdapter.OnItemClickListener, OtherSoundRecordingsAdapter.OnItemClickListener, QitoRingtoneAdapter.OnItemClickListener {

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
    private String recording_header;
    private MediaPlayer mediaPlayer;
    private int playingPosition;
    private QitoRingtoneAdapter.ViewHolder playingHolder;
    private int currentPlayingPosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recordings);

        mContext = SoundRecordingsActivity.this;

        if (getIntent() != null) {
            list_type = getIntent().getStringExtra("list_type");
            recording_header = getIntent().getStringExtra("recording_header");


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
            soundRecordingsAdapter = new SoundRecordingsAdapter(mContext, fileArrayList, this);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(soundRecordingsAdapter);
//            getFaltuSongsList("4");
        } else if (list_type.equalsIgnoreCase("qito_mix")) {
            fabAddSound.setVisibility(View.VISIBLE);
            soundRecordingsAdapter = new SoundRecordingsAdapter(mContext, fileArrayList, this);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(soundRecordingsAdapter);
            tbSoundRecording.setTitle("Qito Mix");
            soundRecordingsAdapter.setListType(list_type);
        } else if (list_type.equalsIgnoreCase("tabu_mix")) {
            tbSoundRecording.setTitle("Tabu Mix 18+");
            fabAddSound.setVisibility(View.GONE);
            qitoRingtoneAdapter = new QitoRingtoneAdapter(mContext, qitoRingtoneList, this);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(qitoRingtoneAdapter);
            qitoRingtoneAdapter.setListType(list_type);
            getFaltuSongsList(0);
        } else if (list_type.equalsIgnoreCase("kece_mix")) {
            tbSoundRecording.setTitle("Kece Mix");
            fabAddSound.setVisibility(View.GONE);
            qitoRingtoneAdapter = new QitoRingtoneAdapter(mContext, qitoRingtoneList, this);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(qitoRingtoneAdapter);
            qitoRingtoneAdapter.setListType(list_type);
            getFaltuSongsList(1);
        } else if (list_type.equalsIgnoreCase("normal")) {
            tbSoundRecording.setTitle("Normal Mix");
            fabAddSound.setVisibility(View.GONE);
            qitoRingtoneAdapter = new QitoRingtoneAdapter(mContext, qitoRingtoneList, this);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(qitoRingtoneAdapter);
            qitoRingtoneAdapter.setListType(list_type);
            getFaltuSongsList(2);
        } else if (list_type.equalsIgnoreCase("adult")) {
            tbSoundRecording.setTitle("Adult Mix");
            fabAddSound.setVisibility(View.GONE);
            qitoRingtoneAdapter = new QitoRingtoneAdapter(mContext, qitoRingtoneList, this);
            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
            rvSoundRecording.setAdapter(qitoRingtoneAdapter);
            qitoRingtoneAdapter.setListType(list_type);
            getFaltuSongsList(3);
        }
//        else if (list_type.equalsIgnoreCase("sound")) {
//            fabAddSound.setVisibility(View.GONE);
//            qitoRingtoneAdapter = new QitoRingtoneAdapter(mContext, qitoRingtoneList);
//            rvSoundRecording.setLayoutManager(new LinearLayoutManager(mContext));
//            rvSoundRecording.setAdapter(qitoRingtoneAdapter);
//            getFaltuSongsList("3");
//        }
        else {
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

    private void getFaltuSongsList(int api_number) {
        try {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new ChuckerInterceptor(mContext))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiInterface.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            ApiCalls apiCalls = retrofit.create(ApiCalls.class);
            Call<QitoRingtone> qitoRingtoneCall = null;
            if (api_number == 0) {
                qitoRingtoneCall = apiCalls.getRingtones();
            } else if (api_number == 1) {
                qitoRingtoneCall = apiCalls.getRingtones1();
            } else if (api_number == 2) {
                qitoRingtoneCall = apiCalls.getRingtones2();
            } else if (api_number == 3) {
                qitoRingtoneCall = apiCalls.getRingtones3();
            }
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
    public void onItemDelete(File soundRecordings, int position, List<File> soundRecordingsList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Recording?");
        builder.setMessage("Are you sure you want to delete recording?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFile(soundRecordings, position);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void deleteFile(File soundRecordings, int position) {
        File fdelete = new File(soundRecordings.getPath());
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                soundRecordingsList.clear();
                soundRecordingsAdapter.notifyDataSetChanged();
                getSoundRecordings();
            } else {
                System.out.println("file not Deleted :" + soundRecordings.getPath());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSoundRecordings();
    }

    private void getSoundRecordings() {
        File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            file = new File(this.getExternalFilesDir(null) + "/" + getResources().getString(R.string.app_name));
        } else {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
        }
        if (list_type.equalsIgnoreCase("qito_mix")) {
            fileArrayList.clear();
            soundRecordingsAdapter.notifyDataSetChanged();
            fileArrayList = findSong(file);
            Log.e(TAG, "onResume: fileArrayList " + fileArrayList);
            soundRecordingsAdapter.setSoundRecordingsList(fileArrayList);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updatePlaying();
    }

    private void updatePlaying() {
        try {
            if (Controller.mediaPlayerRecordings != null) {
                Controller.mediaPlayerRecordings.stop();
                Controller.mediaPlayerRecordings = null;
            }

            if (Controller.ringtone != null) {
                if (Controller.ringtone.isPlaying()) {
                    Controller.ringtone.stop();
                    Controller.ringtone = null;
                }
            }
//            Controller.mediaPlayerRecordings = null;
        } catch (Exception e) {
            Toast.makeText(mContext, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onBackPressed: ", e);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            updatePlaying();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(String string, QitoRingtoneAdapter.ViewHolder holder, int position, List<String> qitoRingtoneList) {
        try {
            if (position == currentPlayingPosition) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    startMediaPlayer(string, holder, true);
                }
            } else {
                currentPlayingPosition = position;
                if (mediaPlayer != null) {
                    if (null != holder) {
                        updateNonPlayingView(playingHolder);
                    }
                    mediaPlayer.release();
                }
                playingHolder = holder;
                startMediaPlayer(string, holder, true);
            }
//                    updatePlayingView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMediaPlayer(String audioFile, QitoRingtoneAdapter.ViewHolder holder, boolean isAutoPlayOn) {
        if (mediaPlayer != null) {
            releaseMediaPlayer();

        }
        mediaPlayer = MediaPlayer.create(mContext, Uri.parse(qitoRingtoneList.get(playingPosition)));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playingPosition++;
                if (playingPosition < qitoRingtoneList.size()) {
                    startMediaPlayer(qitoRingtoneList.get(playingPosition), holder, false);
                    updatePlayingView();
                    qitoRingtoneAdapter.notifyItemChanged(playingPosition+1);
                }
            }
        });
        mediaPlayer.start();
        Controller.mediaPlayerRecordings = mediaPlayer;
    }

    private void updatePlayingView() {
//        playingHolder.sbProgress.setMax(mediaPlayer.getDuration());
//        playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
//        playingHolder.sbProgress.setEnabled(true);
//        if (mediaPlayer.isPlaying()) {
//            playingHolder.sbProgress.postDelayed(seekBarUpdater, 100);
//            playingHolder.ivPlayPause.setImageResource(R.drawable.ic_pause);
//        } else {
//            playingHolder.sbProgress.removeCallbacks(seekBarUpdater);
//            playingHolder.ivPlayPause.setImageResource(R.drawable.ic_play_arrow);
//        }
        if (mediaPlayer.isPlaying()) {
            playingHolder.animationView.setVisibility(View.VISIBLE);
            playingHolder.ivMusicArt.setVisibility(View.GONE);
        } else {
            playingHolder.animationView.setVisibility(View.GONE);
            playingHolder.ivMusicArt.setVisibility(View.VISIBLE);
        }

        if (playingPosition < rvSoundRecording.getAdapter().getItemCount()) { // make sure the next position is within the range of the adapter
            QitoRingtoneAdapter.ViewHolder nextHolder = (QitoRingtoneAdapter.ViewHolder) rvSoundRecording.findViewHolderForAdapterPosition(playingPosition++); // get the ViewHolder for the next item
            if (nextHolder != null) { // make sure the ViewHolder exists
                String string = qitoRingtoneList.get(playingPosition++); // get the next item from your data source (e.g. a List)
                nextHolder.bindData(string); // update the ViewHolder with the new data
            }
        }
    }



    private void releaseMediaPlayer() {
        if (null != playingHolder) {
            updateNonPlayingView(playingHolder);
        }
        mediaPlayer.release();
        mediaPlayer = null;
        currentPlayingPosition = -1;
    }

    private void updateNonPlayingView(QitoRingtoneAdapter.ViewHolder holder) {
//        holder.sbProgress.removeCallbacks(seekBarUpdater);
//        holder.sbProgress.setEnabled(false);
//        holder.sbProgress.setProgress(0);
//        holder.ivPlayPause.setImageResource(R.drawable.ic_play_arrow);
//        holder.animationView.setVisibility(View.GONE);
//        holder.ivMusicArt.setVisibility(View.VISIBLE);

    }
}