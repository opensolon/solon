package org.smartboot.http.server.decode.websocket;

import org.smartboot.http.server.impl.WebSocketRequestImpl;

import java.nio.ByteBuffer;

/**
 * @author 三刀（zhengjunweimail@163.com）
 * @version V1.0 , 2023/2/24
 */
class MaskingKeyDecoder implements Decoder {
    private final Decoder payloadDecoder = new PayloadDecoder();


    /**
     * 客户端发送到服务器的所有帧通过一个包含在帧中的 32 位值来掩码。如果 mask
     * 位设置为 1，则该字段存在，如果 mask 位设置为 0，则该字段缺失。
     */
    @Override
    public Decoder decode(ByteBuffer byteBuffer, WebSocketRequestImpl request) {
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
