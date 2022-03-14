package com.lahiriproductions.socialapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.ViewPagerAdp;
import com.lahiriproductions.socialapp.main_functions.ConstantData;

public class HomeFrag extends Fragment {
    public static ViewPager viewPager;
    ViewPagerAdp adapter;
    Activity activity;
    Context context;
    TabLayout tabLayout;
    Toolbar toolbar;
    private SharedPreferences sp;

    View view;
    int transValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = getActivity();
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_home, container, false);
        this.sp = getActivity().getSharedPreferences(getString(R.string.preference_file_key), 0);
        transValue = this.sp.getInt(ConstantData.trancparentColor, ConstantData.trancparentColorDefaultValue); // default transparancy

        initialization();

        ConstantData.savePrefrence(context);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initialization() {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);




        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        viewPager.setOffscreenPageLimit(1);

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdp(getChildFragmentManager());
        adapter.addFrag(new SongsEffectFrag(), "SONGS");
        adapter.addFrag(new ArtistListFrag(), "ARTISTS");
//        adapter.addFrag(new AlbumSongFrag(), "ALBUMS");
        adapter.addFrag(new GenresFrag(), "GENRES");
        viewPager.setAdapter(adapter);
    }

}

