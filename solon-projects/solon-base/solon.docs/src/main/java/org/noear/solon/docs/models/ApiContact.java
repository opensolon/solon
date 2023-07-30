package org.noear.solon.docs.models;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口联系信息
 *
 * @author noear
 * @since 2.4
 */
public class ApiContact {
    private String name;
    private String url;
    private String email;
    private Map<String, Object> vendorExtensions = new LinkedHashMap();

    public ApiContact name(String name) {
        this.name = name;
        return this;
    }

    public ApiContact url(String url) {
        this.url = url;
        return this;
    }

    public ApiContact email(String email) {
       this.email = email;
        return this;
    }

    public String name(){
        return this.name;
    }

    public String url(){
        return this.url;
    }

    public String email(){
        return this.email;
    }

    public Map<String, Object> vendorExtensions(){
        return vendorExtensions;
    }
}
