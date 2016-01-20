package com.melody.how.retriver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;

import com.melody.how.bean.Album;
import com.melody.how.bean.Artist;
import com.melody.how.bean.Song;
import com.melody.how.database.DbHelper;
import com.melody.how.model.implementation.AlbumDao;
import com.melody.how.model.implementation.ArtistDao;
import com.melody.how.model.implementation.SongDao;

public abstract class DataRetriver extends AsyncTask<String, Void, SparseArray<Song>> {

    private static final String TAG = "DataRetriver";

    private Context context;
    private SongDao songService;
    private AlbumDao albumService;
    private ArtistDao artistService;

    private static int id;

    public DataRetriver(Context context) {
        this.context = context;
        this.songService = new SongDao(context);
        this.albumService = new AlbumDao(context);
        this.artistService = new ArtistDao(context);
        Log.i(TAG, "new instance");
    }

    @Override
    protected abstract void onPreExecute();

    @Override
    protected SparseArray<Song> doInBackground(String[] types) {
        if (types.length == 0) {
            SparseArray<Song> songs = songService.query();
            if (songs.size() > 0) return songs;
        }

        StringBuilder selection = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            if (i == 0)
                selection.append(MediaStore.Audio.Media.MIME_TYPE + "='" + types[i] + "'");
            else
                selection.append(" or " + MediaStore.Audio.Media.MIME_TYPE + "='" + types[i] + "'");
        }
        Log.i(TAG, "doInBackground selection " + selection.toString());

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                selection.toString(),
                null,
                MediaStore.Audio.Media.TITLE);

        if (cursor == null) {
            return null;
        }

        id = 0;
        songService.delete();
        while (cursor.moveToNext()) {
            this.songService.insert(new Song(++id, cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE))));
        }

        cursor.close();

        cursor = new DbHelper(context).getReadableDatabase().
                rawQuery("select distinct album from song order by album", null);
        id = 0;
        albumService.delete();
        while (cursor.moveToNext()) {
            this.albumService.insert(new Album(++id, cursor.getString(cursor.getColumnIndex("album"))));
        }
        cursor.close();

        cursor = new DbHelper(context).getReadableDatabase().
                rawQuery("select distinct artist from song order by artist", null);
        id = 0;
        artistService.delete();
        while (cursor.moveToNext()) {
            this.artistService.insert(new Artist(++id, cursor.getString(cursor.getColumnIndex("artist"))));
        }
        cursor.close();

        return songService.query();
    }

    @Override
    protected abstract void onPostExecute(SparseArray<Song> songSparseArray);
}
