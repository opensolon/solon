package org.noear.solon.boot.undertow.http;

import org.noear.solon.XApp;
import org.noear.solon.boot.undertow.XPluginImp;
import org.noear.solon.boot.undertow.XServerProp;
import org.noear.solon.core.XEventBus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Servlet模式
public class UtHttpHandlerJsp extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UtHttpContext context = new UtHttpContext(request, response);
        context.contentType("text/plain;charset=UTF-8");

        if (XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        try {
            XApp.global().handle(context);

            if (context.getHandled() == false || context.status() == 404) {
                response.setStatus(404);
            }
        } catch (Throwable ex) {
            XEventBus.push(ex);
            response.setStatus(500);

            if (XApp.cfg().isDebugMode()) {
                ex.printStackTrace(response.getWriter());
            }
        }
    }
}