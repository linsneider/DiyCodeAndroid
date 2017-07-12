package com.sneider.diycode.mvp.model.bean;

import com.google.gson.Gson;

import java.io.Serializable;

public class User implements Serializable {

    /**
     * id : 1
     * login : jixiaohua
     * name : 寂小桦
     * avatar_url : https://diycode.b0.upaiyun.com/user/large_avatar/2.jpg
     * location : 北京
     * company :
     * twitter : apkbus
     * website :
     * bio : diycode发起人，致力于打造一个开发者工程师高质量的分享交流社区
     * tagline :
     * github :
     * created_at : 2016-03-08T16:45:05.726+08:00
     * email : robot@diycode.cc
     * topics_count : 77
     * replies_count : 394
     * following_count : 228
     * followers_count : 125
     * favorites_count : 6
     * level : admin
     * level_name : 管理员
     */

    private int id;
    private String login;
    private String name;
    private String avatar_url;
    private String location;
    private String company;
    private String twitter;
    private String website;
    private String bio;
    private String tagline;
    private String github;
    private String created_at;
    private String email;
    private int topics_count;
    private int replies_count;
    private int following_count;
    private int followers_count;
    private int favorites_count;
    private String level;
    private String level_name;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTopics_count() {
        return topics_count;
    }

    public void setTopics_count(int topics_count) {
        this.topics_count = topics_count;
    }

    public int getReplies_count() {
        return replies_count;
    }

    public void setReplies_count(int replies_count) {
        this.replies_count = replies_count;
    }

    public int getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(int following_count) {
        this.following_count = following_count;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFavorites_count() {
        return favorites_count;
    }

    public void setFavorites_count(int favorites_count) {
        this.favorites_count = favorites_count;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel_name() {
        return level_name;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
