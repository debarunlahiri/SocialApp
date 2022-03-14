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
import com.lahiriproductions.socialapp.data_helper.DataBaseHelper;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.music_controls.ControlMusicPlayer;
import com.lahiriproductions.socialapp.music_services.PlayMusicService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongsAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int typeHeader = 0;
    private static final int typeItems = 1;
    private ArrayList<SongsModelData> dataSet;
    Context context;
    DataBaseHelper dataBaseHelper;
    Activity activity;

    public SongsAdp(ArrayList<SongsModelData> data, Context context, Activity activity) {
        this.dataSet = data;
        this.context = context;
        this.dataBaseHelper = new DataBaseHelper(context);
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == typeItems) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_song_item, parent, false);
            return new VHItem(view);
        } else if (viewType == typeHeader) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_fragment_header, parent, false);
            return new VHHeader(view);
        }


        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof VHItem) {
            final SongsModelData dataItem = getItem(position);
            //cast holder to VHItem and set data

            ((VHItem) holder).tvSongName.setText(dataItem.getTitle());
            ((VHItem) holder).tvSongArtist.setText(dataItem.getArtist());

            Picasso.get().load(dataItem.getImg_uri()).placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(((VHItem) holder).imgSong);


            ((VHItem) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ConstantData.sharedpreferences == null) {
                        ConstantData.savePrefrence(context);
                    }

                    if (dataSet != null) {
                        if (PlayMusicService.mediaPlayer != null) {
                            PlayMusicService.mediaPlayer.reset();
                        }
                        ControlMusicPlayer.startSongsWithQueue(context, dataSet, position - 1, "songs");


                    }

                    ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;

                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                    editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                    editor1.commit();


                }
            });

            ((VHItem) holder).imgSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ConstantData.sharedpreferences == null) {
                        ConstantData.savePrefrence(context);
                    }

                    if (dataSet != null) {
                        if (PlayMusicService.mediaPlayer != null) {
                            PlayMusicService.mediaPlayer.reset();
                        }
                        ControlMusicPlayer.startSongsWithQueue(context, dataSet, position - 1, "songs");


                    }

                    ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;

                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                    editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                    editor1.commit();



                }
            });

            ((VHItem) holder).layMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConstantData.showPopUp(((VHItem) holder).imgMore, context, activity, dataItem);
                }
            });

        } else if (holder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header.
            ((VHHeader) holder).txt_totalSongs.setText(getItemCount() - 1 + "");

        }
    }


    @Override
    public int getItemCount() {
        return dataSet.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return typeHeader;

        return typeItems;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private SongsModelData getItem(int position) {
        return dataSet.get(position - 1);
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView tvSongName;
        TextView tvSongArtist;
        ImageView imgSong;
        CardView cardView;
        LinearLayout layMore;
        ImageView imgMore;

        public VHItem(View itemView) {
            super(itemView);
            this.tvSongName = (TextView) itemView.findViewById(R.id.txt_song_name);
            this.tvSongArtist = (TextView) itemView.findViewById(R.id.txt_songs_artist);
            this.imgSong = (ImageView) itemView.findViewById(R.id.img_song);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.layMore = (LinearLayout) itemView.findViewById(R.id.linear_more);
            this.imgMore = (ImageView) itemView.findViewById(R.id.img_more);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView txt_totalSongs;


        public VHHeader(View finalRow) {
            super(finalRow);
            txt_totalSongs = (TextView) finalRow.findViewById(R.id.txt_totalSongs);
        }
    }
}

