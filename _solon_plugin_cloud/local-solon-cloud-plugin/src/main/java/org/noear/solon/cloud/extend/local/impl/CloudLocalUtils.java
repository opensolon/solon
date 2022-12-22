package org.noear.solon.cloud.extend.local.impl;

import org.noear.solon.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author noear
 * @since 1.11
 */
public class CloudLocalUtils {

    public static String getValue(String server, String key) throws IOException {
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
