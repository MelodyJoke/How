package com.melody.how.model.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.melody.how.bean.Song;
import com.melody.how.model.BeanService;

public final class SongDao extends BeanService<Song> {

    public static final String TAG = "SongService";
    public static final String TABLENAME = "Song";
    public static final int QUERY_SELECTION_ARTIST = 0;
    public static final int QUERY_SELECTION_ALBUM = 1;
    public static final String[] COLUMNNAMES = new String[]{
            "id",
            "sysid",
            "displayName",
            "name",
            "path",
            "album",
            "artist",
            "type"};

    public SongDao(Context context) {
        super(context);
        super.TAG = TAG;
        super.TABLENAME = TABLENAME;
    }

    @Override
    public SparseArray<Song> query(String... queryStrings) {
        SQLiteDatabase database = helper.getReadableDatabase();
        StringBuilder selection = new StringBuilder("1=1");

        for (String queryString : queryStrings) {
            selection.append(" and (name like '%" + queryString +
                    "%'or artist like '%" + queryString + "%'or album like '%" + queryString + "%')");
        }
        Log.i(TAG, "query selection " + selection.toString());

        Cursor cursor = database.query(TABLENAME, null,
                queryStrings.length == 0 ? null : selection.toString(), null, null, null, "displayName");
        SparseArray<Song> songs = new SparseArray<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMNNAMES[0]));
            songs.put(id, new Song(id,
                    cursor.getInt(cursor.getColumnIndex(COLUMNNAMES[1])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[2])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[3])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[4])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[5])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[6])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[7]))
            ));

            Log.i(TAG, "query get " + id);
        }
        cursor.close();
        Log.i(TAG, "query " + songs.size() + " songs");

        return songs;
    }

    public SparseArray<Song> query(String queryString, int querySelection) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Log.i(TAG, "query " + queryString);

        Cursor cursor = database.query(TABLENAME, null, querySelection == QUERY_SELECTION_ARTIST ?
                        "artist=?" : "album=?",
                new String[]{queryString}, null, null, "displayName");
        SparseArray<Song> songs = new SparseArray<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMNNAMES[0]));
            songs.put(id, new Song(id,
                    cursor.getInt(cursor.getColumnIndex(COLUMNNAMES[1])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[2])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[3])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[4])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[5])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[6])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[7]))
            ));

            Log.i(TAG, "query get " + id);
        }
        cursor.close();
        Log.i(TAG, "query " + songs.size() + " songs");

        return songs;
    }

    @Override
    protected ContentValues transToValues(Song song) {
        ContentValues values = new ContentValues();

        values.put(COLUMNNAMES[0], song.getId());
        values.put(COLUMNNAMES[1], song.getSysid());
        values.put(COLUMNNAMES[2], song.getDisplayName());
        values.put(COLUMNNAMES[3], song.getName());
        values.put(COLUMNNAMES[4], song.getPath());
        values.put(COLUMNNAMES[5], song.getAlbum());
        values.put(COLUMNNAMES[6], song.getArtist());
        values.put(COLUMNNAMES[7], song.getType());

        return values;
    }
}
