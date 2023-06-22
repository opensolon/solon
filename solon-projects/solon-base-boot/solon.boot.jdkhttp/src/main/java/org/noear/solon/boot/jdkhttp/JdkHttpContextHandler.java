package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Handler;

import java.io.IOException;

public class JdkHttpContextHandler implements HttpHandler {
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
            EventBus.pushTry(e);
        } finally {
            exchange.close();
        }
    }

    protected void handleDo(HttpExchange exchange) throws IOException {
        JdkHttpContext ctx = new JdkHttpContext(exchange); //这里可能会有异常

        try {
            //初始化好后，再处理；异常时，可以获取上下文
            //
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
            EventBus.pushTry(e);

            exchange.sendResponseHeaders(500, -1);
        }
    }
}
