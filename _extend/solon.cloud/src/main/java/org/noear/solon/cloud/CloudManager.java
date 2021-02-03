package org.noear.solon.cloud;

import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.service.*;
import org.noear.solon.core.util.PrintUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 云接口管理器
 *
 * @author noear
 * @since 1.2
 */
public class CloudManager {
    /**
     * 云端发现服务
     */
    private static CloudDiscoveryService discoveryService;
    /**
     * 云端配置服务
     */
    private static CloudConfigService configService;
    /**
     * 云端事件服务
     */
    private static CloudEventService eventService;
    /**
     * 云端锁服务
     */
    private static CloudLockService lockService;

    /**
     * 云端日志服务
     */
    private static CloudLogService logService;

    /**
     * 云端跟踪服务（链路）
     */
    private static CloudTraceService traceService;


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
     * 登记云端配置服务
     */
    public static void register(CloudConfigService service) {
        configService = service;
        PrintUtil.green("[Cloud] ");
        System.out.println("CloudConfigService registered from the " + service.getClass().getTypeName());
    }

    /**
     * 登记云端注册服务
     */
    public static void register(CloudDiscoveryService service) {
        discoveryService = service;
        PrintUtil.green("[Cloud] ");
        System.out.println("CloudDiscoveryService registered from the " + service.getClass().getTypeName());
    }

    /**
     * 登记云端事件服务
     */
    public static void register(CloudEventService service) {
        eventService = service;
        PrintUtil.green("[Cloud] ");
        System.out.println("CloudEventService registered from the " + service.getClass().getTypeName());
    }

    /**
     * 登记云端日志服务
     */
    public static void register(CloudLockService service) {
        lockService = service;
        PrintUtil.green("[Cloud] ");
        System.out.println("CloudLockService registered from the " + service.getClass().getTypeName());
    }

    /**
     * 登记云端日志服务
     */
    public static void register(CloudLogService service) {
        logService = service;
        PrintUtil.green("[Cloud] ");
        System.out.println("CloudLogService registered from the " + service.getClass().getTypeName());
    }

    /**
     * 登记云端跟踪服务
     */
    public static void register(CloudTraceService service) {
        traceService = service;
        PrintUtil.green("[Cloud] ");
        System.out.println("CloudTraceService registered from the " + service.getClass().getTypeName());
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

    protected static CloudLockService lockService() {
        return lockService;
    }

    protected static CloudLogService logService() {
        return logService;
    }

    protected static CloudTraceService traceService() {
        return traceService;

    }

}
