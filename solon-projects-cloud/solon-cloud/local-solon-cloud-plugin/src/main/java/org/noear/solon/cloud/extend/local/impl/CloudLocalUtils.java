package org.noear.solon.cloud.extend.local.impl;

import org.noear.solon.Utils;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.ResourceUtil;

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
            //默认目录
            String resourceKey = "META-INF/solon-cloud/" + key;
            return ResourceUtil.getResourceAsString(resourceKey);
        } else if (ResourceUtil.hasClasspath(server)) {
            //资源目录
            String resourceKey = ResourceUtil.remClasspath(server) + key;
            return ResourceUtil.getResourceAsString(resourceKey);
        } else {
            //本地目录
            File resourceFile = new File(server, key);
            if (resourceFile.exists()) {
                return IoUtil.transferToString(new FileInputStream(resourceFile));
            } else {
                return null;
            }
        }
    }
}
