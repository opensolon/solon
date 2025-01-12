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
package org.noear.solon.docs.integration.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分布式发现配置属性
 *
 * @author noear
 * @since 2.9
 */
public class DiscoverProperties {
    private boolean enabled;
    private boolean syncStatus;

    private String uriPattern = "";
    private String contextPathPattern = "";

    private Map<String, String> basicAuth;

    private List<String> excludedServices = new ArrayList<>();

    private List<String> includedServices = new ArrayList<>();

    /**
     * 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 同步服务上下线状态
     */
    public boolean isSyncStatus() {
        return syncStatus;
    }

    /**
     * uri 构建模式
     */
    public String getUriPattern() {
        return uriPattern;
    }

    /**
     * contextPath 构建模式
     */
    public String getContextPathPattern() {
        return contextPathPattern;
    }

    /**
     * 基础鉴权配置
     */
    public Map<String, String> getBasicAuth() {
        return basicAuth;
    }

    /**
     * 排除服务
     */
    public List<String> getExcludedServices() {
        return excludedServices;
    }

    /**
     * 包括服务
     */
    public List<String> getIncludedServices() {
        return includedServices;
    }
}
