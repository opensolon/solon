package org.noear.solon.boot.undertow.http;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.web.servlet.SolonServletHandler;
import org.noear.solon.boot.undertow.XPluginImp;
import org.noear.solon.core.handle.Context;

//Servlet模式
public class UtHandlerHandler extends SolonServletHandler {
    @Override
    protected void preHandle(Context ctx) {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
        }
    }
}