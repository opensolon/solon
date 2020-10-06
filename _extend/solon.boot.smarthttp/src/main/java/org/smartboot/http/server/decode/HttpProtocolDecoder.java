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
import org.smartboot.http.utils.Constant;
import org.smartboot.http.utils.StringUtils;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2020/3/30
 */
class HttpProtocolDecoder implements Decoder {

    private final HttpHeaderDecoder decoder = new HttpHeaderDecoder();

    @Override
    public Decoder deocde(ByteBuffer byteBuffer, char[] cacheChars, AioSession aioSession, Request request) {
        int length = StringUtils.scanUntilAndTrim(byteBuffer, Constant.LF, cacheChars, true);
        if (length > 0) {
            if (cacheChars[length - 1] != Constant.CR) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            String protocol = StringUtils.convertToString(cacheChars, 0, length - 1, StringUtils.String_CACHE_URL);
            request.setProtocol(protocol);
            return decoder.deocde(byteBuffer, cacheChars, aioSession, request);
        } else {
            return this;
        }

    }
}
