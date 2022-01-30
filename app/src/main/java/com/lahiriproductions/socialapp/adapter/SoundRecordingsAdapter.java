package com.lahiriproductions.socialapp.adapter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.activities.SoundRecordingsActivity;
import com.lahiriproductions.socialapp.models.SoundRecordings;
import com.lahiriproductions.socialapp.utils.Constants;
import com.lahiriproductions.socialapp.utils.FileUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SoundRecordingsAdapter extends RecyclerView.Adapter<SoundRecordingsAdapter.ViewHolder> {

    private Context mContext;
    private List<File> soundRecordingsList;
    private OnItemClickListener onItemClickListener;

    private MediaPlayer mediaPlayer;
    private int currentPlayingPosition;
    private ViewHolder playingHolder;
    private int playingPosition;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SoundRecordingsAdapter(Context mContext, List<File> soundRecordingsList, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.soundRecordingsList = soundRecordingsList;
        this.onItemClickListener = onItemClickListener;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        File soundRecordings = soundRecordingsList.get(position);
        sharedPreferences = mContext.getSharedPreferences(Constants.SELECTED_AUDIO, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        holder.tvSoundFileName.setText(soundRecordings.getAbsolutePath().substring(soundRecordings.getAbsolutePath().lastIndexOf("/")+1));
        holder.tvSoundFileFormat.setText("MP3 File");
        mediaPlayer = MediaPlayer.create(mContext, Uri.parse(soundRecordings.getAbsolutePath()));
        if (position == playingPosition) {
            playingHolder = holder;
            // this view holder corresponds to the currently playing audio cell
            // update its view to show playing progress
            updatePlayingView();
        } else {
            // and this one corresponds to non playing
            updateNonPlayingView(holder);
        }
        holder.cvSoundRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (position == currentPlayingPosition) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        } else {
                            mediaPlayer.start();
                        }
                    } else {
                        currentPlayingPosition = position;
                        if (mediaPlayer != null) {
                            if (null != holder) {
                                updateNonPlayingView(playingHolder);
                            }
                            mediaPlayer.release();
                        }
                        playingHolder = holder;
                        startMediaPlayer(soundRecordingsList.get(currentPlayingPosition), holder);
                    }
                    updatePlayingView();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        holder.ivSoundSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(Constants.SELECTED_AUDIO_PATH, soundRecordings.getAbsolutePath());
                editor.apply();
                Toast.makeText(mContext, "Audio selected successfully", Toast.LENGTH_SHORT).show();
            }
        });

        holder.ivSetRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkSystemWritePermission()) {
                        setAsRingtoneAndroid(soundRecordings);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.System.canWrite(mContext))
                return true;
            else
                openAndroidPermissionsMenu();
        }
        return false;
    }

    private void openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            mContext.startActivity(intent);
        }
    }

    private void setAsRingtoneAndroid(File k) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, k.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, getMIMEType(k.getAbsolutePath()));//// getMIMEType(k.getAbsolutePath())
        values.put(MediaStore.MediaColumns.SIZE, k.length());
        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri newUri = mContext.getContentResolver()
                    .insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            try (OutputStream os = mContext.getContentResolver().openOutputStream(newUri)) {
                int size = (int) k.length();
                byte[] bytes = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(k));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();
                    os.write(bytes);
                    os.close();
                    os.flush();
                } catch (IOException e) {
                }
            } catch (Exception ignored) {
            }
            RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.makeText(mContext, "Ringtone set", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getMIMEType(String url) {
        String mType = null;
        String mExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (mExtension != null) {
            mType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExtension);
        }
        return mType;
    }

    private void updateNonPlayingView(ViewHolder holder) {
//        holder.sbProgress.removeCallbacks(seekBarUpdater);
//        holder.sbProgress.setEnabled(false);
//        holder.sbProgress.setProgress(0);
//        holder.ivPlayPause.setImageResource(R.drawable.ic_play_arrow);
        holder.animationView.setVisibility(View.GONE);
        holder.ivMusicArt.setVisibility(View.VISIBLE);

    }

    private void updatePlayingView() {
//        playingHolder.sbProgress.setMax(mediaPlayer.getDuration());
//        playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
//        playingHolder.sbProgress.setEnabled(true);
//        if (mediaPlayer.isPlaying()) {
//            playingHolder.sbProgress.postDelayed(seekBarUpdater, 100);
//            playingHolder.ivPlayPause.setImageResource(R.drawable.ic_pause);
//        } else {
//            playingHolder.sbProgress.removeCallbacks(seekBarUpdater);
//            playingHolder.ivPlayPause.setImageResource(R.drawable.ic_play_arrow);
//        }
        if (mediaPlayer.isPlaying()) {
            playingHolder.animationView.setVisibility(View.VISIBLE);
            playingHolder.ivMusicArt.setVisibility(View.GONE);
        } else {
            playingHolder.animationView.setVisibility(View.GONE);
            playingHolder.ivMusicArt.setVisibility(View.VISIBLE);
        }
    }

    private void startMediaPlayer(File audioFile, ViewHolder holder) {
        mediaPlayer = MediaPlayer.create(mContext, Uri.parse(audioFile.getAbsolutePath()));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseMediaPlayer();
                playingHolder.animationView.setVisibility(View.GONE);
                playingHolder.ivMusicArt.setVisibility(View.VISIBLE);
            }
        });
        mediaPlayer.start();
    }

    private void releaseMediaPlayer() {
        if (null != playingHolder) {
            updateNonPlayingView(playingHolder);
        }
        mediaPlayer.release();
        mediaPlayer = null;
        currentPlayingPosition = -1;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        if (currentPlayingPosition == holder.getAdapterPosition()) {
            if (playingHolder != null) {
                updateNonPlayingView(playingHolder);
            }
            playingHolder = null;
        }
    }

    @Override
    public int getItemCount() {
        return soundRecordingsList.size();
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
        void onItemClick(File soundRecordings);
    }
}
