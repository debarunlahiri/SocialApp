package com.lahiriproductions.socialapp.fragments;

import static com.lahiriproductions.socialapp.utils.Controller.sendNotification;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.YoutubeAdapter;
import com.lahiriproductions.socialapp.models.Youtube;
import com.lahiriproductions.socialapp.utils.Controller;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YoutubeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoutubeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;

    private EditText etYoutubeUrl;
    private Button bYoutubeSubmit;
    private LinearLayout llYoutube;
    private TextInputLayout tilYoutubeUrl;

    private RecyclerView rvYoutube;
    private YoutubeAdapter youtubeAdapter;
    private List<Youtube> youtubeList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    public YoutubeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YoutubeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoutubeFragment newInstance(String param1, String param2) {
        YoutubeFragment fragment = new YoutubeFragment();
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
        return inflater.inflate(R.layout.fragment_youtube, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        etYoutubeUrl = view.findViewById(R.id.etYoutubeUrl);
        bYoutubeSubmit = view.findViewById(R.id.bYoutubeSubmit);
        llYoutube = view.findViewById(R.id.llYoutube);
        tilYoutubeUrl = view.findViewById(R.id.tilYoutubeUrl);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl(getString(R.string.storage_reference_url));

        rvYoutube = view.findViewById(R.id.rvYoutube);
        youtubeAdapter = new YoutubeAdapter(mContext, youtubeList);
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvYoutube.setLayoutManager(linearLayoutManager);
        rvYoutube.setAdapter(youtubeAdapter);

        bYoutubeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtube_url = etYoutubeUrl.getText().toString();

                if (youtube_url.isEmpty()) {
                    tilYoutubeUrl.setError("Please enter youtube url");
                } else if (Controller.extractYTId(youtube_url).equalsIgnoreCase("error")) {
                    tilYoutubeUrl.setError("Youtube url is not proper");
                } else {
                    String videoId = Controller.extractYTId(youtube_url);
                    String youtube_id = mDatabase.child("youtube_urls").push().getKey().toString();
                    HashMap<String, Object> mDataMap = new HashMap<>();
                    mDataMap.put("youtube_url", youtube_url);
                    mDataMap.put("youtube_video_id", videoId);
                    mDataMap.put("user_id", currentUser.getUid());
                    mDataMap.put("youtube_id", youtube_id);
                    mDataMap.put("posted_on", System.currentTimeMillis());
                    mDatabase.child("youtube_urls").child(youtube_id).setValue(mDataMap);
                    etYoutubeUrl.setText("");
                    sendNotification(mDatabase, currentUser.getUid(), "youtube");
                }
            }
        });

    }

    private void fetchYoutubeVideos() {
        youtubeList.clear();
        mDatabase.child("youtube_urls").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Youtube youtube = snapshot.getValue(Youtube.class);
                    youtubeList.add(youtube);
                    youtubeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String chat_key = snapshot.getKey();
                for (Youtube youtube : youtubeList) {
                    if (youtube.getYoutube_id().equals(chat_key)) {
                        youtubeList.remove(youtube);
                        youtubeAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchYoutubeVideos();
    }
}