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

import org.noear.solon.lang.Preview;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 链上下文
 *
 * @author noear
 * @since 3.0
 */
@Preview("3.0")
public class ChainContext implements Serializable {
    //存放记数器（汇聚网关会用法）
    private final Map<String, AtomicInteger> counters = new LinkedHashMap<>();
    //存放执行参数
    private final Map<String, Object> params = new LinkedHashMap<>();
    //存放过程记录（可选）
    private final Map<String, Object> attrs = new LinkedHashMap<>();
    //控制执行中断（可选）
    private boolean interrupted = false;

    /**
     * 存放执行结果（可选）
     */
    public Object result;

    /**
     * 是否已中断
     */
    public boolean isInterrupted() {
        return interrupted;
    }

    /**
     * 中断（执行中可中断流）
     */
    public void interrupt() {
        this.interrupted = true;
    }

    /**
     * 计数器获取
     */
    public int counterGet(String id) {
        return counters.computeIfAbsent(id, k -> new AtomicInteger(0))
                .get();
    }

    /**
     * 计数器设置
     */
    public void counterSet(String id, int value) {
        counters.computeIfAbsent(id, k -> new AtomicInteger(0))
                .set(value);
    }

    /**
     * 计数器增量
     */
    public int counterIncr(String id) {
        return counters.computeIfAbsent(id, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    /**
     * 参数集合
     */
    public Map<String, Object> params() {
        return params;
    }

    /**
     * 参数获取
     */
    public Object param(String key) {
        return params.get(key);
    }

    /**
     * 参数设置
     */
    public ChainContext paramSet(String key, Object value) {
        params.put(key, value);
        return this;
    }

    /**
     * 属性集合
     */
    public Map<String, Object> attrs() {
        return attrs;
    }

    /**
     * 属性获取
     */
    public Object attr(String key) {
        return attrs.get(key);
    }

    /**
     * 属性设置
     */
    public ChainContext attrSet(String key, Object value) {
        attrs.put(key, value);
        return this;
    }
}