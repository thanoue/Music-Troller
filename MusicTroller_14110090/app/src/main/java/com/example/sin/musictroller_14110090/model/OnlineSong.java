package com.example.sin.musictroller_14110090.model;

/**
 * Created by Sin on 05/19/17.
 */

public class OnlineSong {
    public int getSong_id() {
        return Song_id;
    }

    public void setSong_id(int song_id) {
        Song_id = song_id;
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

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getSource_128() {
        return Source_128;
    }

    public void setSource_128(String source_128) {
        Source_128 = source_128;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    private int Song_id;
    private String Title;
    private String Artist;
    private String Thumbnail;
    private String Source_128;
    private String Link;

    public OnlineSong(int song_id, String title, String artist, String thumbnail, String source_128, String link, String link_download_128) {
        Song_id = song_id;
        Title = title;
        Artist = artist;
        Thumbnail = thumbnail;
        Source_128 = source_128;
        Link = link;
        Link_download_128 = link_download_128;
    }

    public String getLink_download_128() {
        return Link_download_128;

    }

    public void setLink_download_128(String link_download_128) {
        Link_download_128 = link_download_128;
    }

    private String Link_download_128;
    public OnlineSong(){}
}
