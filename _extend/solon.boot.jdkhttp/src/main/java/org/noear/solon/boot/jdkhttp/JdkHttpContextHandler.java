package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XMonitor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JdkHttpContextHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            handleDo(exchange);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private void handleDo(HttpExchange exchange) {
        JdkHttpContext context = new JdkHttpContext(exchange);

        try {
            handleDo0(context);
        } catch (Throwable err) {
            XMonitor.sendError(context, err);
        }
    }

    private void handleDo0(JdkHttpContext context) throws IOException {

        context.contentType("text/plain;charset=UTF-8");
        if(XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        try {
            XApp.global().handle(context);
        } catch (Throwable ex) {
            XMonitor.sendError(context,ex);

            context.status(500);
            context.setHandled(true);
            context.output(XUtil.getFullStackTrace(ex));
        }

        if (context.getHandled() && context.status() != 404) {
            context.commit();
        } else {
            context.status(404);
            context.commit();
        }
    }
}
