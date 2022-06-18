package org.noear.solon.boot.jetty.http;

import org.noear.solon.Solon;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.XPluginImp;
import org.noear.solon.net.servlet.SolonServletContext;
import org.noear.solon.core.event.EventBus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JtHttpContextHandler extends AbstractHandler {
    protected boolean debug;

    public JtHttpContextHandler() {
        this.debug = Solon.cfg().isDebugMode();
    }

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            handleDo(baseRequest, request, response);

        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            EventBus.push(ex);

            response.setStatus(500);
        }
    }

    private void handleDo(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        SolonServletContext ctx = new SolonServletContext(request, response);
        ctx.attrSet("signal", XPluginImp.signal());

        ctx.contentType("text/plain;charset=UTF-8");
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
        }

        Solon.app().tryHandle(ctx);

        if (ctx.getHandled() && ctx.status() >= 200) {
            baseRequest.setHandled(true);
        } else {
            response.setStatus(404);
            baseRequest.setHandled(true);
        }
    }
}
