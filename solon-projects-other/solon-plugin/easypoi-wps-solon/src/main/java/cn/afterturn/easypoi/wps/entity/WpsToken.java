package cn.afterturn.easypoi.wps.entity;

import cn.afterturn.easypoi.wps.entity.resreq.WpsResponse;

import java.io.Serializable;

/**
 * @author JueYue
 * 界面的Token数据,默认不使用
 */
public class WpsToken extends WpsResponse implements Serializable {

    private int    expires_in;
    private String token;
    private String wpsUrl;

    public int getExpires_in(){
        return expires_in;
    }
    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWpsUrl(){
        return wpsUrl;
    }

    public void setWpsUrl(String wpsUrl) {
        this.wpsUrl = wpsUrl;
    }
}
