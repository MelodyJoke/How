package com.melody.how.bean;

import java.io.Serializable;

public class Queue implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String tableName;

    public Queue() {

    }

    public Queue(int id, String name, String tableName) {
        this.id = id;
        this.name = name;
        this.tableName = tableName;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
