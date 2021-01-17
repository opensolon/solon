package org.noear.solon.cloud;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.Node;
import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.solon.cloud.utils.LocalUtils;

import java.util.Properties;

/**
 * @author noear 2021/1/16 created
 */
public class CloudClient {
    /**
     * 配置服务
     */
    public static CloudConfigService config() {
        return CloudManager.configService();
    }

    /**
     * 配置配置
     */
    public static void configLoad(String group, String key) {
        if (CloudClient.config() == null) {
            return;
        }

        if (Utils.isNotEmpty(key)) {
            Config config = CloudClient.config().get(group, key);

            if (config != null && Utils.isNotEmpty(config.value)) {
                Properties properties = Utils.buildProperties(config.value);
                Solon.cfg().loadAdd(properties);
            }
        }
    }

    /**
     * 发现服务
     */
    public static CloudDiscoveryService discovery() {
        return CloudManager.discoveryService();
    }

    /**
     * 发现服务
     */
    public static void discoveryPush(String hostname) {
        if (CloudClient.discovery() == null) {
            return;
        }

        if (Utils.isNotEmpty(Solon.cfg().appName())) {
            Node node = new Node();
            node.service = Solon.cfg().appName();
            node.port = Solon.global().port();
            node.protocol = "http";

            if (Utils.isEmpty(hostname)) {
                node.ip = LocalUtils.getLocalAddress();
            } else {
                node.ip = hostname;
            }

            CloudClient.discovery().register(node);
        }
    }

    /**
     * 事件服务
     */
    public static CloudEventService event() {
        return CloudManager.eventService();
    }

//    public static void eventReceive(Event event) {
//        CloudEventHandler handler;
//        if (Utils.isEmpty(event.queue)) {
//            handler = CloudManager.eventHandlerMap2.get(event.topic);
//        } else {
//            handler = CloudManager.eventHandlerMap2.get(event.queue + "::" + event.topic);
//        }
//
//        if (handler != null) {
//            handler.handler(event);
//        }
//    }
}
