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
package org.noear.solon.flow.driver;

import org.noear.solon.flow.TaskComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map 组件管理链驱动器
 *
 * @author noear
 * @since 3.1
 */
public class MapChainDriver extends AbstractChainDriver {
    private static final MapChainDriver instance = new MapChainDriver();

    public static MapChainDriver getInstance() {
        return instance;
    }

    protected final Map<String, TaskComponent> components = new ConcurrentHashMap<>();

    /**
     * 添加组件
     */
    public void putComponent(String key, TaskComponent component) {
        components.put(key, component);
    }

    /**
     * 移除组件
     */
    public void removeComponent(String key) {
        components.remove(key);
    }

    /**
     * 获取组件
     */
    @Override
    public Object getComponent(String key) {
        return components.get(key);
    }
}