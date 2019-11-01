package org.noear.solon.extend.sessionstate.local.util;

import java.util.UUID;

public class IDUtil {
    public static String guid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
