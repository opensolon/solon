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
package org.noear.solon.net.websocket;

import org.noear.solon.core.util.MultiMap;

import java.net.URI;

/**
 * 握手信息
 *
 * @author noear
 * @since 2.6
 */
public interface Handshake {
    /**
     * 获取连接地址
     */
    String getUrl();

    /**
     * 获取连接地址
     */
    URI getUri();

    /**
     * 获取参数字典
     */
    MultiMap<String> getParamMap();
}