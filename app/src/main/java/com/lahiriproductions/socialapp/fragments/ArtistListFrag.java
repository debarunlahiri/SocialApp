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
import com.lahiriproductions.socialapp.adapter.ArtistSongAdp;
import com.lahiriproductions.socialapp.load_song_data.LoadArtistList;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.recycle_click.ItemOffsetDecoration;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;
import com.lahiriproductions.socialapp.utils_data.SongSortOrder;


public class ArtistListFrag extends Fragment {

    ArtistSongAdp adapter;
    RecyclerView artistRecyclerview;
    Context context;
    Activity activity;
    ItemOffsetDecoration itemDecoration;
    static ProgressBar progressbar;
    private PreferencesData mPreferences;
    private boolean isGrid;
    GridLayoutManager layoutManager;
    TextView tvNotFound;
    Menu menu;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesData.getInstance(getActivity());
        isGrid = mPreferences.isArtistsInGrid();
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_artist, container, false);
        setHasOptionsMenu(true);

        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tvNotFound = (TextView) view.findViewById(R.id.txt_not_found);
        tvNotFound.setText(activity.getString(R.string.artist_not_found));

        artistRecyclerview = (RecyclerView) view.findViewById(R.id.artistRecyclerview);
        artistRecyclerview.setHasFixedSize(true);


        context = getContext();
        activity = getActivity();

        itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
        artistRecyclerview.addItemDecoration(itemDecoration);

        setLayoutManager();

        if (ConstantData.artistArrayList.size() > 0) {
            if (getActivity() != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvNotFound.setVisibility(View.GONE);
                        adapter = new ArtistSongAdp(ConstantData.artistArrayList, context, activity);
                        artistRecyclerview.setAdapter(adapter);
                        if (progressbar != null)
                            progressbar.setVisibility(View.GONE);
                    }
                }, 50);
            }

        } else {
            if (getActivity() != null) {
                new Handler().postDelayed(() -> new loadArtists().execute(""), 50);
            }
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        this.menu = menu;
        inflater.inflate(R.menu.artistmenu, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                mPreferences.setArtistSortOrder(SongSortOrder.ArtistSortOrder.strArtistAToZ);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_za:
                mPreferences.setArtistSortOrder(SongSortOrder.ArtistSortOrder.strArtistZToA);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_number_of_songs:
                mPreferences.setArtistSortOrder(SongSortOrder.ArtistSortOrder.noOfArtistSongs);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_number_of_albums:
                mPreferences.setArtistSortOrder(SongSortOrder.ArtistSortOrder.noOfArtistAlbums);
                reloadAdapter();
                return true;
            case R.id.menu_show_as_list:
                mPreferences.setArtistsInGrid(false);
                isGrid = false;
                updateLayoutManager(1);
                return true;
            case R.id.menu_show_as_grid:
                mPreferences.setArtistsInGrid(true);
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
        adapter = new ArtistSongAdp(ConstantData.artistArrayList, context, activity);
        artistRecyclerview.setAdapter(adapter);
        layoutManager.setSpanCount(column);
        layoutManager.requestLayout();
        progressbar.setVisibility(View.GONE);
        // setItemDecoration();
    }

    private void reloadAdapter() {

        if (getActivity() != null)
            new loadArtists().execute("");
    }

    private class loadArtists extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                ConstantData.artistArrayList = LoadArtistList.getAllArtists(getActivity());

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            if (getActivity() != null) {
                if (ConstantData.artistArrayList.size() > 0) {
                    tvNotFound.setVisibility(View.GONE);
                    adapter = new ArtistSongAdp(ConstantData.artistArrayList, context, activity);
                    artistRecyclerview.setAdapter(adapter);
                } else {
                    tvNotFound.setVisibility(View.VISIBLE);
                }
                progressbar.setVisibility(View.GONE);
            }
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