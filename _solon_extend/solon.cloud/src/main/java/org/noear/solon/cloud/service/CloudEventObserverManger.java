package org.noear.solon.cloud.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.EventObserver;

import java.util.*;

/**
 * @author noear
 * @since 1.5
 */
public class CloudEventObserverManger {
    private static final String TAG_SPLIT_MARK = "@";

    private Map<String, EventObserver> topicObserverMap = new LinkedHashMap<>();
    private Map<String, EventObserver> topicAndTagObserverMap = new LinkedHashMap<>();

    private Map<String, Set<String>> topicTagsMap = new LinkedHashMap<>();

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
     * 所有主题与标签
     */
    public Map<String, Set<String>> topicTags() {
        return topicTagsMap;
    }

    /**
     * 获取事件处理
     */
    public CloudEventHandler getByTopic(String topic) {
        return topicObserverMap.get(topic);
    }

    /**
     * 获取事件处理（支持 tag）
     */
    public CloudEventHandler getByTopicAndTag(String topicAndTag) {
        return topicAndTagObserverMap.get(topicAndTag);
    }

    /**
     * 获取事件处理（支持 tag）
     */
    public CloudEventHandler getByTopicAndTag(String topic, String tag) {
        String topicAndTag = topic + TAG_SPLIT_MARK + tag;

        return getByTopicAndTag(topicAndTag);
    }

    /**
     * 添加主题事件处理
     */
    public void add(String topic, EventLevel level, String group, String topicRaw, String tag, CloudEventHandler observer) {
        //主题关注关系
        addTopicObserver(topic, level, group, topicRaw, tag, observer);

        //主题标签关注关系
        addTopicAndTagObserver(topic, level, group, topicRaw, tag, observer);

        //主题与标签
        addTopicTags(topic, tag);
    }

    private void addTopicObserver(String topic, EventLevel level, String group, String topicRaw, String tag, CloudEventHandler observer) {
        EventObserver eventObserver = topicObserverMap.get(topic);
        if (eventObserver == null) {
            eventObserver = new EventObserver(level, group, topicRaw, tag);
            topicObserverMap.put(topic, eventObserver);
        }

        eventObserver.addHandler(observer);
    }

    private void addTopicAndTagObserver(String topic, EventLevel level, String group, String topicRaw, String tag, CloudEventHandler observer) {
        if (Utils.isEmpty(tag)) {
            return;
        }

        String topicAndTag = topic + TAG_SPLIT_MARK + tag;
        EventObserver eventObserver = topicAndTagObserverMap.get(topicAndTag);
        if (eventObserver == null) {
            eventObserver = new EventObserver(level, group, topicRaw, tag);
            topicAndTagObserverMap.put(topic, eventObserver);
        }

        eventObserver.addHandler(observer);
    }

    private void addTopicTags(String topic, String tag) {
        Set<String> tags = topicTagsMap.get(topic);
        if (tags == null) {
            tags = new HashSet<>();
            topicTagsMap.put(topic, tags);
        }

        tags.add(tag);
    }
}
