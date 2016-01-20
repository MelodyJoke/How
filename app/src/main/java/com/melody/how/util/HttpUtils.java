package com.melody.how.util;

import android.app.NotificationManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    private static final String TAG = "HttpUtils";

    public static String getString(String urlString, String[] keys, String[] values) {
        HttpURLConnection connection = null;
        URL url = null;

        try {
            StringBuilder parameters = new StringBuilder(urlString);

            if (keys != null) {
                for (int i = 0; i < values.length; i++) {
                    if (i != 0) parameters.append("&");
                    else parameters.append("?");
                    parameters.append(keys[i]).append("=").append(values[i]);
                }
            }

            Log.i(TAG, "getString: " + parameters.toString());
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);

            return new String(StreamReader.readInputStream(connection.getInputStream()), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "timeout";
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public static String getSong(String path) {
        HttpURLConnection connection = null;
        URL url = null;
        FileOutputStream outputStream = null;

        String savePath = Values.SAVE_SONG_PATH + "/" + path.split("/")[2];
        Log.i(TAG, "savePath: " + savePath);

        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File file = new File(savePath);

                if (file.exists()) {
                    Log.i(TAG, "Already downloaded: " + path.split("/")[2]);
                    return "exists";
                }

                String songUrl = Values.ROOT_URL + Values.APP_NAME + "/" + path;

                if (songUrl.contains(" "))
                    songUrl = songUrl.replaceAll(" ", "%20");

                Log.i(TAG, "getSong: " + songUrl);
                url = new URL(songUrl);
                connection = (HttpURLConnection) url.openConnection();
                outputStream = new FileOutputStream(new File(savePath));

                int length = 1024;
                byte[] buffer = new byte[length];
                InputStream inputStream = connection.getInputStream();
                while ((length = inputStream.read(buffer)) != -1)
                    outputStream.write(buffer, 0, length);

                outputStream.flush();

                return "ok";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (connection != null)
                    connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "failed";
    }

    public static String getPicture(String album, String artist) {
        HttpURLConnection connection = null;
        URL url = null;
        FileOutputStream outputStream = null;

        String saveAlbumPath = Values.SAVE_ALBUM_PATH + "/" + album;
        String saveArtistPath = Values.SAVE_ARTIST_PATH + "/" + artist;
        Log.i(TAG, "saveAlbumPath: " + saveAlbumPath);
        Log.i(TAG, "saveArtistPath: " + saveArtistPath);

        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File albumFile = new File(saveAlbumPath);
                File artistFile = new File(saveArtistPath);

                if (albumFile.exists() && artistFile.exists()) {
                    Log.i(TAG, "Already downloaded: " + album + ", " + artist);
                    return "exists";
                }

                String albumUrl = Values.ALBUM_PIC_URL + "/" + album + ".png";
                String artistUrl = Values.ARTIST_PIC_URL + "/" + artist + ".png";

                if (albumUrl.contains(" "))
                    albumUrl = albumUrl.replaceAll(" ", "%20");

                if (artistUrl.contains(" "))
                    artistUrl = artistUrl.replaceAll(" ", "%20");

                Log.i(TAG, "getPicture: album: " + albumUrl + " artist: " + artistUrl);
                url = new URL(albumUrl);
                connection = (HttpURLConnection) url.openConnection();

                byte[] buffer = StreamReader.readInputStream(connection.getInputStream());

                outputStream = new FileOutputStream(new File(saveAlbumPath));
                outputStream.write(buffer, 0, buffer.length);
                connection.disconnect();
                outputStream.flush();
                outputStream.close();

                url = new URL(artistUrl);
                connection = (HttpURLConnection) url.openConnection();

                buffer = StreamReader.readInputStream(connection.getInputStream());

                outputStream = new FileOutputStream(new File(saveArtistPath));
                outputStream.write(buffer, 0, buffer.length);
                outputStream.flush();

                return "ok";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (connection != null)
                    connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "failed";
    }
}
