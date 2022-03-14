package com.lahiriproductions.socialapp.main_functions;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lahiriproductions.socialapp.BuildConfig;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.PlayDetailslistAdp;
import com.lahiriproductions.socialapp.adapter.VideoAdp;
import com.lahiriproductions.socialapp.models.AlbumModelData;
import com.lahiriproductions.socialapp.models.ArtistModelData;
import com.lahiriproductions.socialapp.models.GenersModelData;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.models.VideosDataType;
import com.lahiriproductions.socialapp.data_helper.DataBaseHelper;
import com.lahiriproductions.socialapp.fragments.MainSongsListFrag;
import com.lahiriproductions.socialapp.fragments.PlayListDetaillFrag;
import com.lahiriproductions.socialapp.music_controls.ControlMusicPlayer;
import com.lahiriproductions.socialapp.music_services.PlayMusicService;
import com.lahiriproductions.socialapp.utils_data.VideoDataUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ConstantData {


    //----------------- equilizer ------------------------------------------------------------------

    public static final String band1 = "com.myphoto.musicplayer.band1";
    public static final String band2 = "com.myphoto.musicplayer.band2";
    public static final String band3 = "com.myphoto.musicplayer.band3";
    public static final String band4 = "com.myphoto.musicplayer.band4";
    public static final String band5 = "com.myphoto.musicplayer.band5";
    public static final String band6 = "com.myphoto.musicplayer.band6";
    public static final String band7 = "com.myphoto.musicplayer.band7";
    public static final String band8 = "com.myphoto.musicplayer.band8";
    public static final String bassBoost = "com.myphoto.musicplayer.bass_boost";
    public static final String isEqualizer = "com.myphoto.musicplayer.is_equalizer";

    //------------------- shared object-------------------------------------------------------------

    public final static String strTimeUp = "com.music.time_up";
    public static final int actionSleepTimerCode = 1234;

    final public static String broadcastPlayerPause = "musicplayer.action.BROADCAST_PLAYPAUSE";
    public static final String broadcastPrev = "musicplayer.action.BROADCAST_PREV";
    public static final String broadcastPause = "musicplayer.action.BROADCAST_PAUSE";
    public static final String broadcastNext = "musicplayer.action.BROADCAST_NEXT";


    public static String strSongNumber = "songnumber";
    public static String strSleepTimer = "sleeptimer";
    public static String strSleepHour = "sleephour";
    public static String strSleepMinit = "sleepminit";
    public static String isRepeat = "is_repeat";
    public static String isShuffle = "is_shuffle";
    public static SharedPreferences sharedpreferences;
    public static String prefreanceLastSongKey = "SONGNUMBER";
    public static String prefreanceMainImgBackground = "MAINCUSTOMEBACKGROUND";
    public static String prefreanceMainDefaultBackground = "MAINBACKGROUND";

    public static String trancparentColor = "TRANCPARENTCOLOR";
    public static int trancparentColorDefaultValue = (0 * 0x03000000) + 0x000000; // full transparent
    public static String blurSeekbarPos = "BLURSEEKBARPOS";
    public static String adsCount = "adscount";
    public static String rateUs = "rate_us";
    public static String appusedCount = "appusedcount";
    public static String lastSong;


    public static boolean breakInsertQueue = false;
    public static int adsCounts = 0;

    public static int totalAdsCount = 10;
    public static int videoAdsCount = 2;

    public static boolean isGrid = false;


    public static ArrayList<SongsModelData> mediaItemsArrayList = new ArrayList<SongsModelData>();
    public static ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
    public static ArrayList<Integer> integerArrayList_small = new ArrayList<Integer>();
    public static ArrayList<SongsModelData> songsArrayList = new ArrayList<>();
    public static ArrayList<GenersModelData> generArrayList = new ArrayList<>();
    public static List<ArtistModelData> artistArrayList = new ArrayList<>();
    public static List<AlbumModelData> albumArrayList = new ArrayList<>();

    public static Activity activity;
    public static ConstantData global;

    public static ConstantData sharedInstance(Activity ac) {
        activity = ac;
        if (global == null) {
            global = new ConstantData();
        }
        return global;
    }


    public static Uri getImgUri(Long album_id) {
        try {
            return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), album_id.longValue());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }


    public static boolean currentVersionSupportLockScreenControls() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return true;
        }
        return false;
    }

    public static boolean isServiceRunning(String serviceName, Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceName.equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);
        // return current duration in milliseconds
        return currentDuration * 1000;
    }


    public static void showPopUp(View view, final Context context, final Activity activity, final SongsModelData mediaItems) {
        final PopupMenu popupMenu = new PopupMenu(activity, view);

        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        MenuInflater menuInflater = popupMenu.getMenuInflater();


        menuInflater.inflate(R.menu.popup_menu_music, popupMenu.getMenu());
        popupMenu.show();
        final DataBaseHelper dataBaseHelper = DataBaseHelper.sharedInstance(context);

        final boolean ifPlaylistsongExist = dataBaseHelper.isPlaylist(mediaItems.getSong_id());
        final boolean ifQueuesongExist = dataBaseHelper.isQueuelist(mediaItems.getSong_id());
        final String fileSize = mediaItems.getSize();
        if (ifPlaylistsongExist == false) {
            popupMenu.getMenu().getItem(1).setTitle(activity.getResources().getString(R.string.add_to_playlist));
        } else {
            popupMenu.getMenu().getItem(1).setTitle(activity.getResources().getString(R.string.remove_from_playlist));
        }

        if (ifQueuesongExist == false) {
            popupMenu.getMenu().getItem(0).setTitle(activity.getResources().getString(R.string.add_to_queue));
        } else {
            popupMenu.getMenu().getItem(0).setTitle(activity.getResources().getString(R.string.remove_from_queue));
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().equals(activity.getResources().getString(R.string.add_to_queue))) {
                    dataBaseHelper.insertQueue(mediaItems.getSong_id(), mediaItems.getImg_uri() + "", mediaItems.getTitle(), mediaItems.getPath(), mediaItems.getArtist(), mediaItems.getSize());
                    mediaItemsArrayList.add(mediaItems);
                    MainSongsListFrag.fillQueueAdapter();

                    if (mediaItemsArrayList.size() == 1) {
                        if (PlayMusicService.mediaPlayer != null) {
                            PlayMusicService.mediaPlayer.reset();
                        }
                        ControlMusicPlayer.startSongsWithQueue(context, mediaItemsArrayList, 0, "addqueue");
                    }
                } else if (item.getTitle().equals(activity.getResources().getString(R.string.remove_from_queue))) {
                    int pos = (int) ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber).getSong_id();
                    if ((int) ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber).getSong_id() != (int) (mediaItems.getSong_id())) {
                        dataBaseHelper.deleteQueueSong(mediaItems.getSong_id());
                        ConstantData.mediaItemsArrayList = dataBaseHelper.getQueueData(context);
                        MainSongsListFrag.queueAdp.notifyDataSetChanged();
                        MainSongsListFrag.viewpageSwipeSongPagerAdp.notifyDataSetChanged();
                        try {
                            if (pos > 0) {
                                int i = 0;
                                for (SongsModelData mediaItem : ConstantData.mediaItemsArrayList) {

                                    if ((int) (mediaItem.getSong_id()) == pos) {
                                        ControlMusicPlayer.songNumber = i;

                                        SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                                        editor.putString(ConstantData.prefreanceLastSongKey, ControlMusicPlayer.songNumber + "");
                                        editor.putString("songId", ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber) + "");
                                        editor.commit();

                                        SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                                        editor1.putInt(ConstantData.strSongNumber, ControlMusicPlayer.songNumber);
                                        editor1.commit();
                                        break;
                                    }
                                    i++;
                                }
                            } else {
                                ControlMusicPlayer.songNumber = 0;
                                MainSongsListFrag.changeUi(ControlMusicPlayer.songNumber);  // preference nakhva nu baki 6.

                                SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                                editor.putString(ConstantData.prefreanceLastSongKey, ControlMusicPlayer.songNumber + "");
                                editor.putString("songId", ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber) + "");
                                editor.commit();

                                SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                                editor1.putInt(ConstantData.strSongNumber, ControlMusicPlayer.songNumber);
                                editor1.commit();
                            }

                            MainSongsListFrag.queueAdp.clearSelection();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        if (ConstantData.mediaItemsArrayList.size() == 0) {
                            try {
                                if (MainSongsListFrag.playerSlidingUpPanelLayout != null) {
                                    MainSongsListFrag.playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(context, "Song removed", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "song currently playing", Toast.LENGTH_SHORT).show();
                    }
                } else if (item.getTitle().equals(activity.getResources().getString(R.string.proparty))) {
                    propertyDialog(activity, mediaItems.getPath(), fileSize);
                } else if (item.getTitle().equals(activity.getResources().getString(R.string.add_to_playlist))) {
                    AllFunctions.songAddToPlaylist(activity, mediaItems);
                } else if (item.getTitle().equals(activity.getResources().getString(R.string.remove_from_playlist))) {
                    dataBaseHelper.deletePlayListSong(mediaItems.getSong_id());

                    if (PlayDetailslistAdp.dataSet != null && PlayListDetaillFrag.playDetailslistAdp != null) {
                        PlayDetailslistAdp.dataSet = dataBaseHelper.getPlayListData(PlayListDetaillFrag.playlistId, context);
                        PlayListDetaillFrag.playDetailslistAdp.notifyDataSetChanged();
                    }
                }
                if (item.getTitle().equals(activity.getResources().getString(R.string.share))) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("audio/*");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse(/*"file:///" +*/ mediaItems.getPath()));
                    activity.startActivity(Intent.createChooser(share, "Share Music"));
                }
                return false;
            }
        });
    }

    public static void propertyDialog(Activity activity, String path, String size) {
        LayoutInflater inflater = LayoutInflater.from(activity.getApplicationContext());
        View view = inflater.inflate(R.layout.poperty_alert_dialog, null, false);
        TextView tvPath = (TextView) view.findViewById(R.id.tvPath);
        TextView tvSize = (TextView) view.findViewById(R.id.tvSize);
        tvPath.setText(path);
        try {
            tvSize.setText(toNumInUnits(Long.parseLong(size)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder propertyAlertBuilder = new AlertDialog.Builder(activity);
        propertyAlertBuilder.setTitle("Property");
        propertyAlertBuilder.setView(view);
        propertyAlertBuilder.setPositiveButton(Html.fromHtml("<font color='black'>OK</font>"), null);
        propertyAlertBuilder.show();
    }

    public static String toNumInUnits(long bytes) {
        int u = 0;
        for (; bytes > 1024 * 1024; bytes >>= 10) {
            u++;
        }
        if (bytes > 1024)
            u++;
        return String.format("%.1f %cB", bytes / 1024f, " kMGTPE".charAt(u));
    }

    public static void changeStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }

    //----------------set sleeper time----------------------------------

    public static void savePrefrence(Context context) {
        sharedpreferences = context.getSharedPreferences("preference", Context.MODE_PRIVATE);
    }

    public static FragmentTransaction fragmentTransaction;

    public static void fragmentReplaceTransition(Fragment fragment, String fragment_name, Activity activity) {
        fragmentTransaction = ((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.framlayout_main, fragment, fragment_name);
        fragmentTransaction.addToBackStack(fragment_name);
        fragmentTransaction.commit();

    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }


    //// ------------------ video converter -------------------------

    public static String convert(long miliSeconds) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }

    public static String filesize(String file_path) {

        File file = new File(file_path);
        double length = file.length();

        //   length = length / 1024;
        return formatFileSize(length);
    }

    public static String formatFileSize(double size) {
        String hrSize = null;

        double b = size;
        double k = b / 1024.0;
        double m = k / 1024.0;
        double g = m / 1024.0;
        double t = g / 1024.0;

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public static void popupWindow(View view, final Activity act, final VideosDataType vidModel, final int pos, final String callFrom, final VideoAdp videoAdp) {
        final PopupMenu mPopupMenu = new PopupMenu(act, view, Gravity.BOTTOM);

        try {
            Field[] fields = mPopupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(mPopupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MenuInflater menuInflater = mPopupMenu.getMenuInflater();

        menuInflater.inflate(R.menu.popup_menu_video, mPopupMenu.getMenu());
        mPopupMenu.show();

        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().equals("Watch")) {
//                    Intent intent = new Intent(act, PlayVideoActivity.class);
//                    intent.putExtra("videoFilePath", vidModel.filePath);
//                    intent.putExtra("pos", pos);
//                    intent.putExtra("title", vidModel.title);
//                    act.startActivity(intent);
                } else if (item.getTitle().equals("Delete")) {
                    deleteVideo(act, vidModel, callFrom, videoAdp);
                } else if (item.getTitle().equals("Property")) {
                    PropertyDialog(act, vidModel);
                } else if (item.getTitle().equals("Rename")) {
                    try {
                        renameVideo(vidModel, act, callFrom, videoAdp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item.getTitle().equals("Share")) {
                    Uri videoURI = FileProvider.getUriForFile(act,
                            BuildConfig.APPLICATION_ID + ".provider",
                            new File(vidModel.filePath));

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("video/*");
                    share.putExtra(Intent.EXTRA_STREAM, videoURI);
                    act.startActivity(Intent.createChooser(share, "Share Video"));
                }

                return false;
            }
        });
    }

    public static void PropertyDialog(Activity activity, VideosDataType videoModel) {

        LayoutInflater inflater = LayoutInflater.from(activity.getApplicationContext());
        View view = inflater.inflate(R.layout.property_video_dialog, null, false);
        TextView tvPath = (TextView) view.findViewById(R.id.text_path_value);
        TextView tvSize = (TextView) view.findViewById(R.id.text_video_size_value);
        TextView txt_resolution = (TextView) view.findViewById(R.id.text_resolution_value);
        TextView txt_date = (TextView) view.findViewById(R.id.text_date_value);
        TextView txt_duration = (TextView) view.findViewById(R.id.text_duration);
        tvPath.setText(videoModel.filePath);
        txt_date.setText(dateformate(videoModel.date));
        txt_resolution.setText(videoModel.resolution);
        tvSize.setText(VideoDataUtils.filesize(videoModel.filePath));
        txt_duration.setText(convert(Long.parseLong(videoModel.duration)));
        androidx.appcompat.app.AlertDialog.Builder propertyAlertBuilder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        propertyAlertBuilder.setTitle("Property");
        propertyAlertBuilder.setView(view);
        propertyAlertBuilder.setPositiveButton("OK", null);
        propertyAlertBuilder.show();
    }

    public static void deleteVideo(final Activity ctx, final VideosDataType videoModel, final String call_from, final VideoAdp videoAdp) {
        androidx.appcompat.app.AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(ctx);
        String deleteBody = "Delete File";
        adb.setTitle(deleteBody);
        adb.setMessage(Html.fromHtml("The following video will be deleted permanentily <br><br>" + "<font color='#515151' size='12px'>" + videoModel.filePath.substring(videoModel.filePath.lastIndexOf("/") + 1) + "</font>"));//"The Following video will be deleted permanentily" + "\n\n" +videoModel.filePath);
        adb.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            boolean result = VideoDataUtils.removeMedia(ctx, videoModel.strID, videoModel.filePath);

                            if (result) {
                                if (VideoAdp.dataSet.contains(videoModel)) {
                                    int i = VideoAdp.dataSet.indexOf(videoModel);
                                    VideoAdp.dataSet.remove(videoModel);
                                    videoAdp.notifyDataSetChanged();
                                }
                                Toast.makeText(ctx, "video remove successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ctx, "video can't delete!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ctx, "video can't delete!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        adb.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        androidx.appcompat.app.AlertDialog alert = adb.create();
        alert.show();
    }

    public static void renameVideo(final VideosDataType track, final Activity activity, final String call_from, final VideoAdp videoAdp) {
        View view = LayoutInflater.from(activity).inflate(R.layout.rename_video_dialog, null);
        final EditText inputTitle = (EditText) view.findViewById(R.id.titleEdit);
        try {
            String title = track.title;
            inputTitle.setText(title);
            inputTitle.setSelection(title.length());
        } catch (Exception e) {

        }
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(activity);
        alert.setTitle("Rename File");
        alert.setPositiveButton("Rename",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String newTitle = inputTitle.getText().toString().trim();
                        if (TextUtils.isEmpty(newTitle)) {
                            Toast.makeText(activity, "Enter Text", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        boolean result = renameVideo1(activity, track, call_from, newTitle, videoAdp);
                        if (result) {
                            Toast.makeText(activity, "rename successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "video can't rename", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        alert.setView(view);
        alert.show();

    }

    public static boolean renameVideo1(Context ctx, VideosDataType track, String call_from, String newTitle, VideoAdp videoAdp) {


        try {
            if (!TextUtils.isEmpty(newTitle)) {

                try {
                    newTitle = newTitle.replaceAll(newTitle.substring(newTitle.lastIndexOf(".")), "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String[] selArg = {track.strID};

                if (new File(track.filePath).exists()) {

                    String currentFileName = track.filePath.substring(track.filePath.lastIndexOf("/"), track.filePath.length());
                    File filePath = new File(track.filePath);

                    File dir = filePath.getParentFile();
                    File from = new File(dir, currentFileName);

                    String final_name = newTitle + currentFileName.substring(currentFileName.lastIndexOf("."));
                    File to = new File(dir, final_name);

                    from.renameTo(to);

                    ContentResolver resolver = ctx.getContentResolver();

                    ContentValues valuesMedia = new ContentValues();
                    valuesMedia.put(MediaStore.Video.Media.TITLE, final_name);
                    valuesMedia.put(MediaStore.Video.Media.DISPLAY_NAME, final_name);
                    valuesMedia.put(MediaStore.Video.Media.DATA, to.getAbsolutePath());
                    resolver.update(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, valuesMedia, MediaStore.Video.Media._ID + " = ?", selArg);

                    if (call_from.equals("folder")) {

                    } else if (call_from.equals("search")) {

                    } else {
                        if (videoAdp.dataSet.contains(track)) {
                            int i = videoAdp.dataSet.indexOf(track);
                            track.title = final_name;
                            videoAdp.dataSet.get(i).title = final_name;
                            videoAdp.dataSet.get(i).filePath = to.getAbsolutePath();
                            videoAdp.notifyItemChanged(i);
                        }
                    }
                }

                return true;
            }
        } catch (Exception e) {
            Log.e("exception..rename", e.getMessage() + "..");
            e.printStackTrace();
        }
        return false;
    }

    public static String dateformate(String str_date) {
        try {
            long millisecond = Long.parseLong(str_date) * 1000;
            // or you already have long value of date, use this instead of milliseconds variable.
            String dateString = DateFormat.format("dd-MM-yyyy", new Date(millisecond)).toString();
            return dateString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



}

