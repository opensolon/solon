/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: RequestLineDecoder.java
 * Date: 2020-03-30
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.decode;

import org.smartboot.http.server.HttpRequestProtocol;
import org.smartboot.http.server.Request;
import org.smartboot.http.utils.AttachKey;
import org.smartboot.http.utils.Attachment;
import org.smartboot.http.utils.FixedLengthFrameDecoder;
import org.smartboot.http.utils.SmartDecoder;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2020/3/30
 */
class HttpBodyDecoder implements Decoder {
    private final AttachKey<SmartDecoder> ATTACH_KEY_FIX_LENGTH_DECODER = AttachKey.valueOf("fixLengthDecoder");

    @Override
    public Decoder deocde(ByteBuffer byteBuffer, char[] cacheChars, AioSession aioSession, Request request) {
        Attachment attachment = aioSession.getAttachment();
        SmartDecoder smartDecoder = attachment.get(ATTACH_KEY_FIX_LENGTH_DECODER);
        if (smartDecoder == null) {
            smartDecoder = new FixedLengthFrameDecoder(request.getContentLength());
            attachment.put(ATTACH_KEY_FIX_LENGTH_DECODER, smartDecoder);
        }

        if (smartDecoder.decode(byteBuffer)) {
            request.setFormUrlencoded(new String(smartDecoder.getBuffer().array()));
            attachment.remove(ATTACH_KEY_FIX_LENGTH_DECODER);
            return HttpRequestProtocol.HTTP_FINISH_DECODER;
        } else {
            return this;
        }
    }
}
