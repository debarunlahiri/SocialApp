package com.lahiriproductions.socialapp.models;

import static com.lahiriproductions.socialapp.fragments.GenresDetailFrag.genresid;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.ArtistAlbumAdp;
import com.lahiriproductions.socialapp.load_song_data.LoadNormalAlbum;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.music_controls.ControlMusicPlayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GenresDetailAdp extends RecyclerView.Adapter<GenresDetailAdp.MyViewHolder> {

    private ArrayList<SongsModelData> dataSet;
    Context context;
    Activity activity;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        TextView tvSongArtist;
        ImageView imgSong;
        CardView cardView;
        LinearLayout layMore;
        protected RecyclerView albumsRecyclerView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvSongName = (TextView) itemView.findViewById(R.id.txt_song_name);
            this.tvSongArtist = (TextView) itemView.findViewById(R.id.txt_songs_artist);
            this.imgSong = (ImageView) itemView.findViewById(R.id.img_song);
            this.cardView = (CardView)itemView.findViewById(R.id.card_view);
            this.layMore = (LinearLayout)itemView.findViewById(R.id.linear_more);

            this.albumsRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view_album);
        }
    }

    public GenresDetailAdp(ArrayList<SongsModelData> data, Context context, Activity activity) {
        this.dataSet = data;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        if (viewType == 0) {
            View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_artist_albums_header, null);
            MyViewHolder ml = new MyViewHolder(v0);
            return ml;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_song_item, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition)
    {
        if (getItemViewType(listPosition) == 0) {
            //nothing
            setUpAlbums(holder.albumsRecyclerView);
        } else {

            holder.tvSongName.setText(dataSet.get(listPosition-1).getTitle());
            holder.tvSongArtist.setText(dataSet.get(listPosition-1).getArtist());
            Picasso.get().load(dataSet.get(listPosition-1).getImg_uri()).placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(holder.imgSong);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (dataSet != null) {
                        ControlMusicPlayer.startSongsWithQueue(context, dataSet, listPosition-1, "genresdetail");
                    }

                }
            });

            (holder).imgSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (dataSet != null) {
                        ControlMusicPlayer.startSongsWithQueue(context, dataSet, listPosition-1, "genresdetail");

                    }

                }
            });

            holder.layMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConstantData.showPopUp(holder.layMore, context, activity, dataSet.get(listPosition-1));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size() +1 ;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (position == 0) {
            viewType = 0;
        } else viewType = 1;
        return viewType;
    }

    private void setUpAlbums(RecyclerView albumsRecyclerview) {

        albumsRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        albumsRecyclerview.setHasFixedSize(true);
        albumsRecyclerview.setNestedScrollingEnabled(false);


        ArtistAlbumAdp mAlbumAdapter = new ArtistAlbumAdp(activity, LoadNormalAlbum.getAlbumsForGener(context, genresid));
        albumsRecyclerview.setAdapter(mAlbumAdapter);
    }
}
