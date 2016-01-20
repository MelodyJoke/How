package com.melody.how.util;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    private String path;
    private Resources resources;
    private int width, height;

    private static ConcurrentHashMap<String, SoftReference<Bitmap>> cache = new ConcurrentHashMap<>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10, Executors.defaultThreadFactory());

    public ImageLoader(Resources resources, String path, int width, int height) {
        this.resources = resources;
        this.path = path;
        this.width = width;
        this.height = height;
    }

    @SuppressLint("HandlerLeak")
    public void loadImage(final ImageCallBack callBack) {
        SoftReference<Bitmap> softReference = cache.get(path + width + "x" + height);
        if (softReference != null) {
            Log.i("load from cache", path + width + "x" + height);
            Bitmap bitmap = softReference.get();
            if (bitmap != null) {
                callBack.getDrawable(new BitmapDrawable(resources, bitmap));
                return;
            }
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                callBack.getDrawable((Drawable) msg.obj);
            }
        };

        threadPool.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = compressBitmap(Environment.getExternalStorageDirectory().getAbsolutePath() + "/How/" + path,
                        width, height);
                cache.put(path + width + "x" + height, new SoftReference<>(bitmap));
                Log.i("insert into cache", path + width + "x" + height);
                Message.obtain(handler, 1,
                        new BitmapDrawable(resources, bitmap))
                        .sendToTarget();
            }
        }));
    }

    public static Drawable fixPictureScale(Resources resources, Drawable drawable) {
        if (drawable == null) return drawable;

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        return new BitmapDrawable(resources,
                Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 4, bitmap.getWidth(), bitmap.getHeight() / 2));
    }

    public static Bitmap compressBitmap(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int width, int height) {
        final int outWidth = options.outWidth;
        final int outHeight = options.outHeight;

        if (outHeight > height || outWidth > width) {
            final int widthRatio = (int) Math.round(outWidth / (double) width);
            final int heightRatio = (int) Math.round(outHeight / (double) height);

            return widthRatio > heightRatio ? widthRatio : heightRatio;
        }

        return 1;
    }
}
