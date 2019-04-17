package org.noear.solon.boot.undertow;


import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.handlers.ServletRequestContext;
import org.noear.solon.XApp;
import org.noear.solon.boot.undertow.context.UtHttpServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author : Yukai
 * Description : 基础handler
 **/
public class UtHttpExchangeHandler implements HttpHandler {
    protected XApp xapp;
    protected boolean debug;

    public UtHttpExchangeHandler(boolean debug, XApp xapp) {
        this.xapp = xapp;
        this.debug = debug;
    }


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        // 对.jsp结尾页面不进行分发操作
        // 自动添加带有SessionId 的Cookie!!!
      /*  SessionManager sessionManager = exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
        SessionConfig sessionConifg = exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
        Session session = sessionManager.getSession(exchange, sessionConifg);
        if (session == null) {
            sessionManager.createSession(exchange, sessionConifg);
        }*/

        //gain ServletRequestContext as the way the Class `ServletHandler` does
        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        HttpServletRequest request = (HttpServletRequest) servletRequestContext.getServletRequest();
        HttpServletResponse response = (HttpServletResponse) servletRequestContext.getServletResponse();
        //UtHttpExchangeContext代表 对XNIO友好的上下文, UtHttpServletContext 依旧采用老版本IO技术(阻塞式)
        // UtHttpExchangeContext context = new UtHttpExchangeContext(request, response, exchange);
        UtHttpServletContext context = new UtHttpServletContext(request, response, exchange);
        context.contentType("text/plain;charset=UTF-8");

        try {
            if (exchange.getRequestURI() != null && !exchange.getRequestURI().endsWith(".jsp"))
                xapp.handle(context);

            if (context.getHandled() && context.status() != 404) {
                //end the handler
                exchange.endExchange();
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            if (this.debug) {
                exchange.endExchange();
                ex.printStackTrace(response.getWriter());
                exchange.setResponseCode(500);
                //exchange.setStatusCode(500);
            } else {
                throw new ServletException(ex);
            }
        }
        // The root handler returns normally without completing the exchange
        exchange.endExchange();
    }

}
