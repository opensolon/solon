package org.noear.solon.admin.client.utils;

import org.noear.snack.ONode;

/**
 * @author noear
 * @since 2.3
 */
public class JsonUtils {
    public static String toJson(Object obj) {
        return ONode.stringify(obj);
    }
}
