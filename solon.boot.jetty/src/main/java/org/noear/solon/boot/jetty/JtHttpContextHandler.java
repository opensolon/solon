package org.noear.solon.boot.jetty;

import org.noear.solon.XApp;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JtHttpContextHandler extends AbstractHandler {
    protected XApp xapp;
    protected boolean debug;

    public JtHttpContextHandler(XApp xapp) {
        this.xapp = xapp;
        this.debug = xapp.prop().argx().getInt("debug") == 1;
    }

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        JtHttpContext context = new JtHttpContext(request,response);
        context.contentType("text/plain;charset=UTF-8");

        try {
            xapp.handle(context);

            if(context.getHandled() && context.status() != 404){
                baseRequest.setHandled(true);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();

            baseRequest.setHandled(true);
            ex.printStackTrace(response.getWriter());
            response.setStatus(500);
        }
    }
}
