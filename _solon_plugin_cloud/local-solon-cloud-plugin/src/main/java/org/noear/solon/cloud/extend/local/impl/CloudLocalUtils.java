package org.noear.solon.cloud.extend.local.impl;

import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.local.LocalProps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author noear
 * @since 1.11
 */
public class CloudLocalUtils {
    private static String location;

    public static String getLocation() {
        if (location == null) {
            location = LocalProps.instance.getValue("location", "");
        }

        return location;
    }

    public static String getValue(String key) throws IOException {
        if (Utils.isEmpty(getLocation())) {
            String resourceKey = "META-INF/solon-cloud/" + key;
            return Utils.getResourceAsString(resourceKey);
        } else {
            File resourceFile = new File(getLocation(), key);
            return Utils.transferToString(new FileInputStream(resourceFile));
        }
    }
}
