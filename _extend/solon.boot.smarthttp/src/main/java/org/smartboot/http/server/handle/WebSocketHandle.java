/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpHandle.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.handle;

import org.smartboot.http.WebSocketRequest;
import org.smartboot.http.WebSocketResponse;

/**
 * WebSocket消息处理器
 *
 * @author 三刀
 * @version V1.0 , 2018/2/6
 */
public abstract class WebSocketHandle extends Handle<WebSocketRequest, WebSocketResponse> {
}
