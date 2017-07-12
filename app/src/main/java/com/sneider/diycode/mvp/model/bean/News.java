package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class News implements Serializable {

    /**
     * id : 2128
     * title : 震惊!这年Android面试的那些套路
     * created_at : 2017-03-15T21:17:48.387+08:00
     * updated_at : 2017-03-15T21:18:20.019+08:00
     * user : {"id":524,"login":"dannie","name":"Dannie","avatar_url":"https://diycode.b0.upaiyun.com/user/large_avatar/524.jpg"}
     * node_name : Android
     * node_id : 1
     * last_reply_user_id : 524
     * last_reply_user_login : dannie
     * replied_at : 2017-03-15T21:18:20.001+08:00
     * address : http://www.jianshu.com/p/c3965e82b164
     * replies_count : 1
     */

    private int id;
    private String title;
    private String created_at;
    private String updated_at;
    private UserBean user;
    private String node_name;
    private int node_id;
    private int last_reply_user_id;
    private String last_reply_user_login;
    private String replied_at;
    private String address;
    private int replies_count;

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

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
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

    public String getReplied_at() {
        return replied_at;
    }

    public void setReplied_at(String replied_at) {
        this.replied_at = replied_at;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getReplies_count() {
        return replies_count;
    }

    public void setReplies_count(int replies_count) {
        this.replies_count = replies_count;
    }
}
