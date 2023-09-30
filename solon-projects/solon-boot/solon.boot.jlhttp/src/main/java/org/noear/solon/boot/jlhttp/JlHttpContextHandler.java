package org.noear.solon.boot.jlhttp;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.handle.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JlHttpContextHandler implements HTTPServer.ContextHandler {
    static final Logger log = LoggerFactory.getLogger(JlHttpContextHandler.class);

    private final Handler handler;

    public JlHttpContextHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void serve(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
        try {
            handleDo(request, response);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);

            if (!response.headersSent()) {
                response.sendHeaders(500);
            }
        }
    }

    protected void handleDo(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
        JlHttpContext ctx = new JlHttpContext(request, response);

        try {
            ctx.contentType("text/plain;charset=UTF-8");

            if (ServerProps.output_meta) {
                ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
            }

            handler.handle(ctx);

            if (ctx.innerIsAsync()) {
                //如果启用了异步?
                ctx.asyncAwait();
            } else {
                ctx.innerCommit();
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);

            if (!response.headersSent()) {
                response.sendError(500);
            }
        }
    }
}