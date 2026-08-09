/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common.codec.websocket;


import io.github.smartboot.socket.extension.decoder.SmartDecoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public interface WebSocket {
    int WS_DEFAULT_MAX_FRAME_SIZE = (1 << 15) - 1;
    int WS_PLAY_LOAD_126 = 126;
    int WS_PLAY_LOAD_127 = 127;
    byte OPCODE_CONTINUE = 0x0;
    byte OPCODE_TEXT = 0x1;
    byte OPCODE_BINARY = 0x2;
    byte OPCODE_CLOSE = 0x8;
    byte OPCODE_PING = 0x9;
    byte OPCODE_PONG = 0xA;
    Decoder PAYLOAD_FINISH = new Decoder() {
        @Override
        public Decoder decode(ByteBuffer byteBuffer, WebSocket request) {
            return this;
        }
    };

    boolean isFrameFinalFlag();

    void setFrameFinalFlag(boolean frameFinalFlag);

    boolean isFrameMasked();

    void setFrameMasked(boolean frameMasked);

    int getFrameRsv();

    void setFrameRsv(int frameRsv);

    int getFrameOpcode();

    void setFrameOpcode(int frameOpcode);

    byte[] getPayload();

    long getPayloadLength();

    void setPayloadLength(long payloadLength);

    byte[] getMaskingKey();

    void setMaskingKey(byte[] maskingKey);

    void setPayload(byte[] payload);

    SmartDecoder getPayloadDecoder();

    void setPayloadDecoder(SmartDecoder decoder);

    public static void send(OutputStream outputStream, byte opCode, byte[] bytes) throws IOException {
        send(outputStream, opCode, bytes, 0, bytes.length);
    }

    public static void send(OutputStream outputStream, byte opCode, byte[] bytes, int offset, int len) throws IOException {
        int maxlength;
        if (len < WS_PLAY_LOAD_126) {
            maxlength = 2 + len;
        } else {
            maxlength = 4 + Math.min(WS_DEFAULT_MAX_FRAME_SIZE, len);
        }
        byte[] writBytes = new byte[maxlength];
        do {
            int payloadLength = len - offset;
            if (payloadLength > WS_DEFAULT_MAX_FRAME_SIZE) {
                payloadLength = WS_DEFAULT_MAX_FRAME_SIZE;
            }
            byte firstByte = offset + payloadLength < len ? (byte) 0x00 : (byte) 0x80;
            if (offset == 0) {
                firstByte |= opCode;
            } else {
                firstByte |= WebSocket.OPCODE_CONTINUE;
            }
            byte secondByte = payloadLength < WS_PLAY_LOAD_126 ? (byte) payloadLength : WS_PLAY_LOAD_126;
            writBytes[0] = firstByte;
            writBytes[1] = secondByte;
            if (secondByte == WS_PLAY_LOAD_126) {
                writBytes[2] = (byte) (payloadLength >> 8 & 0xff);
                writBytes[3] = (byte) (payloadLength & 0xff);
                System.arraycopy(bytes, offset, writBytes, 4, payloadLength);
            } else {
                System.arraycopy(bytes, offset, writBytes, 2, payloadLength);
            }
            outputStream.write(writBytes, 0, payloadLength < WS_PLAY_LOAD_126 ? 2 + payloadLength : 4 + payloadLength);
            offset += payloadLength;
        } while (offset < len);
    }
}
