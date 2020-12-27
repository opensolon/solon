package org.noear.solon.boot.servlet;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class ServletHandler extends HttpServlet {

    protected void preHandle(){

    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = new ServletContext(request, response);
        context.contentType("text/plain;charset=UTF-8");

        preHandle();

        try {
            Solon.global().handle(context);

            if (context.getHandled() == false || context.status() == 404) {
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
