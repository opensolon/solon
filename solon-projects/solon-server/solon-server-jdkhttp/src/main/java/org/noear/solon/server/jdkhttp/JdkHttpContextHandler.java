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
package org.noear.solon.server.jdkhttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.jdkhttp.integration.JdkHttpPlugin;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.util.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JdkHttpContextHandler implements HttpHandler {
    static final Logger log = LoggerFactory.getLogger(JdkHttpContextHandler.class);

    private final Handler handler;

    public JdkHttpContextHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            handleDo(exchange);
        } catch (Throwable e) {
            //context 初始化时，可能会出错
            //
            log.warn(e.getMessage(), e);
        } finally {
            exchange.close();
        }
    }

    protected void handleDo(HttpExchange exchange) throws IOException {
        JdkHttpContext ctx = new JdkHttpContext(exchange); //这里可能会有异常

        try {
            //初始化好后，再处理；异常时，可以获取上下文
            //
            ctx.contentType(MimeType.TEXT_PLAIN_UTF8_VALUE);

            if (ServerProps.output_meta) {
                ctx.headerSet("Solon-Server", JdkHttpPlugin.solon_server_ver());
            }

            handler.handle(ctx);

            if (ctx.asyncStarted()) {
                //如果启用了异步?
                ctx.asyncAwait();
            } else {
                ctx.innerCommit();
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);

            exchange.sendResponseHeaders(500, -1);
        }
    }
}
