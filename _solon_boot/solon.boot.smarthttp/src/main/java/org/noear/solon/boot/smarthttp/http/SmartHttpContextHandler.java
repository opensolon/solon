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
        try {
            if ("PRI".equals(request.getMethod())) {
                response.setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
                return;
            }

            SmartHttpContext ctx = new SmartHttpContext(request, response);

            ctx.contentType("text/plain;charset=UTF-8");
            if (ServerProps.output_meta) {
                ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
            }

            Solon.app().tryHandle(ctx);

            if (ctx.getHandled() || ctx.status() >= 200) {
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
