package com.lahiriproductions.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.PlayAlertListData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class QueueAlertAdp extends ArrayAdapter<PlayAlertListData> {
    private final Context context;
    private final List<PlayAlertListData> data;

    public QueueAlertAdp(Context context, List<PlayAlertListData> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.adp_queue_alert_list, parent, false);
        TextView tvSongNameAlertQueue = (TextView) rowView.findViewById(R.id.txt_song_name_alertqueue_item);
        ImageView imgSongAlertQueue = (ImageView) rowView.findViewById(R.id.img_song_alertqueue_item);
        tvSongNameAlertQueue.setText(data.get(position).getTitle());

        if (!data.get(position).getImg_url().equals("")) {
            Picasso.get().load(data.get(position).getImg_url()).error(R.drawable.musicalicon).placeholder(R.drawable.musicalicon).into(imgSongAlertQueue);
        }

        return rowView;
    }
}
