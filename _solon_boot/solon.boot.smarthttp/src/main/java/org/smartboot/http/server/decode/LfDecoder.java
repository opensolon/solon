/*******************************************************************************
 * Copyright (c) 2017-2022, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: LfDecoder.java
 * Date: 2022-01-12
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.decode;

import org.smartboot.http.common.enums.HttpStatus;
import org.smartboot.http.common.exception.HttpException;
import org.smartboot.http.common.utils.Constant;
import org.smartboot.http.server.HttpServerConfiguration;
import org.smartboot.http.server.impl.Request;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

/**
 * @author 三刀（zhengjunweimail@163.com）
 * @version V1.0 , 2022/1/12
 */
class LfDecoder extends AbstractDecoder {
    private final AbstractDecoder nextDecoder;

    public LfDecoder(AbstractDecoder nextDecoder, HttpServerConfiguration configuration) {
        super(configuration);
        this.nextDecoder = nextDecoder;
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, AioSession aioSession, Request request) {
        if (byteBuffer.hasRemaining()) {
            if (byteBuffer.get() != Constant.LF) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            return nextDecoder.decode(byteBuffer, aioSession, request);
        }
        return this;
    }
}
