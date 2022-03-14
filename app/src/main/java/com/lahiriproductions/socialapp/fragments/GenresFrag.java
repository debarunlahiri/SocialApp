package com.lahiriproductions.socialapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.GenresAdp;
import com.lahiriproductions.socialapp.models.GenersModelData;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.recycle_click.ItemOffsetDecoration;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;

import java.util.ArrayList;

public class GenresFrag extends Fragment {
    GenresAdp adapter;
    RecyclerView artistRecyclerview;
    Context context;
    Activity activity;
    ItemOffsetDecoration itemDecoration;
    ProgressBar progressbar;
    private boolean isGrid;
    GridLayoutManager layoutManager;
    private PreferencesData mPreferences;
    TextView tvNotFound;
    Menu menu;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferencesData.getInstance(getActivity());
        isGrid = mPreferences.isGenerInGrid();
        context = getContext();
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_artist, container, false);
        setHasOptionsMenu(true);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tvNotFound = (TextView) view.findViewById(R.id.txt_not_found);
        tvNotFound.setText(activity.getResources().getString(R.string.gener_not_found));
        artistRecyclerview = (RecyclerView) view.findViewById(R.id.artistRecyclerview);
        artistRecyclerview.setHasFixedSize(true);

        itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
        artistRecyclerview.addItemDecoration(itemDecoration);
        setLayoutManager();

        if (getActivity() != null) {
            if (ConstantData.generArrayList.size() > 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvNotFound.setVisibility(View.GONE);
                        adapter = new GenresAdp(ConstantData.generArrayList, context, activity);
                        artistRecyclerview.setAdapter(adapter);
                        if (progressbar != null)
                            progressbar.setVisibility(View.GONE);
                    }
                }, 50);


            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new FetchGenerList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                    }
                }, 50);
            }
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        this.menu = menu;
        inflater.inflate(R.menu.genermenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_as_list:
                mPreferences.setGenerInGrid(false);
                isGrid = false;
                updateLayoutManager(1);
                return true;
            case R.id.menu_show_as_grid:
                mPreferences.setGenerInGrid(true);
                isGrid = true;
                updateLayoutManager(2);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setLayoutManager() {
        if (isGrid) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 1);
        }
        artistRecyclerview.setLayoutManager(layoutManager);
    }

    private void updateLayoutManager(int column) {
        adapter = new GenresAdp(ConstantData.generArrayList, context, activity);
        artistRecyclerview.setAdapter(adapter);
        layoutManager.setSpanCount(column);
        layoutManager.requestLayout();
        progressbar.setVisibility(View.GONE);
    }


    public class FetchGenerList extends AsyncTask<Void, Void, ArrayList<GenersModelData>> {
        @Override
        protected ArrayList<GenersModelData> doInBackground(Void... arg0) {

            Long albumIDLong = (long) 0;
            Long generId;
            int song_count = 0;
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID}, null, null, "");
            try {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            try {
                                GenersModelData genres = new GenersModelData();
                                genres.setGenerName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)));
                                genres.setGenerId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID))));
                                generId = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID)));

                                Cursor c = context.getContentResolver().query(MediaStore.Audio.Genres.Members.getContentUri("external", generId), new String[]{"title", "_data", "duration", "_id", "album", "artist", "album_id"}, null, null, null);
                                if (c != null) {
                                    if (c.moveToFirst()) {
                                        albumIDLong = c.getLong(c.getColumnIndex("album_id"));
                                        Uri img_uri = ConstantData.getImgUri(albumIDLong);
                                        song_count = c.getCount();
                                        genres.setGenerUri(img_uri);
                                        genres.setAlbumId(albumIDLong);
                                        genres.setSongCount(song_count);
                                    }
                                }

                                if (albumIDLong != 0 && song_count > 0) {
                                    ConstantData.generArrayList.add(genres);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return ConstantData.generArrayList;
        }


        @Override
        protected void onPostExecute(ArrayList<GenersModelData> result) {
            super.onPostExecute(result);

            if (result.size() > 0) {
                tvNotFound.setVisibility(View.GONE);
                adapter = new GenresAdp(ConstantData.generArrayList, context, activity);
                artistRecyclerview.setAdapter(adapter);

            } else {
                tvNotFound.setVisibility(View.VISIBLE);
            }
            if (progressbar != null)
                progressbar.setVisibility(View.GONE);

        }
    }

}