package org.noear.solon.boot.undertow;

import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import org.noear.solon.XApp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Servlet模式
public class UnderServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServerExchange exchange = ((HttpServletRequestImpl) req).getExchange();
        UtHttpServletContext context = new UtHttpServletContext(req, resp,exchange);
        context.contentType("text/plain;charset=UTF-8");
        if(XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        try {
            XApp.global().handle(context);

            if(context.getHandled() && context.status() != 404){
              return;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();

            if( XApp.cfg().isDebugMode()) {
                ex.printStackTrace(resp.getWriter());
                resp.setStatus(500);
                return;
            }else{
                throw new ServletException(ex);
            }
        }

    }
}