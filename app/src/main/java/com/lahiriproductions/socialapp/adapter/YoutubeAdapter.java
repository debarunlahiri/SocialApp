package com.lahiriproductions.socialapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.Youtube;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController;

import java.util.List;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.ViewHolder> {

    private static final String TAG = YoutubeAdapter.class.getSimpleName();
    private Context mContext;
    private List<Youtube> youtubeList;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    public YoutubeAdapter(Context mContext, List<Youtube> youtubeList) {
        this.mContext = mContext;
        this.youtubeList = youtubeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_youtube_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Youtube youtube = youtubeList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl(mContext.getResources().getString(R.string.storage_reference_url));


        try {
            holder.youtube_player_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.cueVideo(youtube.getYoutube_video_id(), 0);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        }

        if (currentUser != null) {
            if (youtube.getUser_id().equals(currentUser.getUid())) {
                holder.bYoutubeDelete.setVisibility(View.VISIBLE);
            } else {
                holder.bYoutubeDelete.setVisibility(View.GONE);
            }
        }

        holder.bYoutubeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Are you sure you want to delete this video?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteVideo(youtube);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void deleteVideo(Youtube youtube) {
        mDatabase.child("youtube_urls").child(youtube.getYoutube_id()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Video deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return youtubeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout flYoutube;
        private YouTubePlayerView youtube_player_view;
        private CardView cvYoutube;
        private Button bYoutubeDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            youtube_player_view = itemView.findViewById(R.id.youtube_player_view);
            cvYoutube = itemView.findViewById(R.id.cvYoutube);
            bYoutubeDelete = itemView.findViewById(R.id.bYoutubeDelete);

            try {
                YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        // using pre-made custom ui
                        DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(youtube_player_view, youTubePlayer);
                        youtube_player_view.setCustomPlayerUi(defaultPlayerUiController.getRootView());
                    }
                };

                youtube_player_view.setEnableAutomaticInitialization(false);
                IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
                youtube_player_view.initialize(listener, options);

            } catch (Exception e) {
                Log.e(TAG, "onBindViewHolder: ", e);
            }
        }
    }
}
