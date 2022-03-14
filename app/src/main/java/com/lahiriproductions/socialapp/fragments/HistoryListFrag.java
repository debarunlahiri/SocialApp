package com.lahiriproductions.socialapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.SongHistoryAdp;
import com.lahiriproductions.socialapp.models.HistoryModelData;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.data_helper.DataBaseHelper;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;


public class HistoryListFrag extends Fragment {

    Activity activity;
    Context context;
    RecyclerView historyRecyclerview;
    ArrayList<SongsModelData> mediaItemArrayList;
    SongHistoryAdp adapter;
    ArrayList<HistoryModelData> historyArrayList;
    DataBaseHelper dataBaseHelper;
    SongsModelData mediaItem;
    StickyRecyclerHeadersDecoration headersDecor;
    private SharedPreferences sp;
    AppBarLayout appBarLayout;

    View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (ConstantData.sharedpreferences == null) {
            ConstantData.savePrefrence(getContext());
        }
        this.sp = getActivity().getSharedPreferences(getString(R.string.preference_file_key), 0);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_history_list, container, false);

        activity = getActivity();
        context = getContext();
        Initialize();


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        toolbar.setTitle(activity.getResources().getString(R.string.my_history));
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayShowHomeEnabled(true);

        mediaItemArrayList = new ArrayList<SongsModelData>();
        historyArrayList = new ArrayList<>();
        mediaItemArrayList.clear();
        dataBaseHelper = new DataBaseHelper(context);


        mediaItem = new SongsModelData();

        historyArrayList = dataBaseHelper.getHistory();

        for (int i = 0; i < historyArrayList.size(); i++) {
            try {
                mediaItemArrayList.add(fatchHistorySongs(historyArrayList.get(i).getSongId(), historyArrayList.get(i).getHistoryDate()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        adapter = new SongHistoryAdp(mediaItemArrayList, activity, context);
        historyRecyclerview.setAdapter(adapter);

        headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        historyRecyclerview.addItemDecoration(headersDecor);

        toolbar.getMenu().clear();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) activity).getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void Initialize() {
        historyRecyclerview = (RecyclerView) view.findViewById(R.id.historyRecyclerview);
        historyRecyclerview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        historyRecyclerview.setLayoutManager(layoutManager);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);


    }

    private SongsModelData fatchHistorySongs(String _id, String historyDate) {
        Cursor cursor = null;
        SongsModelData songData = null;
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor c = context.getContentResolver().query(uri, null, MediaStore.MediaColumns._ID + "='" + _id + "'", null, null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                songData = new SongsModelData();
                long id = c.getLong(c.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = c.getString(c.getColumnIndex("title"));
                String artist = c.getString(c.getColumnIndex("artist"));
                String album = c.getString(c.getColumnIndex("album"));
                long duration = c.getLong(c.getColumnIndex("duration"));
                String data = c.getString(c.getColumnIndex("_data"));
                long albumId = c.getLong(c.getColumnIndex("album_id"));
                String size = c.getString(c.getColumnIndex("_size"));

                songData.setSong_id(id);
                songData.setTitle(title);
                songData.setAlbum(album);
                songData.setArtist(artist);
                songData.setDuration(duration);
                songData.setPath(data);
                songData.setAlbumId(albumId);
                songData.setSize(size);
                songData.setHistory_date(historyDate);
                songData.setImg_uri(ConstantData.getImgUri(Long.valueOf(albumId)));

            } else {
                dataBaseHelper.deleteFavoriteSong(_id);
            }

        } catch (SQLiteException e) {
            Log.e("Error..Favourite", e.getMessage() + "..");
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return songData;
    }

}
