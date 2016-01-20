package com.melody.how.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.melody.how.database.DbHelper;

public abstract class BeanService<T> {

    protected String TAG;
    protected String TABLENAME;

    protected DbHelper helper;

    protected BeanService(Context context) {
        this.helper = getDbHelper(context);
    }

    protected DbHelper getDbHelper(Context context) {
        return this.helper == null ? new DbHelper(context) : this.helper;
    }

    public boolean insert(T type) {
        SQLiteDatabase database = helper.getWritableDatabase();
        long id = database.insert(TABLENAME, null, transToValues(type));
        database.close();
        Log.i(TAG, "insert no." + id);

        return id != -1;
    }

    public boolean update(int id, T type) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsAffected = database.update(TABLENAME, transToValues(type),
                "id=?", new String[]{String.valueOf(id)});
        database.close();
        Log.i(TAG, "update " + rowsAffected + " rows");

        return rowsAffected > 0;
    }

    public boolean delete(int id) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsAffected = database.delete(TABLENAME, "id=?", new String[]{String.valueOf(id)});
        database.close();
        Log.i(TAG, "delete " + rowsAffected + " rows, no." + id);

        return rowsAffected > 0;
    }

    public boolean delete() {
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsAffected = database.delete(TABLENAME, null, null);
        database.close();
        Log.i(TAG, "delete " + rowsAffected + " rows");

        return rowsAffected > 0;
    }

    public abstract SparseArray<T> query(String... queryStrings);

    protected abstract ContentValues transToValues(T type);
}
