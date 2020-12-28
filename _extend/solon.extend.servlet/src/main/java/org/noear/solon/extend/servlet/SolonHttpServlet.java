package org.noear.solon.extend.servlet;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SolonHttpServlet extends HttpServlet {

    protected void preHandle(Context ctx){

    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SolonServletContext ctx = new SolonServletContext(request, response);
        ctx.contentType("text/plain;charset=UTF-8");

        preHandle(ctx);

        try {
            Solon.global().handle(ctx);

            if (ctx.getHandled() == false || ctx.status() == 404) {
                response.setStatus(404);
            }
        } catch (Throwable ex) {
            EventBus.push(ex);
            response.setStatus(500);

            if (Solon.cfg().isDebugMode()) {
                ex.printStackTrace(response.getWriter());
            }
        }
    }
}
