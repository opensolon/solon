/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: RequestLineDecoder.java
 * Date: 2020-03-30
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.decode;

import org.smartboot.http.common.enums.HttpStatus;
import org.smartboot.http.common.exception.HttpException;
import org.smartboot.http.common.utils.ByteTree;
import org.smartboot.http.common.utils.Constant;
import org.smartboot.http.common.utils.StringUtils;
import org.smartboot.http.server.HttpServerConfiguration;
import org.smartboot.http.server.ServerHandler;
import org.smartboot.http.server.impl.HttpRequestProtocol;
import org.smartboot.http.server.impl.Request;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * @author 三刀
 * @version V1.0 , 2020/3/30
 */
class HttpHeaderDecoder extends AbstractDecoder {
    private static final ByteTree.EndMatcher COLON_END_MATCHER = endByte -> endByte == Constant.COLON;

    private final HeaderValueDecoder headerValueDecoder = new HeaderValueDecoder();
    private final IgnoreHeaderDecoder ignoreHeaderDecoder = new IgnoreHeaderDecoder();
    private final LfDecoder lfDecoder = new LfDecoder(this, getConfiguration());

    public HttpHeaderDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, AioSession aioSession, Request request) {
        if (request.getHeaderSize() >= 0 && request.getHeaderSize() >= getConfiguration().getHeaderLimiter()) {
            return ignoreHeaderDecoder.decode(byteBuffer, aioSession, request);
        }
        if (byteBuffer.remaining() < 2) {
            return this;
        }
        //header解码结束
        if (byteBuffer.get(byteBuffer.position()) == Constant.CR) {
            if (byteBuffer.get(byteBuffer.position() + 1) != Constant.LF) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            byteBuffer.position(byteBuffer.position() + 2);
//            return decoder.decode(byteBuffer, aioSession, request);
            return HttpRequestProtocol.BODY_READY_DECODER;
        }
        //Header name解码
        ByteTree<Function<String, ServerHandler<?, ?>>> name = StringUtils.scanByteTree(byteBuffer, COLON_END_MATCHER, getConfiguration().getHeaderNameByteTree());
        if (name == null) {
            return this;
        }
//        System.out.println("headerName: " + name);
        request.setHeaderTemp(name);
        return headerValueDecoder.decode(byteBuffer, aioSession, request);
    }

    /**
     * Value值解码
     */
    class HeaderValueDecoder implements Decoder {
        @Override
        public Decoder decode(ByteBuffer byteBuffer, AioSession aioSession, Request request) {
            ByteTree<?> value = StringUtils.scanByteTree(byteBuffer, CR_END_MATCHER, getConfiguration().getByteCache());
            if (value == null) {
                if (byteBuffer.remaining() == byteBuffer.capacity()) {
                    throw new HttpException(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
                }
                return this;
            }
//            System.out.println("value: " + value);
            request.setHeadValue(value.getStringValue());
            return lfDecoder.decode(byteBuffer, aioSession, request);
        }
    }
}
