package com.lahiriproductions.socialapp.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.lahiriproductions.socialapp.AppInterface.ApiInterface;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.Radio;
import com.lahiriproductions.socialapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dm.audiostreamer.AudioStreamingManager;
import dm.audiostreamer.CurrentSessionCallback;
import dm.audiostreamer.MediaMetaData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RadioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RadioFragment extends Fragment implements MediaPlayer.OnPreparedListener, CurrentSessionCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = RadioFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String radio_url = "http://sc-bb.1.fm:8017/;stream.l7u6";


    private AutoCompleteTextView actRadioCountries;
    private ImageView ivRadioArt, ivRadioBg;
    private ImageButton ibRadioPlayPause, ibRadioNext, ibRadioPrevious, ibRadioQuit;
    private TextView tvRadioName;
    private CardView cvRadioArt;
    private ProgressBar pbRadio;
    private LinearLayout llRadioPlaceHolder;

    private ArrayAdapter<String> radioCountriesArrayAdapter;
    private boolean prepared = false;
    private MediaPlayer mediaPlayer;
    private List<Radio> radioList = new ArrayList<>();
    private int radio_stream_position = 0;
    private boolean isAudioTrackNeedsChange = false;
    private String radio_name;
    private boolean isPrepared = false;
    private String radio_image;
    private CircleLineVisualizer mVisualizer;
    private boolean isAlreadyPlayed = false;
    private Context mContext;
    private AudioStreamingManager streamingManager;
    // creating a variable for exoplayerview.
    PlayerView exoPlayerView;

    // creating a variable for exoplayer
    SimpleExoPlayer exoPlayer;

    public RadioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RadioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RadioFragment newInstance(String param1, String param2) {
        RadioFragment fragment = new RadioFragment();
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
        return inflater.inflate(R.layout.fragment_radio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        //get reference to visualizer
        actRadioCountries = view.findViewById(R.id.actRadioCountries);
        ivRadioArt = view.findViewById(R.id.ivRadioArt);
        ibRadioPlayPause = view.findViewById(R.id.ibRadioPlayPause);
        ivRadioBg = view.findViewById(R.id.ivRadioBg);
        ibRadioNext = view.findViewById(R.id.ibRadioNext);
        ibRadioPrevious = view.findViewById(R.id.ibRadioPrevious);
        tvRadioName = view.findViewById(R.id.tvRadioName);
        ibRadioQuit = view.findViewById(R.id.ibRadioQuit);
        cvRadioArt = view.findViewById(R.id.cvRadioArt);
        pbRadio = view.findViewById(R.id.pbRadio);
        llRadioPlaceHolder = view.findViewById(R.id.llRadioPlaceHolder);
        mVisualizer = view.findViewById(R.id.blast);
        exoPlayerView = view.findViewById(R.id.idExoPlayerVIew);


        streamingManager = AudioStreamingManager.getInstance(mContext);

        radioCountriesArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.radio_countries));
        radioCountriesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actRadioCountries.setAdapter(radioCountriesArrayAdapter);
        radioCountriesArrayAdapter.notifyDataSetChanged();

        mediaPlayer = new MediaPlayer();
//        Glide.with(getActivity()).load(R.drawable.ic_baseline_music_note_24).into(ivRadioArt);
        pbRadio.setVisibility(View.GONE);
        cvRadioArt.setVisibility(View.INVISIBLE);
        tvRadioName.setVisibility(View.INVISIBLE);
        mVisualizer.setVisibility(View.INVISIBLE);

        actRadioCountries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getRadioList(position);
                if ((position == 0)) {
                    radio_stream_position = 0;
                    isAlreadyPlayed = true;
                    cvRadioArt.setVisibility(View.INVISIBLE);
                    tvRadioName.setVisibility(View.INVISIBLE);
                    mVisualizer.setVisibility(View.INVISIBLE);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer = new MediaPlayer();
                    }
                    llRadioPlaceHolder.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    radio_stream_position = 0;
                    isAlreadyPlayed = true;
                    ibRadioNext.setEnabled(true);
                    cvRadioArt.setVisibility(View.VISIBLE);
                    tvRadioName.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    radio_stream_position = 0;
                    isAlreadyPlayed = true;
                    ibRadioNext.setEnabled(true);
                    cvRadioArt.setVisibility(View.VISIBLE);
                    tvRadioName.setVisibility(View.VISIBLE);
                } else if (position == 3) {
                    radio_stream_position = 0;
                    isAlreadyPlayed = true;
                    ibRadioNext.setEnabled(true);
                    cvRadioArt.setVisibility(View.VISIBLE);
                    tvRadioName.setVisibility(View.VISIBLE);
                } else if (position == 4) {
                    radio_stream_position = 0;
                    isAlreadyPlayed = true;
                    ibRadioNext.setEnabled(true);
                    cvRadioArt.setVisibility(View.VISIBLE);
                    tvRadioName.setVisibility(View.VISIBLE);
                }
            }
        });

        ibRadioNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    radio_stream_position++;
                    if (radio_stream_position < radioList.size() && !radioList.isEmpty()) {
                        radio_url = radioList.get(radio_stream_position).getRadio_stream_url();
                        radio_name = radioList.get(radio_stream_position).getRadio_name();
                        radio_image = radioList.get(radio_stream_position).getRadio_image();
                        isAudioTrackNeedsChange = true;
                        startRadio();
                    }

                    if (radio_stream_position >= radioList.size() - 1) {
                        ibRadioNext.setEnabled(false);
//                        Toast.makeText(mContext, "There is only one station", Toast.LENGTH_SHORT).show();
//                        ibRadioNext.setImageResource(R.drawable.grey_circle_bg);
                    } else {
                        ibRadioNext.setEnabled(true);
//                        ibRadioNext.setImageResource(R.drawable.color_circle_bg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        ibRadioPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    radio_stream_position--;
                    if (radio_stream_position >= 0 && !radioList.isEmpty()) {
                        radio_url = radioList.get(radio_stream_position - 1).getRadio_stream_url();
                        radio_name = radioList.get(radio_stream_position - 1).getRadio_name();
                        radio_image = radioList.get(radio_stream_position - 1).getRadio_image();
                        isAudioTrackNeedsChange = true;
                        startRadio();
                        Log.d(TAG, "onClick: " + radio_name);
                    }
                    if (radio_stream_position <= 0) {
                        ibRadioPrevious.setEnabled(false);
                        Toast.makeText(mContext, "You have reached end", Toast.LENGTH_SHORT).show();
//                        ibRadioPrevious.setImageResource(R.drawable.grey_circle_bg);

                    } else {
                        ibRadioPrevious.setEnabled(true);
//                        ibRadioPrevious.setImageResource(R.drawable.color_circle_bg);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        ibRadioPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                requestStoragePermission();
                if (!isAlreadyPlayed && !radioList.isEmpty()) {
                    radio_url = radioList.get(0).getRadio_stream_url();
                    radio_name = radioList.get(0).getRadio_name();
                    radio_image = radioList.get(0).getRadio_image();
                    isAlreadyPlayed = true;
                }

//                if (streamingManager.isPlaying()) {
//                    streamingManager.onStop();
//                    Glide.with(getActivity()).load(R.drawable.ic_outline_play_arrow_24).into(ibRadioPlayPause);
//                } else {
//                    MediaMetaData obj = new MediaMetaData();
//                    obj.setMediaId(String.valueOf(0));
//                    obj.setMediaUrl(radio_url);
//                    if (streamingManager.isPlaying()) {
//                        streamingManager.onStop();
//                        streamingManager.onPlay(obj);
//                    } else {
//                        streamingManager.onPlay(obj);
//                    }
//                    Glide.with(getActivity()).load(R.drawable.ic_outline_pause_24).into(ibRadioPlayPause);
//                }

                if (exoPlayer != null) {
                    if (exoPlayer.isPlaying()) {
                        exoPlayer.stop();
                        Glide.with(getActivity()).load(R.drawable.ic_outline_play_arrow_24).into(ibRadioPlayPause);
                    } else {
                        try {
                            startRadio();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Glide.with(getActivity()).load(R.drawable.ic_outline_pause_24).into(ibRadioPlayPause);
                    }
                }

//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.pause();
//                    Glide.with(getActivity()).load(R.drawable.ic_outline_play_arrow_24).into(ibRadioPlayPause);
//                } else {
//                    mediaPlayer.start();
//                    Glide.with(getActivity()).load(R.drawable.ic_outline_pause_24).into(ibRadioPlayPause);
//                }
            }
        });

        ibRadioQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Radio has been stopped", Toast.LENGTH_SHORT).show();
//                mediaPlayer.stop();
//                mediaPlayer.reset();
                streamingManager.onStop();
                exoPlayer.stop();
                Glide.with(getActivity()).load(R.drawable.ic_outline_play_arrow_24).into(ibRadioPlayPause);
            }
        });
    }

    private void getRadioList(int position) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiInterface.API_GET_RADIO, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        radioList.clear();
                        Log.e(TAG, "onResponse: " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject jsonObjectRadio = jsonArray.getJSONObject(i);
                            String radio_name = jsonObjectRadio.getString("Radio_name");
                            String radio_logo_url = jsonObjectRadio.getString("radio_logo_url");
                            String radio_stream_url = jsonObjectRadio.getString("radio_stream_url");
                            radioList.add(new Radio(radio_name, radio_logo_url, radio_stream_url));
                            if (i == jsonArray.length()-1) {
                                startPlayingRadio();
                            }
                        }
                        Log.e(TAG, "onResponse: " + jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onFailure: ", error);
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("cid", String.valueOf(position));
                Log.e(TAG, "getParams: " + params);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void startRadio() throws IOException {
        try {
            if (exoPlayer != null) {
                exoPlayer.stop();
                exoPlayer = null;
                Uri videoUri = Uri.parse(radio_url);

                exoPlayer = new SimpleExoPlayer.Builder(getActivity()).build();
//                exoPlayerView.setPlayer(exoPlayer);

                MediaItem mediaItem = MediaItem.fromUri(videoUri);
                exoPlayer.clearMediaItems();
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.setPlayWhenReady(true);
            } else {
                Uri videoUri = Uri.parse(radio_url);

                exoPlayer = new SimpleExoPlayer.Builder(getActivity()).build();
//                exoPlayerView.setPlayer(exoPlayer);

                MediaItem mediaItem = MediaItem.fromUri(videoUri);
                exoPlayer.clearMediaItems();
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.setPlayWhenReady(true);
            }

            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        pbRadio.setVisibility(View.VISIBLE);
                    } else {
                        pbRadio.setVisibility(View.GONE);
                    }
                    Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                }
            });

        } catch (Exception e) {
            // below line is used for
            // handling our errors.
            Log.e("TAG", "Error : " + e.toString());
        }
        try {
//            MediaMetaData obj = new MediaMetaData();
//            obj.setMediaId(String.valueOf(0));
//            obj.setMediaUrl(radio_url);
//            if (streamingManager != null) {
//                streamingManager.onStop();
//                streamingManager.cleanupPlayer(true, true);
//            }
//            streamingManager = null;
//            streamingManager = AudioStreamingManager.getInstance(mContext);
//            streamingManager.onPlay(obj);





            llRadioPlaceHolder.setVisibility(View.GONE);
//            stopRadio();
            Glide.with(getActivity()).load(radio_image).into(ivRadioArt);
            Glide.with(getActivity()).load(R.drawable.ic_outline_pause_24).into(ibRadioPlayPause);
            tvRadioName.setText(radio_name);
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setDataSource(radio_url);
//            mediaPlayer.prepareAsync();
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mp.reset();
//                    mp.start();
//
//                    if (mp.isPlaying()) {
//                        pbRadio.setVisibility(View.GONE);
//                    } else {
//                        pbRadio.setVisibility(View.VISIBLE);
//                    }
//
//                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//
//                        @Override
//                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                            switch (what) {
//                                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                                    pbRadio.setVisibility(View.VISIBLE);
//                                    break;
//                                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                                    pbRadio.setVisibility(View.GONE);
//                                    break;
//
//                            }
//                            return false;
//                        }
//                    });
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void stopRadio() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer = null;
        Glide.with(getActivity()).load(R.drawable.ic_outline_play_arrow_24).into(ibRadioPlayPause);

    }

    private void startPlayingRadio() {
        if (!radioList.isEmpty()) {
            radio_url = radioList.get(0).getRadio_stream_url();
            radio_name = radioList.get(0).getRadio_name();
            radio_image = radioList.get(0).getRadio_image();
            isAudioTrackNeedsChange = true;
            try {
                int i = 0;
                List<MediaMetaData> mediaMetaDataList = new ArrayList<>();
                for (Radio radio : radioList) {
                    MediaMetaData mediaMetaData = new MediaMetaData();
                    mediaMetaData.setMediaId(String.valueOf(i));
                    mediaMetaData.setMediaUrl(radio.getRadio_stream_url());
                    mediaMetaDataList.add(mediaMetaData);
                    i++;
                }
                streamingManager.setMediaList(mediaMetaDataList);
                llRadioPlaceHolder.setVisibility(View.GONE);
                startRadio();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (streamingManager != null) {
            streamingManager.subscribesCallBack(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (streamingManager != null) {
            streamingManager.unSubscribeCallBack();
        }
    }

    @Override
    public void updatePlaybackState(int state) {
        switch (state) {
            case PlaybackStateCompat.STATE_PLAYING:
                pbRadio.setVisibility(View.GONE);
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                break;
            case PlaybackStateCompat.STATE_NONE:
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                pbRadio.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (streamingManager != null) {
//                            streamingManager.onStop();
//                            streamingManager = null;
                        }
                        pbRadio.setVisibility(View.GONE);
                    }
                }, 10500);
                break;
        }
    }

    @Override
    public void playSongComplete() {

    }

    @Override
    public void currentSeekBarPosition(int progress) {

    }

    @Override
    public void playCurrent(int indexP, MediaMetaData currentAudio) {

    }

    @Override
    public void playNext(int indexP, MediaMetaData currentAudio) {

    }

    @Override
    public void playPrevious(int indexP, MediaMetaData currentAudio) {

    }
}