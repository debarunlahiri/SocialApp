package com.lahiriproductions.socialapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.Radio;
import com.lahiriproductions.socialapp.utils.Constants;
import com.rosemaryapp.amazingspinner.AmazingSpinner;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RadioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RadioFragment extends Fragment implements MediaPlayer.OnPreparedListener {

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
    private int radio_image;
    private CircleLineVisualizer mVisualizer;
    private boolean isAlreadyPlayed = false;
    private Context mContext;

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
                    setupFijiRadio();
                    cvRadioArt.setVisibility(View.VISIBLE);
                    tvRadioName.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    radio_stream_position = 0;
                    isAlreadyPlayed = true;
                    ibRadioNext.setEnabled(true);
                    setupUSARadio();
                    cvRadioArt.setVisibility(View.VISIBLE);
                    tvRadioName.setVisibility(View.VISIBLE);
                } else if (position == 3) {
                    radio_stream_position = 0;
                    isAlreadyPlayed = true;
                    ibRadioNext.setEnabled(true);
                    setupNewZealandRadio();
                    cvRadioArt.setVisibility(View.VISIBLE);
                    tvRadioName.setVisibility(View.VISIBLE);
                } else if (position == 4) {
                    radio_stream_position = 0;
                    isAlreadyPlayed = true;
                    ibRadioNext.setEnabled(true);
                    setupIndiaRadio();
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
                        Toast.makeText(mContext, "There is only one station", Toast.LENGTH_SHORT).show();
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

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    Glide.with(getActivity()).load(R.drawable.ic_outline_play_arrow_24).into(ibRadioPlayPause);
                } else {
                    mediaPlayer.start();
                    Glide.with(getActivity()).load(R.drawable.ic_outline_pause_24).into(ibRadioPlayPause);
                }
            }
        });

        ibRadioQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Radio has been stopped", Toast.LENGTH_SHORT).show();
                mediaPlayer.stop();
                mediaPlayer.reset();
                Glide.with(getActivity()).load(R.drawable.ic_outline_play_arrow_24).into(ibRadioPlayPause);
            }
        });
    }

    private void startRadio() throws IOException {
        try {
            llRadioPlaceHolder.setVisibility(View.GONE);
            stopRadio();
            Glide.with(getActivity()).load(radio_image).into(ivRadioArt);
            Glide.with(getActivity()).load(R.drawable.ic_outline_pause_24).into(ibRadioPlayPause);
            tvRadioName.setText(radio_name);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(radio_url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();

                    if (mp.isPlaying()) {
                        pbRadio.setVisibility(View.GONE);
                    } else {
                        pbRadio.setVisibility(View.VISIBLE);
                    }

                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {

                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            switch (what) {
                                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                    pbRadio.setVisibility(View.VISIBLE);
                                    break;
                                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                    pbRadio.setVisibility(View.GONE);
                                    break;

                            }
                            return false;
                        }
                    });
                }
            });
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


    private void setupIndiaRadio() {
        Glide.with(getActivity()).load(Constants.Radio.India.RADIO_LOGO_URL_2).into(ivRadioBg);
        radioList.clear();
        radioList.add(new Radio(Constants.Radio.India.RADIO_NAME_2, Constants.Radio.India.RADIO_LOGO_URL_2, Constants.Radio.India.RADIO_STREAM_URL_2));
        radioList.add(new Radio(Constants.Radio.India.RADIO_NAME_3, Constants.Radio.India.RADIO_LOGO_URL_3, Constants.Radio.India.RADIO_STREAM_URL_3));
        radioList.add(new Radio(Constants.Radio.India.RADIO_NAME_4, Constants.Radio.India.RADIO_LOGO_URL_4, Constants.Radio.India.RADIO_STREAM_URL_4));
        radioList.add(new Radio(Constants.Radio.India.RADIO_NAME_6, Constants.Radio.India.RADIO_LOGO_URL_6, Constants.Radio.India.RADIO_STREAM_URL_6));
        radioList.add(new Radio(Constants.Radio.India.RADIO_NAME_7, Constants.Radio.India.RADIO_LOGO_URL_7, Constants.Radio.India.RADIO_STREAM_URL_7));
        radioList.add(new Radio(Constants.Radio.India.RADIO_NAME_8, Constants.Radio.India.RADIO_LOGO_URL_8, Constants.Radio.India.RADIO_STREAM_URL_8));
        startPlayingRadio();
        Glide.with(getActivity()).load(Constants.Radio.India.RADIO_LOGO_URL_2).into(ivRadioBg);
    }

    private void setupNewZealandRadio() {
        Glide.with(getActivity()).load(R.drawable.new_zealand_bg).into(ivRadioBg);
        radioList.clear();
        radioList.add(new Radio(Constants.Radio.NewZealand.RADIO_NAME, Constants.Radio.NewZealand.RADIO_LOGO_URL, Constants.Radio.NewZealand.RADIO_STREAM_URL));
//        radioList.add(new Radio(Constants.Radio.NewZealand.RADIO_NAME_2, Constants.Radio.NewZealand.RADIO_STREAM_URL_2));
//        radioList.add(new Radio(Constants.Radio.NewZealand.RADIO_NAME_3, Constants.Radio.NewZealand.RADIO_LOGO_URL_3, Constants.Radio.NewZealand.RADIO_STREAM_URL_3));
        startPlayingRadio();
    }

    private void setupUSARadio() {
        Glide.with(getActivity()).load(R.drawable.usa_wallpaper).into(ivRadioBg);
        radioList.clear();
        radioList.add(new Radio(Constants.Radio.USA.RADIO_NAME, Constants.Radio.USA.RADIO_LOGO_URL, Constants.Radio.USA.RADIO_STREAM_URL));
//        radioList.add(new Radio(Constants.Radio.USA.RADIO_NAME_2, Constants.Radio.USA.RADIO_LOGO_URL_2, Constants.Radio.USA.RADIO_STREAM_URL_2));
        radioList.add(new Radio(Constants.Radio.USA.RADIO_NAME_3, Constants.Radio.USA.RADIO_LOGO_URL_3, Constants.Radio.USA.RADIO_STREAM_URL_3));
//        radioList.add(new Radio(Constants.Radio.USA.RADIO_NAME_4, Constants.Radio.USA.RADIO_LOGO_URL_4, Constants.Radio.USA.RADIO_STREAM_URL_4));
//        radioList.add(new Radio(Constants.Radio.USA.RADIO_NAME_5, Constants.Radio.USA.RADIO_STREAM_URL_5));
        startPlayingRadio();
    }

    private void startPlayingRadio() {
        if (!radioList.isEmpty()) {
            radio_url = radioList.get(0).getRadio_stream_url();
            radio_name = radioList.get(0).getRadio_name();
            radio_image = radioList.get(0).getRadio_image();
            isAudioTrackNeedsChange = true;
            try {
                startRadio();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupFijiRadio() {
        Glide.with(getActivity()).load(R.drawable.fiji_bg).into(ivRadioBg);
        radioList.clear();
        radioList.add(new Radio(Constants.Radio.Fiji.RADIO_NAME, Constants.Radio.Fiji.RADIO_LOGO_URL, Constants.Radio.Fiji.RADIO_STREAM_URL));
//        radioList.add(new Radio(Constants.Radio.Fiji.RADIO_NAME_2, Constants.Radio.Fiji.RADIO_LOGO_URL_2,  Constants.Radio.Fiji.RADIO_STREAM_URL_2));
//        radioList.add(new Radio(Constants.Radio.Fiji.RADIO_NAME_3, Constants.Radio.Fiji.RADIO_LOGO_URL_3,  Constants.Radio.Fiji.RADIO_STREAM_URL_3));
        startPlayingRadio();
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
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}