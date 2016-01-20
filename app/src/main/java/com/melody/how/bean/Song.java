package com.melody.how.bean;

import java.io.Serializable;

public class Song implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int sysid;
    private String displayName;
    private String name;
    private String path;
    private String album;
    private String artist;
    private String type;

    public Song() {

    }

    public Song(int id, int sysid, String displayName, String name, String path, String album, String artist, String type) {

        this.id = id;
        this.sysid = sysid;
        this.displayName = displayName;
        this.name = name;
        this.path = path;
        this.album = album;
        this.artist = artist;
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSysid() {
        return sysid;
    }

    public void setSysid(int sysid) {
        this.sysid = sysid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
