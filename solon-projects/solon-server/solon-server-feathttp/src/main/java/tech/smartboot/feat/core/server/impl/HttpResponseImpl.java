/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.impl;

import tech.smartboot.feat.core.common.HttpProtocol;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
class HttpResponseImpl extends AbstractResponse {
    private final HttpEndpoint request;

    public HttpResponseImpl(HttpEndpoint request) {
        this.outputStream = new HttpOutputStream(request, this);
        this.request = request;
    }

    @Override
    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        if (outputStream.isCommitted()) {
            throw new IllegalStateException();
        }
        if (request.getProtocol() == HttpProtocol.HTTP_10) {
            throw new IllegalStateException("HTTP/1.0 request");
        } else if (request.getProtocol() == HttpProtocol.HTTP_11 && !outputStream.isChunkedSupport()) {
            throw new IllegalStateException("unSupport trailer");
        }
        outputStream.setTrailerFields(supplier);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        try {
            if (outputStream != null && !outputStream.isClosed()) {
                outputStream.close();
            }
        } catch (IOException ignored) {
        } finally {
            request.getAioSession().close();
        }
        closed = true;
    }

}
