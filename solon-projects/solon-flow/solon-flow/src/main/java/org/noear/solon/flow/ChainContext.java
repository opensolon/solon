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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 链上下文
 *
 * @author noear
 * @since 3.0
 */
@Preview("3.0")
public class ChainContext implements Serializable {
    //存放执行参数
    private final Map<String, Object> params = new LinkedHashMap<>();
    //存放执行结果（可选）
    public Object result;

    //控制过程计数
    private transient final Counter counter = new Counter();
    //控制过程中断（可选）
    private transient boolean interrupted = false;

    //链驱动器
    protected transient final ChainDriver driver;
    //当前流程引擎
    protected transient FlowEngine engine;

    public ChainContext() {
        this(null);
    }

    public ChainContext(ChainDriver driver) {
        if (driver == null) {
            this.driver = SimpleChainDriver.getInstance();
        } else {
            this.driver = driver;
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
     * 参数集合
     */
    public Map<String, Object> params() {
        return params;
    }

    /**
     * 参数设置
     */
    public ChainContext paramSet(String key, Object value) {
        params.put(key, value);
        return this;
    }

    /**
     * 参数获取
     */
    public <T> T param(String key) {
        return (T) params.get(key);
    }

    /**
     * 参数获取或默认
     */
    public <T> T paramOrDefault(String key, T def) {
        Object tmp = params.get(key);
        if (tmp == null) {
            return def;
        } else {
            return (T) tmp;
        }
    }
}