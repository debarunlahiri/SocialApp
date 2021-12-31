package com.lahiriproductions.socialapp.activities;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.piasy.rxandroidaudio.AudioRecorder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lahiriproductions.socialapp.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SoundRecorderActivity extends AppCompatActivity {

    private static final String TAG = SoundRecorderActivity.class.getSimpleName();

    private Context mContext;

    private Toolbar tbSoundRecorder;

    private Button bStartRecording, bPlayRecording;
    private TextView tvSoundTimeCounter;
    private AudioRecorder mAudioRecorder;
    private File mAudioFile, previousAudioRecordedFile;
    private boolean isPLAYING = false;
    private MediaPlayer mp;
    private com.yashovardhan99.timeit.Stopwatch stopwatch;


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recorder);

        mContext = SoundRecorderActivity.this;

        tbSoundRecorder = findViewById(R.id.tbSoundRecorder);
        tbSoundRecorder.setTitle("Sound Recorder");
        tbSoundRecorder.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tbSoundRecorder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tbSoundRecorder.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_white_arrow_back_24));
        tbSoundRecorder.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            file = new File (this.getExternalFilesDir(null) + "/" + getResources().getString(R.string.app_name));
        } else {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
        }

        if (!file.exists()) {
            file.mkdirs();
        }


        bStartRecording = findViewById(R.id.bStartRecording);
        bPlayRecording = findViewById(R.id.bPlayRecording);
        tvSoundTimeCounter = findViewById(R.id.tvSoundTimeCounter);

        stopwatch = new com.yashovardhan99.timeit.Stopwatch();
        mAudioRecorder = AudioRecorder.getInstance();
        mp = new MediaPlayer();

        bStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(SoundRecorderActivity.this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.MANAGE_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (mAudioRecorder.isStarted()) {
                            bStartRecording.setText("Start Recording");
                            mAudioRecorder.stopRecord();
                            stopwatch.stop();
                            Toast.makeText(mContext, "File saved", Toast.LENGTH_SHORT).show();
                        } else {
                            mAudioFile = new File(file + File.separator + System.nanoTime() + ".mp3");
                            previousAudioRecordedFile = mAudioFile;
                            bStartRecording.setText("Stop Recording");
                            mAudioRecorder.prepareRecord(MediaRecorder.AudioSource.MIC,
                                    MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
                                    mAudioFile);
                            mAudioRecorder.startRecord();
                            stopwatch.start();
                        }
                        stopwatch.setTextView(tvSoundTimeCounter);
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        MultiplePermissionsListener dialogMultiplePermissionsListener =
                                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                                        .withContext(SoundRecorderActivity.this)
                                        .withTitle("Storage & audio permission")
                                        .withMessage("Both storage and audio permission are needed to record and save your recordings.")
                                        .withButtonText(android.R.string.ok)
                                        .withIcon(R.mipmap.ic_launcher_round)
                                        .build();
                        dialogMultiplePermissionsListener.onPermissionRationaleShouldBeShown(permissions, token);
                    }
                }).check();

            }
        });

        bPlayRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    stopPlaying();
                    bPlayRecording.setText("Play Recording");

                } else {
                    startPlay();
                    bPlayRecording.setText("Stop Playing");

                }
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                bPlayRecording.setText("Play Recording");
                isPLAYING = false;
            }
        });
    }

    public void startPlay() {
        if (!isPLAYING) {
            isPLAYING = true;
            try {
                mp.setDataSource(previousAudioRecordedFile.getAbsolutePath());
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                Log.e(TAG, "prepare() failed");
            }
        } else {
            isPLAYING = false;
            stopPlaying();
        }
    }

    private void stopPlaying() {
        mp.release();
        mp = null;
    }
}