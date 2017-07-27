package com.meishu.android.photogallery.dataUtils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meishu.android.photogallery.R;
import com.meishu.android.photogallery.dataModel.GalleryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by Meishu on 20.07.2017.
 */

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";

    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/").buildUpon()
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = urlConnection.getInputStream();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(urlConnection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        finally {
            urlConnection.disconnect();
        }
    }

    public String getURLString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchRecentPhotos(Context context) {
        String url = buildUrl(FETCH_RECENTS_METHOD, null, context);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query, Context context) {
        String url = buildUrl(SEARCH_METHOD, query, context);
        return downloadGalleryItems(url);
    }

    private String buildUrl(String method, String query, Context context) {
        Uri.Builder builder = ENDPOINT.buildUpon()
                .appendQueryParameter("api_key", context.getString(R.string.api_key))
                .appendQueryParameter("method", method);

        if (method.equals(SEARCH_METHOD))
            builder.appendQueryParameter("text", query);

        return builder.build().toString();
    }

    private List<GalleryItem> downloadGalleryItems(String url) {
        List<GalleryItem> list = new ArrayList<>();

        try {
            String jsonString = getURLString(url);
            Log.i(TAG, "Got json string: " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            parseItemsFromJSON(list, jsonObject);
        }
        catch (IOException e) {
            Log.e(TAG, "Fetch items exception: ", e);
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error", e);
        }

        return list;
    }

    private void parseItemsFromJSON(List<GalleryItem> list, JSONObject jsonBody) throws IOException, JSONException{
        JSONObject photosJSONObject = jsonBody.getJSONObject("photos");
        JSONArray photosJSONArray = photosJSONObject.getJSONArray("photo");

        for (int i = 0; i < photosJSONArray.length(); ++i) {

            JSONObject singleJSONPhotoObject = photosJSONArray.getJSONObject(i);
            if (!singleJSONPhotoObject.has("url_s")) {
                continue;
            }

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            GalleryItem galleryItem = gson.fromJson(singleJSONPhotoObject.toString(), GalleryItem.class);


            list.add(galleryItem);
        }
    }
}
