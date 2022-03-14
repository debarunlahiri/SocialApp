package com.lahiriproductions.socialapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.ArtistModelData;
import com.lahiriproductions.socialapp.fragments.ArtistDetailFrag;
import com.lahiriproductions.socialapp.main_functions.AllFunctions;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ArtistSongAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ArtistModelData> dataSet;
    Context context;
    Activity activity;
    private static final int typeItem = 1;
    private static final int adsType = 0;

    int tempPos;
    MyViewHolder tempHolder;
    long albtmID;
    boolean isGrid;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView artistName, albumSongCount;
        ImageView artistImage;
        RelativeLayout content;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.artistName = (TextView) itemView.findViewById(R.id.artist_name);
            this.albumSongCount = (TextView) itemView.findViewById(R.id.album_song_count);
            this.artistImage = (ImageView) itemView.findViewById(R.id.artistImage);
            this.content = (RelativeLayout) itemView.findViewById(R.id.content);

        }
    }


    public ArtistSongAdp(List<ArtistModelData> data, Context context, Activity act) {
        this.dataSet = data;
        this.context = context;
        this.activity = act;
        this.isGrid = PreferencesData.getInstance(context).isArtistsInGrid();
        showIntrestialAds(act);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).artistName.setText(dataSet.get(listPosition).name);

            String albumNmber = AllFunctions.makeLabel(context, R.plurals.Nalbums, dataSet.get(listPosition).albumCount);
            String songCount = AllFunctions.makeLabel(context, R.plurals.Nsongs, dataSet.get(listPosition).songCount);
            ((MyViewHolder) holder).albumSongCount.setText(AllFunctions.makeCombinedString(context, albumNmber, songCount));


            String where = MediaStore.Audio.Media.ARTIST_ID + "=?";
            String whereVal[] = {dataSet.get(listPosition).id + ""};
            String orderBy1 = MediaStore.Audio.Media.TITLE;

            Cursor c1 = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"album_id"}, where, whereVal, orderBy1);
            if (c1 != null) {
                if (c1.moveToFirst()) {
                    albtmID = c1.getLong(c1.getColumnIndex("album_id"));

                }
            }

            Picasso.get().load(ConstantData.getImgUri(albtmID)).placeholder(R.drawable.musicartisticon).error(R.drawable.musicartisticon).into(((MyViewHolder) holder).artistImage);

            ((MyViewHolder) holder).content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;
                    if (ConstantData.sharedpreferences == null) {
                        ConstantData.savePrefrence(context);
                    }
                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                    editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                    editor1.commit();

                    tempPos = listPosition;

                    if (ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) >= ConstantData.totalAdsCount) {
                        tempHolder = ((MyViewHolder) holder);
                        ConstantData.sharedInstance(activity).fragmentReplaceTransition(new ArtistDetailFrag().newInstance(dataSet.get(listPosition).id, dataSet.get(listPosition).name), "ArtistDetailFragment", activity);
                    } else {
                        ConstantData.sharedInstance(activity).fragmentReplaceTransition(new ArtistDetailFrag().newInstance(dataSet.get(listPosition).id, dataSet.get(listPosition).name), "ArtistDetailFragment", activity);
                    }

                }
            });
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == typeItem) {
            if (isGrid) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_artist_item_grid, parent, false);
                MyViewHolder v1 = new MyViewHolder(view1);
                return v1;
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_artist_item_list, parent, false);
                MyViewHolder v1 = new MyViewHolder(view);
                return v1;
            }
        }


        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {

        if (dataSet.get(position).name.equals("ads")) {
            return adsType;
        }
        return typeItem;
    }


    @Override
    public int getItemCount() {

        return dataSet.size();
    }

    public void showIntrestialAds(final Activity act) {
//        ConstantData.sharedInstance(activity).fragmentReplaceTransition(new ArtistDetailFrag().newInstance(dataSet.get(tempPos).id, dataSet.get(tempPos).name), "ArtistDetailFragment", activity);

//        final AdRequest adRequest;
//        adRequest = new AdRequest.Builder().build();
//
//        mInterstitialAd = new InterstitialAd(act);
//
//        if (ManageAdsId.isActiveAdMob) {
//            // set the ad unit ID
//            mInterstitialAd.setAdUnitId(ManageAdsId.admobInterstitial);
//            // Load ads into Interstitial Ads
//            mInterstitialAd.loadAd(adRequest);
//
//            mInterstitialAd.setAdListener(new AdListener() {
//                public void onAdLoaded() {
//
//                }
//
//                @Override
//                public void onAdClosed() {
//                    super.onAdClosed();
//                    //  mInterstitialAd.loadAd(adRequest);
//                    SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
//                    editor.putInt(ConstantData.adsCount, 0);
//                    editor.commit();
//
//                }
//
//            });
//        }
    }

}