package com.lahiriproductions.socialapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.google.android.material.navigation.NavigationView;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.QueueAdp;
import com.lahiriproductions.socialapp.adapter.SwipeSongPagerAdp;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.data_helper.DataBaseHelper;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.music_controls.ControlMusicPlayer;
import com.lahiriproductions.socialapp.music_services.PlayMusicService;
import com.lahiriproductions.socialapp.recycle_click.ItemOffsetDecoration;
import com.lahiriproductions.socialapp.utils_data.PreferencesData;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import me.tankery.lib.circularseekbar.CircularSeekBar;


public class MainSongsListFrag extends Fragment implements NavigationView.OnNavigationItemSelectedListener, QueueAdp.MyViewHolder.ClickListener {

    public static RecyclerView queueRecyclerview;
    public static Activity activity;


    public static QueueAdp.MyViewHolder.ClickListener clickListener;

    //============PLayer Control
    public static SlidingUpPanelLayout playerSlidingUpPanelLayout;
    public static LinearLayout layPanel, layPlay;
    public static RelativeLayout layQueue;
    public static Context mContext;
    public static SeekBar playerSeekbar;
    public static CircularSeekBar customProgressBar;
    public static ViewPager playerViewpager;
    boolean isExpanded = false;
    boolean isCollapsed = false;
    public static LinearLayout laySlidingPanelHeader, layPlayerControl;

    public static ImageView imgPlayerPlayPause, imgPlayerNext, imgPlayerPrevious, imgDeleteList, imgPlayerMenu, imgPlayerBackground, imgRepeatMusicPlayer, imgShuffleMusicPlayer;
    public static TextView tvPlayerStarttime, tvPlayerEndtime;
    public static CircleImageView circlrPlayerImage;

    DataBaseHelper dataBaseHelper;
    public static QueueAdp queueAdp;
    public static SwipeSongPagerAdp viewpageSwipeSongPagerAdp;
    int tempPosition, pos;
    boolean seekbarTrack = true;

    public static CircleImageView imgAlbumFooter;

    //============Footer Panel Control
    public static ImageView imgPlayPauseFooter, imgQueueList, imgPlayMenuFooter, imgNavHeader;
    public static TextView tvSingerNameFooter, tvSongNameFooter, tvStartTimeFooterLayout;

    int tempQueuePos;
    public static int queueId;
    static int mainDefaultBackground = 0;




    String str_temp;
    public static TextView tvHeader;

    //===================== Equalizer ==================================

    public static Equalizer equalizer = null;
    public static BassBoost bassBoost = null;

    PreferencesData mPreferences;
    RelativeLayout layout;

    Menu menu;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.musicplayer_main, container, false);

        mContext = getContext();
        activity = getActivity();
        if (ConstantData.sharedpreferences == null) {
            ConstantData.savePrefrence(mContext);
        }
        mPreferences = PreferencesData.getInstance(activity);


        clickListener = this;
        playerControlInit(view);
        replaceFragmentWithAnimation(new HomeFrag());


        String showRateDialog = ConstantData.sharedpreferences.getString(ConstantData.rateUs, "yes");
        if (showRateDialog.equals("yes")) {
            int appUsedCount = ConstantData.sharedpreferences.getInt(ConstantData.appusedCount, 0) + 1;
            ConstantData.sharedpreferences.edit().putInt(ConstantData.appusedCount, appUsedCount).apply();
            if (appUsedCount == 5) {
                AskForRating();
            }
        }


        playerSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset > 0.5) {
                    imgPlayerBackground.setAlpha(slideOffset);
                } else {
                    imgPlayerBackground.setAlpha(0.5f);
                }
                if (slideOffset == 1.0) {
                    layQueue.setVisibility(View.VISIBLE);
                    layPlay.setVisibility(View.GONE);
                } else if (slideOffset == 0.0) {
                    layPlay.setVisibility(View.VISIBLE);
                    layQueue.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    queueAdp.clearSelection();
                    queueAdp.notifyDataSetChanged();
                    isExpanded = true;
                    isCollapsed = false;
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    imgDeleteList.setVisibility(View.GONE);
                    imgQueueList.setVisibility(View.VISIBLE);
                    isExpanded = false;
                    isCollapsed = true;
                } else if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                    if (isExpanded) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                            }
                        }, 100);
                    } else if (isCollapsed) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                            }
                        }, 100);
                    }
                }
                queueRecyclerview.setVisibility(View.GONE);

            }
        });

        laySlidingPanelHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playerSlidingUpPanelLayout.setTouchEnabled(true);

                if (isExpanded) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                    }, 100);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        }
                    }, 100);
                }
                queueRecyclerview.setVisibility(View.GONE);
            }
        });


        if (queueRecyclerview.getVisibility() == View.GONE) {
            playerSlidingUpPanelLayout.setTouchEnabled(true);
        }

        imgQueueList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (queueRecyclerview.getVisibility() == View.GONE) {
                    queueAdp.clearSelection();
                    ConstantData.mediaItemsArrayList = dataBaseHelper.getQueueData(mContext);
                    viewpageSwipeSongPagerAdp.notifyDataSetChanged();
                    queueRecyclerview.scrollToPosition(ControlMusicPlayer.songNumber);
                    queueRecyclerview.setVisibility(View.VISIBLE);
                    playerSlidingUpPanelLayout.setTouchEnabled(false);

                } else {
                    queueRecyclerview.setVisibility(View.GONE);
                    playerSlidingUpPanelLayout.setTouchEnabled(true);
                }

            }
        });
        imgPlayPauseFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!ConstantData.isServiceRunning(PlayMusicService.class.getName(), mContext)) {
                    Intent musIntent = new Intent(mContext, PlayMusicService.class);
                    activity.startService(musIntent);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PlayMusicService.playSong();
                        }
                    }, 200);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mContext.sendBroadcast(new Intent(ConstantData.broadcastPlayerPause));
                        }
                    }, 100);
                }
            }
        });


        imgPlayerPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ConstantData.isServiceRunning(PlayMusicService.class.getName(), mContext)) {
                    Intent musIntent = new Intent(mContext, PlayMusicService.class);
                    activity.startService(musIntent);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PlayMusicService.playSong();
                        }
                    }, 100);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mContext.sendBroadcast(new Intent(ConstantData.broadcastPlayerPause));
                        }
                    }, 100);
                }
            }
        });

        imgPlayerNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConstantData.isServiceRunning(PlayMusicService.class.getName(), mContext)) {
                    mContext.sendBroadcast(new Intent(ConstantData.broadcastNext));
                }
            }
        });

        imgPlayerPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConstantData.isServiceRunning(PlayMusicService.class.getName(), mContext)) {
                    mContext.sendBroadcast(new Intent(ConstantData.broadcastPrev));
                }
            }
        });

        playerViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 1 || state == 2) {
                    tempPosition = pos;
                }

                if (state == 0 && tempPosition != pos) {
                    ControlMusicPlayer.songNumber = pos;

                    if (!ConstantData.isServiceRunning(PlayMusicService.class.getName(), mContext)) {
                        Intent musIntent = new Intent(mContext, PlayMusicService.class);
                        activity.startService(musIntent);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PlayMusicService.playSong();
                            }
                        }, 100);
                    } else {
                        PlayMusicService.playSong();
                    }
                }
            }
        });

        imgRepeatMusicPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PlayMusicService.isRepeat()) {
                    imgRepeatMusicPlayer.setImageResource(R.drawable.ic_repeat_off);
                    Toast.makeText(mContext, "repeat off", Toast.LENGTH_SHORT).show();
                } else {
                    imgRepeatMusicPlayer.setImageResource(R.drawable.ic_repeat_on);
                    Toast.makeText(mContext, "repeat on", Toast.LENGTH_SHORT).show();
                }

                PlayMusicService.setRepeat();
            }
        });

        imgShuffleMusicPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayMusicService.isShuffle()) {
                    imgShuffleMusicPlayer.setImageResource(R.drawable.ic_shuffle_off);
                    Toast.makeText(mContext, "shuffle off", Toast.LENGTH_SHORT).show();
                } else {
                    imgShuffleMusicPlayer.setImageResource(R.drawable.ic_shuffle_white_24dp);
                    Toast.makeText(mContext, "shuffle on", Toast.LENGTH_SHORT).show();
                }
                PlayMusicService.setShuffle();
            }
        });

        imgDeleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deletePermission = new AlertDialog.Builder(activity);
                deletePermission.setTitle("Delete");
                deletePermission.setMessage("Are you sure?\nYou want to delete songs from Queue?");
                deletePermission.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, queueAdp.getSelectedItemCount() + " song deleted !", Toast.LENGTH_SHORT).show();
                        List<Integer> list = queueAdp.getSelectedItems();
                        for (int i = (list.size() - 1); i >= 0; i--) {
                            int pos = list.get(i);
                            long song_id = ConstantData.mediaItemsArrayList.get(pos).getSong_id();

                            if (ControlMusicPlayer.songNumber == pos) {
                                tempQueuePos = ConstantData.mediaItemsArrayList.get(pos).getQueue_id();
                            }
                            dataBaseHelper.deleteQueueSong(song_id);
                            ConstantData.mediaItemsArrayList = dataBaseHelper.getQueueData(mContext);
                            queueAdp.notifyDataSetChanged();
                            viewpageSwipeSongPagerAdp.notifyDataSetChanged();

                        }
                        if (ConstantData.mediaItemsArrayList.size() > 0) {
                            if (tempQueuePos > 0) {
                                int pos = dataBaseHelper.getNext(tempQueuePos);

                                if (pos > 0) {
                                    int i = 0;
                                    for (SongsModelData mediaItem : ConstantData.mediaItemsArrayList) {
                                        if (mediaItem.getQueue_id() == pos) {
                                            ControlMusicPlayer.songNumber = i;

                                            SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                                            editor.putString(ConstantData.prefreanceLastSongKey, ControlMusicPlayer.songNumber + "");
                                            editor.putString("songId", ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber) + "");
                                            editor.commit();

                                            SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                                            editor1.putInt(ConstantData.strSongNumber, ControlMusicPlayer.songNumber);
                                            editor1.commit();

                                            songNext();
                                            tempQueuePos = 0;
                                            break;
                                        }
                                        i++;
                                    }
                                } else {
                                    ControlMusicPlayer.songNumber = 0;
                                    songNext();

                                    SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                                    editor.putString(ConstantData.prefreanceLastSongKey, ControlMusicPlayer.songNumber + "");
                                    editor.putString("songId", ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber) + "");
                                    editor.commit();

                                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                                    editor1.putInt(ConstantData.strSongNumber, ControlMusicPlayer.songNumber);
                                    editor1.commit();

                                }
                                //GlobalApp.SONG_NUMBER=pos;
                            } else {
                                int pos = dataBaseHelper.getCurrent_song(queueId); // queue id


                                if (pos > 0) {
                                    int i = 0;
                                    for (SongsModelData mediaItem : ConstantData.mediaItemsArrayList) {
                                        if (mediaItem.getQueue_id() == pos) {
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
                                    changeUi(ControlMusicPlayer.songNumber);

                                    SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                                    editor.putString(ConstantData.prefreanceLastSongKey, ControlMusicPlayer.songNumber + "");
                                    editor.putString("songId", ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber) + "");
                                    editor.commit();

                                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                                    editor1.putInt(ConstantData.strSongNumber, ControlMusicPlayer.songNumber);
                                    editor1.commit();
                                }
                            }
                        }

                        imgDeleteList.setVisibility(View.GONE);
                        imgQueueList.setVisibility(View.VISIBLE);
                        queueAdp.clearSelection();

                        if (ConstantData.mediaItemsArrayList.size() == 0) {
                            if (ConstantData.isServiceRunning(PlayMusicService.class.getName(), mContext)) {
                                PlayMusicService.stopService();
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainSongsListFrag.playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                                }
                            });
                        }
                    }
                });

                deletePermission.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        queueAdp.clearSelection();
                    }
                });
                deletePermission.show();
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        ConstantData.lastSong = ConstantData.sharedpreferences.getString(ConstantData.prefreanceLastSongKey, "");

        if (ConstantData.lastSong.equals("") || ConstantData.mediaItemsArrayList.size() <= 0) {
            playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        } else {
            ControlMusicPlayer.songNumber = Integer.parseInt(ConstantData.lastSong);
            playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            changeUi(ControlMusicPlayer.songNumber);
            playerViewpager.setCurrentItem(ControlMusicPlayer.songNumber);
            queueId = dataBaseHelper.getCurrent_song(ControlMusicPlayer.songNumber);
        }

        ControlMusicPlayer.progressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Integer i[] = (Integer[]) msg.obj;
                tvPlayerStarttime.setText(ConstantData.getDuration(i[0]));
                tvPlayerEndtime.setText(ConstantData.getDuration(i[1]));
                if (seekbarTrack) {
                    playerSeekbar.setProgress(i[2]);
                    customProgressBar.setProgress((float) i[2]);
                }
            }
        };

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekbarTrack = false;
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {

                if (PlayMusicService.isPng()) {
                    try {
                        int totalDuration = PlayMusicService.getDur();
                        int currentPosition = ConstantData.progressToTimer(seekBar.getProgress(), totalDuration);
                        PlayMusicService.seek(currentPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                seekbarTrack = true;
            }
        });

        customProgressBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {


            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                if (PlayMusicService.isPng()) {
                    try {
                        int totalDuration = PlayMusicService.getDur();
                        int currentPosition = ConstantData.progressToTimer((int) seekBar.getProgress(), totalDuration);
                        PlayMusicService.seek(currentPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                seekbarTrack = true;
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                seekbarTrack = false;
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        this.menu = menu;
        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    private void playerControlInit(View view) {

        // Playe controle
        playerViewpager = (ViewPager) view.findViewById(R.id.viewpager_music_player);

        imgPlayerNext = (ImageView) view.findViewById(R.id.img_next_music_player);
        imgPlayerPrevious = (ImageView) view.findViewById(R.id.img_prev_music_player);
        imgPlayerPlayPause = (ImageView) view.findViewById(R.id.img_play_music_player);

        playerSeekbar = (SeekBar) view.findViewById(R.id.seekBar_music_player);
        customProgressBar = (CircularSeekBar) view.findViewById(R.id.custom_progressBar);
        playerSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        playerSeekbar.setMax(99);
        customProgressBar.setMax(99);

        tvPlayerEndtime = (TextView) view.findViewById(R.id.txt_end_time_music_player);
        tvPlayerStarttime = (TextView) view.findViewById(R.id.txt_start_time_music_player);

        laySlidingPanelHeader = (LinearLayout) view.findViewById(R.id.slidingpanel_header);
        layPlayerControl = (LinearLayout) view.findViewById(R.id.lin_player_control);


        //  Player footer controle
        imgAlbumFooter = (CircleImageView) view.findViewById(R.id.img_album_footer_layout);
        imgPlayMenuFooter = (ImageView) view.findViewById(R.id.img_play_menu_footer);
        imgPlayPauseFooter = (ImageView) view.findViewById(R.id.img_play_song_footer_layout);
        tvSongNameFooter = (TextView) view.findViewById(R.id.txt_song_name_footer_layout);
        tvSongNameFooter.setSelected(true);
        tvSingerNameFooter = (TextView) view.findViewById(R.id.txt_artist_name_footer_layout);
        tvSingerNameFooter.setSelected(true);
        tvStartTimeFooterLayout = (TextView) view.findViewById(R.id.txt_start_time_footer_layout);
        imgPlayerBackground = (ImageView) view.findViewById(R.id.img_background_music_player);
        circlrPlayerImage = (CircleImageView) view.findViewById(R.id.circle_player_image);
        imgShuffleMusicPlayer = (ImageView) view.findViewById(R.id.img_shuffle_music_player);
        imgRepeatMusicPlayer = (ImageView) view.findViewById(R.id.img_repeat_music_player);
        imgQueueList = (ImageView) view.findViewById(R.id.img_queue_list);
        imgDeleteList = (ImageView) view.findViewById(R.id.img_delete_list);


        //====Relative Layout ====
        playerSlidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);

        layPanel = (LinearLayout) view.findViewById(R.id.footer_layout_music_player);
        layPlay = (LinearLayout) view.findViewById(R.id.linear_play);
        layQueue = (RelativeLayout) view.findViewById(R.id.linear_queue);

        queueRecyclerview = (RecyclerView) view.findViewById(R.id.queueRecyclerview);
        queueRecyclerview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        queueRecyclerview.setLayoutManager(layoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(mContext, R.dimen.item_offset);
        queueRecyclerview.addItemDecoration(itemDecoration);

        dataBaseHelper = DataBaseHelper.sharedInstance(mContext);

        int opacity_value = ConstantData.sharedpreferences.getInt(ConstantData.trancparentColor, ConstantData.trancparentColorDefaultValue); // default transparancy
        try {
            laySlidingPanelHeader.setBackgroundColor(opacity_value);

            customProgressBar.setCircleProgressColor(getResources().getColor(R.color.circle_progress_color));
            customProgressBar.setCircleColor(getResources().getColor(R.color.circle_color));
            customProgressBar.setPointerColor(getResources().getColor(R.color.circle_pointer_color));
            customProgressBar.setPointerHaloColor(getResources().getColor(R.color.circle_pointer_holo_color));
        } catch (Exception e) {
            e.printStackTrace();
        }

        fillQueueAdapter();

        setPlayerUI();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        try {
            SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
            editor.putString(ConstantData.prefreanceLastSongKey, ControlMusicPlayer.songNumber + "");
            editor.putString("songId", ConstantData.mediaItemsArrayList.get(ControlMusicPlayer.songNumber) + "");
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.framlayout_main, fragment);
        transaction.commit();
    }

    public static void changeUi(int index) {
        try {
            mainDefaultBackground = Integer.parseInt(ConstantData.sharedpreferences.getString(ConstantData.prefreanceMainDefaultBackground, ConstantData.integerArrayList.get(0) + ""));
            if (ConstantData.mediaItemsArrayList.size() > 0 && tvSongNameFooter != null) {
                if (ConstantData.sharedpreferences == null) {
                    ConstantData.savePrefrence(mContext);
                }
                SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                editor.putString(ConstantData.prefreanceLastSongKey, index + "");
                editor.commit();

                tvSongNameFooter.setText(ConstantData.mediaItemsArrayList.get(index).getTitle());
                tvSingerNameFooter.setText("Artist: " + ConstantData.mediaItemsArrayList.get(index).getArtist());

                if (PlayMusicService.isPng() == true) {
                    imgPlayerPlayPause.setImageResource(R.drawable.ic_pause_circle_outline);
                    imgPlayPauseFooter.setImageResource(R.drawable.ic_pause_circle_outline);
                } else {
                    imgPlayerPlayPause.setImageResource(R.drawable.ic_play_circle_outline);
                    imgPlayPauseFooter.setImageResource(R.drawable.ic_play_circle_outline);
                }
                playerViewpager.setCurrentItem(index);

                Uri img_uri = ConstantData.mediaItemsArrayList.get(index).getImg_uri();
                tvHeader.setText(ConstantData.mediaItemsArrayList.get(index).getTitle());

                Picasso.get().load(img_uri).transform(new BlurTransformation(mContext, 25, 1)).error(mainDefaultBackground).placeholder(mainDefaultBackground).into(imgPlayerBackground);
                Picasso.get().load(img_uri).error(R.drawable.splash_icon).placeholder(R.drawable.splash_icon).into(imgNavHeader);
                Picasso.get().load(img_uri).resize(70, 70).error(R.drawable.splash_icon).placeholder(R.drawable.splash_icon).into(imgAlbumFooter);
                Picasso.get().load(img_uri).resize(500, 500).error(R.drawable.splash_icon).placeholder(R.drawable.splash_icon).into(circlrPlayerImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeButton() {
        try {
            if (ConstantData.isServiceRunning(PlayMusicService.class.getName(), mContext)) {
                if (PlayMusicService.isPng()) {
                    imgPlayerPlayPause.setImageResource(R.drawable.ic_pause_circle_outline);
                    imgPlayPauseFooter.setImageResource(R.drawable.ic_pause_circle_outline);
                } else {
                    imgPlayerPlayPause.setImageResource(R.drawable.ic_play_circle_outline);
                    imgPlayPauseFooter.setImageResource(R.drawable.ic_play_circle_outline);
                }
            } else {
                imgPlayerPlayPause.setImageResource(R.drawable.ic_play_circle_outline);
                imgPlayPauseFooter.setImageResource(R.drawable.ic_play_circle_outline);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fillQueueAdapter() {
        if (ConstantData.mediaItemsArrayList != null) {
            queueAdp = new QueueAdp(null, mContext, activity, clickListener);
            queueRecyclerview.setAdapter(queueAdp);

            viewpageSwipeSongPagerAdp = null;
            viewpageSwipeSongPagerAdp = new SwipeSongPagerAdp(mContext);
            playerViewpager.setAdapter(viewpageSwipeSongPagerAdp);
            playerViewpager.setPageTransformer(true, new DefaultTransformer());

        }
    }

    public void setPlayerUI() {
        ConstantData.lastSong = ConstantData.sharedpreferences.getString(ConstantData.prefreanceLastSongKey, "");
        if (ConstantData.lastSong.equals("")) {
            playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        } else {
            playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            ControlMusicPlayer.songNumber = Integer.parseInt(ConstantData.lastSong);
            playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            //onresume to not playing song
            playerViewpager.setCurrentItem(ControlMusicPlayer.songNumber, true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_myplaylist) {
            str_temp = "playlist";
            ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;

            SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
            editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
            editor1.commit();

            // Handle the camera action
        } else if (id == R.id.nav_equalizer) {

            if (equalizer != null) {
                equalizer.release();
            }
            if (bassBoost != null) {
                bassBoost.release();
            }

            str_temp = "equalizer";

            ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;

            SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
            editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
            editor1.commit();


        } else if (id == R.id.nav_myhistory) {
            str_temp = "history";

            ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;
            if (ConstantData.sharedpreferences == null) {
                ConstantData.savePrefrence(mContext);
            }
            SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
            editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
            editor1.commit();

            if (ConstantData.adsCounts >= ConstantData.totalAdsCount) {
                ConstantData.sharedInstance(activity).fragmentReplaceTransition(new HistoryListFrag(), "HistoryFragment", activity);
            } else {
                ConstantData.sharedInstance(activity).fragmentReplaceTransition(new HistoryListFrag(), "HistoryFragment", activity);
            }
        } else if (id == R.id.nav_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Music Player");
                String sAux = "\n" + getResources().getString(R.string.share_app_msg) + "\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=" + mContext.getPackageName();
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception e) {
            }
        } else if (id == R.id.nav_rateus) {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName()));
            startActivity(i);
        }
        return true;
    }

    @Override
    public void onItemClicked(int position) {
        if (queueAdp.getSelectedItemCount() > 0) {
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        toggleSelection(position);
        return false;
    }

    private void toggleSelection(int position) {
        queueAdp.toggleSelection(position);
    }

    public static void togggle() {

        if (queueAdp.getSelectedItemCount() > 0) {
            imgDeleteList.setVisibility(View.VISIBLE);
            imgQueueList.setVisibility(View.GONE);
        } else {
            imgQueueList.setVisibility(View.VISIBLE);
            imgDeleteList.setVisibility(View.GONE);
        }
    }

    public void songNext() {
        if (ControlMusicPlayer.songNumber < (ConstantData.mediaItemsArrayList.size() - 1)) {
            if (ConstantData.sharedpreferences == null) {
                ConstantData.savePrefrence(mContext);
            }
            SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
            editor.putString(ConstantData.prefreanceLastSongKey, ControlMusicPlayer.songNumber + "");
            editor.commit();
            if (ConstantData.isServiceRunning(PlayMusicService.class.getName(), mContext)) {
                if (PlayMusicService.isPng()) {

                    PlayMusicService.playSong();
                } else {
                    Intent intent = new Intent(mContext, PlayMusicService.class);
                    activity.stopService(intent);
                    changeUi(ControlMusicPlayer.songNumber);

                }
            } else {
                Intent intent = new Intent(mContext, PlayMusicService.class);
                activity.stopService(intent);
                changeUi(ControlMusicPlayer.songNumber);
            }
        }
    }

    private void AskForRating() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(getResources().getString(R.string.ask_rating_title));
        alert.setMessage(getResources().getString(R.string.ask_rating_msg));
        alert.setPositiveButton(Html.fromHtml("<font color='black'>YES</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ConstantData.sharedpreferences.edit().putString(ConstantData.rateUs, "no").apply();
                Intent i = new Intent("android.intent.action.VIEW");
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName()));
                startActivity(i);
            }
        });
        alert.setNegativeButton(Html.fromHtml("<font color='black'>NO,THANKS</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ConstantData.sharedpreferences.edit().putString(ConstantData.rateUs, "no").apply();
            }
        });
        alert.setNeutralButton(Html.fromHtml("<font color='black'>REMIND LATER</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = ConstantData.sharedpreferences.edit();
                editor.putInt(ConstantData.appusedCount, 0);
                editor.apply();
            }
        });
        alert.show();
    }


}
