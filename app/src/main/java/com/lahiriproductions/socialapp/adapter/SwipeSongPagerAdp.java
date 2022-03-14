package com.lahiriproductions.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.main_functions.ConstantData;

public class SwipeSongPagerAdp extends PagerAdapter {
    Context mContext;
    LayoutInflater mInflater;

    public SwipeSongPagerAdp(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        if (ConstantData.mediaItemsArrayList == null || ConstantData.mediaItemsArrayList.size() <= 0) {
            return 0;
        } else {
            return ConstantData.mediaItemsArrayList.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.change_songe_fragment_layout, container, false);
        view.setVisibility(View.GONE);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

}
