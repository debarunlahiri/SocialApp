package com.lahiriproductions.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.Music;

import java.util.List;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private Context mContext;
    private List<Music> musicList;
    private OnItemClickListener onItemClickListener;

    public MusicAdapter(Context mContext, List<Music> musicList, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.musicList = musicList;
        this.onItemClickListener = onItemClickListener;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_sound_recordings_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.ivSoundSelect.setVisibility(View.INVISIBLE);
        holder.ivSetRingtone.setVisibility(View.INVISIBLE);
        holder.tvSoundFileName.setText(music.getFile_name());
        holder.tvSoundFileFormat.setText(music.getArtist_name() + " | " + music.getAlbum_name());
        holder.cvSoundRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(music, holder, musicList, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSoundFileName, tvSoundFileFormat;
        private CardView cvSoundRecording;
        private ImageView ivSoundSelect, ivMusicArt, ivSetRingtone;
        private LottieAnimationView animationView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSoundFileName = itemView.findViewById(R.id.tvSoundFileName);
            tvSoundFileFormat = itemView.findViewById(R.id.tvSoundFileFormat);
            cvSoundRecording = itemView.findViewById(R.id.cvSoundRecording);
            ivSoundSelect = itemView.findViewById(R.id.ivSoundSelect);
            animationView = itemView.findViewById(R.id.animationView);
            ivMusicArt = itemView.findViewById(R.id.ivMusicArt);
            ivSetRingtone = itemView.findViewById(R.id.ivSetRingtone);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Music music, ViewHolder holder, List<Music> musicList, int position);
    }
}
