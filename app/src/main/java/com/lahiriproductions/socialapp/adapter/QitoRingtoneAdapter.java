package com.lahiriproductions.socialapp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.utils.Constants;
import com.lahiriproductions.socialapp.utils.Controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public class QitoRingtoneAdapter extends RecyclerView.Adapter<QitoRingtoneAdapter.ViewHolder> {

    private Context mContext;
    private List<String> qitoRingtoneList;

    private MediaPlayer mediaPlayer;
    private int currentPlayingPosition;
    private ViewHolder playingHolder;
    private int playingPosition;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String list_type;

    private OnItemClickListener onItemClickListener;

    public QitoRingtoneAdapter(Context mContext, List<String> qitoRingtoneList, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.qitoRingtoneList = qitoRingtoneList;
        this.onItemClickListener = onItemClickListener;
    }

    public void setQitoRingtoneList(List<String> qitoRingtoneList) {
        this.qitoRingtoneList = qitoRingtoneList;
        notifyDataSetChanged();
    }

    public void setListType(String list_type) {
        this.list_type = list_type;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_sound_recordings_layout, parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String string = qitoRingtoneList.get(position);
        holder.ivSetRingtone.setVisibility(View.GONE);
        sharedPreferences = mContext.getSharedPreferences(Constants.SELECTED_AUDIO, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        holder.tvSoundFileName.setText("Audio Recording #" + position+1);
        holder.tvSoundFileFormat.setText("MP3 File");
        mediaPlayer = MediaPlayer.create(mContext, Uri.parse(string));
        Controller.mediaPlayerRecordings = mediaPlayer;
        if (position == playingPosition) {
            playingHolder = holder;
            // this view holder corresponds to the currently playing audio cell
            // update its view to show playing progress
//            updatePlayingView();
        } else {
            // and this one corresponds to non playing
//            updateNonPlayingView(holder);
        }
        if (Objects.equals(list_type, "qito_mix")) {
            holder.ivSoundDelete.setVisibility(View.VISIBLE);
        } else {
            holder.ivSoundDelete.setVisibility(View.GONE);
        }

        holder.cvSoundRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(string, holder, position, qitoRingtoneList);

            }
        });

        holder.ivSoundSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(Constants.SELECTED_AUDIO_PATH, string);
                editor.apply();
                Toast.makeText(mContext, "Audio selected successfully", Toast.LENGTH_SHORT).show();
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

    private void updateNonPlayingView(QitoRingtoneAdapter.ViewHolder holder) {
//        holder.sbProgress.removeCallbacks(seekBarUpdater);
//        holder.sbProgress.setEnabled(false);
//        holder.sbProgress.setProgress(0);
//        holder.ivPlayPause.setImageResource(R.drawable.ic_play_arrow);
//        holder.animationView.setVisibility(View.GONE);
//        holder.ivMusicArt.setVisibility(View.VISIBLE);

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
        return qitoRingtoneList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvSoundFileName, tvSoundFileFormat;
        public CardView cvSoundRecording;
        public ImageView ivSoundSelect, ivMusicArt, ivSetRingtone, ivSoundDelete;
        public LottieAnimationView animationView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSoundFileName = itemView.findViewById(R.id.tvSoundFileName);
            tvSoundFileFormat = itemView.findViewById(R.id.tvSoundFileFormat);
            cvSoundRecording = itemView.findViewById(R.id.cvSoundRecording);
            ivSoundSelect = itemView.findViewById(R.id.ivSoundSelect);
            animationView = itemView.findViewById(R.id.animationView);
            ivMusicArt = itemView.findViewById(R.id.ivMusicArt);
            ivSetRingtone = itemView.findViewById(R.id.ivSetRingtone);
            ivSoundDelete = itemView.findViewById(R.id.ivSoundDelete);
        }

        public void bindData(String string) {
            updateNonPlayingView(playingHolder);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(String string, ViewHolder holder, int position, List<String> qitoRingtoneList);
    }
}
