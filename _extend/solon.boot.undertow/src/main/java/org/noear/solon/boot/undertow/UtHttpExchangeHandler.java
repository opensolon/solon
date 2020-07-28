package org.noear.solon.boot.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import org.noear.solon.XApp;
import org.noear.solon.core.XMonitor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author : Yukai
 * Description : 基础handler
 **/
public class UtHttpExchangeHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        HttpServletRequest request = (HttpServletRequest) servletRequestContext.getServletRequest();
        HttpServletResponse response = (HttpServletResponse) servletRequestContext.getServletResponse();

        UtHttpServletContext context = new UtHttpServletContext(request, response);
        context.contentType("text/plain;charset=UTF-8");
        context.headerSet("solon.boot", XPluginImp.solon_boot_ver());

        try {
            if (exchange.getRequestURI() != null && !exchange.getRequestURI().endsWith(".jsp")) {
                XApp.global().tryHandle(context);
            }

            if (context.getHandled() && context.status() == 404) {
                exchange.setStatusCode(404);
            }
        } catch (Throwable ex) {
            XMonitor.sendError(context, ex);

            ex.printStackTrace(response.getWriter());
            exchange.setStatusCode(500);

        } finally {
            exchange.endExchange();
        }
    }
}
