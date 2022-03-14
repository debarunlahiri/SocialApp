package com.lahiriproductions.socialapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.ArtistDetailAdp;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.load_song_data.LoadArtistSong;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.recycle_click.ItemOffsetDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ArtistDetailFrag extends Fragment {

    ArrayList<SongsModelData> mediaItemsArrayList;
    RecyclerView artistDetailRecyclerview;
    public static String artistname;
    public static long artistID, albtmID;
    Context context;
    Activity activity;
    private Animation animation;
    View view;
    ConstantData global;
    private SharedPreferences sharedPreferences;


    public ArtistDetailFrag newInstance(long Id, String type) {
        ArtistDetailFrag fragment = new ArtistDetailFrag();
        Bundle args = new Bundle();
        args.putLong("artist_id", Id);
        args.putString("artist_name", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (ConstantData.sharedpreferences == null) {
            ConstantData.savePrefrence(getContext());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_artist_detail, container, false);
        context = getContext();
        activity = getActivity();
        global = new ConstantData();
        initialization();

        if (getArguments() != null) {
            artistID = getArguments().getLong("artist_id");
            artistname = getArguments().getString("artist_name");

            String where = MediaStore.Audio.Media.ARTIST_ID + "=?";
            String whereVal[] = {artistID + ""};
            String orderBy1 = MediaStore.Audio.Media.TITLE;

            Cursor c1 = getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"album_id"}, where, whereVal, orderBy1);
            if (c1 != null) {
                if (c1.moveToFirst()) {
                    albtmID = c1.getLong(c1.getColumnIndex("album_id"));
                }
            }
        }

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), 0);

        mediaItemsArrayList = new ArrayList<SongsModelData>();


        setUpSongs();



        return view;
    }


    private void setUpSongs() {

        mediaItemsArrayList = LoadArtistSong.getSongsForArtist(getActivity(), artistID);

        ArtistDetailAdp artistDetailAdp = new ArtistDetailAdp(mediaItemsArrayList, context, activity);
        artistDetailRecyclerview.setAdapter(artistDetailAdp);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);

    }

    private void initialization() {
        animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);


        artistDetailRecyclerview = (RecyclerView) view.findViewById(R.id.artistDetailRecyclerview);
        artistDetailRecyclerview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager((AppCompatActivity) activity);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
        artistDetailRecyclerview.setLayoutManager(layoutManager);
        artistDetailRecyclerview.addItemDecoration(itemDecoration);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
