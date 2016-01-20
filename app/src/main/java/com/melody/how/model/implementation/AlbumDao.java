package com.melody.how.model.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.melody.how.bean.Album;
import com.melody.how.model.BeanService;

public class AlbumDao extends BeanService<Album> {

    public static final String TAG = "AlbumService";
    public static final String TABLENAME = "album";
    public static final String[] COLUMNNAMES = new String[]{
            "id",
            "name"};

    public AlbumDao(Context context) {
        super(context);
        super.TAG = TAG;
        super.TABLENAME = TABLENAME;
    }

    @Override
    public SparseArray<Album> query(String... queryStrings) {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = null;
        if (queryStrings.length == 0 || queryStrings[0].equals(""))
            cursor = database.query(TABLENAME, null, null, null, null, null, "id");
        else
            cursor = database.query(TABLENAME, null, "name like '%" + queryStrings[0] + "%'",
                    null, null, null, "id");

        SparseArray<Album> albums = new SparseArray<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMNNAMES[0]));
            albums.put(id, new Album(id, cursor.getString(cursor.getColumnIndex(COLUMNNAMES[1]))));
            Log.i(TAG, "query get " + id);
        }
        cursor.close();
        Log.i(TAG, "query " + albums.size() + " albums");

        return albums;
    }

    @Override
    public ContentValues transToValues(Album album) {
        ContentValues values = new ContentValues();

        values.put(COLUMNNAMES[0], album.getId());
        values.put(COLUMNNAMES[1], album.getName());

        return values;
    }
}
