
package org.noear.solon.boot.tomcat;


import org.noear.solon.XApp;

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

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            TCHttpContext context = new TCHttpContext(request, response);
            context.contentType("text/plain;charset=UTF-8");
            context.headerSet("solon.boot",XPluginImp.solon_boot_ver());

            XApp.global().tryHandle(context);

            if (context.getHandled() && context.status() != 404) {
                return;
            }

        } catch (Throwable ex) {
            XMonitor.sendError(null, ex);
            response.setStatus(500);
        }
    }
}