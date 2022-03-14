package com.lahiriproductions.socialapp.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.AlbumModelData;
import com.lahiriproductions.socialapp.fragments.AlbumDetailFrg;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistAlbumAdp extends RecyclerView.Adapter<ArtistAlbumAdp.ItemHolder> {

    private List<AlbumModelData> arraylist;
    private Activity mContext;
    int tempAdpPos;
    ImageView imgTempAlbum;


    public ArtistAlbumAdp(Activity context, List<AlbumModelData> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
//        showIntrestialAds(mContext);

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adp_artist_album, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {

        AlbumModelData localItem = arraylist.get(i);

        itemHolder.title.setText(localItem.title);
        String songCount = localItem.songCount + " Songs"; //Fantastic_MyMusicUtils.makeLabel(mContext, R.plurals.Nsongs, localItem.songCount);
        itemHolder.details.setText(songCount);

        try {
            Picasso.get().load(localItem.img_uri).placeholder(R.drawable.musicalbumicon).error(R.drawable.musicalbumicon).into(itemHolder.albumArt);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.musicalbumicon).placeholder(R.drawable.musicalbumicon).error(R.drawable.musicalbumicon).into(itemHolder.albumArt);
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, details;
        protected ImageView albumArt;
        protected CardView rootView;

        public ItemHolder(View view) {
            super(view);
            this.rootView = (CardView) view.findViewById(R.id.root_view);
            this.title = (TextView) view.findViewById(R.id.album_title);
            this.details = (TextView) view.findViewById(R.id.album_details);
            this.albumArt = (ImageView) view.findViewById(R.id.album_art);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            tempAdpPos = getAdapterPosition();
            imgTempAlbum = albumArt;

            ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;

            SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
            editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
            editor1.commit();

            if (ConstantData.adsCounts >= ConstantData.totalAdsCount) {
                ConstantData.sharedInstance(mContext).fragmentReplaceTransition(new AlbumDetailFrg().newInstance(arraylist.get(getAdapterPosition()).id, arraylist.get(getAdapterPosition()).title), "AlbumDetailFragment", mContext);

            } else {
                ConstantData.sharedInstance(mContext).fragmentReplaceTransition(new AlbumDetailFrg().newInstance(arraylist.get(getAdapterPosition()).id, arraylist.get(getAdapterPosition()).title), "AlbumDetailFragment", mContext);
            }

        }

    }

//    public void showIntrestialAds(final Activity act) {
//
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
//                    //   mInterstitialAd.loadAd(adRequest);
//                    SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
//                    editor.putInt(ConstantData.adsCount, 0);
//                    editor.commit();
//
//                    ConstantData.sharedInstance(mContext).fragmentReplaceTransition(new AlbumDetailFrg().newInstance(arraylist.get(tempAdpPos).id, arraylist.get(tempAdpPos).title), "AlbumDetailFragment", mContext);
//
//                }
//
//            });
//        }
//    }
}