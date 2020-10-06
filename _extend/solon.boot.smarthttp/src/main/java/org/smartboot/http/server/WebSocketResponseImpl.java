/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Http11Response.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.WebSocketResponse;
import org.smartboot.http.logging.RunLogger;
import org.smartboot.http.utils.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * @author 三刀
 * @version V1.0 , 2018/2/3
 */
public class WebSocketResponseImpl extends AbstractResponse implements WebSocketResponse {

    public WebSocketResponseImpl(WebSocketRequestImpl request, OutputStream outputStream) {
        init(request, new WebSocketOutputStream(request, this, outputStream));
    }

    @Override
    public void sendTextMessage(String text) {
        RunLogger.getLogger().log(Level.FINEST, "发送字符串消息:" + text);
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        try {
            send(WebSocketRequestImpl.OPCODE_TEXT, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendBinaryMessage(byte[] bytes) {
        RunLogger.getLogger().log(Level.FINEST, "发送二进制消息:" + bytes);
        try {
            send(WebSocketRequestImpl.OPCODE_BINARY, bytes);
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

    private void send(byte opCode, byte[] bytes) throws IOException {
        int maxlength;
        if (bytes.length < Constant.WS_PLAY_LOAD_126) {
            maxlength = 2 + bytes.length;
        } else if (bytes.length < Constant.WS_DEFAULT_MAX_FRAME_SIZE) {
            maxlength = 4 + bytes.length;
        } else {
            maxlength = 4 + Constant.WS_DEFAULT_MAX_FRAME_SIZE;
        }
        byte[] writBytes = new byte[maxlength];
        int offset = 0;

        while (offset < bytes.length) {
            int length = bytes.length - offset;
            if (length > Constant.WS_DEFAULT_MAX_FRAME_SIZE) {
                length = Constant.WS_DEFAULT_MAX_FRAME_SIZE;
            }
            byte firstByte = offset + length < bytes.length ? (byte) 0x00 : (byte) 0x80;
            if (offset == 0) {
                firstByte |= opCode;
            } else {
                firstByte |= WebSocketRequestImpl.OPCODE_CONT;
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
