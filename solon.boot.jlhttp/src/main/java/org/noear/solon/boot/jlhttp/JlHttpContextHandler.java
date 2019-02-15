package org.noear.solon.boot.jlhttp;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;

import java.io.IOException;

public class JlHttpContextHandler implements HTTPServer.ContextHandler {
    protected XApp xapp;
    protected boolean debug;

    public JlHttpContextHandler(boolean debug, XApp xapp) {
        this.xapp = xapp;
        this.debug = debug;
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

        try {
            xapp.handle(context);
        } catch (Exception ex) {
            ex.printStackTrace();

            context.status(500);
            context.setHandled(true);

            if (this.debug) {
                context.output(XUtil.getFullStackTrace(ex));
            } else {
                throw new IOException(ex);
            }
        }

        if (context.getHandled() && context.status() != 404) {
            if (!response.headersSent()) {
                response.sendHeaders(context.status());
            }

            context.close();

            return 0;
        } else {
            return 404;
        }
    }
}
