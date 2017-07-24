package com.meishu.android.photogallery.dataUtils;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by Meishu on 24.07.2017.
 */

public class ThumbnailDownloader<T> extends HandlerThread {

    public static final String TAG = "ThumbnailDownloader";

    public ThumbnailDownloader() {
        super(TAG);
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got url: " + url);
    }


}
