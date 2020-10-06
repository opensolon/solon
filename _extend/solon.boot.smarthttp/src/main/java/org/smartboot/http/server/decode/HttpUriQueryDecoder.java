/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: RequestLineDecoder.java
 * Date: 2020-03-30
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.decode;

import org.smartboot.http.server.Request;
import org.smartboot.http.utils.Constant;
import org.smartboot.http.utils.StringUtils;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2020/3/30
 */
class HttpUriQueryDecoder implements Decoder {

    private final HttpProtocolDecoder decoder = new HttpProtocolDecoder();

    @Override
    public Decoder deocde(ByteBuffer byteBuffer, char[] cacheChars, AioSession aioSession, Request request) {
        int length = StringUtils.scanUntilAndTrim(byteBuffer, Constant.SP, cacheChars, false);
        if (length > 0) {
            String query = StringUtils.convertToString(cacheChars, 0, length, StringUtils.String_CACHE_URL);
            request.setQueryString(query);
            return decoder.deocde(byteBuffer, cacheChars, aioSession, request);
        } else {
            return this;
        }

    }
}
