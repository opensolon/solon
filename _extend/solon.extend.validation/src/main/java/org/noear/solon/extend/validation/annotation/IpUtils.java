package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;

class IpUtils {
    public static String getIp(XContext ctx) {
        String ip = ctx.header("RemoteIp");

        if (XUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = ctx.header("X-Forwarded-For");
        }

        if (XUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = ctx.header("Proxy-Client-IP");
        }

        if (XUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = ctx.header("WL-Proxy-Client-IP");
        }

        if (XUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = ctx.ip();
        }

        return ip;
    }
}
