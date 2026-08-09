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
class MaskingKeyDecoder implements Decoder {
    private final Decoder payloadDecoder = new PayloadDecoder();


    /**
     * 客户端发送到服务器的所有帧通过一个包含在帧中的 32 位值来掩码。如果 mask
     * 位设置为 1，则该字段存在，如果 mask 位设置为 0，则该字段缺失。
     */
    @Override
    public Decoder decode(ByteBuffer byteBuffer, WebSocket request) {
        if (request.isFrameMasked()) {
            if (byteBuffer.remaining() < 4) {
                return this;
            } else {
                byte[] maskingKey = new byte[4];
                byteBuffer.get(maskingKey);
                request.setMaskingKey(maskingKey);
            }
        }
        return payloadDecoder.decode(byteBuffer, request);
    }
}
