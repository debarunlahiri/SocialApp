package com.lahiriproductions.socialapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.activities.SoundRecordingsActivity;
import com.lahiriproductions.socialapp.adapter.StopWatchLapAdapter;
import com.lahiriproductions.socialapp.models.StopWatchLap;
import com.lahiriproductions.socialapp.utils.Constants;
import com.lahiriproductions.socialapp.utils.MyBroadcastService;
import com.lahiriproductions.socialapp.utils.Variables;
import com.yashovardhan99.timeit.Stopwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StopWatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StopWatchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = StopWatchFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tvStopWatch, tvSelectedAudioName;

    private com.yashovardhan99.timeit.Stopwatch stopwatch;

    private long lapMilLi = 0;

    private Context mContext;

    private RecyclerView rvStopWatchLap;
    private StopWatchLapAdapter stopWatchLapAdapter;
    private List<StopWatchLap> stopWatchLapList = new ArrayList<>();

    private ImageButton ibPause;
    private Button ibPlayPause, ibReset;
    private Button bSetLap, bSoundRecordings;
    private boolean isPaused = false;
    private boolean isStarted = false;
    private boolean isReset = false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences2;
    private SharedPreferences.Editor editor;
    private String selectedAudioPath;
    private MediaPlayer mediaPlayer, randomMP;
    private boolean isDurationMessageShown = false;
    private boolean isSelectedAudio = false;
    private Ringtone ringtone;
    private Uri ringtoneUri;
    private String selectedAudioPath2;

    List<Integer> soundList = new ArrayList<Integer>();



    public StopWatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StopWatchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StopWatchFragment newInstance(String param1, String param2) {
        StopWatchFragment fragment = new StopWatchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stop_watch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        sharedPreferences = mContext.getSharedPreferences(Constants.SELECTED_AUDIO, Context.MODE_PRIVATE);
        sharedPreferences2 = mContext.getSharedPreferences(Constants.SELECTED_RINGTONE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        tvStopWatch = view.findViewById(R.id.tvStopWatch);
        ibPlayPause = view.findViewById(R.id.ibPlayPause);
        ibReset = view.findViewById(R.id.ibReset);
        bSetLap = view.findViewById(R.id.bSetLap);
        bSoundRecordings = view.findViewById(R.id.bSoundRecordings);
        ibPause = view.findViewById(R.id.ibPause);
        tvSelectedAudioName = view.findViewById(R.id.tvSelectedAudioName);

        stopwatch = new com.yashovardhan99.timeit.Stopwatch();

        rvStopWatchLap = view.findViewById(R.id.rvStopWatchLap);
        stopWatchLapAdapter = new StopWatchLapAdapter(mContext, stopWatchLapList);
        rvStopWatchLap.setItemAnimator(new DefaultItemAnimator());
        rvStopWatchLap.setLayoutManager(new LinearLayoutManager(mContext));
        rvStopWatchLap.setAdapter(stopWatchLapAdapter);

        tvStopWatch.setText("0.00s");

        tvSelectedAudioName.setVisibility(View.GONE);

        soundList.add(R.raw.ringtone1);
        soundList.add(R.raw.ringtone2);
        soundList.add(R.raw.ringtone3);
        soundList.add(R.raw.ringtone4);
        soundList.add(R.raw.ringtone5);
        soundList.add(R.raw.ringtone6);

        ibPlayPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_play_arrow_24, 0, 0, 0);
        ibPlayPause.setText("Start");
        ibPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (stopwatch.isStarted() && !stopwatch.isPaused()) {
                        stopwatch.pause();
                        ibPlayPause.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_outline_play_arrow_24, 0, 0, 0);
                        ibPlayPause.setText("Resume");
                    } else if (stopwatch.isPaused()) {
                        stopwatch.resume();
                        ibPlayPause.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_outline_pause_24, 0, 0, 0);
                        ibPlayPause.setText("Pause");
                    } else {
                        if (selectedAudioPath != null) {
                            tvSelectedAudioName.setVisibility(View.VISIBLE);
                        } else {
                            tvSelectedAudioName.setVisibility(View.GONE);
                        }
                        stopwatch.start();
                        ibPlayPause.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_outline_pause_24, 0, 0, 0);
                        ibPlayPause.setText("Pause");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                stopwatch.setTextView(tvStopWatch);

            }
        });

        ibPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isPaused = true;
                    stopwatch.pause();
                    stopwatch.setTextView(tvStopWatch);
                    ibPlayPause.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_outline_play_arrow_24, 0, 0, 0);
                    if (selectedAudioPath != null) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                    } else {
                        if (randomMP != null && randomMP.isPlaying()) {
                            randomMP.stop();
                            randomMP = null;
                        }
                        if (ringtone != null && ringtone.isPlaying()) {
                            ringtone.stop();
                        }
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                    }
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ibReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isReset = true;
                    tvStopWatch.setText("0.00s");
                    if (stopwatch.isStarted()) {
                        stopwatch.stop();
                    }
                    if (randomMP != null && randomMP.isPlaying()) {
                        randomMP.stop();
                    }
                    if (ringtone != null && ringtone.isPlaying()) {
                        ringtone.stop();
                    }
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    stopWatchLapList.clear();
                    stopWatchLapAdapter.notifyDataSetChanged();
                    ibPlayPause.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_outline_play_arrow_24, 0, 0, 0);
                    ibPlayPause.setText("Start");
                    lapMilLi = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        stopwatch.setOnTickListener(new Stopwatch.OnTickListener() {
            @Override
            public void onTick(Stopwatch stopwatch) {
                try {
                    if (lapMilLi != 0 && stopwatch.getElapsedTime() >= lapMilLi) {
                        stopWatchLapList.add(new StopWatchLap(stopwatch.getElapsedTime()));
                        stopWatchLapAdapter.setStopWatchLapList(stopWatchLapList);
                        stopwatch.stop();
                        stopwatch.start();
                        if (Variables.isRingtoneOn) {
                            if (randomMP != null && randomMP.isPlaying()) {
                                randomMP.stop();
                            }
                            playRandomSound();
                        } else {
                            if (selectedAudioPath != null) {
                                mediaPlayer.start();
                                Log.e(TAG, "onTick: " + (mediaPlayer.getDuration()));
                                if (mediaPlayer.getDuration() > lapMilLi) {
                                    if (!isDurationMessageShown) {
                                        new MaterialDialog.Builder(getActivity())
                                                .title("Info")
                                                .content("Your audio duration is greater than your lap time. Your sound may get overlap with other lap times.")
                                                .positiveText("Okay")
                                                .show();
                                        isDurationMessageShown = true;
                                    }

                                }
                            } else {
//                                try {
//                                    if (ringtoneUri != null) {
//                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//                                        ringtone = RingtoneManager.getRingtone(mContext, notification);
//                                    }
//                                    ringtone.play();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        bSetLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopwatch.isStarted()) {
                    lapMilLi = stopwatch.getElapsedTime();
                    stopwatch.stop();
                    stopwatch.start();
                    Log.e(TAG, "lapMilLi: " + String.format("%d", lapMilLi));
                } else {
                    Toast.makeText(mContext, "Cannot set lap time when stop watch is paused", Toast.LENGTH_SHORT).show();
                }

            }
        });

        bSoundRecordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SoundRecordingsActivity.class));
            }
        });
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    private void playRandomSound() {
        int randomInt = (new Random().nextInt(soundList.size()));
        int sound = soundList.get(randomInt);
        randomMP = MediaPlayer.create(getActivity(), sound);
        randomMP.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedPreferences.contains(Constants.SELECTED_AUDIO_PATH)) {
            selectedAudioPath = sharedPreferences.getString(Constants.SELECTED_AUDIO_PATH, "");
            tvSelectedAudioName.setText("Selected Audio - " + selectedAudioPath.substring(selectedAudioPath.lastIndexOf("/")+1));
            mediaPlayer = MediaPlayer.create(mContext, Uri.parse(selectedAudioPath));
            isSelectedAudio = true;
        } else if (sharedPreferences2.contains(Constants.SELECTED_RINGTONE)) {
            selectedAudioPath2 = sharedPreferences.getString(Constants.SELECTED_RINGTONE, "");
            ringtoneUri = Uri.parse(selectedAudioPath2);
            ringtone = RingtoneManager.getRingtone(mContext, ringtoneUri);
            isSelectedAudio = false;
        }
        mContext.registerReceiver(br, new IntentFilter(MyBroadcastService.COUNTDOWN_BR));
        Log.i(TAG, "Registered broacast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(br);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            mContext.unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }
    @Override
    public void onDestroy() {
        mContext.stopService(new Intent(mContext, MyBroadcastService.class));
        Log.i(TAG, "Stopped service");
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            stopwatch.setTextView(tvStopWatch);
        }
    }


}