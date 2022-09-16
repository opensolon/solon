package org.noear.solon.extend.sessionstate.redis;

import java.util.UUID;

@Deprecated
class IDUtil {
    public static String guid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
