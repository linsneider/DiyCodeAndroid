package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;
import java.util.List;

public class Sites implements Serializable {

    /**
     * sites : []
     * name : FUN & COOL
     * id : 18
     */

    private String name;
    private int id;
    private List<SitesBean> sites;

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

    public List<SitesBean> getSites() {
        return sites;
    }

    public void setSites(List<SitesBean> sites) {
        this.sites = sites;
    }

    public static class SitesBean implements Serializable {

        /**
         * name : botlist
         * url : http://botlist.co
         * avatar_url : https://favicon.b0.upaiyun.com/ip2/botlist.co.ico
         */

        private String name;
        private String url;
        private String avatar_url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }
    }
}
