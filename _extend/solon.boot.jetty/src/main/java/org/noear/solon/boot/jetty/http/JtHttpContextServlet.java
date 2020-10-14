package org.noear.solon.boot.jetty.http;

import org.eclipse.jetty.server.Request;
import org.noear.solon.XApp;
import org.noear.solon.core.XEventBus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JtHttpContextServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        call(req, resp);
    }

    private void call(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JtHttpContext context = new JtHttpContext(request, response);
        context.contentType("text/plain;charset=UTF-8");

        try {
            XApp.global().handle(context);

            if (context.getHandled() && context.status() != 404) {
                ((Request) request).setHandled(true);
            } else {
                response.setStatus(404);
                ((Request) request).setHandled(true);
            }

        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            XEventBus.push(ex);

            response.setStatus(500);
            ((Request) request).setHandled(true);

            if (XApp.cfg().isDebugMode()) {
                ex.printStackTrace();
            }
        }
    }
}
