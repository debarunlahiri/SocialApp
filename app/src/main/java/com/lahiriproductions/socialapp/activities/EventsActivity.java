package com.lahiriproductions.socialapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lahiriproductions.socialapp.R;

public class EventsActivity extends AppCompatActivity {

    private Toolbar tbEvents;

    private TextView tvEventsDetailDesc, tvEventsDetailDate;

    private com.lahiriproductions.socialapp.events.EventsData eventsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        eventsData = (com.lahiriproductions.socialapp.events.EventsData) getIntent().getSerializableExtra("eventsData");

        tbEvents = findViewById(R.id.tbEvents);
        tbEvents.setTitle(eventsData.getEventName());
        tbEvents.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tbEvents);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tbEvents.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_white_arrow_back_24));
        tbEvents.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvEventsDetailDesc = findViewById(R.id.tvEventsDetailDesc);
        tvEventsDetailDate = findViewById(R.id.tvEventsDetailDate);


        tvEventsDetailDesc.setText(eventsData.getDescription());
        tvEventsDetailDate.setText(eventsData.getDateTime());

    }
}