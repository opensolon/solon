package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;

import java.io.IOException;

public class JdkHttpContextHandler implements HttpHandler {
    private XApp xapp;
    public JdkHttpContextHandler(XApp xapp){
        this.xapp = xapp;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        /*
         *
         * lhttp 流程
         *
         * 1.处理，并暂存结果
         * 2.输出头
         * 3.输出内容
         *
         * */

        JdkHttpContext context = new JdkHttpContext(exchange);
        context.contentType("text/plain;charset=UTF-8");
        if(XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        try {
            xapp.handle(context);
        } catch (Throwable ex) {
            ex.printStackTrace();

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
