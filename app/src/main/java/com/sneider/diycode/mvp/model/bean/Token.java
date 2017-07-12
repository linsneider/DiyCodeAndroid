package com.sneider.diycode.mvp.model.bean;

import com.google.gson.Gson;

import java.io.Serializable;

public class Token implements Serializable {

    /**
     * access_token : e38f290f8b97d36f44dd0121ee481621775eb4c8b3ca850cefb81063f5d6db27
     * token_type : bearer
     * expires_in : 5184000
     * refresh_token : c66f37132fa127732ec66bd34076290a5d96fa6533f933f6622dc33ea4605764
     * created_at : 1489999125
     */

    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    private int created_at;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
