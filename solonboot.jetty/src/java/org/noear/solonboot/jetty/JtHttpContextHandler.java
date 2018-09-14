package org.noear.solonboot.jetty;

import org.noear.solonboot.XApp;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JtHttpContextHandler extends AbstractHandler {
    protected XApp xapp;
    protected boolean debug;

    public JtHttpContextHandler(boolean debug, XApp xapp) {
        this.xapp = xapp;
        this.debug = debug;
    }

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        JtHttpContext context = new JtHttpContext(request,response);
        context.contentType("text/plain;charset=UTF-8");

        try {
            xapp.handle(context);

            if(context.getHandled()){
                baseRequest.setHandled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            if(this.debug) {
                baseRequest.setHandled(true);
                ex.printStackTrace(response.getWriter());
                response.setStatus(500);
            }else{
                throw new ServletException(ex);
            }
        }
    }
}
