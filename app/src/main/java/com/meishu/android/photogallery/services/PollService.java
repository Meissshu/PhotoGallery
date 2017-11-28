package com.meishu.android.photogallery.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.meishu.android.photogallery.R;
import com.meishu.android.photogallery.activities.PhotoGalleryActivity;
import com.meishu.android.photogallery.dataModel.GalleryItem;
import com.meishu.android.photogallery.dataUtils.FlickrFetchr;
import com.meishu.android.photogallery.dataUtils.QueryPreferencesUtils;

import java.util.List;

/**
 * Created by Meishu on 23.11.2017.
 */

public class PollService extends IntentService {

    private static final String TAG = "PollService";
    private static final long POLL_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES; // 60sec

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

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
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

        Resources resourses = getResources();
        Intent i = PhotoGalleryActivity.newIntent(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resourses.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resourses.getString(R.string.new_pictures_title))
                .setContentText(resourses.getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(0, notification);

        QueryPreferencesUtils.setLastResultId(this, resultId);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return  cm != null &&
                cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnected();
    }
}
