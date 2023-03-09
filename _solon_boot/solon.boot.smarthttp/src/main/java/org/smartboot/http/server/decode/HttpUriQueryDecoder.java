/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: RequestLineDecoder.java
 * Date: 2020-03-30
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.decode;

import org.smartboot.http.common.utils.Constant;
import org.smartboot.http.common.utils.StringUtils;
import org.smartboot.http.server.HttpServerConfiguration;
import org.smartboot.http.server.impl.Request;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2020/3/30
 */
class HttpUriQueryDecoder extends AbstractDecoder {

    private final HttpProtocolDecoder decoder = new HttpProtocolDecoder(getConfiguration());

    public HttpUriQueryDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, AioSession aioSession, Request request) {
        int length = scanUriQuery(byteBuffer);
        if (length >= 0) {
            String query = StringUtils.convertToString(byteBuffer, byteBuffer.position() - 1 - length, length);
            request.setQueryString(query);
            return decoder.decode(byteBuffer, aioSession, request);
        } else {
            return this;
        }

    }

    private int scanUriQuery(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        int i = 0;
        buffer.mark();
        while (buffer.hasRemaining()) {
            if (buffer.get() == Constant.SP) {
                return i;
            }
            i++;
        }
        buffer.reset();
        return -1;
    }
}
