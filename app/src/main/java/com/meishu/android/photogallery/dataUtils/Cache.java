package com.meishu.android.photogallery.dataUtils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import static android.R.attr.key;
import static android.R.attr.value;

/**
 * Created by Meishu on 26.07.2017.
 */

public class Cache {

    private static final LruCache<String, Bitmap> memoryCache = new LruCache<>(40);

    private Cache() {}

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemoryCache(String key) {
        return memoryCache.get(key);
    }

    public static void clearCache() {
        memoryCache.evictAll();
    }
}
