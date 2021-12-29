package com.lahiriproductions.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.activities.SoundRecordingsActivity;
import com.lahiriproductions.socialapp.models.SoundRecordings;

import java.io.File;
import java.util.List;

public class SoundRecordingsAdapter extends RecyclerView.Adapter<SoundRecordingsAdapter.ViewHolder> {

    private Context mContext;
    private List<File> soundRecordingsList;

    public SoundRecordingsAdapter(Context mContext, List<File> soundRecordingsList) {
        this.mContext = mContext;
        this.soundRecordingsList = soundRecordingsList;
    }

    public void setSoundRecordingsList(List<File> soundRecordingsList) {
        this.soundRecordingsList = soundRecordingsList;
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
        File soundRecordings = soundRecordingsList.get(position);
    }

    @Override
    public int getItemCount() {
        return soundRecordingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
