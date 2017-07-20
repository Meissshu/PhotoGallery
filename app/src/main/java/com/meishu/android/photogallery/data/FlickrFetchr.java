package com.meishu.android.photogallery.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Meishu on 20.07.2017.
 */

public class FlickrFetchr {

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
}
