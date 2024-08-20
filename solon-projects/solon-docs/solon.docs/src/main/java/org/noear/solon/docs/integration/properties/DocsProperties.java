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
package org.noear.solon.docs.integration.properties;

import org.noear.solon.docs.DocDocket;

import java.util.HashMap;
import java.util.Map;

/**
 * 文档配置属性
 *
 * @author noear
 * @since 2.9
 */
public class DocsProperties {
    private DiscoverProperties discover = new DiscoverProperties();
    private Map<String, DocDocket> routes = new HashMap<>();

    /**
     * 获取发现配置
     */
    public DiscoverProperties getDiscover() {
        return discover;
    }

    /**
     * 获取路由配置
     */
    public Map<String, DocDocket> getRoutes() {
        return routes;
    }
}
