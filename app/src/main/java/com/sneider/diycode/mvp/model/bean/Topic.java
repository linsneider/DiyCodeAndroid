package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class Topic implements Serializable {

    /**
     * id : 648
     * title : 招募 5-20 人左右的技术外包团队，长期有活儿
     * created_at : 2017-03-09T14:15:55.672+08:00
     * updated_at : 2017-03-20T23:17:14.211+08:00
     * replied_at : 2017-03-16T22:52:16.587+08:00
     * replies_count : 5
     * node_name : 程序人生
     * node_id : 35
     * last_reply_user_id : 4248
     * last_reply_user_login : jackie_chen
     * user :
     * deleted : false
     * excellent : false
     * abilities : {"update":false,"destroy":false}
     * body :
     * hits : 598
     * likes_count : 1
     * suggested_at : 2016-11-09T22:49:08.087+08:00
     * followed : false
     * liked : false
     * favorited : false
     */

    private int id;
    private String title;
    private String created_at;
    private String updated_at;
    private String replied_at;
    private int replies_count;
    private String node_name;
    private int node_id;
    private int last_reply_user_id;
    private String last_reply_user_login;
    private UserBean user;
    private boolean deleted;
    private boolean excellent;
    private Abilities abilities;
    private String body;
    private String body_html;
    private int hits;
    private int likes_count;
    private String suggested_at;
    private boolean followed;
    private boolean liked;
    private boolean favorited;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getReplied_at() {
        return replied_at;
    }

    public void setReplied_at(String replied_at) {
        this.replied_at = replied_at;
    }

    public int getReplies_count() {
        return replies_count;
    }

    public void setReplies_count(int replies_count) {
        this.replies_count = replies_count;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public int getNode_id() {
        return node_id;
    }

    public void setNode_id(int node_id) {
        this.node_id = node_id;
    }

    public int getLast_reply_user_id() {
        return last_reply_user_id;
    }

    public void setLast_reply_user_id(int last_reply_user_id) {
        this.last_reply_user_id = last_reply_user_id;
    }

    public String getLast_reply_user_login() {
        return last_reply_user_login;
    }

    public void setLast_reply_user_login(String last_reply_user_login) {
        this.last_reply_user_login = last_reply_user_login;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isExcellent() {
        return excellent;
    }

    public void setExcellent(boolean excellent) {
        this.excellent = excellent;
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

    public String getBody_html() {
        return body_html;
    }

    public void setBody_html(String body_html) {
        this.body_html = body_html;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public String getSuggested_at() {
        return suggested_at;
    }

    public void setSuggested_at(String suggested_at) {
        this.suggested_at = suggested_at;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }
}
