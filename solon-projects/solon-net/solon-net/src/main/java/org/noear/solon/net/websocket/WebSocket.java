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

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * WebSocket 会话接口
 *
 * @author noear
 * @since 2.6
 */
public interface WebSocket {
    /**
     * 会话id
     */
    String id();

    /**
     * 名字（由用户设定）
     */
    String name();

    /**
     * 名字命为
     *
     * @param name 名字
     */
    void nameAs(String name);

    /**
     * 是否有效
     */
    boolean isValid();

    /**
     * 是否安全
     */
    boolean isSecure();

    /**
     * 获取请求地址
     */
    String url();

    /**
     * 获取请求路径
     */
    String path();

    /**
     * 设置新路径
     */
    void pathNew(String pathNew);

    /**
     * 获取参数字典
     */
    MultiMap<String> paramMap();

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
     * 添加参数
     *
     * @param name  名字
     * @param value 值
     */
    void param(String name, String value);

    /**
     * 获取远程地址
     */
    InetSocketAddress remoteAddress();

    /**
     * 获取本地地址
     */
    InetSocketAddress localAddress();

    /**
     * 获取所有属性
     */
    Map<String, Object> attrMap();

    /**
     * 是有属性
     *
     * @param name 名字
     */
    boolean attrHas(String name);

    /**
     * 获取属性
     *
     * @param name 名字
     */
    <T> T attr(String name);

    /**
     * 获取属性或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    <T> T attrOrDefault(String name, T def);

    /**
     * 设置属性
     *
     * @param name  名字
     * @param value 值
     */
    <T> void attr(String name, T value);

    /**
     * 获取闲置超时
     */
    long getIdleTimeout();

    /**
     * 设置闲置超时
     */
    void setIdleTimeout(long idleTimeout);

    /**
     * 发送文本
     *
     * @param text 文本
     */
    Future<Void> send(String text);

    /**
     * 发送字节
     *
     * @param binary 二进制
     */
    Future<Void> send(ByteBuffer binary);

    /**
     * 关闭
     */
    void close();

    /**
     * 关闭
     *
     * @param reason 原因
     */
    void close(int code, String reason);
}