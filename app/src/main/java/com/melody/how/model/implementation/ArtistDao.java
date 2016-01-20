package com.melody.how.model.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.melody.how.bean.Artist;
import com.melody.how.model.BeanService;

public class ArtistDao extends BeanService<Artist> {

    public static final String TAG = "ArtistService";
    public static final String TABLENAME = "artist";
    public static final String[] COLUMNNAMES = new String[]{
            "id",
            "name"};

    public ArtistDao(Context context) {
        super(context);
        super.TAG = TAG;
        super.TABLENAME = TABLENAME;
    }

    @Override
    public SparseArray<Artist> query(String... queryStrings) {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = null;
        if (queryStrings.length == 0 || queryStrings[0].equals(""))
            cursor = database.query(TABLENAME, null, null, null, null, null, "id");
        else
            cursor = database.query(TABLENAME, null, "name like '%" + queryStrings[0] + "%'",
                    null, null, null, "id");

        SparseArray<Artist> albums = new SparseArray<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMNNAMES[0]));
            albums.put(id, new Artist(id, cursor.getString(cursor.getColumnIndex(COLUMNNAMES[1]))));
            Log.i(TAG, "query get " + id);
        }
        cursor.close();
        Log.i(TAG, "query " + albums.size() + " artists");

        return albums;
    }

    @Override
    public ContentValues transToValues(Artist artist) {
        ContentValues values = new ContentValues();

        values.put(COLUMNNAMES[0], artist.getId());
        values.put(COLUMNNAMES[1], artist.getName());

        return values;
    }
}
