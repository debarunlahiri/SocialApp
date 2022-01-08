package com.lahiriproductions.socialapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    }

    @Override
    public int getItemCount() {
        return youtubeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout flYoutube;
        private YouTubePlayerView youtube_player_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            youtube_player_view = itemView.findViewById(R.id.youtube_player_view);

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
