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

import io.github.smartboot.socket.extension.decoder.SmartDecoder;
import tech.smartboot.feat.core.common.Reset;
import tech.smartboot.feat.core.common.codec.websocket.WebSocket;
import tech.smartboot.feat.core.server.WebSocketRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
class WebSocketRequestImpl implements WebSocketRequest, WebSocket, Reset {
    private SmartDecoder payloadDecoder;
    private final ByteArrayOutputStream payload = new ByteArrayOutputStream();
    private boolean frameFinalFlag;
    private boolean frameMasked;
    private int frameRsv;
    private int frameOpcode;
    /**
     * payload长度
     */
    private long payloadLength;

    private byte[] maskingKey;

    @Override
    public void reset() {
        if (frameOpcode != WebSocket.OPCODE_CONTINUE) {
            payload.reset();
        }
    }

    public boolean isFrameFinalFlag() {
        return frameFinalFlag;
    }

    public void setFrameFinalFlag(boolean frameFinalFlag) {
        this.frameFinalFlag = frameFinalFlag;
    }

    public boolean isFrameMasked() {
        return frameMasked;
    }

    public void setFrameMasked(boolean frameMasked) {
        this.frameMasked = frameMasked;
    }

    public int getFrameRsv() {
        return frameRsv;
    }

    public void setFrameRsv(int frameRsv) {
        this.frameRsv = frameRsv;
    }

    public int getFrameOpcode() {
        return frameOpcode;
    }

    public void setFrameOpcode(int frameOpcode) {
        this.frameOpcode = frameOpcode;
    }

    public byte[] getPayload() {
        return payload.toByteArray();
    }

    public long getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(long payloadLength) {
        this.payloadLength = payloadLength;
    }

    public byte[] getMaskingKey() {
        return maskingKey;
    }

    public void setMaskingKey(byte[] maskingKey) {
        this.maskingKey = maskingKey;
    }

    public void setPayload(byte[] payload) {
        try {
            this.payload.write(payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SmartDecoder getPayloadDecoder() {
        return payloadDecoder;
    }

    @Override
    public void setPayloadDecoder(SmartDecoder payloadDecoder) {
        this.payloadDecoder = payloadDecoder;
    }
}
