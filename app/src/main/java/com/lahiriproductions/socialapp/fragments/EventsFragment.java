package com.lahiriproductions.socialapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lahiriproductions.socialapp.AppInterface.ApiCalls;
import com.lahiriproductions.socialapp.AppInterface.ApiInterface;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.EventsAdapter;
import com.lahiriproductions.socialapp.events.Events;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = EventsFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;

    private RecyclerView rvEvents;
    private EventsAdapter eventsAdapter;
    private List<com.lahiriproductions.socialapp.events.EventsData> eventsDataList = new ArrayList<>();

    private Events events;

    public EventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        rvEvents = view.findViewById(R.id.rvEvents);

        eventsAdapter = new EventsAdapter(mContext, eventsDataList);
        rvEvents.setLayoutManager(new LinearLayoutManager(mContext));
        rvEvents.setAdapter(eventsAdapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        eventsDataList.clear();
        eventsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        callEventsApi();
    }

    private void callEventsApi() {
        try {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiInterface.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            ApiCalls apiCalls = retrofit.create(ApiCalls.class);
            Call<Events> eventsCall = apiCalls.getEvents();
            eventsCall.enqueue(new Callback<Events>() {
                @Override
                public void onResponse(Call<Events> call, @NonNull Response<Events> response) {
                    Log.e(TAG, "onResponseUrl: " + response.raw().request().url());
                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponseSuccess: " + response.body().getData());
                        eventsDataList.addAll(response.body().getData());
                        eventsAdapter.setEventsDataList(eventsDataList);
                    } else {
                        Log.e(TAG, "onResponseFailure: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Events> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + new Gson().toJson(t));
                    Toast.makeText(mContext, "Internal server error", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}