package com.lahiriproductions.socialapp.adapter;

import static com.lahiriproductions.socialapp.main_functions.ConstantData.mediaItemsArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.data_helper.DataBaseHelper;
import com.lahiriproductions.socialapp.fragments.MainSongsListFrag;
import com.lahiriproductions.socialapp.fragments.PlayListDetaillFrag;
import com.lahiriproductions.socialapp.main_functions.AllFunctions;
import com.lahiriproductions.socialapp.main_functions.ConstantData;
import com.lahiriproductions.socialapp.music_controls.ControlMusicPlayer;
import com.lahiriproductions.socialapp.music_services.PlayMusicService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SearchSongAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static ArrayList<SongsModelData> dataSet;
    Context context;
    Activity activity;
    private static final int typeItems = 1;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        TextView tvSongArtist;
        ImageView imgSong;
        LinearLayout layMore;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvSongName = (TextView) itemView.findViewById(R.id.txt_song_name);
            this.tvSongArtist = (TextView) itemView.findViewById(R.id.txt_songs_artist);
            this.imgSong = (ImageView) itemView.findViewById(R.id.img_song);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.layMore = (LinearLayout) itemView.findViewById(R.id.linear_more);
        }
    }


    public SearchSongAdp(ArrayList<SongsModelData> data, Activity activity, Context context) {
        this.dataSet = data;
        this.context = context;
        this.activity = activity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == typeItems) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adp_song_item, parent, false);

            return new MyViewHolder(view);
        }


        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {
        if (holder instanceof MyViewHolder && dataSet.size() > 0) {
            if (dataSet.get(listPosition) != null) {
                ((MyViewHolder) holder).tvSongName.setText(dataSet.get(listPosition).getTitle());
                ((MyViewHolder) holder).tvSongArtist.setText(dataSet.get(listPosition).getArtist());

                Picasso.get().load(dataSet.get(listPosition).getImg_uri()).placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(((MyViewHolder) holder).imgSong);

            }

            ((MyViewHolder) holder).layMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopUp(((MyViewHolder) holder).layMore, context, activity, dataSet.get(listPosition));

                }
            });

            ((MyViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;
                    if (ConstantData.sharedpreferences == null) {
                        ConstantData.savePrefrence(context);
                    }
                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                    editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                    editor1.commit();

                    if (dataSet != null) {
                        if (PlayMusicService.mediaPlayer != null) {
                            PlayMusicService.mediaPlayer.reset();
                        }
                        ControlMusicPlayer.startSongsWithQueue(context, dataSet, listPosition, "search");

                    }


                }
            });

            ((MyViewHolder) holder).imgSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ConstantData.adsCounts = ConstantData.sharedpreferences.getInt(ConstantData.adsCount, 0) + 1;
                    if (ConstantData.sharedpreferences == null) {
                        ConstantData.savePrefrence(context);
                    }
                    SharedPreferences.Editor editor1 = ConstantData.sharedpreferences.edit();
                    editor1.putInt(ConstantData.adsCount, ConstantData.adsCounts);
                    editor1.commit();

                    if (dataSet != null) {
                        if (PlayMusicService.mediaPlayer != null) {
                            PlayMusicService.mediaPlayer.reset();
                        }
                        ControlMusicPlayer.startSongsWithQueue(context, dataSet, listPosition, "search");

                    }

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (dataSet.size() > 0) {
            return dataSet.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        return typeItems;
    }


    public static void showPopUp(View view, final Context context, final Activity activity, final SongsModelData mediaItems) {
        final androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(activity, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();


        menuInflater.inflate(R.menu.popup_menu_music, popupMenu.getMenu());
        popupMenu.show();
        final DataBaseHelper dataBaseHelper = DataBaseHelper.sharedInstance(context);

        final boolean ifPlaylistsongExist = dataBaseHelper.isPlaylist(mediaItems.getSong_id());
        final boolean ifQueuesongExist = dataBaseHelper.isQueuelist(mediaItems.getSong_id());
        if (ifPlaylistsongExist == false) {
            popupMenu.getMenu().getItem(1).setTitle("Add to Playlist");
        } else {
            popupMenu.getMenu().getItem(1).setTitle("Remove from Playlist");
        }

        if (ifQueuesongExist == false) {
            popupMenu.getMenu().getItem(0).setTitle("Add to Queue");
        } else {
            popupMenu.getMenu().getItem(0).setTitle("Remove from Queue");

        }
        popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().equals("Add to Queue")) {
                    dataBaseHelper.insertQueue(mediaItems.getSong_id(), mediaItems.getImg_uri() + "", mediaItems.getTitle(), mediaItems.getPath(), mediaItems.getArtist(), mediaItems.getSize());
                    mediaItemsArrayList.add(mediaItems);
                    MainSongsListFrag.fillQueueAdapter();
                } else if (item.getTitle().equals("Remove from Queue")) {
                    dataBaseHelper.deleteQueueSong(mediaItems.getSong_id());
                    if (mediaItemsArrayList.get(ControlMusicPlayer.songNumber).getSong_id() != mediaItems.getSong_id()) {
                        mediaItemsArrayList = dataBaseHelper.getQueueData(context);
                        MainSongsListFrag.queueAdp.notifyDataSetChanged();
                        MainSongsListFrag.viewpageSwipeSongPagerAdp.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "song currently playing", Toast.LENGTH_SHORT).show();
                    }
                } else if (item.getTitle().equals("Property")) {
                    propertyDialog(activity, mediaItems.getPath(), mediaItems.getSize());
                } else if (item.getTitle().equals("Add to Playlist")) {
                    AllFunctions.songAddToPlaylist(activity, mediaItems);
                } else if (item.getTitle().equals("Remove from Playlist")) {
                    dataBaseHelper.deletePlayListSong(mediaItems.getSong_id());

                    if (PlayDetailslistAdp.dataSet != null && PlayListDetaillFrag.playDetailslistAdp != null) {
                        PlayDetailslistAdp.dataSet = dataBaseHelper.getPlayListData(PlayListDetaillFrag.playlistId, context);
                        PlayListDetaillFrag.playDetailslistAdp.notifyDataSetChanged();
                    }
                } else if (item.getTitle().equals("Share")) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("audio");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + mediaItems.getPath()));
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
        tvSize.setText(ConstantData.toNumInUnits(Long.parseLong(size)));
        AlertDialog.Builder propertyAlertBuilder = new AlertDialog.Builder(activity);
        propertyAlertBuilder.setTitle("Property");
        propertyAlertBuilder.setView(view);
        propertyAlertBuilder.setPositiveButton("ok", null);
        propertyAlertBuilder.show();
    }

}
