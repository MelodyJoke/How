package com.melody.how.bean;

import java.io.Serializable;

public class Artist implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;

    public Artist() {

    }

    public Artist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
