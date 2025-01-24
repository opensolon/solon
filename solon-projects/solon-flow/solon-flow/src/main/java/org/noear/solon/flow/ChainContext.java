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
package org.noear.solon.flow;

import org.noear.solon.flow.driver.SimpleChainDriver;
import org.noear.solon.lang.Preview;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 链上下文
 *
 * @author noear
 * @since 3.0
 */
@Preview("3.0")
public class ChainContext implements Serializable {
    //存放数据模型
    private final Map<String, Object> model;
    //存放执行结果（可选）
    public Object result;

    //控制过程计数
    private transient final Counter counter = new Counter();
    //控制过程中断（可选）
    private transient boolean interrupted = false;

    //当前流程引擎
    protected transient FlowEngine engine;

    public ChainContext() {
        this(null);
    }

    public ChainContext(Map<String, Object> model) {
        if (model == null) {
            this.model = new HashMap<>();
        } else {
            this.model = model;
        }
    }

    /**
     * 当前流程引擎
     */
    public FlowEngine engine() {
        return engine;
    }

    /**
     * 计数器
     */
    public Counter counter() {
        return counter;
    }

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

    /// ////////

    /**
     * 数据模型
     */
    public Map<String, Object> model() {
        return model;
    }

    /**
     * 设置
     */
    public ChainContext put(String key, Object value) {
        model.put(key, value);
        return this;
    }

    /**
     * 设置
     */
    public ChainContext putAll(Map<String, Object> model) {
        model.putAll(model);
        return this;
    }

    /**
     * 获取
     */
    public <T> T get(String key) {
        return (T) model.get(key);
    }

    /**
     * 获取或默认
     */
    public <T> T getOrDefault(String key, T def) {
        return (T) model.getOrDefault(key, def);
    }
}