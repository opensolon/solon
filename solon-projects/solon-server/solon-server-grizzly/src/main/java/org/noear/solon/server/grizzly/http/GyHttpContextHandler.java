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
package org.noear.solon.server.grizzly.http;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.grizzly.integration.GyHttpPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author noear
 * @since 3.6
 */
public class GyHttpContextHandler extends HttpHandler {
    static final Logger log = LoggerFactory.getLogger(GyHttpContextHandler.class);

    protected Executor executor;
    private final Handler handler;

    public GyHttpContextHandler(Handler handler) {
        this.handler = handler;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }



    @Override
    public void service(Request request, Response response) throws Exception {
        if(request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(ServerProps.request_encoding);
        }

        GyHttpContext ctx = new GyHttpContext(request, response);

        try {
            if (executor == null) {
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
        } finally {
            if (ctx.asyncStarted() == false) {

            }
        }
    }

    protected void handle0(GyHttpContext ctx) {
        try {
            handleDo(ctx);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (ctx.asyncStarted() == false) {
                ctx.innerGetResponse().finish();
            }
        }
    }

    protected void handleDo(GyHttpContext ctx) {
        try {
            if ("PRI".equals(ctx.method())) {
                ctx.innerGetResponse().setStatus(HttpStatus.NOT_IMPLEMENTED_501);
                return;
            }

            //ctx.contentType(MimeType.TEXT_PLAIN_UTF8_VALUE);
            if (ServerProps.output_meta) {
                ctx.headerSet("Solon-Server", GyHttpPlugin.solon_server_ver());
            }

            handler.handle(ctx);

            if (ctx.asyncStarted() == false) {
                ctx.innerCommit();
            }

        } catch (Throwable e) {
            log.warn(e.getMessage(), e);

            ctx.innerGetResponse().setStatus(500);
        }
    }
}
