package org.noear.solon.docs.models;

/**
 * 接口扩展文档
 *
 * @author noear
 * @since 2.2
 */
public class ApiExternalDocs {
    private String description;
    private String url;

    public String description(){
        return description;
    }

    public String url(){
        return url;
    }

    public ApiExternalDocs() {

    }

    public ApiExternalDocs(String description, String url) {
        this.description = description;
        this.url = url;
    }
}
