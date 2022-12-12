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
    private static final String server = LocalProps.instance.getServer();

    public static String getValue(String key) throws IOException {
        if (Utils.isEmpty(server)) {
            String resourceKey = "META-INF/solon-cloud/" + key;
            return Utils.getResourceAsString(resourceKey);
        } else {
            File resourceFile = new File(server, key);
            if (resourceFile.exists()) {
                return Utils.transferToString(new FileInputStream(resourceFile));
            } else {
                return null;
            }
        }
    }
}
