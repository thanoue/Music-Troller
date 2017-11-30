package com.example.sin.musictroller_14110090.model;

import android.graphics.Bitmap;

/**
 * Created by Sin on 05/20/17.
 */

public class DownloadSong {
    String Title;
    String Artist;
    String Url;

    public DownloadSong() {
    }

    public Bitmap getThumbnail() {
        return Thumbnail;

    }

    public void setThumbnail(Bitmap thumbnail) {
        Thumbnail = thumbnail;
    }

    Bitmap Thumbnail;

    public DownloadSong(String title, String artist, String url,Bitmap thumbnail) {
        Title = title;
        Artist = artist;
        Url = url;
        Thumbnail=thumbnail;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
