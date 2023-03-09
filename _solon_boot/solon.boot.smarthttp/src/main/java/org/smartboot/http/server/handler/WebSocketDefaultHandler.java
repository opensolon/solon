/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: WebSocketHandle.java
 * Date: 2020-03-31
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.handler;

import org.smartboot.http.common.logging.Logger;
import org.smartboot.http.common.logging.LoggerFactory;
import org.smartboot.http.server.WebSocketHandler;
import org.smartboot.http.server.WebSocketRequest;
import org.smartboot.http.server.WebSocketResponse;
import org.smartboot.http.server.impl.WebSocketRequestImpl;
import org.smartboot.http.server.impl.WebSocketResponseImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * @author 三刀
 * @version V1.0 , 2020/3/31
 */
public class WebSocketDefaultHandler extends WebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketDefaultHandler.class);

    @Override
    public void whenHeaderComplete(WebSocketRequestImpl request, WebSocketResponseImpl response) {
        onHandShake(request, response);
    }

    @Override
    public final void handle(WebSocketRequest request, WebSocketResponse response) throws IOException {
        try {
            switch (request.getFrameOpcode()) {
                case WebSocketRequestImpl.OPCODE_TEXT:
                    handleTextMessage(request, response, new String(request.getPayload(), StandardCharsets.UTF_8));
                    break;
                case WebSocketRequestImpl.OPCODE_BINARY:
                    handleBinaryMessage(request, response, request.getPayload());
                    break;
                case WebSocketRequestImpl.OPCODE_CLOSE:
                    try {
                        onClose(request, response);
                    } finally {
                        response.close();
                    }
                    break;
                case WebSocketRequestImpl.OPCODE_PING:
//                            LOGGER.warn("unSupport ping now");
                    break;
                case WebSocketRequestImpl.OPCODE_PONG:
//                            LOGGER.warn("unSupport pong now");
                    break;
                case WebSocketRequestImpl.OPCODE_CONTINUE:
                    System.out.println(new String(request.getPayload()));
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        } catch (Throwable throwable) {
            onError(request, throwable);
            throw throwable;
        }
    }

    /**
     * 握手成功
     *
     * @param request
     * @param response
     */
    public void onHandShake(WebSocketRequest request, WebSocketResponse response) {
        LOGGER.warn("handShake success");
    }

    /**
     * 连接关闭
     *
     * @param request
     * @param response
     */
    public void onClose(WebSocketRequest request, WebSocketResponse response) {
        LOGGER.warn("close connection");
    }

    /**
     * 处理字符串请求消息
     *
     * @param request
     * @param response
     * @param data
     */
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        System.out.println(data);
    }

    /**
     * 处理二进制请求消息
     *
     * @param request
     * @param response
     * @param data
     */
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        System.out.println(data);
    }

    /**
     * 连接异常
     *
     * @param request
     * @param throwable
     */
    public void onError(WebSocketRequest request, Throwable throwable) {
        throwable.printStackTrace();
    }
}
