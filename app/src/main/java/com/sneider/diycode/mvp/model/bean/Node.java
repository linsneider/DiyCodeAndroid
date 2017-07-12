package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class Node implements Serializable {

    /**
     * id : 1
     * name : Android
     * topics_count : 299
     * summary : Android是一个以Linux为基础的半开源操作系统，主要用于移动设备，由Google和开放手持设备联盟开发与领导。 Android 系统最初由安迪·鲁宾（Andy Rubin）制作，最初主要支持手机。2005年8月17日被Google收购。2007年11月5日，Google与84家硬件制造商、软件开发商及电信营运商组成开放手持设备联盟（Open Handset Alliance）来共同研发改良Android系统并生产搭载Android的智慧型手机，并逐渐拓展到平板电脑及其他领域上。随后，Google以Apache免费开源许可证的授权方式，发布了Android的源代码。
     * section_id : 1
     * sort : 0
     * section_name : Mobile Dev
     * updated_at : 2016-03-29T12:42:55.971+08:00
     */

    private int id;
    private String name;
    private int topics_count;
    private String summary;
    private int section_id;
    private int sort;
    private String section_name;
    private String updated_at;

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

    public int getTopics_count() {
        return topics_count;
    }

    public void setTopics_count(int topics_count) {
        this.topics_count = topics_count;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getSection_id() {
        return section_id;
    }

    public void setSection_id(int section_id) {
        this.section_id = section_id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
