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
import com.lahiriproductions.socialapp.adapter.AlbumDetailAdp;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.models.LoadAlbumSongData;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.recycle_click.ItemOffsetDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AlbumDetailFrg extends Fragment {
    ArrayList<SongsModelData> mediaItemsArrayList;
    RecyclerView albumDetailRecyclerview;
    long albumId;
    ArrayList<SongsModelData> playAlbumList;
    ImageView header, imgHeader;
    String albumName;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Context context;
    Activity activity;
    FrameLayout frameLayout;
    private Animation animation;
    AppBarLayout appBarLayout;
    View view;


    public AlbumDetailFrg newInstance(long Id, String type) {
        AlbumDetailFrg fragment = new AlbumDetailFrg();

        Bundle args = new Bundle();
        args.putLong("album_id", Id);
        args.putString("album_name", type);
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

        if (getArguments() != null) {
            albumId = getArguments().getLong("album_id");
            albumName = getArguments().getString("album_name");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_album_detail, container, false);

        context = getContext();
        activity = getActivity();
        initialization();


        mediaItemsArrayList = new ArrayList<SongsModelData>();

        playAlbumList = new ArrayList<SongsModelData>();


        Picasso.get().load(ConstantData.getImgUri(albumId)).placeholder(R.drawable.musicalbumicon).error(R.drawable.musicalbumicon).into(header);
        setUpAlbumSongs();


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    frameLayout.startAnimation(animation);
                    isShow = true;
                } else if (isShow) {
                    frameLayout.setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                frameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);

    }

    private void setUpAlbumSongs() {

        mediaItemsArrayList = LoadAlbumSongData.getSongsForAlbum(getActivity(), albumId);

        AlbumDetailAdp artistDetailAdapter = new AlbumDetailAdp(mediaItemsArrayList, context, activity);
        albumDetailRecyclerview.setAdapter(artistDetailAdapter);

        setCollapsingToolbarLayoutTitle(albumName);
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        collapsingToolbarLayout.setTitle(title);
    }


    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public void initialization() {


        albumDetailRecyclerview = (RecyclerView) view.findViewById(R.id.albumDetailRecyclerview);
        albumDetailRecyclerview.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        albumDetailRecyclerview.setLayoutManager(layoutManager);
        albumDetailRecyclerview.addItemDecoration(itemDecoration);

        appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out);
    }
}
