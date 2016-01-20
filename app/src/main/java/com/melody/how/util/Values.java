package com.melody.how.util;

import android.os.Environment;

public class Values {

    public static final String ROOT_URL = "http://172.17.12.202:8080";
    // public static final String ROOT_URL = "http://192.168.1.222:8080";

    public static final String APP_NAME = "/HowServer";

    public static final String LIST_SONG_URL = ROOT_URL + APP_NAME + "/servlet/listSong";

    public static final String ARTIST_PIC_URL = ROOT_URL + APP_NAME + "/res/heads";

    public static final String ALBUM_PIC_URL = ROOT_URL + APP_NAME + "/res/faces";

    public static final String SAVE_SONG_PATH = Environment.getExternalStorageDirectory().getPath() + "/How/songs";

    public static final String SAVE_ALBUM_PATH = Environment.getExternalStorageDirectory().getPath() + "/How/faces";

    public static final String SAVE_ARTIST_PATH = Environment.getExternalStorageDirectory().getPath() + "/How/heads";

    public static final String[] LIST_SONG_PARAMS = {
            "songName", "albumName", "artistName"
    };

    public static final int BOUNDS_DEFAULT = 200;

    public static final int BOUNDS_BIG = 400;
}
