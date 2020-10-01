package org.noear.solon.boot.jetty.http;

import org.noear.solon.XApp;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.noear.solon.boot.jetty.XPluginImp;
import org.noear.solon.boot.jetty.XServerProp;
import org.noear.solon.core.XEventBus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JtHttpContextHandler extends AbstractHandler {
    protected boolean debug;

    public JtHttpContextHandler() {
        this.debug = XApp.cfg().isDebugMode();
    }

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            handleDo(baseRequest, request, response);

        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            XEventBus.push(ex);

            response.setStatus(500);

            if (XApp.cfg().isDebugMode()) {
                ex.printStackTrace();
            }
        }
    }

    private void handleDo(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        JtHttpContext context = new JtHttpContext(request, response);

        context.contentType("text/plain;charset=UTF-8");
        if (XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        XApp.global().tryHandle(context);

        if (context.getHandled() && context.status() != 404) {
            baseRequest.setHandled(true);
        }else{
            response.setStatus(404);
        }
    }
}
