package com.lahiriproductions.socialapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.lahiriproductions.socialapp.adapter.AlbumAdp;
import com.lahiriproductions.socialapp.models.AlbumModelData;
import com.lahiriproductions.socialapp.load_song_data.LoadAlbumData;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.recycle_click.ItemOffsetDecoration;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;
import com.lahiriproductions.socialapp.utils_data.SongSortOrder;

import java.util.List;


public class AlbumSongFrag extends Fragment {
    AlbumAdp adapter;
    RecyclerView artistRecyclerview;
    Context context;
    Activity activity;
    ItemOffsetDecoration itemDecoration;
    ProgressBar progressbar;
    private PreferencesData mPreferences;
    private boolean isGrid;
    GridLayoutManager layoutManager;
    TextView tvNotFound;
    Menu menu;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferencesData.getInstance(getActivity());
        isGrid = mPreferences.isAlbumsInGrid();
        context = getContext();
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_artist, container, false);
        setHasOptionsMenu(true);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tvNotFound = (TextView) view.findViewById(R.id.txt_not_found);
        tvNotFound.setText(activity.getResources().getString(R.string.album_not_found));
        artistRecyclerview = (RecyclerView) view.findViewById(R.id.artistRecyclerview);
        artistRecyclerview.setHasFixedSize(true);

        itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
        artistRecyclerview.addItemDecoration(itemDecoration);
        setLayoutManager();

        if (getActivity() != null) {
            if (ConstantData.albumArrayList.size() > 0) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvNotFound.setVisibility(View.GONE);
                        adapter = new AlbumAdp(ConstantData.albumArrayList, context, activity);
                        artistRecyclerview.setAdapter(adapter);

                        if (progressbar != null)
                            progressbar.setVisibility(View.GONE);
                    }
                }, 50);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new loadAlbums().execute("");
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
        inflater.inflate(R.menu.albummenu, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                mPreferences.setAlbumSortOrder(SongSortOrder.AlbumSortOrder.strAlbumAtoZ);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_za:
                mPreferences.setAlbumSortOrder(SongSortOrder.AlbumSortOrder.strAlbumZtoA);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_year:
                mPreferences.setAlbumSortOrder(SongSortOrder.AlbumSortOrder.strAlbumYear);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_artist:
                mPreferences.setAlbumSortOrder(SongSortOrder.AlbumSortOrder.strAlbumArtist);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_number_of_songs:
                mPreferences.setAlbumSortOrder(SongSortOrder.AlbumSortOrder.noOfAlbumSong);
                reloadAdapter();
                return true;
            case R.id.menu_show_as_list:
                mPreferences.setAlbumsInGrid(false);
                isGrid = false;
                updateLayoutManager(1);
                return true;
            case R.id.menu_show_as_grid:
                mPreferences.setAlbumsInGrid(true);
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
        adapter = new AlbumAdp(ConstantData.albumArrayList, context, activity);
        artistRecyclerview.setAdapter(adapter);
        layoutManager.setSpanCount(column);
        layoutManager.requestLayout();
        progressbar.setVisibility(View.GONE);
    }

    private void reloadAdapter() {
        if (getActivity() != null)
            new loadAlbums().execute("");
    }


    private class loadAlbums extends AsyncTask<String, Void, List<AlbumModelData>> {

        @Override
        protected List<AlbumModelData> doInBackground(String... params) {
            if (getActivity() != null)
                ConstantData.albumArrayList = LoadAlbumData.getAllAlbums(getActivity());
            return ConstantData.albumArrayList;
        }

        @Override
        protected void onPostExecute(List<AlbumModelData> result) {

            if (result.size() > 0) {
                tvNotFound.setVisibility(View.GONE);
                adapter = new AlbumAdp(ConstantData.albumArrayList, context, activity);
                artistRecyclerview.setAdapter(adapter);

            } else {
                tvNotFound.setVisibility(View.VISIBLE);
            }
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

}