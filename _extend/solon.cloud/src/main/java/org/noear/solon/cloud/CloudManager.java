package org.noear.solon.cloud;

import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.solon.cloud.service.CloudDiscoveryService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口管理器
 *
 * @author noear
 * @since 1.2
 */
public class CloudManager {
    private static CloudDiscoveryService discoveryService;
    private static CloudConfigService configService;
    private static CloudEventService eventService;

    protected final static Map<CloudConfig, CloudConfigHandler> configHandlerMap = new LinkedHashMap<>();
    protected final static Map<CloudEvent, CloudEventHandler> eventHandlerMap = new LinkedHashMap<>();
    //protected final static Map<String, CloudEventHandler> eventHandlerMap2 = new LinkedHashMap<>();

    /**
     * 登记配置订阅
     */
    public static void register(CloudConfig anno, CloudConfigHandler handler) {
        configHandlerMap.put(anno, handler);
    }

    /**
     * 登记事件订阅
     */
    public static void register(CloudEvent anno, CloudEventHandler handler) {
        eventHandlerMap.put(anno, handler);

//        if (Utils.isEmpty(anno.queue())) {
//            eventHandlerMap2.put(anno.topic(), handler);
//        } else {
//            eventHandlerMap2.put(anno.queue() + "::" + anno.topic(), handler);
//        }
    }

    /**
     * 登记配置服务
     */
    public static void register(CloudConfigService service) {
        configService = service;
    }

    /**
     * 登记注册服务
     */
    public static void register(CloudDiscoveryService service) {
        discoveryService = service;
    }

    /**
     * 登记事件服务
     */
    public static void register(CloudEventService service) {
        eventService = service;
    }


    protected static CloudConfigService configService() {
        return configService;
    }

    protected static CloudDiscoveryService discoveryService() {
        return discoveryService;
    }

    protected static CloudEventService eventService() {
        return eventService;
    }
}
