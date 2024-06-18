package org.noear.solon.net.http;

import org.noear.solon.Utils;
import org.noear.solon.core.LoadBalance;

/**
 * 负截均衡工具类
 *
 * @author noear
 * @since 2.8
 */
public class LoadBalanceUtils {
    /**
     * 获取服务地址
     *
     * @param group   分组
     * @param service 服务
     */
    public static String getServer(String group, String service) {
        LoadBalance upstream = null;
        if (Utils.isEmpty(group)) {
            upstream = LoadBalance.get(service);
        } else {
            upstream = LoadBalance.get(group, service);
        }

        if (upstream == null) {
            throw new IllegalStateException("No service upstream found: " + service);
        }

        String server = upstream.getServer();

        if (Utils.isEmpty(server)) {
            throw new IllegalStateException("No service address found: " + service);
        }

        return server;
    }
}
