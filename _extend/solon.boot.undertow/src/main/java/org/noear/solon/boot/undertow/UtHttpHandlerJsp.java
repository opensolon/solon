package org.noear.solon.boot.undertow;

import org.noear.solon.XApp;
import org.noear.solon.core.XMonitor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Servlet模式
public class UtHttpHandlerJsp extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UtHttpContext context = new UtHttpContext(req, resp);
        context.contentType("text/plain;charset=UTF-8");

        if (XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        try {
            XApp.global().handle(context);

            if (context.getHandled() == false || context.status() == 404) {
                resp.setStatus(404);
            }
        } catch (Throwable ex) {
            XMonitor.sendError(context, ex);
            resp.setStatus(500);

            if (XApp.cfg().isDebugMode()) {
                ex.printStackTrace(resp.getWriter());
            }
        }
    }
}