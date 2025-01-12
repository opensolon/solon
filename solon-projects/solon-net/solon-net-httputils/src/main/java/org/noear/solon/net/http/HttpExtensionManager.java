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

import java.util.Collection;

/**
 * Http 扩展管理
 *
 * @author noear
 * @since 2.9
 * @deprecated 3.0
 */
@Deprecated
public class HttpExtensionManager {
    /**
     * 添加扩展
     */
    public static void add(HttpExtension extension) {
        HttpConfiguration.addExtension(extension);
    }

    /**
     * 移除扩展
     */
    public static void remove(HttpExtension extension) {
        HttpConfiguration.removeExtension(extension);
    }

    /**
     * 获取所有扩展
     */
    public static Collection<HttpExtension> getExtensions() {
        return HttpConfiguration.getExtensions();
    }
}
