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
package org.noear.solon.net.stomp;

import org.noear.solon.core.util.MultiMap;

/**
 * Stomp 帧
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public interface Frame {
    /**
     * 获取原始数据
     */
    String getSource();

    /**
     * 获取命令, 如send...等。参考#Commands
     *
     * @see Commands
     */
    String getCommand();

    /**
     * 获取有效载荷
     *
     * @return
     */
    String getPayload();

    /**
     * 获取head
     *
     * @param key 参考#Header
     */
    String getHeader(String key);

    /**
     * 获取head集合
     *
     * @return
     */
    MultiMap<String> getHeaderAll();

    /**
     * 新建构建器
     */
    static FrameBuilder newBuilder() {
        return new FrameBuilder();
    }
}