package com.meishu.android.photogallery.dataModel;

/**
 * Created by Meishu on 23.07.2017.
 */

public class GalleryItem {

    private String caption;
    private String id;
    private String url;

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
