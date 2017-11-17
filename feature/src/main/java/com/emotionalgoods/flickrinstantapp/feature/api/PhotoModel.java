package com.emotionalgoods.flickrinstantapp.feature.api;

/**
 * Created by jacob on 11/16/17.
 */

public class PhotoModel {

    private final String url;
    private final String title;

    public PhotoModel(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
