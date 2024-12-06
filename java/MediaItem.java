package com.esther.perfect;

public class MediaItem {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    private String url;
    private int type;

    public MediaItem(String url, int type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public int getType() {
        return type;
    }

    public boolean isVideo() {
        return type == TYPE_VIDEO;
    }
}
