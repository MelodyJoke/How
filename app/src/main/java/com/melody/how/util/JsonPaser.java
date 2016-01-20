package com.melody.how.util;

import android.util.Log;
import android.util.SparseArray;

import com.melody.how.bean.Song;
import com.melody.how.model.implementation.SongDao;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonPaser {

    private static final String TAG = "JsonPaser";

    public static SparseArray<Song> getSongs(String jsonString) {
        Log.i(TAG, "getSongs: " + jsonString);
        SparseArray<Song> songs = new SparseArray<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray array = jsonObject.getJSONArray("songs");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                int id = object.getInt(SongDao.COLUMNNAMES[0]);
                String name = object.getString(SongDao.COLUMNNAMES[2]);

                songs.put(id, new Song(id,
                        object.getInt(SongDao.COLUMNNAMES[1]),
                        name,
                        object.getString(SongDao.COLUMNNAMES[3]),
                        object.getString(SongDao.COLUMNNAMES[4]),
                        object.getString(SongDao.COLUMNNAMES[5]),
                        object.getString(SongDao.COLUMNNAMES[6]),
                        object.getString(SongDao.COLUMNNAMES[7])));

                Log.i(TAG, "getSongs:id: " + id + " name: " + name);
            }

            return songs;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
