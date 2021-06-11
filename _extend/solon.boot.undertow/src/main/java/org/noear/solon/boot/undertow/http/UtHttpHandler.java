package org.noear.solon.boot.undertow.http;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import org.noear.solon.Solon;
import org.noear.solon.extend.servlet.SolonServletContext;
import org.noear.solon.boot.undertow.XPluginImp;
import org.noear.solon.boot.undertow.XServerProp;
import org.noear.solon.core.event.EventBus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author : Yukai
 * Description : 基础handler
 **/
public class UtHttpHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        HttpServletRequest request = (HttpServletRequest) servletRequestContext.getServletRequest();
        HttpServletResponse response = (HttpServletResponse) servletRequestContext.getServletResponse();

        try {
            service(request, response);
        }finally {
            exchange.endExchange();
        }
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SolonServletContext context = new SolonServletContext(request, response);
        context.attrSet("signal", XPluginImp.signal());

        context.contentType("text/plain;charset=UTF-8");

        if (XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        Solon.global().tryHandle(context);

        if (context.getHandled() == false || context.status() == 404) {
            response.setStatus(404);
        }
    }
}
