package com.lahiriproductions.socialapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.activities.SoundRecordingsActivity;
import com.lahiriproductions.socialapp.adapter.StopWatchLapAdapter;
import com.lahiriproductions.socialapp.models.StopWatchLap;
import com.lahiriproductions.socialapp.utils.MyBroadcastService;
import com.yashovardhan99.timeit.Stopwatch;

import java.util.ArrayList;
import java.util.List;


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

    private TextView tvStopWatch, tvSoundRecordings;

    private com.yashovardhan99.timeit.Stopwatch stopwatch;

    private long lapMilLi = 0;

    private Context mContext;

    private RecyclerView rvStopWatchLap;
    private StopWatchLapAdapter stopWatchLapAdapter;
    private List<StopWatchLap> stopWatchLapList = new ArrayList<>();

    private ImageButton ibPlayPause, ibReset, ibPause;
    private Button bSetLap;
    private boolean isPaused = false;
    private boolean isStarted = false;
    private boolean isReset = false;


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

        tvStopWatch = view.findViewById(R.id.tvStopWatch);
        ibPlayPause = view.findViewById(R.id.ibPlayPause);
        ibReset = view.findViewById(R.id.ibReset);
        bSetLap = view.findViewById(R.id.bSetLap);
        tvSoundRecordings = view.findViewById(R.id.tvSoundRecordings);
        ibPause = view.findViewById(R.id.ibPause);

        stopwatch = new com.yashovardhan99.timeit.Stopwatch();

        rvStopWatchLap = view.findViewById(R.id.rvStopWatchLap);
        stopWatchLapAdapter = new StopWatchLapAdapter(mContext, stopWatchLapList);
        rvStopWatchLap.setItemAnimator(new DefaultItemAnimator());
        rvStopWatchLap.setLayoutManager(new LinearLayoutManager(mContext));
        rvStopWatchLap.setAdapter(stopWatchLapAdapter);

        tvStopWatch.setText("0.00s");

        ibPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (stopwatch.isStarted() && !stopwatch.isPaused()) {
                        stopwatch.pause();
                        ibPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_outline_play_arrow_24));
                    } else if (stopwatch.isPaused()) {
                        stopwatch.resume();
                        ibPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_outline_pause_24));
                    } else {
                        stopwatch.start();
                        ibPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_outline_pause_24));
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
                isPaused = true;
                stopwatch.pause();
                stopwatch.setTextView(tvStopWatch);
                ibPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_outline_play_arrow_24));
            }
        });

        ibReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReset = true;
                tvStopWatch.setText("0.00s");
                stopwatch.stop();
            }
        });

        stopwatch.setOnTickListener(new Stopwatch.OnTickListener() {
            @Override
            public void onTick(Stopwatch stopwatch) {
                if (lapMilLi != 0 && stopwatch.getElapsedTime() >= lapMilLi) {
                    stopWatchLapList.add(new StopWatchLap(stopwatch.getElapsedTime()));
                    stopWatchLapAdapter.setStopWatchLapList(stopWatchLapList);
                    stopwatch.stop();
                    stopwatch.start();
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

        tvSoundRecordings.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onResume() {
        super.onResume();
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