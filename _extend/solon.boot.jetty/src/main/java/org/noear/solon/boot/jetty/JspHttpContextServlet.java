package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.Request;
import org.noear.solon.XApp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JspHttpContextServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        call(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        call(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        call(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        call(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        call(req, resp);
    }

    private void call(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JtHttpContext context = new JtHttpContext(request,response);
        context.contentType("text/plain;charset=UTF-8");

        try {
            XApp.global().handle(context);

            if(context.getHandled() && context.status() != 404) {
                ((Request)request).setHandled(true);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();

            if(XApp.global().prop().isDebugMode()) {
                ((Request)request).setHandled(true);
                ex.printStackTrace(response.getWriter());
                response.setStatus(500);
            }else{
                throw new ServletException(ex);
            }
        }
    }
}
