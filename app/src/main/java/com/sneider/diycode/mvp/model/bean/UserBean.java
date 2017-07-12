package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class UserBean implements Serializable {

    /**
     * id : 2735
     * login : sword
     * name : ice_Anson
     * avatar_url : https://diycode.cc/system/letter_avatars/2/S/162_136_126/240.png
     */

    private int id;
    private String login;
    private String name;
    private String avatar_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
