package com.lahiriproductions.socialapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
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
import com.lahiriproductions.socialapp.fragments.MainSongsListFrag;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.music_controls.ControlMusicPlayer;
import com.lahiriproductions.socialapp.music_services.PlayMusicService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class QueueAdp extends SongSelectableAdp<QueueAdp.MyViewHolder> {

    Context context;
    Activity activity;
    private MyViewHolder.ClickListener clickListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView tvSongName;
        TextView tvSongArtist;
        ImageView imgSong;
        CardView cardView;
        LinearLayout layMore;
        private ClickListener listener;
        public View selectedOverlay;

        public MyViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            this.listener = listener;
            this.tvSongName = (TextView) itemView.findViewById(R.id.txt_song_name);
            this.tvSongArtist = (TextView) itemView.findViewById(R.id.txt_songs_artist);
            this.imgSong = (ImageView) itemView.findViewById(R.id.img_song);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.layMore = (LinearLayout) itemView.findViewById(R.id.linear_more);
            selectedOverlay = (View) itemView.findViewById(R.id.selected_overlay);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
            }

        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition());
            }

            return false;
        }

        public interface ClickListener {
            public void onItemClicked(int position);

            public boolean onItemLongClicked(int position);
        }
    }

    public QueueAdp(ArrayList<SongsModelData> data, Context context, Activity activity, MyViewHolder.ClickListener clickListener) {
        this.context = context;
        this.activity = activity;
        this.clickListener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_song_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view, clickListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        holder.tvSongName.setText(ConstantData.mediaItemsArrayList.get(listPosition).getTitle());
        holder.tvSongArtist.setText(ConstantData.mediaItemsArrayList.get(listPosition).getArtist());
        Picasso.get().load(ConstantData.mediaItemsArrayList.get(listPosition).getImg_uri()).placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(holder.imgSong);

        if (ControlMusicPlayer.songNumber == listPosition) {

            MainSongsListFrag.queueId = ConstantData.mediaItemsArrayList.get(listPosition).getQueue_id();
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.queue_current_play));
        } else {
            holder.cardView.setBackgroundColor(Color.parseColor("#00000000"));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;
                if (ConstantData.sharedpreferences == null) {
                    ConstantData.savePrefrence(context);
                }
                SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                editor1.commit();


                if (getSelectedItemCount() == 0) {

                    if (!ConstantData.isServiceRunning(PlayMusicService.class.getName(), context)) {
                        Intent musIntent = new Intent(context, PlayMusicService.class);
                        context.startService(musIntent);
                    }
                    ControlMusicPlayer.songNumber = listPosition;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PlayMusicService.playSong();
                        }
                    }, 100);
                    notifyDataSetChanged();
                    holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.queue_current_play));

                    MainSongsListFrag.queueRecyclerview.setVisibility(View.GONE);

                } else {
                    toggleSelection(listPosition);
                }
            }
        });


        holder.layMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstantData.showPopUp(holder.layMore, context, activity, ConstantData.mediaItemsArrayList.get(listPosition));
            }
        });
        holder.selectedOverlay.setVisibility(isSelected(listPosition) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return ConstantData.mediaItemsArrayList.size();
    }

}
