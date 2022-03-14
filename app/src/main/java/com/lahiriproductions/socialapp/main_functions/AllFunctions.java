package com.lahiriproductions.socialapp.main_functions;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.QueueAlertAdp;
import com.lahiriproductions.socialapp.models.PlayAlertListData;
import com.lahiriproductions.socialapp.models.SongsModelData;
import com.lahiriproductions.socialapp.data_helper.DataBaseHelper;

import java.util.List;


public class AllFunctions {

    public static DataBaseHelper dataBaseHelper;


    // single song add in playlist
    public static void songAddToPlaylist(final Activity activity, final SongsModelData mediaItem) {

        dataBaseHelper = DataBaseHelper.sharedInstance(activity);
        final AlertDialog.Builder alertAddToPlayList = new AlertDialog.Builder(activity);
        LayoutInflater inflater = LayoutInflater.from(activity.getApplicationContext());
        View convertView = (View) inflater.inflate(R.layout.add_to_playlist_alert, null);
        alertAddToPlayList.setView(convertView);
        RelativeLayout rLayAddToPlayList = (RelativeLayout) convertView.findViewById(R.id.rel_add_to_playlist);
        ListView listCreateNewPlaylist = (ListView) convertView.findViewById(R.id.list_create_new_playlist);
        alertAddToPlayList.setTitle(Html.fromHtml("<font color='#184ea6'>Add To Playlists</font>"));
        final List<PlayAlertListData> alertQueueGeterSeterArrayList = dataBaseHelper.getPlaylist();
        final QueueAlertAdp queueAlertAdp = new QueueAlertAdp(activity, alertQueueGeterSeterArrayList);
        listCreateNewPlaylist.setAdapter(queueAlertAdp);
        final AlertDialog addToPlaylistAlert = alertAddToPlayList.create();
        addToPlaylistAlert.show();

        listCreateNewPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayAlertListData alertQueueGeterSeter = alertQueueGeterSeterArrayList.get(position);
                String strPlayListId = alertQueueGeterSeter.getId();
                String strPlayListName = alertQueueGeterSeter.getTitle();
                int song_count = 0;
                if (dataBaseHelper.AddToPlaylistData(strPlayListId, mediaItem.getSong_id(), mediaItem.getPath())) {
                    song_count++;
                } else {
                    song_count++;
                }
                Toast.makeText(activity.getApplicationContext(), song_count + " Songs Add to Playlist " + strPlayListName, Toast.LENGTH_LONG).show();
                addToPlaylistAlert.cancel();
            }

        });


        rLayAddToPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToPlaylistAlert.cancel();
                final AlertDialog.Builder alertCreateNewPlaylist = new AlertDialog.Builder(activity);
                final LayoutInflater inflater = LayoutInflater.from(activity);
                View convertView = (View) inflater.inflate(R.layout.createnew_playlist_alert, null);
                alertCreateNewPlaylist.setView(convertView);
                final EditText etTitleCreateNewPlaylistAlert = (EditText) convertView.findViewById(R.id.edt_title_createnew_playlist_alert);
                TextView tvCancle = (TextView) convertView.findViewById(R.id.tvCancle);
                TextView tvAdd = (TextView) convertView.findViewById(R.id.tvAdd);
                alertCreateNewPlaylist.setTitle(Html.fromHtml("<font color='#184ea6'>Create New Playlists</font>"));
                final AlertDialog alertDialog = alertCreateNewPlaylist.create();
                alertDialog.show();

                tvCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
                tvAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!etTitleCreateNewPlaylistAlert.getText().toString().trim().equals("") && etTitleCreateNewPlaylistAlert != null) {
                            String playlistName = etTitleCreateNewPlaylistAlert.getText().toString().trim();

                            if (!dataBaseHelper.checkPlaylistExistOrNot(playlistName)) {
                                String playlist_id = dataBaseHelper.insertPlaylist(playlistName, mediaItem.getImg_uri() + "");

                                int song_count = 0;
                                if (dataBaseHelper.AddToPlaylistData(playlist_id, mediaItem.getSong_id(), mediaItem.getPath())) {
                                    song_count++;
                                } else {
                                    song_count++;
                                }
                                Toast.makeText(activity.getApplicationContext(), song_count + " Songs Add to Playlist " + playlistName, Toast.LENGTH_LONG).show();
                            }
                            alertDialog.cancel();
                        } else {
                            Toast.makeText(activity.getApplicationContext(), "Playlist all ready exist", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public SongsModelData fatchSongsDetail(long _id, Context context, int queue_id) {
        dataBaseHelper = DataBaseHelper.sharedInstance(context);
        Cursor c = null;
        SongsModelData songData = null;
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            c = context.getContentResolver().query(uri, null, MediaStore.MediaColumns._ID + "='" + _id + "'", null, null);

            if (c != null && c.getCount() > 0) {
                if (c.moveToFirst()) {
                    long _id1 = c.getLong(c.getColumnIndex("_id"));

                    songData = new SongsModelData();

                    String title = c.getString(c.getColumnIndex("title"));
                    String artist = c.getString(c.getColumnIndex("artist"));
                    String album = c.getString(c.getColumnIndex("album"));
                    long duration = c.getLong(c.getColumnIndex("duration"));
                    String data = c.getString(c.getColumnIndex("_data"));
                    String size = c.getString(c.getColumnIndex("_size"));
                    long albumId = c.getLong(c.getColumnIndex("album_id"));

                    songData.setQueue_id(queue_id);
                    songData.setSong_id(_id1);
                    songData.setSize(size);
                    songData.setTitle(title);
                    songData.setAlbum(album);
                    songData.setArtist(artist);
                    songData.setDuration(duration);
                    songData.setPath(data);
                    songData.setAlbumId(albumId);
                    songData.setImg_uri(ConstantData.getImgUri(Long.valueOf(albumId)));
                }
            } else {
                dataBaseHelper.deleteQueueSong(_id);
            }
        } catch (SQLiteException e) {
            Log.e("Error_favourite", e.getMessage() + "..");
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
        return songData;
    }


    public static final String makeLabel(final Context context, final int pluralInt,
                                         final int number) {
        return context.getResources().getQuantityString(pluralInt, number, number);
    }

    public static final String makeCombinedString(final Context context, final String first,
                                                  final String second) {
        final String formatter = context.getResources().getString(R.string.combine_two_strings);
        return String.format(formatter, first, second);
    }

    public static Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) {

        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }


}
