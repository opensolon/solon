/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: RequestLineDecoder.java
 * Date: 2020-03-30
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.decode;

import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.exception.HttpException;
import org.smartboot.http.server.Request;
import org.smartboot.http.utils.CharArray;
import org.smartboot.http.utils.Constant;
import org.smartboot.http.utils.StringUtils;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2020/3/30
 */
class HttpHeaderDecoder implements Decoder {

    private final HttpHeaderEndDecoder decoder = new HttpHeaderEndDecoder();
    private final HeaderValueDecoder headerValueDecoder = new HeaderValueDecoder();

    @Override
    public Decoder deocde(ByteBuffer byteBuffer, char[] cacheChars, AioSession aioSession, Request request) {
        if (byteBuffer.remaining() < 2) {
            return this;
        }
        //header解码结束
        if (byteBuffer.get(byteBuffer.position()) == Constant.CR) {
            if (byteBuffer.get(byteBuffer.position() + 1) != Constant.LF) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            byteBuffer.position(byteBuffer.position() + 2);
            return decoder.deocde(byteBuffer, cacheChars, aioSession, request);
        }
        //Header name解码
        int length = StringUtils.scanUntilAndTrim(byteBuffer, Constant.COLON, cacheChars, true);
        if (length < 0) {
            return this;
        }
        String name = StringUtils.convertToString(cacheChars, 0, length, StringUtils.String_CACHE_HEADER_NAME);
        request.setHeaderTemp(name);
        return headerValueDecoder.deocde(byteBuffer, cacheChars, aioSession, request);
    }

    /**
     * Value值解码
     */
    class HeaderValueDecoder implements Decoder {
        @Override
        public Decoder deocde(ByteBuffer byteBuffer, char[] cacheChars, AioSession aioSession, Request request) {
            CharArray charArray = request.getHeaderValueCache();
            int startIndex = charArray.getWriteIndex();
            int length = StringUtils.scanUntilAndTrim(byteBuffer, Constant.LF, charArray.getData(), startIndex, true);
            if (length == -1) {
                return this;
            }
            charArray.setWriteIndex(startIndex + length - 1);
            request.setHeader(startIndex, length - 1);
            return HttpHeaderDecoder.this.deocde(byteBuffer, cacheChars, aioSession, request);
        }
    }
}
