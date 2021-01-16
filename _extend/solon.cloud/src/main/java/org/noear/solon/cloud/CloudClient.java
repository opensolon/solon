package org.noear.solon.cloud;

import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.solon.cloud.service.CloudEventService;

/**
 * @author noear 2021/1/16 created
 */
public class CloudClient {
    /**
     * 配置服务
     * */
    public static CloudConfigService config() {
        return CloudManager.configService();
    }

    /**
     * 发现服务
     * */
    public static CloudDiscoveryService discovery() {
        return CloudManager.discoveryService();
    }

    /**
     * 事件服务
     * */
    public static CloudEventService event(){
        return CloudManager.eventService();
    }
}
