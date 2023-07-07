package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

/**
 * IP 工具
 *
 * @author noear
 * @since 1.3
 * */
public class IpUtil {
    //
    // 可以进行替换扩展
    //
    private static IpUtil global = new IpUtil();

    public static IpUtil global() {
        return global;
    }

    public static void globalSet(IpUtil instance) {
        if (instance != null) {
            global = instance;
        }
    }


    /**
     * 获取 Ip
     * */
    public String getRealIp(Context ctx) {
        //客户端ip
        String ip = ctx.header("X-Real-IP");

        if (Utils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            //包含了客户端和各级代理ip的完整ip链路
            ip = ctx.headerOrDefault("X-Forwarded-For", "");
            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }

        if (Utils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = ctx.ip();
        }

        return ip;
    }
}
