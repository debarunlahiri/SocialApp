package com.lahiriproductions.socialapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.SongFolderData;
import com.lahiriproductions.socialapp.utils_data.OnItemClickListener;

import java.util.List;


public class AudioFolderAdp extends RecyclerView.Adapter<AudioFolderAdp.MyViewHolder> {

    public static List<SongFolderData> listSong;
    private Context context;
    public static final int tremFolder = 1;
    static OnItemClickListener onItemClickListener;
    Activity activity;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout rLayFolder;
        TextView tvFolderName;
        TextView tvVideoCount;

        public MyViewHolder(View videoRow) {
            super(videoRow);

            tvFolderName = (TextView) videoRow.findViewById(R.id.text_folder_name);
            tvVideoCount = (TextView) videoRow.findViewById(R.id.text_video_count);
            this.rLayFolder = (RelativeLayout) itemView.findViewById(R.id.relative_folder);
            this.rLayFolder.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(v, getAdapterPosition());
            }
        }
    }


    public AudioFolderAdp(Context context, Activity act, List<SongFolderData> data, OnItemClickListener listener) {
        this.context = context;
        listSong = data;
        this.activity = act;
        onItemClickListener = listener;
    }

    public AudioFolderAdp() {

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        MyViewHolder myViewHolder;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_folder_video, parent, false);
        myViewHolder = new MyViewHolder(view);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tvFolderName.setText(listSong.get(position).getBucketName());
        holder.tvVideoCount.setText(listSong.get(position).getTotalCount() + " Songs");

    }

    @Override
    public int getItemCount() {
        if (listSong == null) {
            return 0;
        } else {
            return listSong.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return tremFolder;
    }


}