package com.lahiriproductions.socialapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.GenersModelData;
import com.lahiriproductions.socialapp.fragments.GenresDetailFrag;
import com.lahiriproductions.socialapp.main_functions.AllFunctions;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GenresAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int typeItems = 1;
    private ArrayList<GenersModelData> dataSet;
    Context context;
    Activity activity;
    int tempPos;
    RecyclerView.ViewHolder temp_holder;
    boolean isGrid;


    public GenresAdp(ArrayList<GenersModelData> data, Context context, Activity activity) {
        this.dataSet = data;
        this.context = context;
        this.activity = activity;
        this.isGrid = PreferencesData.getInstance(context).isGenerInGrid();
        showIntrestialAds(activity);
    }

    private GenersModelData getItem(int position) {

        return dataSet.get(position);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof VHItem) {
            final GenersModelData dataItem = getItem(position);
            ((VHItem) holder).tvArtistName.setText(dataItem.getGenerName());
            String songCount = AllFunctions.makeLabel(context, R.plurals.Nsongs, dataSet.get(position).getSongCount());
            ((VHItem) holder).tvAlbumSongCount.setText(songCount);
            temp_holder = holder;

            Picasso.get().load(dataItem.getGenerUri()).placeholder(R.drawable.musicgenericon).error(R.drawable.musicgenericon).into(((VHItem) holder).imgArtist);

            ((VHItem) holder).rLayContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;
                    if (ConstantData.sharedpreferences == null) {
                        ConstantData.savePrefrence(context);
                    }
                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                    editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                    editor1.commit();

                    tempPos = position;

                    if (ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) >= ConstantData.totalAdsCount) {
                        temp_holder = holder;
                        ConstantData.sharedInstance(activity).fragmentReplaceTransition(new GenresDetailFrag().newInstance(dataSet.get(position).getGenerId(), dataSet.get(position).getGenerName(), dataSet.get(position).getAlbumId()), "GenresDetailFragment", activity);
                    } else {
                        ConstantData.sharedInstance(activity).fragmentReplaceTransition(new GenresDetailFrag().newInstance(dataSet.get(position).getGenerId(), dataSet.get(position).getGenerName(), dataSet.get(position).getAlbumId()), "GenresDetailFragment", activity);
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == typeItems) {
            if (isGrid) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_artist_item_grid, parent, false);
                VHItem v1 = new VHItem(view1);
                return v1;
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_artist_item_list, parent, false);
                VHItem v1 = new VHItem(view);
                return v1;
            }
        }


        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {

        return typeItems;
    }


    @Override
    public int getItemCount() {

        return dataSet.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView tvArtistName, tvAlbumSongCount;
        ImageView imgArtist;
        RelativeLayout rLayContent;

        public VHItem(View itemView) {
            super(itemView);

            this.tvArtistName = (TextView) itemView.findViewById(R.id.artist_name);
            this.tvAlbumSongCount = (TextView) itemView.findViewById(R.id.album_song_count);
            this.imgArtist = (ImageView) itemView.findViewById(R.id.artistImage);
            this.rLayContent = (RelativeLayout) itemView.findViewById(R.id.content);

        }
    }

    public void showIntrestialAds(final Activity act) {
//        ConstantData.sharedInstance(activity).fragmentReplaceTransition(new GenresDetailFrag().newInstance(dataSet.get(tempPos).getGenerId(), dataSet.get(tempPos).getGenerName(), dataSet.get(tempPos).getAlbumId()), "GenresDetailFragment", activity);

//        final AdRequest adRequest;
//        adRequest = new AdRequest.Builder().build();
//
//        mInterstitialAd = new InterstitialAd(act);
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
//
//                    mInterstitialAd.loadAd(adRequest);
//
//                    SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
//                    editor.putInt(ConstantData.adsCount, 0);
//                    editor.commit();
//
//                }
//
//                @Override
//                public void onAdFailedToLoad(LoadAdError loadAdError) {
//                    super.onAdFailedToLoad(loadAdError);
//                }
//            });
//        }
    }
}