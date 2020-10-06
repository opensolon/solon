/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HandlePipeline.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.handle;

import java.io.IOException;

/**
 * @author 三刀
 * @version V1.0 , 2019/11/3
 */
public final class HandlePipeline<REQ, RSP> extends Handle<REQ, RSP> implements Pipeline<REQ, RSP> {
    /**
     * 管道尾
     */
    private Handle tailHandle;

    /**
     * 添加HttpHandle至末尾
     *
     * @param handle 尾部handle
     * @return 当前管道对象
     */
    public Pipeline<REQ, RSP> next(Handle<REQ, RSP> handle) {
        if (nextHandle == null) {
            nextHandle = tailHandle = handle;
            return this;
        }
        Handle httpHandle = tailHandle;
        while (httpHandle.nextHandle != null) {
            httpHandle = httpHandle.nextHandle;
        }
        httpHandle.nextHandle = handle;
        return this;
    }

    @Override
    public void doHandle(REQ request, RSP response) throws IOException {
        nextHandle.doHandle(request, response);
    }
}
