package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class Mention implements Serializable {

    /**
     * id : 3093
     * body_html :
     * created_at : 2017-03-30T15:08:29.960+08:00
     * updated_at : 2017-03-30T15:08:29.960+08:00
     * deleted : false
     * topic_id : 719
     * user :
     * likes_count : 0
     * abilities : {"update":false,"destroy":false}
     */

    private int id;
    private String body_html;
    private String created_at;
    private String updated_at;
    private boolean deleted;
    private int topic_id;
    private UserBean user;
    private int likes_count;
    private Abilities abilities;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody_html() {
        return body_html;
    }

    public void setBody_html(String body_html) {
        this.body_html = body_html;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(int topic_id) {
        this.topic_id = topic_id;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public Abilities getAbilities() {
        return abilities;
    }

    public void setAbilities(Abilities abilities) {
        this.abilities = abilities;
    }
}
