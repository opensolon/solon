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
package org.noear.solon.core.handle;

import org.noear.solon.core.SignalType;

/**
 * 方法枚举
 *
 * @author noear
 * @since 1.0
 * */
public enum MethodType {
    //http
    GET("GET", SignalType.HTTP), //获取资源
    POST("POST", SignalType.HTTP), //新建资源

    PUT("PUT", SignalType.HTTP), //修改资源 //客户端要提供改变后的完整资源
    DELETE("DELETE", SignalType.HTTP), //删除资源
    PATCH("PATCH", SignalType.HTTP), //修改资源 //客户端只提供改变的局部属性

    HEAD("HEAD", SignalType.HTTP), //相当于GET，但不返回内容

    OPTIONS("OPTIONS", SignalType.HTTP), //获取服务器支持的HTTP请求方法

    TRACE("TRACE", SignalType.HTTP),//回馈服务器收到的请求，用于远程诊断服务器。
    CONNECT("CONNECT", SignalType.HTTP),//用于代理进行传输

    UNKNOWN("UNKNOWN", SignalType.HTTP), //未知

    /**
     * http general all
     */
    HTTP("HTTP", SignalType.HTTP),

    /**
     * socket listen
     */
    SOCKET("SOCKET", SignalType.SOCKET),

    /**
     * message forwarding
     */
    MESSAGE("MESSAGE", SignalType.SOCKET),

    ALL("ALL", SignalType.ALL);

    public final String name;
    public final SignalType signal;

    MethodType(String name, SignalType signal) {
        this.name = name;
        this.signal = signal;
    }
}
