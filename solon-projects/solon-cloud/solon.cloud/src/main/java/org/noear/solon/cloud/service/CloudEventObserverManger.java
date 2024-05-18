package org.noear.solon.cloud.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.EventObserver;
import org.noear.solon.cloud.model.EventObserverMap;

import java.util.*;

/**
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class CloudEventObserverManger {
    private Map<String, EventObserverMap> topicObserverMap = new LinkedHashMap<>();

    /**
     * 主题数量
     */
    public int topicSize() {
        return topicObserverMap.size();
    }

    /**
     * 所有主题
     */
    public Set<String> topicAll() {
        return topicObserverMap.keySet();
    }

    /**
     * 主题映射射
     */
    public EventObserverMap topicOf(String topic) {
        return topicObserverMap.get(topic);
    }

    /**
     * 获取事件处理
     */
    public EventObserver getByTopic(String topic) {
        EventObserverMap tmp = topicObserverMap.get(topic);

        if (tmp == null) {
            return null;
        } else {
            return tmp.topicObserver();
        }
    }

    /**
     * 获取事件处理（支持 tag）
     */
    public EventObserver getByTopicAndTag(String topic, String tag) {
        EventObserverMap tmp = topicObserverMap.get(topic);

        if (tmp == null) {
            return null;
        } else {
            return tmp.tagObserver(tag);
        }
    }

    /**
     * 添加主题事件处理
     */
    public void add(String topic, EventLevel level, String group, String topicRaw, String tag, int qos, CloudEventHandler handler) {
        //构建主题关注映射
        EventObserverMap eventObserverMap = topicObserverMap.computeIfAbsent(topic, t ->
                new EventObserverMap(new EventObserver(level, group, topicRaw, tag, qos)));

        //添加主题处理
        eventObserverMap.topicObserver().addHandler(handler);

        if (Utils.isNotEmpty(tag)) {
            //添加标签处理
            eventObserverMap.tagObserverIfAbsent(tag, t ->
                            new EventObserver(level, group, topicRaw, tag, qos))
                    .addHandler(handler);
        }
    }
}