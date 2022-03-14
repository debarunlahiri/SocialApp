package com.lahiriproductions.socialapp.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.VideosDataType;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.utils_data.OnItemClickListener;
//import com.lahiriproductions.socialapp.video_player.PlayVideoActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class VideoAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static ArrayList<VideosDataType> dataSet;
    Context context;
    private static final int typeItems = 1;
    static final int adsType = 0;
    int pos;
    OnItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView videoThumb;
        LinearLayout layMore;
        TextView tvTitle;
        TextView tvDuration;
        TextView tvSize;
        RelativeLayout rLayVideo;


        public MyViewHolder(View videoRow) {
            super(videoRow);

            videoThumb = (ImageView) videoRow.findViewById(R.id.img_video);
            layMore = (LinearLayout) videoRow.findViewById(R.id.lin_menu);
            tvTitle = (TextView) videoRow.findViewById(R.id.txt_title);
            tvDuration = (TextView) videoRow.findViewById(R.id.txt_time);
            tvSize = (TextView) videoRow.findViewById(R.id.tvSize);
            rLayVideo = (RelativeLayout) videoRow.findViewById(R.id.rel_main);
            layMore.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, getAdapterPosition());
            }
        }
    }

    class VHAdsItem extends RecyclerView.ViewHolder {

        public VHAdsItem(View finalRow) {
            super(finalRow);
        }
    }


    public VideoAdp(final Context context, ArrayList<VideosDataType> data, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        if (ConstantData.sharedpreferences == null) {
            ConstantData.savePrefrence(context);
        }
//        showIntrestialAds(context);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case adsType: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
                return new VHAdsItem(view);
            }
            case typeItems: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);

                return new MyViewHolder(view);
            }
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case adsType:

                break;
            case typeItems:
                if (dataSet.get(position) != null) {

                    try {
                        Picasso.get().load(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.parseLong(dataSet.get(position).strID))).resize(500, 500).centerCrop().into(((MyViewHolder) holder).videoThumb);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                    ((MyViewHolder) holder).tvTitle.setText(dataSet.get(position).title);
                    try {
                        ((MyViewHolder) holder).tvDuration.setText(ConstantData.convert(Long.parseLong(dataSet.get(position).duration)));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        ((MyViewHolder) holder).tvSize.setText(ConstantData.filesize(dataSet.get(position).filePath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ((MyViewHolder) holder).rLayVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (v != null) {
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                            pos = position;

                            ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;

                            SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                            editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                            editor1.commit();

//                            if (ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) % ConstantData.videoAdsCount == 0) {
//                                Intent intent = new Intent(context, PlayVideoActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.putExtra("videoFilePath", dataSet.get(position).filePath);
//                                intent.putExtra("pos", position);
//                                intent.putExtra("title", dataSet.get(position).title);
//                                context.startActivity(intent);
//                            } else {
//
//                                Intent intent = new Intent(context, PlayVideoActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.putExtra("videoFilePath", dataSet.get(position).filePath);
//                                intent.putExtra("pos", position);
//                                intent.putExtra("title", dataSet.get(position).title);
//                                context.startActivity(intent);
//                            }
                        }
                    });
                }
                break;
        }
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    @Override
    public int getItemViewType(int position) {

        return typeItems;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    public void showIntrestialAds(final Context act) {
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
//                    mInterstitialAd.loadAd(adRequest);
//
//                    Intent intent = new Intent(context, PlayVideoActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("videoFilePath", dataSet.get(pos).filePath);
//                    intent.putExtra("pos", pos);
//                    intent.putExtra("title", dataSet.get(pos).title);
//                    context.startActivity(intent);
//                }
//
//                @Override
//                public void onAdFailedToLoad(LoadAdError loadAdError) {
//                    super.onAdFailedToLoad(loadAdError);
//                }
//            });
//        }
//    }

}
