package org.noear.solon.docs.models;

import java.io.Serializable;

/**
 * @author noear
 * @since 2.3
 */
public class ApiGroupResource implements Serializable {
    private String name;
    private String url;
    private String location;
    private String swaggerVersion;

    public ApiGroupResource(String name, String version, String url) {
        this.name = name;
        this.url = url;
        this.location = url;
        this.swaggerVersion = version;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getLocation() {
        return location;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }
}
