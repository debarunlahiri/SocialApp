package com.lahiriproductions.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.StopWatchLap;
import com.lahiriproductions.socialapp.utils.Controller;

import java.util.List;

public class StopWatchLapAdapter extends RecyclerView.Adapter<StopWatchLapAdapter.ViewHolder> {

    private Context mContext;
    private List<StopWatchLap> stopWatchLapList;

    public StopWatchLapAdapter(Context mContext, List<StopWatchLap> stopWatchLapList) {
        this.mContext = mContext;
        this.stopWatchLapList = stopWatchLapList;
    }

    public void setStopWatchLapList(List<StopWatchLap> stopWatchLapList) {
        this.stopWatchLapList = stopWatchLapList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_stopwatch_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StopWatchLap stopWatchLap = stopWatchLapList.get(position);
        holder.tvStopWatchCounter.setText("#" + (position + 1));
        holder.tvStopWatchLap.setText(Controller.millisecondsToTime(stopWatchLap.getMilliseconds()));
    }

    @Override
    public int getItemCount() {
        return stopWatchLapList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvStopWatchLap, tvStopWatchCounter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStopWatchLap = itemView.findViewById(R.id.tvStopWatchLap);
            tvStopWatchCounter = itemView.findViewById(R.id.tvStopWatchCounter);
        }
    }
}


