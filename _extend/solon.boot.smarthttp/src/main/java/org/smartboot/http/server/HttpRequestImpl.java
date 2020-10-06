/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Http11Request.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.utils.PostInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author 三刀
 * @version V1.0 , 2018/8/31
 */
final class HttpRequestImpl extends AbstractRequest {
    /**
     * 空流
     */
    private static final InputStream EMPTY_INPUT_STREAM = new InputStream() {
        @Override
        public int read() {
            return -1;
        }
    };
    private InputStream inputStream;

    private final HttpResponseImpl response;

    HttpRequestImpl(Request request) {
        init(request);
        this.response = new HttpResponseImpl(this, request.getAioSession().writeBuffer());
    }

    public final HttpResponseImpl getResponse() {
        return response;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (inputStream != null) {
            return inputStream;
        }
        int contentLength = getContentLength();
        if (contentLength <= 0 || request.getFormUrlencoded() != null) {
            inputStream = EMPTY_INPUT_STREAM;
        } else {
            inputStream = new PostInputStream(request.getAioSession().getInputStream(contentLength), contentLength);
        }
        return inputStream;
    }


    public void reset() {
        getRequest().reset();
        response.reset();
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
        }
    }

}
