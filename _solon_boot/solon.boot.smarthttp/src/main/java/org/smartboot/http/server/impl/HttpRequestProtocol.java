/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpRequestProtocol.java
 * Date: 2021-02-07
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.impl;

import org.smartboot.http.server.HttpServerConfiguration;
import org.smartboot.http.server.decode.Decoder;
import org.smartboot.http.server.decode.HttpMethodDecoder;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2018/8/31
 */
public class HttpRequestProtocol implements Protocol<Request> {
    public static final Decoder BODY_READY_DECODER = (byteBuffer, aioSession, response) -> null;
    public static final Decoder BODY_CONTINUE_DECODER = (byteBuffer, aioSession, response) -> null;
    /**
     * websocket负载数据读取成功
     */
    private final HttpMethodDecoder httpMethodDecoder;

    public HttpRequestProtocol(HttpServerConfiguration configuration) {
        httpMethodDecoder = new HttpMethodDecoder(configuration);
    }

    @Override
    public Request decode(ByteBuffer buffer, AioSession session) {
        if (buffer.remaining() == 0) {
            return null;
        }
        RequestAttachment attachment = session.getAttachment();

        Request request = attachment.getRequest();
        Decoder decodeChain = attachment.getDecoder();
        if (decodeChain == null) {
            decodeChain = httpMethodDecoder;
        }
        // 数据还未就绪，继续读
        if (decodeChain == BODY_CONTINUE_DECODER) {
            attachment.setDecoder(BODY_READY_DECODER);
            return null;
        } else if (decodeChain == BODY_READY_DECODER) {
            return request;
        }

        decodeChain = decodeChain.decode(buffer, session, request);
        attachment.setDecoder(decodeChain);
        if (decodeChain == BODY_READY_DECODER) {
            return request;
        }
        if (buffer.remaining() == buffer.capacity()) {
            throw new RuntimeException("buffer is too small when decode " + decodeChain.getClass().getName() + " ," + request);
        }
        return null;
    }
}

