/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: WebSocketRequest.java
 * Date: 2020-04-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http;

import org.smartboot.http.enums.WebsocketStatus;

import java.net.InetSocketAddress;

/**
 * WebSocket消息请求接口
 *
 * @author 三刀
 * @version V1.0 , 2020/4/1
 */
public interface WebSocketRequest {
    public WebsocketStatus getWebsocketStatus();

    public int getFrameOpcode();

    public byte[] getPayload();

    String getRequestURI();

    InetSocketAddress getRemoteAddress();

    /**
     * 获取套接字绑定的本地地址。
     *
     * @return
     */
    InetSocketAddress getLocalAddress();
}
