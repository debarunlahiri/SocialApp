package com.lahiriproductions.socialapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.GenresDetailAdp;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.recycle_click.ItemOffsetDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GenresDetailFrag extends Fragment {
    ArrayList<SongsModelData> mediaItemsArrayList;
    RecyclerView albumDetailRecyclerview;
    public static long genresid;
    public static long albumid;
    ArrayList<SongsModelData> play;
    public static String albumName;
    Context context;
    Activity activity;
    private Animation animation;
    View view;
    private SharedPreferences sharedPreferences;
    ProgressBar progressbar;


    public static GenresDetailFrag newInstance(Long Id, String type, Long albumId) {
        GenresDetailFrag fragment = new GenresDetailFrag();
        albumName = type;
        genresid = Id;
        try {
            albumid = albumId;
        } catch (Exception e) {
            albumid = 0;
            e.printStackTrace();
        }

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_album_detail, container, false);

        context = getContext();
        activity = getActivity();
        initialization();

        this.sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), 0);

        mediaItemsArrayList = new ArrayList<SongsModelData>();

        play = new ArrayList<SongsModelData>();



        new loadArtists().execute("");
        return view;
    }


    private class loadArtists extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null) {
                ArrayList<SongsModelData> songList = new ArrayList();
                Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Genres.Members.getContentUri("external", genresid), new String[]{"title", "_data", "duration", "_id", "album", "artist", "album_id", "_size"}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {

                            SongsModelData MediaItems = new SongsModelData();

                            long _id = cursor.getLong(cursor.getColumnIndex("_id"));
                            String title = cursor.getString(cursor.getColumnIndex("title"));
                            long duration = cursor.getLong(cursor.getColumnIndex("duration"));
                            String artist = cursor.getString(cursor.getColumnIndex("artist"));
                            String data = cursor.getString(cursor.getColumnIndex("_data"));
                            long albumId = cursor.getLong(cursor.getColumnIndex("album_id"));
                            String size = cursor.getString(cursor.getColumnIndex("_size"));

                            MediaItems.setSong_id(_id);
                            MediaItems.setTitle(title);
                            MediaItems.setAlbum(albumName);
                            MediaItems.setArtist(artist);
                            MediaItems.setDuration(duration);
                            MediaItems.setPath(data);
                            MediaItems.setImg_uri(ConstantData.getImgUri(albumId));
                            MediaItems.setAlbumId(albumId);
                            MediaItems.setSize(size);
                            mediaItemsArrayList.add(MediaItems);


                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    //  setCollapsingToolbarLayoutTitle(albumName);
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressbar.setVisibility(View.GONE);
            GenresDetailAdp artistDetailAdapter = new GenresDetailAdp(mediaItemsArrayList, context, activity);
            albumDetailRecyclerview.setAdapter(artistDetailAdapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);

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
        progressbar = view.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.VISIBLE);


        albumDetailRecyclerview = (RecyclerView) view.findViewById(R.id.albumDetailRecyclerview);
        albumDetailRecyclerview.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        albumDetailRecyclerview.setLayoutManager(layoutManager);
        albumDetailRecyclerview.addItemDecoration(itemDecoration);

        animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out);
    }


}
