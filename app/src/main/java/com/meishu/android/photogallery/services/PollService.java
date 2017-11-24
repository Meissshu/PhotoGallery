package com.meishu.android.photogallery.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.meishu.android.photogallery.dataModel.GalleryItem;
import com.meishu.android.photogallery.dataUtils.FlickrFetchr;
import com.meishu.android.photogallery.dataUtils.QueryPreferencesUtils;

import java.util.List;

/**
 * Created by Meishu on 23.11.2017.
 */

public class PollService extends IntentService {

    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000 * 60; // 60sec

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }
        Log.i(TAG, "Received an intent " + intent);
        String lastResultId = QueryPreferencesUtils.getLastResultId(this);
        String query = QueryPreferencesUtils.getStoredQuery(this);
        List<GalleryItem> items;

        if (query == null) {
            items = new FlickrFetchr().fetchRecentPhotos(this);
        } else {
            items = new FlickrFetchr().searchPhotos(query, this);
        }

        if (items.size() == 0) {
            return;
        }

        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);
        }

        QueryPreferencesUtils.setLastResultId(this, resultId);

    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return  cm != null &&
                cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnected();
    }
}
