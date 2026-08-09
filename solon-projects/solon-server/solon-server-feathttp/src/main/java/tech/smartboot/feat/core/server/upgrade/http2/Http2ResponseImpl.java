/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.upgrade.http2;

import tech.smartboot.feat.core.common.Cookie;
import tech.smartboot.feat.core.server.impl.AbstractResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class Http2ResponseImpl extends AbstractResponse {
    private List<Cookie> cookies = Collections.emptyList();

    public Http2ResponseImpl(int streamId, Http2Endpoint httpRequest, boolean push) {
        outputStream = new Http2OutputStream(streamId, httpRequest, this, push);
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    @Override
    public void addCookie(Cookie cookie) {
        super.addCookie(cookie);
        List<Cookie> emptyList = Collections.emptyList();
        if (cookies == emptyList) {
            cookies = new ArrayList<>();
        }
        cookies.add(cookie);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closed = true;
        }
    }
}
