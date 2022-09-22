package org.noear.solon.boot.tomcat.http;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.tomcat.XPluginImp;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.servlet.SolonServletHandler;

import javax.servlet.annotation.WebServlet;

//Servlet模式 注解用于JSP混合模式的搭建
@WebServlet(
        name = "solon",
        urlPatterns = {"/"}
)
public class TCHttpContextHandler extends SolonServletHandler {
    @Override
    protected void preHandle(Context ctx) {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
        }
    }
}