package org.noear.solon.docs.models;

import org.noear.solon.Utils;

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

    public ApiGroupResource(String group, String groupName) {
        name = groupName;
        if (Utils.isNotEmpty(group)) {
            url = ("/swagger/v2?group=" + group);
            location = ("/swagger/v2?group=" + group);
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
