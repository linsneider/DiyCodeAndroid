package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class Reply implements Serializable {

    /**
     * id : 3006
     * body_html :
     * created_at : 2017-03-21T11:11:07.940+08:00
     * updated_at : 2017-03-21T14:56:20.471+08:00
     * deleted : false
     * topic_id : 411
     * news_id : 2108
     * project_id: 16943
     * user : {"id":3919,"login":"sneider","name":"sneider","avatar_url":"https://diycode.cc/system/letter_avatars/2/S/162_136_126/240.png"}
     * likes_count : 0
     * abilities : {"update":true,"destroy":true}
     * body :
     * topic_title : Diycode 社区、项目、News、sites 的 API 发布了
     */

    private int id;
    private String body_html;
    private String created_at;
    private String updated_at;
    private boolean deleted;
    private int topic_id;
    private int news_id;
    private int project_id;
    private UserBean user;
    private int likes_count;
    private Abilities abilities;
    private String body;
    private String topic_title;

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

    public int getNews_id() {
        return news_id;
    }

    public void setNews_id(int news_id) {
        this.news_id = news_id;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTopic_title() {
        return topic_title;
    }

    public void setTopic_title(String topic_title) {
        this.topic_title = topic_title;
    }
}
