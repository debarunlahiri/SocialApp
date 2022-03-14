package com.lahiriproductions.socialapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.SongsAdp;
import com.lahiriproductions.socialapp.load_song_data.LoadSongList;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.recycle_click.ItemOffsetDecoration;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;
import com.lahiriproductions.socialapp.utils_data.SongSortOrder;


public class SongsEffectFrag extends Fragment {
    SongsAdp adapter;
    Activity activity;
    private PreferencesData mPreferences;

    RecyclerView songsRecyclerview;
    Context context;
    ProgressBar progressbar;
    TextView tvNotFound;
    //===================== Equalizer ==================================

    Equalizer equalizer = null;
    boolean isEqualizer;
    int band1Pos;
    int band2Pos;
    int band3Pos;
    int band4Pos;
    int band5Pos;
    int band6Pos;
    int band7Pos;
    int band8Pos;
    BassBoost bassBoost = null;
    int bassBoostPos;
    int numBands;
    int maxLevel = 100;
    int minLevel = 0;
    SharedPreferences sp;
    Menu menu;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesData.getInstance(getActivity());
        context = getContext();
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_songs_effect, container, false);
        songsRecyclerview = (RecyclerView) view.findViewById(R.id.songsRecyclerview);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tvNotFound = (TextView) view.findViewById(R.id.txt_not_found);
        tvNotFound.setText(activity.getResources().getString(R.string.song_not_found));
        songsRecyclerview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        songsRecyclerview.setLayoutManager(layoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
        songsRecyclerview.addItemDecoration(itemDecoration);


        setEqualizer();

        if (ConstantData.songsArrayList.size() == 0) {
            new loadSongs().execute("");
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int resId = R.anim.layout_animation_from_bottom;
                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context, resId);
                    songsRecyclerview.setLayoutAnimation(animation);

                    tvNotFound.setVisibility(View.GONE);
                    adapter = new SongsAdp(ConstantData.songsArrayList, (AppCompatActivity) context, (AppCompatActivity) activity);
                    songsRecyclerview.setAdapter(adapter);

                    if (progressbar != null)
                        progressbar.setVisibility(View.GONE);
                }
            }, 100);
        }


        return view;
    }

    private void reloadAdapter() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new loadSongs().execute("");
            }
        }, 50);
    }


    public class loadSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (activity != null)

                ConstantData.songsArrayList = LoadSongList.getAllSongs(activity);


            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            int resId = R.anim.layout_animation_from_bottom;
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context, resId);
            songsRecyclerview.setLayoutAnimation(animation);

            if (ConstantData.songsArrayList.size() > 0) {
                tvNotFound.setVisibility(View.GONE);
                adapter = new SongsAdp(ConstantData.songsArrayList, (AppCompatActivity) context, (AppCompatActivity) activity);
                songsRecyclerview.setAdapter(adapter);
            } else {
                tvNotFound.setVisibility(View.VISIBLE);
            }

            if (progressbar != null)
                progressbar.setVisibility(View.GONE);

        }

        @Override
        protected void onPreExecute() {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setEqualizer() {
        this.sp = context.getSharedPreferences(context.getString(R.string.preference_file_key), 0);
        this.isEqualizer = this.sp.getBoolean(ConstantData.isEqualizer, false);
        this.bassBoostPos = this.sp.getInt(ConstantData.bassBoost, 0);

        this.band1Pos = this.sp.getInt(ConstantData.band1, 50);
        this.band2Pos = this.sp.getInt(ConstantData.band2, 50);
        this.band3Pos = this.sp.getInt(ConstantData.band3, 50);
        this.band4Pos = this.sp.getInt(ConstantData.band4, 50);
        this.band5Pos = this.sp.getInt(ConstantData.band5, 50);
        this.band6Pos = this.sp.getInt(ConstantData.band6, 50);
        this.band7Pos = this.sp.getInt(ConstantData.band7, 50);
        this.band8Pos = this.sp.getInt(ConstantData.band8, 50);
        this.numBands = 0;


        if (this.isEqualizer) {
            this.equalizer = new Equalizer(0, 0);
            if (this.equalizer != null) {
                this.equalizer.setEnabled(this.isEqualizer);
                this.numBands = this.equalizer.getNumberOfBands();
                short[] r = this.equalizer.getBandLevelRange();
                this.minLevel = r[0];
                this.maxLevel = r[1];
                this.bassBoost = new BassBoost(0, 0);
                if (this.bassBoost != null) {
                    this.bassBoost.setEnabled(this.bassBoostPos > 0);
                    this.bassBoost.setStrength((short) this.bassBoostPos);
                }
                for (int i = 0; i < this.numBands; i++) {
                    int level = 0;
                    if (i == 0) {
                        level = this.band1Pos;
                    } else if (i == 1) {
                        level = this.band2Pos;
                    } else if (i == 2) {
                        level = this.band3Pos;
                    } else if (i == 3) {
                        level = this.band4Pos;
                    } else if (i == 4) {
                        level = this.band5Pos;
                    } else if (i == 5) {
                        level = this.band6Pos;
                    } else if (i == 6) {
                        level = this.band7Pos;
                    } else if (i == 7) {
                        level = this.band8Pos;
                    }
                    this.equalizer.setBandLevel((short) i, (short) (this.minLevel + (((this.maxLevel - this.minLevel) * level) / 100)));
                }
            }
        } else {
            if (this.bassBoost != null) {
                this.bassBoost.setEnabled(false);
            }
        }
    }

}