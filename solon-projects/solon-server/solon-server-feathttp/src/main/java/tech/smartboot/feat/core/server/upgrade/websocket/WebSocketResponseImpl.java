/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.upgrade.websocket;

import tech.smartboot.feat.core.common.codec.websocket.CloseReason;
import tech.smartboot.feat.core.common.codec.websocket.WebSocket;
import tech.smartboot.feat.core.common.logging.Logger;
import tech.smartboot.feat.core.common.logging.LoggerFactory;
import tech.smartboot.feat.core.server.HttpResponse;
import tech.smartboot.feat.core.server.WebSocketResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
class WebSocketResponseImpl implements WebSocketResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketResponseImpl.class);
    private boolean closed;
    private final HttpResponse httpResponse;

    public WebSocketResponseImpl(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public void sendTextMessage(String text) {
        if (LOGGER.isInfoEnabled()) LOGGER.info("发送字符串消息: " + text);
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        try {
            WebSocket.send(httpResponse.getOutputStream(), WebSocket.OPCODE_TEXT, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendBinaryMessage(byte[] bytes) {
        if (LOGGER.isInfoEnabled()) LOGGER.info("发送二进制消息: " + Arrays.toString(bytes));
        try {
            WebSocket.send(httpResponse.getOutputStream(), WebSocket.OPCODE_BINARY, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendBinaryMessage(byte[] bytes, int offset, int length) {
        try {
            WebSocket.send(httpResponse.getOutputStream(), WebSocket.OPCODE_BINARY, bytes, offset, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pong(byte[] bytes) {
        try {
            WebSocket.send(httpResponse.getOutputStream(), WebSocket.OPCODE_PONG, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        close(CloseReason.NORMAL_CLOSURE, "");
    }

    @Override
    public void close(int code, String reason) {
        if (closed) {
            return;
        }
        closed = true;
        try {
            WebSocket.send(httpResponse.getOutputStream(), WebSocket.OPCODE_CLOSE, new CloseReason(code, reason).toBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpResponse.close();
        }
    }

    @Override
    public void ping(byte[] bytes) {
        try {
            WebSocket.send(httpResponse.getOutputStream(), WebSocket.OPCODE_PING, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void flush() {
        try {
            httpResponse.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
