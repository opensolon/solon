package org.noear.solon.boot.jlhttp;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;

import java.io.IOException;

public class JlHttpContextHandler implements HTTPServer.ContextHandler {
    protected XApp xapp;
    protected boolean debug;

    public JlHttpContextHandler(XApp xapp) {
        this.xapp = xapp;
        this.debug = xapp.prop().argx().getInt("debug") == 1;
    }

    @Override
    public int serve(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
        /*
         *
         * jlhttp 流程
         *
         * 1.处理，并暂存结果
         * 2.输出头
         * 3.输出内容
         *
         * */

        JlHttpContext context = new JlHttpContext(request, response);
        context.contentType("text/plain;charset=UTF-8");
        context.headerSet("solon.boot",XPluginImp.solon_boot_ver());

        try {
            xapp.handle(context);
        } catch (Throwable ex) {
            ex.printStackTrace();

            context.status(500);
            context.setHandled(true);
            context.output(XUtil.getFullStackTrace(ex));
        }

        if (context.getHandled() && context.status() != 404) {
            if (!response.headersSent()) {
                response.sendHeaders(context.status());
            }

            context.commit();

            return 0;
        } else {
            return 404;
        }
    }
}
