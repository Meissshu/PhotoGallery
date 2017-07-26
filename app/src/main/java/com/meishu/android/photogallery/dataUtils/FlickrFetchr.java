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

/**
 * Created by Meishu on 20.07.2017.
 */

public class FlickrFetchr {

    public static final String TAG = "FlickrFetchr";

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

    public List<GalleryItem> fetchItems(Context context) {
        List<GalleryItem> list = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", context.getString(R.string.api_key))
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build()
                    .toString();
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
