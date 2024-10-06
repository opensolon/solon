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
package org.noear.solon.net.stomp.broker.impl;

/**
 * 命令
 * @author limliu
 * @since 2.7
 */
public class Commands {

    /**
     * 发起连接
     */
    public static final String CONNECT = "CONNECT";

    /**
     * 已连接
     */
    public static final String CONNECTED = "CONNECTED";

    /**
     * 断开连接
     */
    public static final String DISCONNECT = "DISCONNECT";

    /**
     * 发送消息
     */
    public static final String SEND = "SEND";

    /**
     * 收到消息
     */
    public static final String MESSAGE = "MESSAGE";

    /**
     * 收到凭据
     */
    public static final String RECEIPT = "RECEIPT";

    /**
     * 发起订阅
     */
    public static final String SUBSCRIBE = "SUBSCRIBE";

    /**
     * 发起退订
     */
    public static final String UNSUBSCRIBE = "UNSUBSCRIBE";

    /**
     * 确认消息
     */
    public static final String ACK = "ACK";

    /**
     * 确认消息
     */
    public static final String NACK = "NACK";

    /**
     * 收到错误
     */
    public static final String ERROR = "ERROR";

    /**
     * 未知命令
     */
    public static final String UNKNOWN = "UNKNOWN";

    /**
     * PING
     */
    public static final String PING = "PING";

}
