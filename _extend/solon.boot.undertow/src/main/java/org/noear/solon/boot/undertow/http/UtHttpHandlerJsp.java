package org.noear.solon.boot.undertow.http;

import org.noear.solon.extend.servlet.SolonHttpServlet;
import org.noear.solon.boot.undertow.XPluginImp;
import org.noear.solon.boot.undertow.XServerProp;
import org.noear.solon.core.handle.Context;

//Servlet模式
public class UtHttpHandlerJsp extends SolonHttpServlet {
    @Override
    protected void preHandle(Context ctx) {
        if (XServerProp.output_meta) {
            ctx.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }
    }
}