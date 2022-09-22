package org.noear.solon.boot.undertow.jsp;

import io.undertow.servlet.api.ServletInfo;
import org.apache.jasper.servlet.JspServlet;
import org.noear.solon.core.handle.Context;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class JspServletEx extends JspServlet {

    public static ServletInfo createServlet(String name, String path) {
        ServletInfo servlet = new ServletInfo(name, JspServletEx.class);
        servlet.addMapping(path);
        servlet.setRequireWelcomeFileMapping(true);
        return servlet;
    }


    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if(Context.current() == null){
            return;
        }

        super.service(req, res);
    }
}
