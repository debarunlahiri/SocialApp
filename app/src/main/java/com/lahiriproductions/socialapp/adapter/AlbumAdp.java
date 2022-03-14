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
import com.lahiriproductions.socialapp.models.AlbumModelData;
import com.lahiriproductions.socialapp.fragments.AlbumDetailFrg;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AlbumAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int typeItem = 1;
    private List<AlbumModelData> dataSet;
    Context context;
    Activity activity;
    int tempPos;
    boolean isGrid;

    VHItem temp_holder;

    public AlbumAdp(List<AlbumModelData> data, Context context, Activity activity) {
        this.dataSet = data;
        this.context = context;
        this.activity = activity;
        this.isGrid = PreferencesData.getInstance(context).isAlbumsInGrid();
        showIntrestialAds(activity);
    }

    private AlbumModelData getItem(int position) {

        return dataSet.get(position);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof VHItem) {
            final AlbumModelData dataItem = getItem(position);
            ((VHItem) holder).artistName.setText(dataItem.title);
            ((VHItem) holder).albumSongCount.setText(dataItem.artistName);

            Picasso.get().load(dataItem.img_uri).placeholder(R.drawable.musicalbumicon).error(R.drawable.musicalbumicon).into(((VHItem) holder).artistImage);

            ((VHItem) holder).content.setOnClickListener(new View.OnClickListener() {
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
                        temp_holder = ((VHItem) holder);


                    } else {
                        ConstantData.sharedInstance(activity).fragmentReplaceTransition(new AlbumDetailFrg().newInstance(dataItem.id, dataItem.title), "AlbumDetailFragment", activity);
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

        return typeItem;
    }


    @Override
    public int getItemCount() {

        return dataSet.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView artistName, albumSongCount;
        ImageView artistImage;
        RelativeLayout content;

        public VHItem(View itemView) {
            super(itemView);

            this.artistName = (TextView) itemView.findViewById(R.id.artist_name);
            this.albumSongCount = (TextView) itemView.findViewById(R.id.album_song_count);
            this.artistImage = (ImageView) itemView.findViewById(R.id.artistImage);
            this.content = (RelativeLayout) itemView.findViewById(R.id.content);

        }
    }


    public void showIntrestialAds(final Activity act) {

//        ConstantData.sharedInstance(activity).fragmentReplaceTransition(new AlbumDetailFrg().newInstance(dataSet.get(tempPos).id, dataSet.get(tempPos).title), "AlbumDetailFragment", activity);


    }

}