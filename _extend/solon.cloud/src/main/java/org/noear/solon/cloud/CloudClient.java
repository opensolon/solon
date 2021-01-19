package org.noear.solon.cloud;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.solon.cloud.service.CloudLogService;

import java.util.Properties;

/**
 * 云操作客户端
 *
 * @author noear
 * @since 1.2
 */
public class CloudClient {
    /**
     * 配置服务
     */
    public static CloudConfigService config() {
        return CloudManager.configService();
    }

    /**
     * 配置服务，加载默认配置
     */
    public static void configLoad(String group, String key) {
        if (CloudClient.config() == null) {
            return;
        }

        if (Utils.isNotEmpty(key)) {
            Config config = CloudClient.config().get(group, key);

            if (config != null && Utils.isNotEmpty(config.value)) {
                Properties properties = config.toProps();
                Solon.cfg().loadAdd(properties);
            }

            //关注实时更新
            CloudClient.config().attention(group, key, (cfg) -> {
                Properties properties = config.toProps();
                Solon.cfg().loadAdd(properties);
            });
        }
    }

    /**
     * 发现服务
     */
    public static CloudDiscoveryService discovery() {
        return CloudManager.discoveryService();
    }

    /**
     * 发现服务，推送本地服务（即注册）
     */
    public static void discoveryPush(String hostname) {
        if (CloudClient.discovery() == null) {
            return;
        }

        if (Utils.isNotEmpty(Solon.cfg().appName())) {
            Instance instance = Instance.localNew();

            if (Utils.isNotEmpty(hostname)) {
                if (hostname.contains(":")) {
                    instance.address = hostname;
                } else {
                    instance.address = hostname + ":" + Solon.global().port();
                }
            }

            CloudClient.discovery().register(Solon.cfg().appGroup(), instance);
        }
    }

    /**
     * 事件服务
     */
    public static CloudEventService event() {
        return CloudManager.eventService();
    }

    public static CloudLogService log(){
        return CloudManager.logService();
    }
}
