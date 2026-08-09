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

import java.nio.ByteBuffer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class BasicFrameDecoder implements Decoder {

    private final Decoder payloadLengthDecoder = new PayloadLengthDecoder();

    @Override
    public Decoder decode(ByteBuffer byteBuffer, WebSocket webSocket) {
        if (byteBuffer.remaining() < 2) {
            return this;
        }
        int first = byteBuffer.get();
        int second = byteBuffer.get();
        boolean mask = (second & 0x80) != 0;

        boolean fin = (first & 0x80) != 0;
        int rsv = (first & 0x70) >> 4;
        int opcode = first & 0x0f;
        webSocket.setFrameFinalFlag(fin);
        webSocket.setFrameRsv(rsv);
        webSocket.setFrameOpcode(opcode);
        webSocket.setFrameMasked(mask);
        webSocket.setPayloadLength(second & 0x7F);

        return payloadLengthDecoder.decode(byteBuffer, webSocket);
    }
}
