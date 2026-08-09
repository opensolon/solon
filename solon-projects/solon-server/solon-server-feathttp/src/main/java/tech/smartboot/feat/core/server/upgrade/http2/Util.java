/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.upgrade.http2;

import tech.smartboot.feat.core.common.HeaderValue;
import tech.smartboot.feat.core.common.codec.h2.hpack.Encoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0 6/10/25
 */
class Util {
    static List<ByteBuffer> HPackEncoder(Encoder encoder, Map<String, HeaderValue> headers) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        List<ByteBuffer> buffers = new ArrayList<>();

        for (Map.Entry<String, HeaderValue> entry : headers.entrySet()) {
            if (entry.getKey().charAt(0) != ':') {
                continue;
            }
            HeaderValue headerValue = entry.getValue();
            while (headerValue != null) {
                //todo: System.out.println("encode: " + entry.getKey() + ":" + entry.getValue().getValue());
                encoder.header(entry.getKey().toLowerCase(), headerValue.getValue());
                while (!encoder.encode(buffer)) {
                    buffer.flip();
                    buffers.add(buffer);
                    buffer = ByteBuffer.allocate(1024);
                }
                headerValue = headerValue.getNextValue();
            }
        }

        for (Map.Entry<String, HeaderValue> entry : headers.entrySet()) {
            if (entry.getKey().charAt(0) == ':') {
                continue;
            }

            HeaderValue headerValue = entry.getValue();
            while (headerValue != null) {
                System.out.println("encode: " + entry.getKey() + ":" + headerValue.getValue());
                encoder.header(entry.getKey().toLowerCase(), headerValue.getValue());
                while (!encoder.encode(buffer)) {
                    buffer.flip();
                    buffers.add(buffer);
                    buffer = ByteBuffer.allocate(1024);
                }
                headerValue = headerValue.getNextValue();
            }
        }
        buffer.flip();
        if (buffer.hasRemaining()) {
            buffers.add(buffer);
        }
        return buffers;
    }
}
