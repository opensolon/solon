/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpServerHandle.java
 * Date: 2021-02-07
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.common.enums.HeaderNameEnum;
import org.smartboot.http.common.enums.HeaderValueEnum;
import org.smartboot.http.common.enums.HttpMethodEnum;
import org.smartboot.http.common.enums.HttpStatus;
import org.smartboot.http.common.exception.HttpException;
import org.smartboot.http.common.utils.Constant;
import org.smartboot.http.common.utils.FixedLengthFrameDecoder;
import org.smartboot.http.common.utils.SmartDecoder;
import org.smartboot.http.common.utils.StringUtils;
import org.smartboot.http.server.impl.Request;
import org.smartboot.http.server.impl.WebSocketResponseImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Http消息处理器
 *
 * @author 三刀
 * @version V1.0 , 2018/2/6
 */
public abstract class Http2ServerHandler implements ServerHandler<HttpRequest, HttpResponse> {
    private final Map<Request, SmartDecoder> bodyDecoderMap = new ConcurrentHashMap<>();

    @Override
    public void onHeaderComplete(Request request) throws IOException {
        String htt2Settings=request.getHeader(HeaderNameEnum.HTTP2_SETTINGS.getName());
        WebSocketResponseImpl response = request.newWebsocketRequest().getResponse();
        response.setHttpStatus(HttpStatus.SWITCHING_PROTOCOLS);
        response.setHeader(HeaderNameEnum.UPGRADE.getName(), HeaderValueEnum.H2C.getName());
        response.setHeader(HeaderNameEnum.CONNECTION.getName(), HeaderValueEnum.UPGRADE.getName());
        OutputStream outputStream = response.getOutputStream();
        outputStream.flush();
    }

    @Override
    public boolean onBodyStream(ByteBuffer buffer, Request request) {
        if (HttpMethodEnum.GET.getMethod().equals(request.getMethod())) {
            return true;
        }
        //Post请求
        if (HttpMethodEnum.POST.getMethod().equals(request.getMethod())
                && StringUtils.startsWith(request.getContentType(), HeaderValueEnum.X_WWW_FORM_URLENCODED.getName())) {
            int postLength = request.getContentLength();
            if (postLength > Constant.maxPostSize) {
                throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE);
            } else if (postLength < 0) {
                throw new HttpException(HttpStatus.LENGTH_REQUIRED);
            }
            SmartDecoder smartDecoder = bodyDecoderMap.computeIfAbsent(request, req -> new FixedLengthFrameDecoder(req.getContentLength()));

            if (smartDecoder.decode(buffer)) {
                bodyDecoderMap.remove(request);
                request.setFormUrlencoded(new String(smartDecoder.getBuffer().array()));
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


    /**
     * 若子类重写 onClose 则必须调用 super.onClose();释放内存
     */
    @Override
    public void onClose(Request request) {
        bodyDecoderMap.remove(request);
    }

}
