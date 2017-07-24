package com.meishu.android.photogallery.dataUtils;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Meishu on 24.07.2017.
 */

public class ThumbnailDownloader<T> extends HandlerThread {

    public static final String TAG = "ThumbnailDownloader";
    public static final int MESSAGE_DOWNLOAD = 0;

    private Handler requestHandler;
    private ConcurrentMap<T, String> requestMap = new ConcurrentHashMap<>();

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
    }

    public ThumbnailDownloader() {
        super(TAG);
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got url: " + url);

        if (url == null) {
            requestMap.remove(target);
        } else {
            requestMap.put(target, url);
            requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    @Override
    protected void onLooperPrepared() {
        requestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a REQUEST for url: " + requestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    private void handleRequest(final T target) {
        try {
            final String url = requestMap.get(target);
            if (url == null)
                return;

            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");
        }
         catch (IOException e) {
           Log.e(TAG, "Error downloading image", e);
        }
    }
}
