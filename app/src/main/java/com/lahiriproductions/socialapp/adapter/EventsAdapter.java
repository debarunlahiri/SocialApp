package com.lahiriproductions.socialapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.activities.EventsActivity;
import com.lahiriproductions.socialapp.events.EventsData;

import java.io.Serializable;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context mContext;
    private List<com.lahiriproductions.socialapp.events.EventsData> eventsDataList;


    public EventsAdapter(Context mContext, List<EventsData> eventsDataList) {
        this.mContext = mContext;
        this.eventsDataList = eventsDataList;
    }

    public void setEventsDataList(List<EventsData> eventsDataList) {
        this.eventsDataList = eventsDataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_events_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventsData eventsData = eventsDataList.get(position);

        holder.tvEventDate.setText(eventsData.getDateTime());
        holder.tvEventDesc.setText(eventsData.getDescription());
        holder.tvEventName.setText(eventsData.getEventName());

        holder.cvEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eventsIntent = new Intent(mContext, EventsActivity.class);
                eventsIntent.putExtra("eventsData", (Serializable) eventsData);
                mContext.startActivity(eventsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvEventName, tvEventDesc, tvEventDate;
        private CardView cvEvents;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDesc = itemView.findViewById(R.id.tvEventDesc);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            cvEvents = itemView.findViewById(R.id.cvEvents);
        }
    }
}
