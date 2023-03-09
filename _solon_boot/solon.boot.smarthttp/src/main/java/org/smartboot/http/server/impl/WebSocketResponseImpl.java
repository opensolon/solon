/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: WebSocketResponseImpl.java
 * Date: 2021-02-07
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.impl;

import org.smartboot.http.common.logging.Logger;
import org.smartboot.http.common.logging.LoggerFactory;
import org.smartboot.http.common.utils.Constant;
import org.smartboot.http.server.WebSocketResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author 三刀
 * @version V1.0 , 2018/2/3
 */
public class WebSocketResponseImpl extends AbstractResponse implements WebSocketResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketResponseImpl.class);

    public WebSocketResponseImpl(WebSocketRequestImpl webSocketRequest, Request request) {
        init(webSocketRequest, new WebSocketOutputStream(webSocketRequest, this, request));
    }

    @Override
    public void sendTextMessage(String text) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("发送字符串消息: " + text);
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        try {
            send(WebSocketRequestImpl.OPCODE_TEXT, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendBinaryMessage(byte[] bytes) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("发送二进制消息: " + Arrays.toString(bytes));
        try {
            send(WebSocketRequestImpl.OPCODE_BINARY, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendBinaryMessage(byte[] bytes, int offset, int length) {
        try {
            send(WebSocketRequestImpl.OPCODE_BINARY, bytes, offset, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flush() {
        try {
            getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(byte opCode, byte[] bytes, int offset, int len) throws IOException {
        int maxlength;
        if (len < Constant.WS_PLAY_LOAD_126) {
            maxlength = 2 + len;
        } else {
            maxlength = 4 + Constant.WS_DEFAULT_MAX_FRAME_SIZE;
        }
        byte[] writBytes = new byte[maxlength];

        while (offset < len) {
            int length = len - offset;
            if (length > Constant.WS_DEFAULT_MAX_FRAME_SIZE) {
                length = Constant.WS_DEFAULT_MAX_FRAME_SIZE;
            }
            byte firstByte = offset + length < len ? (byte) 0x00 : (byte) 0x80;
            if (offset == 0) {
                firstByte |= opCode;
            } else {
                firstByte |= WebSocketRequestImpl.OPCODE_CONTINUE;
            }
            byte secondByte = length < Constant.WS_PLAY_LOAD_126 ? (byte) length : Constant.WS_PLAY_LOAD_126;
            writBytes[0] = firstByte;
            writBytes[1] = secondByte;
            if (secondByte == Constant.WS_PLAY_LOAD_126) {
                writBytes[2] = (byte) (length >> 8 & 0xff);
                writBytes[3] = (byte) (length & 0xff);
                System.arraycopy(bytes, offset, writBytes, 4, length);
            } else {
                System.arraycopy(bytes, offset, writBytes, 2, length);
            }
            this.getOutputStream().write(writBytes, 0, length < Constant.WS_PLAY_LOAD_126 ? 2 + length : 4 + length);
            offset += length;
        }
    }
}
