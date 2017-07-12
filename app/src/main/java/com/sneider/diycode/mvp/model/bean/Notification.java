package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class Notification implements Serializable {

    /**
     * id : 55657
     * type : TopicReply
     * read : true
     * actor :
     * mention_type : Reply
     * mention :
     * topic :
     * reply :
     * node :
     * created_at : 2017-03-31T21:15:45.311+08:00
     * updated_at : 2017-03-31T21:15:45.311+08:00
     */

    private int id;
    private String type;
    private boolean read;
    private UserBean actor;
    private String mention_type;
    private Mention mention;
    private Topic topic;
    private Reply reply;
    private Node node;
    private String created_at;
    private String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public UserBean getActor() {
        return actor;
    }

    public void setActor(UserBean actor) {
        this.actor = actor;
    }

    public String getMention_type() {
        return mention_type;
    }

    public void setMention_type(String mention_type) {
        this.mention_type = mention_type;
    }

    public Mention getMention() {
        return mention;
    }

    public void setMention(Mention mention) {
        this.mention = mention;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
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
}
