package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.EventObserver;

import java.util.*;

/**
 * @author noear
 * @since 1.5
 */
public class CloudEventObserverManger {
    private Map<String, EventObserver> observerMap = new LinkedHashMap<>();
    private Map<String, Set<String>> topicAndTags = new LinkedHashMap<>();

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

    public Map<String, Set<String>> topicAndTags(){
        return topicAndTags;
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
    public void add(String topic, EventLevel level, String group, String topicRaw, String tag, CloudEventHandler observer) {
        //关注关系
        EventObserver eventObserver = observerMap.get(topic);
        if (eventObserver == null) {
            eventObserver = new EventObserver(level, group, topicRaw, tag);
            observerMap.put(topic, eventObserver);
        }

        eventObserver.addHandler(observer);

        //主题与标签
        Set<String> tags = topicAndTags.get(topic);
        if(tags == null){
            tags = new HashSet<>();
            topicAndTags.put(topic, tags);
        }
        tags.add(tag);
    }
}
