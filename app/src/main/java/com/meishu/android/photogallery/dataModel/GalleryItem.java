package com.meishu.android.photogallery.dataModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Meishu on 23.07.2017.
 */

public class GalleryItem {

    @SerializedName("title")
    private String caption;
    private String id;
    @SerializedName("url_s")
    private String url;

    public GalleryItem() {}

    @Override
    public String toString() {
        return caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
