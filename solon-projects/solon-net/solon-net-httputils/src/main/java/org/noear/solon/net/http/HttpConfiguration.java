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
package org.noear.solon.net.http;

import org.noear.solon.net.http.impl.HttpUtilsFactoryDefault;

import java.util.*;

/**
 * Http 配置类
 *
 * @author noear
 * @since 2.9
 */
public class HttpConfiguration {
    private static Set<HttpExtension> extensions = new LinkedHashSet<>();
    private static HttpUtilsFactory factory = new HttpUtilsFactoryDefault();

    /**
     * 添加扩展
     */
    public static void addExtension(HttpExtension extension) {
        extensions.add(extension);
    }

    /**
     * 移除扩展
     */
    public static void removeExtension(HttpExtension extension) {
        extensions.remove(extension);
    }

    /**
     * 获取所有扩展
     */
    public static Collection<HttpExtension> getExtensions() {
        return extensions;
    }

    /**
     * 获取工厂
     */
    public static HttpUtilsFactory getFactory() {
        return factory;
    }

    /**
     * 设置工厂
     */
    public static void setFactory(HttpUtilsFactory factory) {
        HttpConfiguration.factory = factory;
    }
}