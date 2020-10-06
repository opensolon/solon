/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: WebSocketHandle.java
 * Date: 2020-03-31
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.handle;

import org.smartboot.http.WebSocketRequest;
import org.smartboot.http.WebSocketResponse;
import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.exception.HttpException;
import org.smartboot.http.logging.RunLogger;
import org.smartboot.http.server.WebSocketRequestImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;


/**
 * @author 三刀
 * @version V1.0 , 2020/3/31
 */
public class WebSocketDefaultHandle extends WebSocketHandle {

    @Override
    public final void doHandle(WebSocketRequest request, WebSocketResponse response) throws IOException {
        try {
            switch (request.getWebsocketStatus()) {
                case HandShake:
                    onHandShark(request, response);
                    break;
                case DataFrame: {
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
                            RunLogger.getLogger().log(Level.FINE, "unSupport ping now");
                            break;
                        case WebSocketRequestImpl.OPCODE_PONG:
                            RunLogger.getLogger().log(Level.FINE, "unSupport pong now");
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                }
                break;
                default:
                    throw new HttpException(HttpStatus.BAD_REQUEST);
            }
        } catch (Throwable throwable) {
            onError(throwable);
            throw throwable;
        }
    }

    /**
     * 握手成功
     *
     * @param request
     * @param response
     */
    public void onHandShark(WebSocketRequest request, WebSocketResponse response) {
        RunLogger.getLogger().log(Level.FINE, "handShark success");
    }

    /**
     * 握手成功
     *
     * @param request
     * @param response
     */
    public void onClose(WebSocketRequest request, WebSocketResponse response) {
        RunLogger.getLogger().log(Level.FINE, "close connection");
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

    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
