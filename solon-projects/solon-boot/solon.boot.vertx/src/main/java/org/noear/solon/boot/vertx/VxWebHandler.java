/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.boot.vertx;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.boot.ServerProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author noear
 * @since 2.9
 */
public class VxWebHandler implements Handler<HttpServerRequest> {
    static final Logger log = LoggerFactory.getLogger(VxWebHandler.class);

    protected Executor executor;
    private final org.noear.solon.core.handle.Handler handler;

    public VxWebHandler(org.noear.solon.core.handle.Handler handler) {
        this.handler = handler;
    }

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
            response.setStatusCode(500);
            response.end();
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
            ctx.contentType("text/plain;charset=UTF-8");
            if (ServerProps.output_meta) {
                ctx.headerSet("Solon-Boot", XPluginImpl.solon_boot_ver());
            }

            handler.handle(ctx);

            if (ctx.innerIsAsync() == false) {
                ctx.innerCommit();
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);

            ctx.innerGetResponse().setStatusCode(500);
            ctx.innerGetResponse().end();
        }
    }
}
