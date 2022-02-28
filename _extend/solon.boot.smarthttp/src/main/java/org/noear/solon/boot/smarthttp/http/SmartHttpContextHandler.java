package org.noear.solon.boot.smarthttp.http;

import org.noear.solon.Solon;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.smarthttp.XPluginImp;
import org.noear.solon.core.event.EventBus;
import org.smartboot.http.common.enums.HttpStatus;
import org.smartboot.http.server.HttpRequest;
import org.smartboot.http.server.HttpResponse;
import org.smartboot.http.server.HttpServerHandler;

import java.io.IOException;

public class SmartHttpContextHandler extends HttpServerHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
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
            SmartHttpContext ctx = new SmartHttpContext(request, response);

            ctx.contentType("text/plain;charset=UTF-8");
            if (ServerProps.output_meta) {
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
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
