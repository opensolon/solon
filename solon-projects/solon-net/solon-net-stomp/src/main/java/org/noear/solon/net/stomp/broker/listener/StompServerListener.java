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
package org.noear.solon.net.stomp.broker.listener;

import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.websocket.WebSocket;

/**
 * Stomp 服务端消息监听器
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public interface StompServerListener {

    /**
     * 打开链接（可以鉴权；参数通过url和head方式指定）
     *
     * @param socket
     */
    void onOpen(WebSocket socket);

    /**
     * 关闭链接（被动监听；当断开时触发）
     */
    void onClose(WebSocket socket);


    /**
     * 协议连接
     */
    void onConnect(WebSocket socket, Frame frame);

    /**
     * 协议断开连接
     */
    void onDisconnect(WebSocket socket, Frame frame);


    /**
     * 协议订阅消息
     */
    void onSubscribe(WebSocket socket, Frame frame);

    /**
     * 协议取消消息订阅
     */
    void onUnsubscribe(WebSocket socket, Frame frame);


    /**
     * 协议发送消息
     */
    void onSend(WebSocket socket, Frame frame);


    /**
     * 协议消息确认
     */
    void onAck(WebSocket socket, Frame frame);
}
