package org.noear.solon.docs.models;

/**
 * @author noear 2023/5/25 created
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
