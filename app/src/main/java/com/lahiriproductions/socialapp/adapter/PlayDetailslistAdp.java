package com.lahiriproductions.socialapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.music_controls.ControlMusicPlayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayDetailslistAdp extends RecyclerView.Adapter<PlayDetailslistAdp.MyViewHolder> {

    public static ArrayList<SongsModelData> dataSet;
    Context context;
    Activity activity;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        TextView tvSongArtist;
        ImageView imgSong;
        CardView cardView;
        LinearLayout layMore;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvSongName = (TextView) itemView.findViewById(R.id.txt_song_name);
            this.tvSongArtist = (TextView) itemView.findViewById(R.id.txt_songs_artist);
            this.imgSong = (ImageView) itemView.findViewById(R.id.img_song);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.layMore = (LinearLayout) itemView.findViewById(R.id.linear_more);
        }
    }

    public PlayDetailslistAdp(ArrayList<SongsModelData> data, Context context, Activity activity) {
        this.dataSet = data;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adp_song_item, parent, false);


        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        try {
            holder.tvSongName.setText(dataSet.get(listPosition).getTitle());
        } catch (Exception e) {
            holder.tvSongName.setText("Untitiled Song");
            e.printStackTrace();
        }

        try {
            holder.tvSongArtist.setText(dataSet.get(listPosition).getArtist());
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Picasso.get().load(dataSet.get(listPosition).getImg_uri()).placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(holder.imgSong);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dataSet != null) {
                    ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;
                    if (ConstantData.sharedpreferences == null) {
                        ConstantData.savePrefrence(context);
                    }
                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                    editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                    editor1.commit();

                    ControlMusicPlayer.startSongsWithQueue(context, dataSet, listPosition, "playlistdetail");

                }
            }
        });

        (holder).imgSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dataSet != null) {
                    ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;
                    if (ConstantData.sharedpreferences == null) {
                        ConstantData.savePrefrence(context);
                    }
                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                    editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                    editor1.commit();

                    ControlMusicPlayer.startSongsWithQueue(context, dataSet, listPosition, "playlistdetail");
                }
            }
        });

        holder.layMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstantData.showPopUp(holder.layMore, context, activity, dataSet.get(listPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
