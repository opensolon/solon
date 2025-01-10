/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.flow.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 链上下文
 *
 * @author noear
 * @since 3.0
 */
public class ChainContext {
    private final Map<String, AtomicInteger> counterMap = new LinkedHashMap<>();
    private final Map<String, Object> paramMap = new LinkedHashMap<>();

    public ChainContext(){
        paramMap.put("context", this);
    }

    /**
     * 是否中断
     */
    public boolean isInterrupt() {
        return false;
    }

    /**
     * 计数器获取
     */
    public int counterGet(String id) {
        return counterMap.computeIfAbsent(id, k -> new AtomicInteger(0))
                .get();
    }

    /**
     * 计数器增量
     */
    public int counterIncr(String id) {
        return counterMap.computeIfAbsent(id, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    /**
     * 参数集合
     */
    public Map<String, Object> paramMap() {
        return paramMap;
    }

    /**
     * 获取参数
     */
    public Object get(String key) {
        return paramMap.get(key);
    }

    /**
     * 设置参数
     */
    public ChainContext set(String key, Object value) {
        paramMap.put(key, value);
        return this;
    }

    /**
     * 结果
     */
    public Object result;
}