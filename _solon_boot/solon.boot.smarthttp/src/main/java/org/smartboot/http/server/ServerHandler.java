/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: ServerHandler.java
 * Date: 2021-07-25
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.common.Handler;
import org.smartboot.http.server.impl.Request;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author 三刀（zhengjunweimail@163.com）
 * @version V1.0 , 2021/7/25
 */
public interface ServerHandler<REQ, RSP> extends Handler<Request> {

    /**
     * 执行当前处理器逻辑。
     * <p>
     * 当前handle运行完后若还有后续的处理器，需要调用doNext
     * </p>
     *
     * @param request
     * @param response
     * @throws IOException
     */
    default void handle(REQ request, RSP response) throws IOException {
    }

    default void handle(REQ request, RSP response, CompletableFuture<Object> completableFuture) throws IOException {
        try {
            handle(request, response);
        } finally {
            completableFuture.complete(null);
        }
    }
}
