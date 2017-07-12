package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;

public class Project implements Serializable {

    /**
     * id : 19412
     * name : FlowLayout
     * description : A flow layout for Android with auto-spacing.
     * readme :
     * github : https://github.com/nex3z/FlowLayout
     * website :
     * download : https://api.github.com/repos/nex3z/FlowLayout/zipball
     * star : 614
     * fork : 56
     * watch : 9
     * project_cover_url : https://diycode.b0.upaiyun.com/photo/2016/9ab579e9a72d23ede4424af3661405f4.png
     * label_str : auto-spacing,flow,layout
     * category : {"name":"Android","id":1}
     * sub_category : {"name":"其他(other)","id":23}
     * last_updated_at : 2017-03-16T09:27:32.000+08:00
     */

    private int id;
    private String name;
    private String description;
    private String readme;
    private String github;
    private String website;
    private String download;
    private int star;
    private int fork;
    private int watch;
    private String project_cover_url;
    private String label_str;
    private CategoryBean category;
    private SubCategoryBean sub_category;
    private String last_updated_at;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReadme() {
        return readme;
    }

    public void setReadme(String readme) {
        this.readme = readme;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getFork() {
        return fork;
    }

    public void setFork(int fork) {
        this.fork = fork;
    }

    public int getWatch() {
        return watch;
    }

    public void setWatch(int watch) {
        this.watch = watch;
    }

    public String getProject_cover_url() {
        return project_cover_url;
    }

    public void setProject_cover_url(String project_cover_url) {
        this.project_cover_url = project_cover_url;
    }

    public String getLabel_str() {
        return label_str;
    }

    public void setLabel_str(String label_str) {
        this.label_str = label_str;
    }

    public CategoryBean getCategory() {
        return category;
    }

    public void setCategory(CategoryBean category) {
        this.category = category;
    }

    public SubCategoryBean getSub_category() {
        return sub_category;
    }

    public void setSub_category(SubCategoryBean sub_category) {
        this.sub_category = sub_category;
    }

    public String getLast_updated_at() {
        return last_updated_at;
    }

    public void setLast_updated_at(String last_updated_at) {
        this.last_updated_at = last_updated_at;
    }

    public static class CategoryBean implements Serializable {

        /**
         * name : Android
         * id : 1
         */

        private String name;
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class SubCategoryBean implements Serializable {

        /**
         * name : 其他(other)
         * id : 23
         */

        private String name;
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
