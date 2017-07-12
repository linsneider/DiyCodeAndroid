package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class NewsNode implements Serializable {

    /**
     * id : 1
     * name : Android
     */

    private int id;
    private String name;

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
