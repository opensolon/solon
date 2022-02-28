package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.event.EventBus;

public class JdkHttpContextHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            handleDo(exchange);
        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            EventBus.push( ex);
        } finally {
            exchange.close();
        }
    }

    private void handleDo(HttpExchange exchange) {
        JdkHttpContext ctx = new JdkHttpContext(exchange); //这里可能会有异常

        try {
            //初始化好后，再处理；异常时，可以获取上下文
            //
            ctx.contentType("text/plain;charset=UTF-8");

            if (XServerProp.output_meta) {
                ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
            }

            Solon.global().tryHandle(ctx);

            if (ctx.getHandled() && ctx.status() >= 200) {
                ctx.commit();
            } else {
                ctx.status(404);
                ctx.commit();
            }
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }
}
