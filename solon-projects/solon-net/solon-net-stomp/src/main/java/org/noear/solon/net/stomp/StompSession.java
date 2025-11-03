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

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Stomp 会话
 *
 * @author noear
 * @since 3.0
 */
public interface StompSession {
    /**
     * id
     */
    String id();

    /**
     * 名字
     */
    String name();

    /**
     * 名字命名为
     */
    void nameAs(String name);

    /**
     * 获取参数
     *
     * @param name 参数名
     */
    String param(String name);

    /**
     * 获取参数或默认值
     *
     * @param name 参数名
     * @param def  默认值
     */
    String paramOrDefault(String name, String def);

    /**
     * 获取远程地址
     */
    InetSocketAddress remoteAddress();

    /**
     * 获取本地地址
     */
    InetSocketAddress localAddress();

    /**
     * 发送
     *
     * @param frame 帧
     */
    void send(Frame frame);

    /**
     * 关闭会话
     */
    void close();
}
