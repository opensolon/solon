/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.web.vertx;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author noear
 * @since 2.9
 */
public class VxWebHandler implements VxHandler {
    static final Logger log = LoggerFactory.getLogger(VxWebHandler.class);

    private @Nullable Executor executor;
    private @Nullable Handler handler;

    /**
     * 预处理
     * */
    protected void preHandle(Context ctx) throws IOException {

    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void handle(HttpServerRequest request) {
        HttpServerResponse response = request.response();

        try {
            if ("GET".equals(request.method().name())) {
                handleDo(request, null, false);
            } else {
                request.bodyHandler(body -> {
                    handleDo(request, body, true);
                });
            }
        } catch (Throwable ex) {
            //异外情况
            log.warn(ex.getMessage(), ex);

            if (response.ended() == false) {
                response.setStatusCode(500);
                response.end();
            }
        }
    }

    private void handleDo(HttpServerRequest request, Buffer requestBody, boolean disPool) {
        VxWebContext ctx = new VxWebContext(request, requestBody);

        if (executor == null || disPool) {
            handle0(ctx);
        } else {
            try {
                executor.execute(() -> {
                    handle0(ctx);
                });
            } catch (RejectedExecutionException e) {
                handle0(ctx);
            }
        }
    }

    private void handle0(VxWebContext ctx) {
        try {
            //ctx.contentType(MimeType.TEXT_PLAIN_UTF8_VALUE);

            preHandle(ctx);

            if (handler == null) {
                Solon.app().tryHandle(ctx);
            } else {
                handler.handle(ctx);
            }

            if (ctx.asyncStarted() == false) {
                ctx.innerCommit();
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);

            ctx.innerGetResponse().setStatusCode(500);
            ctx.innerGetResponse().end();
        }
    }
}
