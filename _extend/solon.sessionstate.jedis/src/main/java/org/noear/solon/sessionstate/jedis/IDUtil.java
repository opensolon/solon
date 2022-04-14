package org.noear.solon.sessionstate.jedis;

import java.util.UUID;

class IDUtil {
    public static String guid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
