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

import tech.smartboot.feat.core.server.HttpRequest;
import tech.smartboot.feat.core.server.HttpResponse;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public abstract class Upgrade {
    protected HttpEndpoint request;

    void setRequest(HttpEndpoint request) {
        this.request = request;
    }

    public abstract void init(HttpRequest request, HttpResponse response) throws IOException;

    public abstract void onBodyStream(ByteBuffer buffer);

    /**
     * 在客户端关闭连接时调用
     */
    public void destroy() {
    }
}
