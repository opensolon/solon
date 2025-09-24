package org.noear.solon.server.grizzly.http;

import com.sun.net.httpserver.HttpExchange;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.grizzly.integration.GyHttpPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/**
 *
 * @author noear 2025/9/24 created
 *
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
            ctx.contentType(MimeType.TEXT_PLAIN_UTF8_VALUE);
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
