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
package org.noear.solon.cloud.gateway.properties;

import java.util.List;

/**
 * 分布式路由配置属性
 *
 * @author noear
 * @since 2.9
 */
public class RouteProperties {
    /**
     * 标识
     */
    private String id;

    /**
     * 顺序
     */
    private int index;

    /**
     * 目标
     */
    private String target;

    /**
     * 匹配检测器
     */
    private List<String> predicates;

    /**
     * 过滤器
     */
    private List<String> filters;

    /**
     * 超时
     */
    private TimeoutProperties timeout;


    /**
     * 标识
     */
    public String getId() {
        return id;
    }

    /**
     * 顺序
     */
    public int getIndex() {
        return index;
    }

    /**
     * 目标
     */
    public String getTarget() {
        return target;
    }

    /**
     * 匹配检测器
     */
    public List<String> getPredicates() {
        return predicates;
    }

    /**
     * 过滤器
     */
    public List<String> getFilters() {
        return filters;
    }

    /**
     * 超时
     */
    public TimeoutProperties getTimeout() {
        return timeout;
    }
}