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
    }

    /**
     * 登记配置服务
     */
    public static void register(CloudConfigService service) {
        configService = service;
    }

    public static CloudConfigService configService() {
        return configService;
    }

    /**
     * 登记注册服务
     */
    public static void register(CloudDiscoveryService service) {
        discoveryService = service;
    }

    public static CloudDiscoveryService discoveryService() {
        return discoveryService;
    }

    /**
     * 登记事件服务
     */
    public static void register(CloudEventService service) {
        eventService = service;
    }

    public static CloudEventService eventService() {
        return eventService;
    }
}
