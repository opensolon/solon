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


import io.github.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;
import io.github.smartboot.socket.extension.decoder.SmartDecoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
class PayloadDecoder implements Decoder {

    @Override
    public Decoder decode(ByteBuffer byteBuffer, WebSocket request) {
        SmartDecoder smartDecoder = request.getPayloadDecoder();
        if (smartDecoder != null) {
            if (smartDecoder.decode(byteBuffer)) {
                finishPayloadDecoder(smartDecoder.getBuffer(), request);
                request.setPayloadDecoder(null);
                return WebSocket.PAYLOAD_FINISH;
            } else {
                return this;
            }
        }
        if (request.getPayloadLength() > byteBuffer.capacity()) {
            request.setPayloadDecoder(new FixedLengthFrameDecoder((int) request.getPayloadLength()));
            return decode(byteBuffer, request);
        }
        if (byteBuffer.remaining() < request.getPayloadLength()) {
            return this;
        }
        finishPayloadDecoder(byteBuffer, request);
        return WebSocket.PAYLOAD_FINISH;
    }

    private void finishPayloadDecoder(ByteBuffer byteBuffer, WebSocket request) {
        byte[] bytes = new byte[(int) request.getPayloadLength()];
        if (request.isFrameMasked()) {
            unmask(byteBuffer, request.getMaskingKey(), bytes.length);
        }
        byteBuffer.get(bytes);
        request.setPayload(bytes);
    }

    private void unmask(ByteBuffer frame, byte[] maskingKey, int length) {
        int i = frame.position();
        int end = i + length;

        ByteOrder order = frame.order();

        // Remark: & 0xFF is necessary because Java will do signed expansion from
        // byte to int which we don't want.
        int intMask = ((maskingKey[0] & 0xFF) << 24)
                | ((maskingKey[1] & 0xFF) << 16)
                | ((maskingKey[2] & 0xFF) << 8)
                | (maskingKey[3] & 0xFF);

        // If the byte order of our buffers it little endian we have to bring our mask
        // into the same format, because getInt() and writeInt() will use a reversed byte order
        if (order == ByteOrder.LITTLE_ENDIAN) {
            intMask = Integer.reverseBytes(intMask);
        }

        for (; i + 3 < end; i += 4) {
            int unmasked = frame.getInt(i) ^ intMask;
            frame.putInt(i, unmasked);
        }
        int j = i;
        for (; i < end; i++) {
            frame.put(i, (byte) (frame.get(i) ^ maskingKey[(i - j) % 4]));
        }
    }
}
