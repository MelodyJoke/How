package com.melody.how.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.melody.how.R;
import com.melody.how.bean.Song;
import com.melody.how.util.HttpUtils;

public class MusicDownloader extends IntentService {

    private static final String TAG = "MusicDownloader";
    private static final int NOTIFICATION_ID = 178;
    private static final int NOTIFICATION_ID_PIC = 179;

    private NotificationManager manager;

    private boolean isDownloading;

    public MusicDownloader() {
        super("downloader");
    }

    public MusicDownloader(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Song temp = (Song) intent.getExtras().get("temp");
        Log.i(TAG, "temp displayname: " + temp.getDisplayName());
        isDownloading = true;

        manager.notify("downloader", NOTIFICATION_ID, new Notification.Builder(MusicDownloader.this)
                .setSmallIcon(isDownloading ?
                        R.drawable.ic_file_download_white_48dp : R.drawable.ic_pause_white_48dp)
                .setColor(getResources().getColor(isDownloading ?
                        R.color.colorPrimary : R.color.colorAccent, getTheme()))
                .setOngoing(true)
                .setContentTitle(getString(isDownloading ? R.string.download_message : R.string.pause))
                .setContentText(temp.getDisplayName())
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentIntent(PendingIntent.getService(MusicDownloader.this, 101,
                        new Intent("com.melody.how.DOWNLOAD_PAUSE"), PendingIntent.FLAG_UPDATE_CURRENT))
                .build());

        String result = HttpUtils.getSong(temp.getPath());

        if (result.equals("ok") || result.equals("exists")) {
            isDownloading = false;

            manager.notify("downloader", NOTIFICATION_ID, new Notification.Builder(MusicDownloader.this)
                    .setSmallIcon(R.drawable.ic_done_white_48dp)
                    .setColor(getResources().getColor(R.color.complete, getTheme()))
                    .setContentTitle(getString(R.string.complete_loading))
                    .setContentText(temp.getDisplayName())
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .build());
        } else if (result.equals("failed")) {
            isDownloading = false;

            manager.notify("downloader", NOTIFICATION_ID, new Notification.Builder(MusicDownloader.this)
                    .setSmallIcon(R.drawable.ic_warning_white_48dp)
                    .setColor(getResources().getColor(R.color.colorAccent, getTheme()))
                    .setContentTitle(getString(R.string.download_error))
                    .setContentText(temp.getDisplayName())
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setContentIntent(PendingIntent.getService(MusicDownloader.this, 101,
                            new Intent("com.melody.how.DOWNLOAD_PAUSE"), PendingIntent.FLAG_UPDATE_CURRENT))
                    .build());
        }

        result = HttpUtils.getPicture(temp.getAlbum(), temp.getArtist());
        if (result.equals("exists") || result.equals("ok"))
            manager.notify("downloader", NOTIFICATION_ID_PIC, new Notification.Builder(MusicDownloader.this)
                    .setSmallIcon(R.drawable.ic_done_white_48dp)
                    .setColor(getResources().getColor(R.color.complete, getTheme()))
                    .setContentTitle(getString(result.equals("ok") || result.equals("exists") ?
                            R.string.picpre_complete
                            : R.string.picpre_error))
                    .setContentText(temp.getDisplayName())
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .build());
    }

}
