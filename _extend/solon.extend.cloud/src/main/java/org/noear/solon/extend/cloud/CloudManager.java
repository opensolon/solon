package org.noear.solon.extend.cloud;

import org.noear.solon.extend.cloud.annotation.CloudConfig;
import org.noear.solon.extend.cloud.annotation.CloudDiscovery;
import org.noear.solon.extend.cloud.annotation.CloudEvent;
import org.noear.solon.extend.cloud.service.CloudConfigService;
import org.noear.solon.extend.cloud.service.CloudEventService;
import org.noear.solon.extend.cloud.service.CloudRegisterService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口管理器
 *
 * @author noear
 * @since 1.2
 */
public class CloudManager {
    private static CloudRegisterService registerService;
    private static CloudConfigService configService;
    private static CloudEventService eventService;

    private static Map<String, CloudConfigHandler> configHandlerMap = new LinkedHashMap<>();
    private static Map<String, CloudDiscoveryHandler> discoveryHandlerMap = new LinkedHashMap<>();
    private static Map<String, CloudEventHandler> eventHandlerMap = new LinkedHashMap<>();

    /**
     * 登记配置订阅
     */
    public static void register(CloudConfig anno, CloudConfigHandler handler) {
        configHandlerMap.put(anno.value(), handler);
    }

    /**
     * 登记发现订阅
     */
    public static void register(CloudDiscovery anno, CloudDiscoveryHandler handler) {
        discoveryHandlerMap.put(anno.value(), handler);
    }

    /**
     * 登记事件订阅
     */
    public static void register(CloudEvent anno, CloudEventHandler handler) {
        eventHandlerMap.put(anno.value(), handler);
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
    public static void register(CloudRegisterService service) {
        registerService = service;
    }

    public static CloudRegisterService registerService() {
        return registerService;
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
