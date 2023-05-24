package org.noear.solon.docs.openapi2;

import org.noear.solon.Utils;

import java.io.Serializable;

/**
 * @author noear
 * @since 2.3
 */
public class Swagger2Resource implements Serializable {
    private String name;
    private String url;
    private String location;
    private String swaggerVersion;

    public Swagger2Resource(String group, String groupName) {
        name = groupName;
        if (Utils.isNotEmpty(group)) {
            url = ("/swagger2/api?group=" + group);
            location = ("/swagger2/api?group=" + group);
            swaggerVersion = ("2.0");
        }
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
