package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class Like implements Serializable {

    /**
     * obj_type : topic
     * obj_id : 648
     * count : 2
     */

    private String obj_type;
    private int obj_id;
    private int count;

    public String getObj_type() {
        return obj_type;
    }

    public void setObj_type(String obj_type) {
        this.obj_type = obj_type;
    }

    public int getObj_id() {
        return obj_id;
    }

    public void setObj_id(int obj_id) {
        this.obj_id = obj_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
