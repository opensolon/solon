package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author noear
 * @since 1.5
 */
public class CloudEventObserverManger {
    private Map<String, CloudEventObserverEntity> observerMap = new HashMap<>();

    /**
     * 主题数量
     */
    public int topicSize() {
        return observerMap.size();
    }

    /**
     * 所有主题
     */
    public Set<String> topicAll() {
        return observerMap.keySet();
    }

    /**
     * 获取事件处理
     */
    public CloudEventHandler get(String topic) {
        return observerMap.get(topic);
    }

    /**
     * 添加主题事件处理
     */
    public void add(String topic, EventLevel level, String group, String topicRaw, CloudEventHandler observer) {
        CloudEventObserverEntity observerEntity = observerMap.get(topic);
        if (observerEntity == null) {
            observerEntity = new CloudEventObserverEntity(level, group, topicRaw);
            observerMap.put(topic, observerEntity);
        }

        observerEntity.addHandler(observer);
    }
}
