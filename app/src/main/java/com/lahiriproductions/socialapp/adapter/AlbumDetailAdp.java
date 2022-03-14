package com.lahiriproductions.socialapp.adapter;

import android.app.Activity;
import android.content.Context;
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

public class AlbumDetailAdp extends RecyclerView.Adapter<AlbumDetailAdp.MyViewHolder> {

    private ArrayList<SongsModelData> dataSet;
    Context context;
    Activity activity;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        TextView tvSongArtist;
        ImageView imgSong;
        CardView cardView;
        LinearLayout layMoreDetails;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvSongName = (TextView) itemView.findViewById(R.id.txt_song_name);
            this.tvSongArtist = (TextView) itemView.findViewById(R.id.txt_songs_artist);
            this.imgSong = (ImageView) itemView.findViewById(R.id.img_song);
            this.cardView = (CardView)itemView.findViewById(R.id.card_view);
            this.layMoreDetails = (LinearLayout)itemView.findViewById(R.id.linear_more);
        }
    }

    public AlbumDetailAdp(ArrayList<SongsModelData> data, Context context, Activity activity) {
        this.dataSet = data;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adp_song_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition)
    {
        holder.tvSongName.setText(dataSet.get(listPosition).getTitle());
        holder.tvSongArtist.setText(dataSet.get(listPosition).getArtist());

        Picasso.get().load(dataSet.get(listPosition).getImg_uri()).placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(holder.imgSong);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dataSet != null)
                {
                    ControlMusicPlayer.startSongsWithQueue(context, dataSet, listPosition,"albumdetail");
                }
            }
        });

        (holder).imgSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataSet != null) {
                    ControlMusicPlayer.startSongsWithQueue(context, dataSet, listPosition,"albumdetail");
                }
            }
        });

        holder.layMoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstantData.showPopUp(holder.layMoreDetails, context,activity,dataSet.get(listPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
