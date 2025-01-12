/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.model;

import org.noear.solon.cloud.annotation.EventLevel;

import java.util.*;
import java.util.function.Function;

/**
 * 云端事件主题观察者 映射字典
 *
 * @author noear
 * @since 2.8
 */
public class EventObserverMap {
    private final EventObserver topicObserver;
    private final Map<String, EventObserver> tagMap = new LinkedHashMap<>();

    public EventObserverMap(EventObserver topicObserver) {
        this.topicObserver = topicObserver;
    }

    /**
     * 主题关注者
     */
    public EventObserver topicObserver() {
        return topicObserver;
    }

    /**
     * 标签关注者
     */
    public EventObserver tagObserver(String tag) {
        return tagMap.get(tag);
    }

    /**
     * 标签关注者补缺并获取
     */
    public EventObserver tagObserverIfAbsent(String tag, Function<String, EventObserver> mappingFunction) {
        return tagMap.computeIfAbsent(tag, mappingFunction);
    }

    /**
     * 获取所有标签
     */
    public Collection<String> getTags() {
        return tagMap.keySet();
    }

    /**
     * 获取所有标签，根据 eventLevel 过滤
     */
    public Collection<String> getTagsByLevel(EventLevel eventLevel) {
        Set<String> tags = new HashSet<>();

        for (Map.Entry<String, EventObserver> kv : tagMap.entrySet()) {
            if (kv.getValue().getLevel() == eventLevel) {
                tags.add(kv.getKey());
            }
        }

        return tags;
    }
}
