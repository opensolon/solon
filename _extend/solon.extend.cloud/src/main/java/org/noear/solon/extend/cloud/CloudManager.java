package org.noear.solon.extend.cloud;

import org.noear.solon.extend.cloud.annotation.CloudConfig;
import org.noear.solon.extend.cloud.annotation.CloudDiscovery;
import org.noear.solon.extend.cloud.annotation.CloudEvent;
import org.noear.solon.extend.cloud.service.ConfigService;
import org.noear.solon.extend.cloud.service.RegisterService;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 云管理
 *
 * @author noear
 * @since 1.2
 */
public class CloudManager {
    private static Set<RegisterService> registerServiceSet = new LinkedHashSet<>();
    private static Set<ConfigService> configServiceSet = new LinkedHashSet<>();

    private static Map<String, CloudConfigHandler> configHandlerMap = new LinkedHashMap<>();
    private static Map<String, CloudDiscoveryHandler> discoveryHandlerMap = new LinkedHashMap<>();
    private static Map<String, CloudEventHandler> eventHandlerMap = new LinkedHashMap<>();

    public static void register(CloudConfig anno, CloudConfigHandler handler) {
        configHandlerMap.put(anno.value(), handler);
    }

    public static void register(CloudDiscovery anno, CloudDiscoveryHandler handler) {
        discoveryHandlerMap.put(anno.value(), handler);
    }

    public static void register(CloudEvent anno, CloudEventHandler handler) {
        eventHandlerMap.put(anno.value(), handler);
    }

    public static void register(ConfigService service) {
        configServiceSet.add(service);
    }

    public static void register(RegisterService service) {
        registerServiceSet.add(service);
    }
}
