package com.lahiriproductions.socialapp.fragments;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.collection.ArraySet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.MusicAdapter;
import com.lahiriproductions.socialapp.models.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MediaPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MediaPlayerFragment extends Fragment implements MusicAdapter.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = MediaPlayerFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;

    private RecyclerView rvMusicList;
    private ImageView ivSoundSelect, ivMusicArt, ivSetRingtone;
    private TextView tvSoundFileName, tvSoundFileFormat;
    private CardView cvSoundRecording;

    private List<Music> musicList = new ArrayList<>();
    private MusicAdapter musicAdapter;

    private Cursor cursor;
    private String song_name;
    private ArraySet<String> fullsongpath = new ArraySet<>();
    private String album_name;
    private String artist_name;


    public MediaPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MediaPlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MediaPlayerFragment newInstance(String param1, String param2) {
        MediaPlayerFragment fragment = new MediaPlayerFragment();
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
        return inflater.inflate(R.layout.fragment_media_player, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        rvMusicList = view.findViewById(R.id.rvMusicList);
        tvSoundFileName = view.findViewById(R.id.tvSoundFileName);
        tvSoundFileFormat = view.findViewById(R.id.tvSoundFileFormat);
        ivSoundSelect = view.findViewById(R.id.ivSoundSelect);
        ivSetRingtone = view.findViewById(R.id.ivSetRingtone);
        cvSoundRecording = view.findViewById(R.id.cvSoundRecording);

        musicAdapter = new MusicAdapter(mContext, musicList, this::onItemClick);
        rvMusicList.setLayoutManager(new LinearLayoutManager(mContext));
        rvMusicList.setAdapter(musicAdapter);

        cvSoundRecording.setVisibility(View.GONE);


//        String[] columns = { MediaStore.Audio.Media.DATA,
//                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
//                MediaStore.Audio.Media.DISPLAY_NAME,
//                MediaStore.Audio.Media.MIME_TYPE,
//                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
//                MediaStore.Audio.Media.IS_RINGTONE,
//                MediaStore.Audio.Media.IS_ALARM,
//                MediaStore.Audio.Media.IS_MUSIC,
//                MediaStore.Audio.Media.IS_NOTIFICATION };
//
//        Cursor cursor = getActivity().managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
//
//        int fileColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
//        int mimeTypeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
//        if (cursor.moveToFirst()) {
//
//            String audioFilePath = cursor.getString(fileColumn);
//            String mimeType = cursor.getString(mimeTypeColumn);
//            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
//            File newFile = new File(audioFilePath);
//            intent.setDataAndType(Uri.fromFile(newFile), mimeType);
//            startActivity(intent);
//        }


    }

    public void getMp3Songs() {
        musicList.clear();
        try {
            Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            String[] columns = { MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.MIME_TYPE,
                    MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.IS_RINGTONE,
                    MediaStore.Audio.Media.IS_ALARM,
                    MediaStore.Audio.Media.IS_MUSIC,
                    MediaStore.Audio.Media.IS_NOTIFICATION };
            cursor = getActivity().managedQuery(allsongsuri, columns, selection, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        song_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        int song_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                        String fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        fullsongpath.add(fullpath);

                        album_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
//                    int album_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                        artist_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                    int artist_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

                        Log.e(TAG, "getMp3Songs: " + song_name);
                        musicList.add(new Music(fullpath, song_name, album_name, artist_name));
                        musicAdapter.setMusicList(musicList);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getMp3Songs: ", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Dexter.withContext(getActivity())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                getMp3Songs();
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                MultiplePermissionsListener dialogMultiplePermissionsListener =
                        DialogOnAnyDeniedMultiplePermissionsListener.Builder
                                .withContext(getActivity())
                                .withTitle("Storage permission")
                                .withMessage("Storage permission is required in order to list your musics.")
                                .withButtonText(android.R.string.ok)
                                .withIcon(R.mipmap.ic_launcher_round)
                                .build();
                dialogMultiplePermissionsListener.onPermissionRationaleShouldBeShown(permissions, token);
            }
        }).check();
    }

    @Override
    public void onItemClick(Music music, MusicAdapter.ViewHolder holder, List<Music> musicList, int position) {
        if (music != null) {
            cvSoundRecording.setVisibility(View.VISIBLE);
            ivSoundSelect.setVisibility(View.INVISIBLE);
            ivSetRingtone.setVisibility(View.INVISIBLE);
            tvSoundFileName.setText(music.getFile_name());
            tvSoundFileFormat.setText(music.getArtist_name() + " | " + music.getAlbum_name());
        }
    }
}