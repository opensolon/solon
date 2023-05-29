package org.noear.solon.cloud.utils;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Discovery;

/**
 * 发现辐助工具
 *
 * @author noear
 * @since 2.2
 */
public class DiscoveryUtils {
    /**
     * 尝试加载发现代理
     */
    public static void tryLoadAgent(Discovery discovery, String group, String service) {
        if (discovery.agent() != null) {
            return;
        }

        if (CloudClient.config() != null) {
            //前缀在前，方便相同配置在一起
            String agent = CloudClient.config().pull(group, "discovery.agent." + service).value();

            if (Utils.isNotEmpty(agent)) {
                discovery.agent(agent);
            } else {
                //为了后面不再做重复检测
                discovery.agent("");
            }
        }
    }
}