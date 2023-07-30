package org.noear.solon.docs.models;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口许可证
 *
 * @author noear
 * @since 2.4
 */
public class ApiLicense {
    private String name;
    private String url;
    private Map<String, Object> vendorExtensions = new LinkedHashMap();

    public ApiLicense name(String name) {
        this.name = name;
        return this;
    }

    public ApiLicense url(String url) {
        this.url = url;
        return this;
    }


    public String name(){
        return this.name;
    }

    public String url(){
        return this.url;
    }

    public Map<String, Object> vendorExtensions(){
        return vendorExtensions;
    }
}
