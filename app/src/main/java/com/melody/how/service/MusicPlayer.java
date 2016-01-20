package com.melody.how.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.melody.how.activity.AlbumActivity;
import com.melody.how.activity.ArtistActivity;
import com.melody.how.activity.MainActivity;
import com.melody.how.activity.QueueActivity;
import com.melody.how.R;
import com.melody.how.adapter.implementation.AlbumAdapter;
import com.melody.how.bean.Song;
import com.melody.how.util.AnimationUtils;
import com.melody.how.util.ImageCallBack;
import com.melody.how.util.ImageLoader;
import com.melody.how.util.Values;

import java.io.IOException;
import java.util.Random;

public class MusicPlayer extends Service {

    // fields
    private static final String TAG = "MusicPlayer";
    private static final int NOTIFICATION_ID = 177;
    private static final int HANDLER_UPDATE_SEEKBAR = 1;
    public static final int MODE_SINGLE = 0;
    public static final int MODE_REPEAT = 1;
    public static final int MODE_SHUFFLE = 2;

    private MediaPlayer player;
    private NotificationManager manager;
    private MainActivity mainActivity;
    private ArtistActivity artistActivity;
    private AlbumActivity albumActivity;
    private QueueActivity queueActivity;
    private Handler handler;

    private View parent;
    private ImageButton prev, play, next;
    private TextView name, artist;
    private ImageView head;
    private SeekBar seekbar;

    private MusicBinder binder = new MusicBinder();

    private SparseArray<Song> songs;
    private int current;
    private boolean isPaused;
    private boolean isReseted;
    private static int mode = MODE_REPEAT;

    // constructor
    public MusicPlayer() {

    }

    // initialize
    public void obtain(View parent, SparseArray<Song> musics, MainActivity activity) {
        this.isPaused = true;
        this.parent = parent;
        this.songs = musics;
        this.mainActivity = activity;
        this.player = new MediaPlayer();
        setListener();
        this.manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i(TAG, "obtain complete");
    }

    // set listeners
    @SuppressLint("HandlerLeak")
    private void setListener() {
        this.head = (ImageView) parent.findViewById(R.id.player_image_head);
        this.prev = (ImageButton) parent.findViewById(R.id.player_image_prev);
        this.play = (ImageButton) parent.findViewById(R.id.player_image_play);
        this.next = (ImageButton) parent.findViewById(R.id.player_image_next);
        this.name = (TextView) parent.findViewById(R.id.player_text_name);
        this.artist = (TextView) parent.findViewById(R.id.player_text_artist);
        this.seekbar = (SeekBar) parent.findViewById(R.id.player_seekbar_seek);

        final Song currentSong = songs.get(songs.keyAt(current));
        new ImageLoader(getResources(), AlbumAdapter.fixImagePath(currentSong.getAlbum()),
                Values.BOUNDS_DEFAULT, Values.BOUNDS_DEFAULT).loadImage(new ImageCallBack() {
            @Override
            public void getDrawable(Drawable drawable) {
                head.setImageDrawable(drawable);
            }
        });
        this.name.setText(currentSong.getDisplayName());
        this.artist.setText(currentSong.getArtist());

        this.head.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, AlbumActivity.class);
                intent.putExtra("albumName", currentSong.getAlbum());
                mainActivity.startActivity(intent);
            }
        });

        this.prev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mode != MODE_SHUFFLE)
                    onPlay(current - mode < 0 ? songs.size() - mode : current - mode);
                else
                    onPlay(new Random().nextInt(songs.size()));
            }
        });

        changePlay(player.isPlaying());

        this.next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mode != MODE_SHUFFLE)
                    onPlay(current + mode > songs.size() - mode ? 0 : current + mode);
                else
                    onPlay(new Random().nextInt(songs.size()));
            }
        });

        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "onCompletion");
                if (isPaused) return;

                if (mode != MODE_SHUFFLE)
                    onPlay(current + mode > songs.size() - mode ? 0 : current + mode);
                else
                    onPlay(new Random().nextInt(songs.size()));
            }
        });


        if (this.seekbar != null) {
            this.handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == HANDLER_UPDATE_SEEKBAR) {
                        try {
                            int progress = (int) msg.obj;
                            if (progress < 0) progress = 0;
                            seekbar.setProgress((int) (1.0 * progress / player.getDuration() * 100));
                        } catch (Exception e) {
                        }
                    }
                }
            };

            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        player.seekTo(player.getDuration() * seekBar.getProgress() / 100);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        try {
                            Message.obtain(handler, HANDLER_UPDATE_SEEKBAR, player.getCurrentPosition())
                                    .sendToTarget();
                        } catch (Exception e) {
                            Log.i(TAG, "stop update seek");
                            break;
                        }
                    }
                }
            }).start();
        }

        Log.i(TAG, "setListener complete");
    }

    // inner class binder to get the service
    public class MusicBinder extends Binder {
        public MusicPlayer getPlayer() {
            return MusicPlayer.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = "";
        if (intent != null) action = intent.getAction();

        if (action.equals("com.melody.how.ACTION_PAUSE")) {
            if (player != null && player.isPlaying()) {
                onPause();
                sendNotification(songs.get(songs.keyAt(current)), false);
            } else if (player != null) {
                onPlay();
                sendNotification(songs.get(songs.keyAt(current)), true);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        manager.cancelAll();
        super.onDestroy();
    }

    // call player to play
    public void onPlay() {
        Log.i(TAG, "current: " + current);
        this.onPlay(current);
    }

    public void onPlay(int position) {
        final Song currentSong = songs.get(songs.keyAt(position));
        if (currentSong == null) return;

        isPaused = false;

        if (position == current && isPaused && !isReseted) {
            Log.i(TAG, "continue playing");
            player.start();
            changeRemoteViews(true);
            return;
        }

        Log.i(TAG, "start playing");
        current = position;
        player.reset();

        new ImageLoader(getResources(), AlbumAdapter.fixImagePath(currentSong.getAlbum()),
                Values.BOUNDS_DEFAULT, Values.BOUNDS_DEFAULT).loadImage(new ImageCallBack() {

            @Override
            public void getDrawable(Drawable drawable) {
                head.startAnimation(AnimationUtils.getAlphaAnimation(1, 0.5f, 800));

                if (drawable != null)
                    head.setImageDrawable(drawable);
                else
                    head.setImageResource(R.drawable.player_head);

                head.startAnimation(AnimationUtils.getAlphaAnimation(0.5f, 1, 800));

                head.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mainActivity, AlbumActivity.class);
                        intent.putExtra("albumName", currentSong.getAlbum());
                        mainActivity.startActivity(intent);
                    }
                });
            }
        });
        this.name.setText(currentSong.getDisplayName());
        this.artist.setText(currentSong.getArtist());

        try {
            player.setDataSource(currentSong.getPath());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        changeRemoteViews(true);
        sendNowPlaying(currentSong.getId());

        player.start();
        isReseted = isReseted ? false : isReseted;
    }

    // call player to pause
    public void onPause() {
        if (player != null && player.isPlaying()) {
            player.pause();
            changeRemoteViews(false);
        }
    }

    // shut the service
    public void shut() {
        if (player != null) {
            player.release();
            player = null;
        }

        stopSelf();
    }

    // tool to change the play or pause button
    private void changePlay(boolean isPlaying) {

        if (isPlaying) {
            this.play.setImageResource(R.drawable.player_selector_pause);
            this.play.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onPause();
                    isPaused = true;
                    changePlay(false);
                }
            });
        } else {
            this.play.setImageResource(R.drawable.player_selector_play);
            this.play.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onPlay();
                    changePlay(true);
                }
            });
        }
    }

    // tool to create or update notifications
    private void sendNotification(Song currentSong, boolean isPlaying) {
        manager.notify("player", NOTIFICATION_ID, new Notification.Builder(MusicPlayer.this)
                .setTicker(getResources().getString(R.string.ticker))
                .setSmallIcon(isPlaying ?
                        R.drawable.ic_play_arrow_white_48dp : R.drawable.ic_pause_white_48dp)
                .setColor(getResources().getColor(isPlaying ?
                        R.color.colorPrimary : R.color.colorAccent, getTheme()))
                .setContentTitle(currentSong.getDisplayName())
                .setContentText(currentSong.getArtist())
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentIntent(PendingIntent.getService(MusicPlayer.this, 100,
                        new Intent("com.melody.how.ACTION_PAUSE"), PendingIntent.FLAG_UPDATE_CURRENT))
                .build());
    }

    // tool to change play or pause views
    private void changeRemoteViews(boolean isPlaying) {
        isPaused = !isPlaying;
        sendNotification(songs.get(songs.keyAt(current)), isPlaying);
        mainActivity.changeFab(isPlaying);
        if (artistActivity != null)
            artistActivity.changeFab(isPlaying);
        if (albumActivity != null)
            albumActivity.changeFab(isPlaying);
        if (queueActivity != null)
            queueActivity.changeFab(isPlaying);
        changePlay(isPlaying);
    }

    // tool to send to activities which song is now playing
    private void sendNowPlaying(int id) {
        mainActivity.setNowPlaying(id);
        if (artistActivity != null)
            artistActivity.setNowPlaying(id);
        if (albumActivity != null)
            albumActivity.setNowPlaying(id);
        if (queueActivity != null)
            queueActivity.setNowPlaying(id);
    }

    public MusicPlayer setSongs(SparseArray<Song> songs) {
        this.songs = songs;
        return this;
    }

    public MusicPlayer reset() {
        player.reset();
        this.current = 0;
        this.isReseted = true;
        changeRemoteViews(false);
        return this;
    }

    public void setParent(View parent) {
        this.parent = parent;
        setListener();
    }

    public void setArtistActivity(ArtistActivity artistActivity) {
        this.artistActivity = artistActivity;
    }

    public void setAlbumActivity(AlbumActivity albumActivity) {
        this.albumActivity = albumActivity;
    }

    public void setQueueActivity(QueueActivity queueActivity) {
        this.queueActivity = queueActivity;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public static void setMode(int mode) {
        MusicPlayer.mode = mode;
    }
}
