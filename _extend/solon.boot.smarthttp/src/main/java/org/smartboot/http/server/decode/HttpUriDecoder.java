/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: UriDecoder.java
 * Date: 2020-03-30
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.decode;

import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.exception.HttpException;
import org.smartboot.http.server.Request;
import org.smartboot.http.utils.Constant;
import org.smartboot.http.utils.StringUtils;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2020/3/30
 */
class HttpUriDecoder implements Decoder {
    private final HttpUriQueryDecoder uriQueryDecoder = new HttpUriQueryDecoder();
    private final HttpProtocolDecoder protocolDecoder = new HttpProtocolDecoder();

    @Override
    public Decoder deocde(ByteBuffer byteBuffer, char[] cacheChars, AioSession aioSession, Request request) {
        int length = scanURI(byteBuffer, cacheChars);
        if (length > 0) {
            String uri = StringUtils.convertToString(cacheChars, 0, length, StringUtils.String_CACHE_URL);
            request.setUri(uri);
            switch (cacheChars[length]) {
                case Constant.SP:
                    return protocolDecoder.deocde(byteBuffer, cacheChars, aioSession, request);
                case '?':
                    return uriQueryDecoder.deocde(byteBuffer, cacheChars, aioSession, request);
                default:
                    throw new HttpException(HttpStatus.BAD_REQUEST);
            }
        } else {
            return this;
        }
    }

    private int scanURI(ByteBuffer buffer, char[] cacheChars) {
        while ((cacheChars[0] = (char) (buffer.get() & 0xFF)) == Constant.SP) ;
        int i = 1;
        while (buffer.hasRemaining()) {
            cacheChars[i] = (char) (buffer.get() & 0xFF);
            if (cacheChars[i] == ' ' || cacheChars[i] == '?') {
                buffer.mark();
                return i;
            }
            i++;
        }
        buffer.reset();
        return -1;
    }
}
