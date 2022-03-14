package com.lahiriproductions.socialapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.lahiriproductions.socialapp.fragments.EventsFragment;
import com.lahiriproductions.socialapp.fragments.GroupChatFragment;
import com.lahiriproductions.socialapp.fragments.HomeFrag;
import com.lahiriproductions.socialapp.fragments.MainSongsListFrag;
import com.lahiriproductions.socialapp.fragments.MediaPlayerFragment;
import com.lahiriproductions.socialapp.fragments.RadioFragment;
import com.lahiriproductions.socialapp.fragments.SongsEffectFrag;
import com.lahiriproductions.socialapp.fragments.StopWatchFragment;
import com.lahiriproductions.socialapp.fragments.YoutubeFragment;

public class BottomViewPagerAdapter extends FragmentStatePagerAdapter {
    public BottomViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new StopWatchFragment();
        } else if (position == 1) {
            return new YoutubeFragment();
        } else if (position == 2) {
            return new RadioFragment();
        } else if (position == 3) { 
            return new MainSongsListFrag();
        } else if (position == 4) {
            return new EventsFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
