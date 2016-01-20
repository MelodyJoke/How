package com.melody.how.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.melody.how.model.implementation.AlbumDao;
import com.melody.how.model.implementation.ArtistDao;
import com.melody.how.model.implementation.QueueDao;
import com.melody.how.model.implementation.SongDao;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";
    private static final String DB_NAME = "how.db";
    private static final int VERSION = 4;

    public DbHelper(Context context) {
        this(context, DB_NAME, null, VERSION);
        Log.i(TAG, "new instance name " + DB_NAME + " version " + VERSION);
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + SongDao.TABLENAME + "(" +
                SongDao.COLUMNNAMES[0] + " integer not null, " +
                SongDao.COLUMNNAMES[1] + " integer not null, " +
                SongDao.COLUMNNAMES[2] + " text null, " +
                SongDao.COLUMNNAMES[3] + " text not null, " +
                SongDao.COLUMNNAMES[4] + " text not null, " +
                SongDao.COLUMNNAMES[5] + " text not null, " +
                SongDao.COLUMNNAMES[6] + " text not null, " +
                SongDao.COLUMNNAMES[7] + " text not null)");
        Log.i(TAG, "onCreate create table song");

        db.execSQL("create table if not exists " + AlbumDao.TABLENAME + "(" +
                AlbumDao.COLUMNNAMES[0] + " integer not null, " +
                AlbumDao.COLUMNNAMES[1] + " text not null)");
        Log.i(TAG, "onCreate create table album");

        db.execSQL("create table if not exists " + ArtistDao.TABLENAME + "(" +
                ArtistDao.COLUMNNAMES[0] + " integer not null, " +
                ArtistDao.COLUMNNAMES[1] + " text not null)");
        Log.i(TAG, "onCreate create table artist");

        db.execSQL("create table if not exists " + QueueDao.TABLENAME + "(" +
                QueueDao.COLUMNNAMES[0] + " integer not null, " +
                QueueDao.COLUMNNAMES[1] + " text not null, " +
                QueueDao.COLUMNNAMES[2] + " text not null)");
        Log.i(TAG, "onCreate create table queue");

        db.execSQL("create table if not exists favourites(" +
                SongDao.COLUMNNAMES[0] + " integer not null, " +
                SongDao.COLUMNNAMES[1] + " integer not null, " +
                SongDao.COLUMNNAMES[2] + " text null, " +
                SongDao.COLUMNNAMES[3] + " text not null, " +
                SongDao.COLUMNNAMES[4] + " text not null, " +
                SongDao.COLUMNNAMES[5] + " text not null, " +
                SongDao.COLUMNNAMES[6] + " text not null, " +
                SongDao.COLUMNNAMES[7] + " text not null)");

        db.execSQL("insert into " + QueueDao.TABLENAME + " values(1, 'Favourites', 'favourites')");
        Log.i(TAG, "onCreate create table favourites");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
