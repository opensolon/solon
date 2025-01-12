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

/**
 * Stomp 命令
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public interface Commands {
    ///
    /// 连接可发送的帧
    ///

    /**
     * 发起连接
     */
    String STOMP = "STOMP";

    /**
     * 发起连接
     */
    String CONNECT = "CONNECT";

    /**
     * 已连接
     */
    String CONNECTED = "CONNECTED";

    /**
     * 断开连接
     */
    String DISCONNECT = "DISCONNECT";

    /**
     * PING
     */
    String PING = "PING";

    ///
    /// 客户端可发送的帧
    ///

    /**
     * 发送消息（可以有主体）
     */
    String SEND = "SEND";

    /**
     * 发起订阅
     */
    String SUBSCRIBE = "SUBSCRIBE";

    /**
     * 发起退订
     */
    String UNSUBSCRIBE = "UNSUBSCRIBE";

    /**
     * 确认消息
     */
    String ACK = "ACK";

    /**
     * 确认消息
     */
    String NACK = "NACK";


    /**
     * 事务开始消息
     */
    String BEGIN = "BEGIN";


    /**
     * 事务提交消息
     */
    String COMMIT = "COMMIT";


    /**
     * 事务中止消息
     */
    String ABORT = "ABORT";


    ///
    /// 服务端可发送的帧
    ///

    /**
     * 收到消息（可以有主体）
     */
    String MESSAGE = "MESSAGE";

    /**
     * 收到凭据
     */
    String RECEIPT = "RECEIPT";

    /**
     * 收到错误（可以有主体）
     */
    String ERROR = "ERROR";

    ///
    /// 未知帧
    ///

    /**
     * 未知命令
     */
    String UNKNOWN = "UNKNOWN";
}