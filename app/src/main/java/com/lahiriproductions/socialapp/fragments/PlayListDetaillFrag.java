package com.lahiriproductions.socialapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.lahiriproductions.socialapp.adapter.PlayDetailslistAdp;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.data_helper.DataBaseHelper;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.recycle_click.ItemOffsetDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PlayListDetaillFrag extends Fragment {
    ArrayList<SongsModelData> playListSongArrayList;
    RecyclerView albumDetailRecyclerview;
    public static String playlistId;
    public static String imageUrl;
    public static String playlistName;
    Context context;
    Activity activity;
    private Animation animation;
    View view;
    DataBaseHelper dataBaseHelper;
    public static PlayDetailslistAdp playDetailslistAdp;


    public static PlayListDetaillFrag newInstance(String Id, String image_uri, String playlist_name) {
        PlayListDetaillFrag fragment = new PlayListDetaillFrag();

        playlistId = Id;
        imageUrl = image_uri;
        playlistName = playlist_name;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_album_detail, container, false);

        context = getContext();
        activity = getActivity();
        initialization();

        //  this.sp = getActivity().getSharedPreferences(getString(R.string.preference_file_key), 0);

        dataBaseHelper = DataBaseHelper.sharedInstance(activity);


        playListSongArrayList = new ArrayList<SongsModelData>();



        playListSongArrayList = dataBaseHelper.getPlayListData(playlistId, context);


        playDetailslistAdp = new PlayDetailslistAdp(playListSongArrayList, context, activity);
        albumDetailRecyclerview.setAdapter(playDetailslistAdp);

        return view;
    }


    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initialization() {

        albumDetailRecyclerview = (RecyclerView) view.findViewById(R.id.albumDetailRecyclerview);
        albumDetailRecyclerview.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        albumDetailRecyclerview.setLayoutManager(layoutManager);
        albumDetailRecyclerview.addItemDecoration(itemDecoration);

        animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out);


    }


}
