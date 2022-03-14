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
import com.lahiriproductions.socialapp.utils_data.SetDateData;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SongHistoryAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter<SongHistoryAdp.Header_DataObjectHolder> {

    private ArrayList<SongsModelData> dataSet;
    Context context;
    Activity activity;
    private static final int typeItems = 1;

    public SongHistoryAdp(ArrayList<SongsModelData> data, Activity activity, Context context) {
        this.dataSet = data;
        this.context = context;
        this.activity = activity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == typeItems) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_song_item, parent, false);

            return new MyViewHolder(view);
        }


        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        if (holder instanceof MyViewHolder) {
            try {
                ((MyViewHolder) holder).tvSongName.setText(dataSet.get(listPosition).getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ((MyViewHolder) holder).tvSongArtist.setText(dataSet.get(listPosition).getArtist());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Picasso.get().load(dataSet.get(listPosition).getImg_uri()).placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(((MyViewHolder) holder).imgSong);
            } catch (Exception e) {
                Picasso.get().load(R.drawable.musicalicon).placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(((MyViewHolder) holder).imgSong);
                e.printStackTrace();
            }

            ((MyViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataSet != null) {
                        ControlMusicPlayer.startSongsWithQueue(context, dataSet, listPosition, "history");
                    }
                    ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;
                    if (ConstantData.sharedpreferences == null) {
                        ConstantData.savePrefrence(context);
                    }
                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                    editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                    editor1.commit();
                }
            });
            ((MyViewHolder) holder).layMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConstantData.showPopUp(((MyViewHolder) holder).layMore, context, activity, dataSet.get(listPosition));
                }
            });
        }
    }

    @Override
    public Header_DataObjectHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_header_date, parent, false);
        Header_DataObjectHolder header_dataObjectHolder = new Header_DataObjectHolder(view);
        return header_dataObjectHolder;
    }

    @Override
    public void onBindHeaderViewHolder(Header_DataObjectHolder holder, int position) {

        if (holder instanceof Header_DataObjectHolder) {

            try {
                String date = dataSet.get(position).getHistory_date();
                String string = null;
                try {
                    if (date instanceof String) {
                        string = SetDateData.formatToYesterdayOrToday_detail(date);
                    } else {
                        string = "";
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                holder.tvHeader.setText(string);
            } catch (Exception e) {
                holder.tvHeader.setVisibility(View.GONE);
                e.printStackTrace();
            }

        }

    }


    @Override
    public long getHeaderId(int position) {
        String value = "0";

        try {
            String date = dataSet.get(position).getHistory_date();
            String str_date = "0";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
                str_date = sdf.format(dateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            try {
                if (str_date instanceof String) {
                    value = str_date;
                } else {
                    value = "0";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Long.parseLong(value);
    }


    @Override
    public int getItemViewType(int position) {

        return typeItems;
    }


    @Override
    public int getItemCount() {

        return dataSet.size();
    }


    public static class Header_DataObjectHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        public Header_DataObjectHolder(View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView.findViewById(R.id.txt_header);
        }
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        TextView tvSongArtist;
        ImageView imgSong;
        LinearLayout layMore;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvSongName = (TextView) itemView.findViewById(R.id.txt_song_name);
            this.tvSongArtist = (TextView) itemView.findViewById(R.id.txt_songs_artist);
            this.imgSong = (ImageView) itemView.findViewById(R.id.img_song);
            this.layMore = (LinearLayout) itemView.findViewById(R.id.linear_more);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);

        }
    }


}
