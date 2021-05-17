package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

public class IpUtil {
    public static String getIp(Context ctx) {
        String ip = ctx.header("X-Real-IP");

        if (Utils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = ctx.header("X-Forwarded-For");
        }

        if (Utils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = ctx.ip();
        }

        return ip;
    }
}
