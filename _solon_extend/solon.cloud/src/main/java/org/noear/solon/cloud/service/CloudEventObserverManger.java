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
    private static final String TAG_SPLIT_MARK = "@";

    private Map<String, EventObserver> topicObserverMap = new LinkedHashMap<>();
    private Map<String, EventObserver> topicTagObserverMap = new LinkedHashMap<>();

    private Map<String, Set<String>> topicAndTags = new LinkedHashMap<>();

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

    public Map<String, Set<String>> topicAndTags(){
        return topicAndTags;
    }

    /**
     * 获取事件处理
     */
    public CloudEventHandler getByTopic(String topic) {
        return topicObserverMap.get(topic);
    }

    /**
     * 添加主题事件处理
     */
    public void add(String topic, EventLevel level, String group, String topicRaw, String tag, CloudEventHandler observer) {
        //主题关注关系
        EventObserver eventObserver = topicObserverMap.get(topic);
        if (eventObserver == null) {
            eventObserver = new EventObserver(level, group, topicRaw, tag);
            topicObserverMap.put(topic, eventObserver);
        }

        eventObserver.addHandler(observer);

        //主题标签关注关系
        String topicTag = topic+tag;


        //主题与标签
        Set<String> tags = topicAndTags.get(topic);
        if(tags == null){
            tags = new HashSet<>();
            topicAndTags.put(topic, tags);
        }
        tags.add(tag);
    }
}
