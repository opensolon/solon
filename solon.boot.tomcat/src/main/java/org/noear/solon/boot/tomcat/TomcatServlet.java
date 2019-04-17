
package org.noear.solon.boot.tomcat;


import org.noear.solon.XApp;
import org.noear.solon.boot.tomcat.context.TCHttpContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Servlet模式
public class TomcatServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        XApp xapp = XApp.global();
        TCHttpContext context = new TCHttpContext(request, response);
        context.contentType("text/plain;charset=UTF-8");

        try {
            xapp.handle(context);

            if(context.getHandled() && context.status() != 404) {
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            if(xapp.prop().argx().getInt("debug") == 1) {
                ex.printStackTrace(response.getWriter());
                response.setStatus(500);
                return;
            }else{
                throw new ServletException(ex);
            }
        }
    }
}