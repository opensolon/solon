
package org.noear.solon.boot.tomcat;


import org.noear.solon.XApp;
import org.noear.solon.boot.tomcat.context.TCHttpContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Servlet模式 注解用于JSP混合模式的搭建
@WebServlet(
        name = "YKIsBrilliant",
        urlPatterns = {"/"}
)
public class TCHttpContextHandler extends HttpServlet {
    protected XApp xapp;
    protected boolean debug;

    public TCHttpContextHandler(XApp xapp) {
        this.xapp = xapp;
        this.debug = xapp.prop().argx().getInt("debug") == 1;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        TCHttpContext context = new TCHttpContext(request, response);
        context.contentType("text/plain;charset=UTF-8");
        context.headerSet("solon.boot","tomcat 8.5/1.0.3.4");

        try {
            xapp.handle(context);

            if (context.getHandled() && context.status() != 404) {
                return;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            ex.printStackTrace(response.getWriter());
            response.setStatus(500);
        }
    }
}