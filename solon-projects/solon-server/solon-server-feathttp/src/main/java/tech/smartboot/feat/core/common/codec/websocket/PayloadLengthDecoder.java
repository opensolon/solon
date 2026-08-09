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
class PayloadLengthDecoder implements Decoder {

    private final Decoder maskingKeyDecoder = new MaskingKeyDecoder();

    @Override
    public Decoder decode(ByteBuffer byteBuffer, WebSocket request) {
        long length = request.getPayloadLength();

        if (length == WebSocket.WS_PLAY_LOAD_126) {
            if (byteBuffer.remaining() < Short.BYTES) {
                return this;
            }
            request.setPayloadLength(Short.toUnsignedInt(byteBuffer.getShort()));
        }

        if (length == WebSocket.WS_PLAY_LOAD_127) {
            if (byteBuffer.remaining() < Long.BYTES) {
                return this;
            } else {
                request.setPayloadLength(byteBuffer.getLong());
            }

        }
        return maskingKeyDecoder.decode(byteBuffer, request);
    }
}
