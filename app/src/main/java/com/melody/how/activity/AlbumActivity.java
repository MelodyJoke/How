package com.melody.how.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.melody.how.R;
import com.melody.how.adapter.implementation.AlbumAdapter;
import com.melody.how.adapter.implementation.SongAdapter;
import com.melody.how.bean.Song;
import com.melody.how.service.MusicPlayer;
import com.melody.how.model.implementation.SongDao;
import com.melody.how.util.AnimationUtils;
import com.melody.how.util.ImageCallBack;
import com.melody.how.util.ImageLoader;
import com.melody.how.util.Values;

public class AlbumActivity extends AppCompatActivity {

    private static final String TAG = "AlbumActivity";
    private static final int ANIMATION_MILLIS = 500;

    private RecyclerView songListView;
    private SongAdapter songAdapter;
    private FloatingActionButton fab_play;

    private SongDao songService;
    private MusicPlayer player;
    private ServiceConnection connection;

    private SparseArray<Song> songs;
    private String albumName;

    private boolean isBound;
    private boolean hasSetSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumName = getIntent().getStringExtra("albumName");
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                synchronized (MainActivity.class) {
                    Log.i(TAG, "onServiceConnected: bindService");
                    player = ((MusicPlayer.MusicBinder) iBinder).getPlayer();
                    MainActivity.class.notifyAll();
                }

                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
        bindService(new Intent(AlbumActivity.this, MusicPlayer.class), connection, Context.BIND_AUTO_CREATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(albumName);
        new ImageLoader(getResources(), AlbumAdapter.fixImagePath(albumName),
                Values.BOUNDS_BIG, Values.BOUNDS_BIG).loadImage(new ImageCallBack() {

            @Override
            public void getDrawable(Drawable drawable) {
                toolbarLayout.setBackground(ImageLoader.fixPictureScale(getResources(), drawable));
            }
        });
        toolbarLayout.startAnimation(AnimationUtils.getAnimationSet(
                AnimationUtils.getAlphaAnimation(0, 1, ANIMATION_MILLIS << 1),
                AnimationUtils.getScaleAnimation(1.2f, 1, 1.2f, 1, ANIMATION_MILLIS << 1)));

        fab_play = (FloatingActionButton) findViewById(R.id.fab);
        fab_play.setAnimation(AnimationUtils.getAnimationSet(
                AnimationUtils.getScaleAnimation(0.5f, 1, 0.5f, 1, ANIMATION_MILLIS << 1),
                AnimationUtils.getTranlateAnmation(60, 0, 60, 0, ANIMATION_MILLIS << 1),
                AnimationUtils.getAlphaAnimation(0, 1, ANIMATION_MILLIS << 1)
        ));

        songService = new SongDao(this);
        songListView = (RecyclerView) findViewById(R.id.album_listview_list);
        songListView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        songListView.setLayoutManager(linearLayoutManager);
        songListView.setItemAnimator(new DefaultItemAnimator());

        new AsyncTask<String, Void, SparseArray<Song>>() {

            @Override
            protected SparseArray<Song> doInBackground(String... params) {
                return songService.query(params[0], SongDao.QUERY_SELECTION_ALBUM);
            }

            @Override
            protected void onPostExecute(SparseArray<Song> songSparseArray) {
                songs = songSparseArray;
                songAdapter = new SongAdapter(R.layout.list_item_main, songs, getResources());

                songAdapter.setOnItemClickListener(new SongAdapter.OnRecyclerViewItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        if (isBound) {
                            if (!hasSetSongs) {
                                player.setSongs(songs);
                                hasSetSongs = true;
                            }

                            player.onPlay(position);
                        }
                    }
                });

                songListView.setAdapter(songAdapter);

                synchronized (MainActivity.class) {
                    if (player == null)
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }

                fab_play.setImageResource(player.isPlaying() ?
                        R.drawable.ic_pause_white_48dp : R.drawable.ic_play_arrow_white_48dp);

                fab_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!hasSetSongs) {
                            player.setSongs(songs);
                            hasSetSongs = true;
                        }

                        if (player.isPlaying()) {
                            player.reset();
                            player.onPause();
                        } else {
                            player.reset();
                            player.onPlay();
                        }
                    }
                });

                player.setAlbumActivity(AlbumActivity.this);
            }
        }.execute(albumName);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: unbindService");
        unbindService(connection);
        super.onDestroy();
    }

    // tool to change play or pause fab
    public void changeFab(boolean isPlaying) {
        if (isPlaying) {
            fab_play.setImageResource(R.drawable.ic_pause_white_48dp);
            fab_play.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isBound) {
                        player.onPause();
                        changeFab(false);
                    }
                }
            });
        } else {
            fab_play.setImageResource(R.drawable.ic_play_arrow_white_48dp);
            fab_play.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isBound) {
                        player.onPlay();
                        changeFab(true);
                    }
                }
            });
        }
    }

    public void setNowPlaying(int id) {
        songAdapter.setNowPlaying(id);
        songAdapter.refresh();
    }
}
