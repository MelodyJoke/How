package com.melody.how.model.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.melody.how.bean.Queue;
import com.melody.how.bean.Song;
import com.melody.how.model.BeanService;

public class QueueDao extends BeanService<Queue> {

    public static final String TAG = "QueueService";
    public static final String TABLENAME = "queue";
    public static final String[] COLUMNNAMES = new String[]{
            "id",
            "name",
            "tableName"};
    private static int id;

    public QueueDao(Context context) {
        super(context);
        super.TAG = TAG;
        super.TABLENAME = TABLENAME;
    }

    public SparseArray<Song> query(String queueName) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(queueName, null, null, null, null, null, "name");
        SparseArray<Song> songs = new SparseArray<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(SongDao.COLUMNNAMES[0]));
            songs.put(id, new Song(id,
                    cursor.getInt(cursor.getColumnIndex(SongDao.COLUMNNAMES[1])),
                    cursor.getString(cursor.getColumnIndex(SongDao.COLUMNNAMES[2])),
                    cursor.getString(cursor.getColumnIndex(SongDao.COLUMNNAMES[3])),
                    cursor.getString(cursor.getColumnIndex(SongDao.COLUMNNAMES[4])),
                    cursor.getString(cursor.getColumnIndex(SongDao.COLUMNNAMES[5])),
                    cursor.getString(cursor.getColumnIndex(SongDao.COLUMNNAMES[6])),
                    cursor.getString(cursor.getColumnIndex(SongDao.COLUMNNAMES[7]))
            ));

            Log.i(TAG, "query get " + id);
        }
        cursor.close();
        Log.i(TAG, "query " + songs.size() + " songs");

        return songs;
    }

    @Override
    public SparseArray<Queue> query(String... queryStrings) {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = null;
        if (queryStrings.length == 0 || queryStrings[0].equals(""))
            cursor = database.query(TABLENAME, null, null, null, null, null, "name");
        else
            cursor = database.query(TABLENAME, null, "name like '%" + queryStrings[0] + "%'",
                    null, null, null, "id");

        SparseArray<Queue> queues = new SparseArray<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMNNAMES[0]));
            queues.put(id, new Queue(id, cursor.getString(cursor.getColumnIndex(COLUMNNAMES[1])),
                    cursor.getString(cursor.getColumnIndex(COLUMNNAMES[2]))));
            Log.i(TAG, "query get " + id);
        }
        cursor.close();
        Log.i(TAG, "query " + queues.size() + " queues");

        return queues;
    }

    @Override
    protected ContentValues transToValues(Queue queue) {
        ContentValues values = new ContentValues();

        values.put(COLUMNNAMES[0], queue.getId());
        values.put(COLUMNNAMES[1], queue.getName());
        values.put(COLUMNNAMES[2], queue.getTableName());

        return values;
    }
}
