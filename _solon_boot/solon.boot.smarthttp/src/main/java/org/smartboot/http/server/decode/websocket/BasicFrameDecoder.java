package org.smartboot.http.server.decode.websocket;

import org.smartboot.http.server.impl.WebSocketRequestImpl;

import java.nio.ByteBuffer;

/**
 * @author 三刀（zhengjunweimail@163.com）
 * @version V1.0 , 2023/2/24
 */
public class BasicFrameDecoder implements Decoder {

    private final Decoder payloadLengthDecoder = new PayloadLengthDecoder();

    @Override
    public Decoder decode(ByteBuffer byteBuffer, WebSocketRequestImpl request) {
        if (byteBuffer.remaining() < 2) {
            return this;
        }
        int first = byteBuffer.get();
        int second = byteBuffer.get();
        boolean mask = (second & 0x80) != 0;

        boolean fin = (first & 0x80) != 0;
        int rsv = (first & 0x70) >> 4;
        int opcode = first & 0x0f;
        request.setFrameFinalFlag(fin);
        request.setFrameRsv(rsv);
        request.setFrameOpcode(opcode);
        request.setFrameMasked(mask);
        request.setPayloadLength(second & 0x7F);

        return payloadLengthDecoder.decode(byteBuffer, request);
    }
}
