package org.noear.solon.boot.jlhttp;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;

import java.io.IOException;

public class JlHttpContextHandler implements HTTPServer.ContextHandler {

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

        try {
            return handleDo(request, response);
        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            EventBus.push( ex);

            response.sendHeaders(500);
            return 0;
        }
    }

    private int handleDo(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
        JlHttpContext context = new JlHttpContext(request, response);

        context.contentType("text/plain;charset=UTF-8");

        if (XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        Solon.global().tryHandle(context);

        if (context.getHandled() && context.status() != 404) {
            context.commit();

            return 0;
        } else {
            return 404;
        }
    }
}
