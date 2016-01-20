package com.melody.how.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.melody.how.R;
import com.melody.how.adapter.BeanAdapter;
import com.melody.how.adapter.implementation.AlbumAdapter;
import com.melody.how.adapter.implementation.ArtistAdapter;
import com.melody.how.adapter.implementation.QueueAdapter;
import com.melody.how.adapter.implementation.SongAdapter;
import com.melody.how.bean.Album;
import com.melody.how.bean.Artist;
import com.melody.how.bean.Queue;
import com.melody.how.bean.Song;
import com.melody.how.fragment.AlbumFragment;
import com.melody.how.fragment.ArtistFragment;
import com.melody.how.fragment.OnlineFragment;
import com.melody.how.fragment.QueueFragment;
import com.melody.how.fragment.SongFragment;
import com.melody.how.retriver.DataRetriver;
import com.melody.how.retriver.OnlineDataRetriver;
import com.melody.how.service.MusicDownloader;
import com.melody.how.service.MusicPlayer;
import com.melody.how.model.implementation.AlbumDao;
import com.melody.how.model.implementation.ArtistDao;
import com.melody.how.model.implementation.QueueDao;
import com.melody.how.model.implementation.SongDao;
import com.melody.how.util.AnimationUtils;
import com.melody.how.util.OnFragmentInteractionListener;
import com.melody.how.util.Values;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener,
        OnFragmentInteractionListener {

    // fields
    private static final String TAG = "MainActivity";
    private static final String FRAGMENT_TAG = "fragment";
    private static final int NAVIGATION_ALBUM = 1;
    private static final int NAVIGATION_ARTIST = 2;
    private static final int NAVIGATION_SONG = 3;
    private static final int NAVIGATION_QUEUE = 4;
    private static final int NAVIGATION_ONLINE = 5;
    private static final int ANIMATION_MILLIS = 500;
    private static final int SHOW_SONG = 1;
    private static final int SHOW_ALBUM = 2;
    private static final int SHOW_ARTIST = 3;
    private static final int SHOW_QUEUE = 4;
    private static final int SHOW_ONLINE = 5;

    private SongDao songService;
    private ArtistDao artistService;
    private AlbumDao albumService;
    private QueueDao queueService;
    private MusicPlayer player;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private SongAdapter songAdapter;
    private ArtistAdapter artistAdapter;
    private AlbumAdapter albumAdapter;
    private QueueAdapter queueAdapter;
    private SongAdapter onlineAdapter;

    private FloatingActionButton fab_play;
    private LinearLayout playerLayout;
    private View parent;

    private ServiceConnection connection;
    private SparseArray<Song> songs;
    private boolean isBound;
    private boolean hasSetSongs;
    private Song temp;
    private static int mode = MusicPlayer.MODE_REPEAT;
    private static int show = SHOW_SONG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ImageView user = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        user.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        songService = new SongDao(this);
        artistService = new ArtistDao(this);
        albumService = new AlbumDao(this);
        queueService = new QueueDao(this);
        fragmentManager = getSupportFragmentManager();

        fab_play = (FloatingActionButton) findViewById(R.id.fab);

        playerLayout = (LinearLayout) findViewById(R.id.main_layout_bottom);
        parent = playerLayout.findViewById(R.id.player_layout);

        playerLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View playLayout = playerLayout.findViewById(R.id.player_layout);
                View playLayoutLarge = playerLayout.findViewById(R.id.player_layout_large);
                if (playLayout.getVisibility() == View.VISIBLE) {
                    playLayout.setVisibility(View.GONE);
                    playLayoutLarge.setVisibility(View.VISIBLE);
                    player.setParent(playLayoutLarge);
                    playLayoutLarge.findViewById(R.id.player_image_head).setAnimation(
                            AnimationUtils.getAnimationSet(
                                    AnimationUtils.getScaleAnimation(0.32f, 1, 0.32f, 1, ANIMATION_MILLIS << 1),
                                    AnimationUtils.getTranlateAnmation(0, 0, 325, 0, ANIMATION_MILLIS << 1)));
                    fab_play.setAnimation(AnimationUtils.getAnimationSet(
                            AnimationUtils.getScaleAnimation(1, 0.5f, 1, 0.5f, ANIMATION_MILLIS),
                            AnimationUtils.getTranlateAnmation(0, 60, 0, 60, ANIMATION_MILLIS),
                            AnimationUtils.getAlphaAnimation(1, 0, ANIMATION_MILLIS)
                    ));
                    fab_play.setVisibility(View.GONE);
                } else {
                    playLayout.setVisibility(View.VISIBLE);
                    playLayoutLarge.setVisibility(View.GONE);
                    player.setParent(playLayout);
                    fab_play.setVisibility(View.VISIBLE);
                    fab_play.setAnimation(AnimationUtils.getAnimationSet(
                            AnimationUtils.getScaleAnimation(0.5f, 1, 0.5f, 1, ANIMATION_MILLIS),
                            AnimationUtils.getTranlateAnmation(60, 0, 60, 0, ANIMATION_MILLIS),
                            AnimationUtils.getAlphaAnimation(0, 1, ANIMATION_MILLIS)
                    ));
                }
            }
        });

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                synchronized (MainActivity.class) {
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

        this.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.hasSetSongs = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");

        if (isBound) {
            unbindService(connection);
            player.shut();
            isBound = false;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i(TAG, "action_setting");
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_search) {
            Log.i(TAG, "action_search");
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(this);
            searchView.onActionViewExpanded();
            return true;
        } else if (id == R.id.action_about) {
            Log.i(TAG, "action_about");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.about);
            builder.setPositiveButton(R.string.ok, null).create().show();
            return true;
        } else if (id == R.id.action_refresh) {
            Log.i(TAG, "action_refresh");
            this.loadData("audio/mpeg");
        } else if (id == R.id.action_mode) {
            Log.i(TAG, "action_mode");
            if (mode == MusicPlayer.MODE_REPEAT) {
                mode = MusicPlayer.MODE_SINGLE;
                item.setIcon(R.drawable.ic_repeat_one_white_24dp);
                item.setTitle(R.string.action_mode_single);
                player.setMode(MusicPlayer.MODE_SINGLE);
            } else if (mode == MusicPlayer.MODE_SINGLE) {
                mode = MusicPlayer.MODE_SHUFFLE;
                item.setIcon(R.drawable.ic_shuffle_white_24dp);
                item.setTitle(R.string.action_mode_random);
                player.setMode(MusicPlayer.MODE_SHUFFLE);
            } else {
                mode = MusicPlayer.MODE_REPEAT;
                item.setIcon(R.drawable.ic_repeat_white_24dp);
                item.setTitle(R.string.action_mode_repeat);
                player.setMode(MusicPlayer.MODE_REPEAT);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_album) {
            Log.i(TAG, "nav_album");
            changeNavigation(NAVIGATION_ALBUM);
            show = SHOW_ALBUM;
        } else if (id == R.id.nav_artist) {
            Log.i(TAG, "nav_artist");
            changeNavigation(NAVIGATION_ARTIST);
            show = SHOW_ARTIST;
        } else if (id == R.id.nav_song) {
            Log.i(TAG, "nav_song");
            changeNavigation(NAVIGATION_SONG);
            show = SHOW_SONG;
        } else if (id == R.id.nav_queue) {
            Log.i(TAG, "nav_queue");
            changeNavigation(NAVIGATION_QUEUE);
            show = SHOW_QUEUE;
        } else if (id == R.id.nav_online) {
            Log.i(TAG, "nav_online");
            changeNavigation(NAVIGATION_ONLINE);
            show = SHOW_ONLINE;
        } else if (id == R.id.nav_share) {
            Log.i(TAG, "nav_share");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.nav_share));
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sharedcontent));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getTitle()));
        } else if (id == R.id.nav_send) {
            Log.i(TAG, "nav_send");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.nav_share));
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sharedcontent));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getTitle()));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i(TAG, "show: " + show + " query: " + newText);
        if (show == SHOW_SONG) {
            new AsyncTask<String, Void, SparseArray<Song>>() {

                @Override
                protected SparseArray<Song> doInBackground(String... params) {
                    return songService.query(params);
                }

                @Override
                protected void onPostExecute(SparseArray<Song> songSparseArray) {
                    MainActivity.this.setSongs(songSparseArray);
                    songAdapter.setBeans(songSparseArray).refresh();
                    player.setSongs(songSparseArray);
                }
            }.execute(newText.split(" "));
        } else if (show == SHOW_ALBUM) {
            new AsyncTask<String, Void, SparseArray<Album>>() {

                @Override
                protected SparseArray<Album> doInBackground(String... params) {
                    return albumService.query(params);
                }

                @Override
                protected void onPostExecute(SparseArray<Album> albumSparseArray) {
                    albumAdapter.setBeans(albumSparseArray).refresh();
                }
            }.execute(newText.split(" ")[0]);
        } else if (show == SHOW_ARTIST) {
            new AsyncTask<String, Void, SparseArray<Artist>>() {

                @Override
                protected SparseArray<Artist> doInBackground(String... params) {
                    return artistService.query(params);
                }

                @Override
                protected void onPostExecute(SparseArray<Artist> artistSparseArray) {
                    artistAdapter.setBeans(artistSparseArray).refresh();
                }
            }.execute(newText.split(" ")[0]);
        } else if (show == SHOW_QUEUE) {
            new AsyncTask<String, Void, SparseArray<Queue>>() {

                @Override
                protected SparseArray<Queue> doInBackground(String... params) {
                    return queueService.query(params);
                }

                @Override
                protected void onPostExecute(SparseArray<Queue> queueSparseArray) {
                    queueAdapter.setBeans(queueSparseArray).refresh();
                }
            }.execute(newText.split(" ")[0]);
        } else if (show == SHOW_ONLINE) {
            // TODO
        }

        return true;
    }

    // tool to load or refresh data
    private void loadData(String... types) {
        new DataRetriver(this) {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage(getString(R.string.local_message));
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected void onPostExecute(SparseArray<Song> songSparseArray) {
                songs = songSparseArray;

                songAdapter = new SongAdapter(R.layout.list_item_main, songs, getResources());
                artistAdapter = new ArtistAdapter(R.layout.grid_item_main, artistService.query(), getResources());
                albumAdapter = new AlbumAdapter(R.layout.grid_item_main, albumService.query(), getResources());
                queueAdapter = new QueueAdapter(R.layout.grid_item_main, queueService.query(), getResources());

                bindService(new Intent(MainActivity.this, MusicPlayer.class), connection, Context.BIND_AUTO_CREATE);

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        synchronized (MainActivity.class) {
                            if (player == null) try {
                                MainActivity.class.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        player.obtain(parent, songs, MainActivity.this);
                        changeFab(false);

                        songAdapter.setOnItemClickListener(new SongAdapter.OnRecyclerViewItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {
                                if (!hasSetSongs) {
                                    player.setSongs(songs);
                                    hasSetSongs = true;
                                }

                                if (isBound) {
                                    player.onPlay(position);
                                }
                            }
                        });

                        artistAdapter.setOnItemClickListener(new ArtistAdapter.OnRecyclerViewItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(MainActivity.this, ArtistActivity.class);
                                intent.putExtra("artistName", view.getTag().toString());
                                startActivity(intent);
                            }
                        });

                        albumAdapter.setOnItemClickListener(new AlbumAdapter.OnRecyclerViewItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                                intent.putExtra("albumName", view.getTag().toString());
                                startActivity(intent);
                            }
                        });

                        queueAdapter.setOnItemClickListener(new QueueAdapter.OnRecyclerViewItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(MainActivity.this, QueueActivity.class);
                                intent.putExtra("queueName", view.getTag().toString());
                                startActivity(intent);
                            }
                        });

                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_linerlayout_layout,
                                SongFragment.newInstance(songAdapter), FRAGMENT_TAG);
                        fragmentTransaction.commit();

                        dialog.dismiss();
                        Snackbar.make(fab_play, R.string.complete_loading, Snackbar.LENGTH_SHORT).show();
                    }
                }.execute();
            }
        }.execute(types);
    }

    // tool to change play or stop fab
    public void changeFab(boolean isPlaying) {
        if (isPlaying) {
            fab_play.setImageResource(R.drawable.ic_pause_white_48dp);
            fab_play.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!hasSetSongs) {
                        player.setSongs(songs);
                        hasSetSongs = true;
                    }

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

    // tool to change navigation
    private void changeNavigation(int navigationPosition) {
        if (navigationPosition == NAVIGATION_ALBUM) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_linerlayout_layout,
                            AlbumFragment.newInstance(albumAdapter), FRAGMENT_TAG);
                    fragmentTransaction.commit();
                }
            }).start();
        } else if (navigationPosition == NAVIGATION_ARTIST) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_linerlayout_layout,
                            ArtistFragment.newInstance(artistAdapter), FRAGMENT_TAG);
                    fragmentTransaction.commit();
                }
            }).start();
        } else if (navigationPosition == NAVIGATION_SONG) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_linerlayout_layout,
                            SongFragment.newInstance(songAdapter), FRAGMENT_TAG);
                    fragmentTransaction.commit();
                }
            }).start();
        } else if (navigationPosition == NAVIGATION_QUEUE) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_linerlayout_layout,
                            QueueFragment.newInstance(queueAdapter), "fragment");
                    fragmentTransaction.commit();
                }
            }).start();
        } else if (navigationPosition == NAVIGATION_ONLINE) {
            onlineAdapter = new SongAdapter(R.layout.list_item_main, new SparseArray<Song>(), getResources());
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_linerlayout_layout,
                    OnlineFragment.newInstance(onlineAdapter), "fragment");

            new OnlineDataRetriver() {
                private ProgressDialog dialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog = new ProgressDialog(MainActivity.this);
                    dialog.setMessage(getString(R.string.online_message));
                    dialog.setCancelable(false);
                    dialog.show();
                }

                @Override
                protected void onPostExecute(final SparseArray<Song> songSparseArray) {
                    if (songSparseArray == null) {
                        dialog.dismiss();
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(getString(R.string.download_error))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                }).create().show();

                        return;
                    }

                    onlineAdapter.setBeans(songSparseArray).refresh();
                    onlineAdapter.setOnItemClickListener(new BeanAdapter.OnRecyclerViewItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {
                            temp = songSparseArray.get(songSparseArray.keyAt(position));

                            String savePath = Values.SAVE_SONG_PATH + "/" + temp.getPath().split("/")[2];
                            if (Environment.getExternalStorageState().equals(
                                    Environment.MEDIA_MOUNTED)) {
                                File file = new File(savePath);

                                if (file.exists()) {
                                    Log.i(TAG, "Already downloaded: " + temp.getPath().split("/")[2]);
                                    SparseArray<Song> songs = new SparseArray<>();
                                    temp.setPath(savePath);
                                    songs.put(temp.getId(), temp);
                                    player.setSongs(songs).reset().onPlay(0);
                                    return;
                                }
                            }

                            Intent intent = new Intent(MainActivity.this, MusicDownloader.class);
                            intent.putExtra("temp", temp);
                            startService(intent);
                            Log.i(TAG, "online click");
                        }
                    });
                    dialog.dismiss();
                }
            }.execute();

            fragmentTransaction.commit();
        }
    }

    public void setSongs(SparseArray<Song> songs) {
        this.songs = songs;
    }

    public void setNowPlaying(int id) {
        songAdapter.setNowPlaying(id);
        songAdapter.refresh();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
