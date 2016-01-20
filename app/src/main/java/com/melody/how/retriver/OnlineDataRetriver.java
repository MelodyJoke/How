package com.melody.how.retriver;

import android.os.AsyncTask;
import android.util.SparseArray;

import com.melody.how.bean.Song;
import com.melody.how.util.HttpUtils;
import com.melody.how.util.JsonPaser;
import com.melody.how.util.Values;

public abstract class OnlineDataRetriver extends AsyncTask<String, Void, SparseArray<Song>> {

    private static final String TAG = "OnlineDataRetriver";

    @Override
    protected SparseArray<Song> doInBackground(String... params) {
        return JsonPaser.getSongs(HttpUtils.getString(Values.LIST_SONG_URL, Values.LIST_SONG_PARAMS, params));
    }

    @Override
    protected abstract void onPostExecute(SparseArray<Song> songSparseArray);
}
