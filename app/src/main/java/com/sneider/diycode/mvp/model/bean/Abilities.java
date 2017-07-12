package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class Abilities implements Serializable {

    /**
     * update : false
     * destroy : false
     */

    private boolean update;
    private boolean destroy;

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }
}
