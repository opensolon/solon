package org.noear.solon.boot.jetty.http;

import org.noear.solon.boot.jetty.XPluginImp;
import org.noear.solon.boot.jetty.XServerProp;
import org.noear.solon.extend.servlet.SolonHttpServlet;
import org.noear.solon.core.handle.Context;

public class JtHttpContextHttpServlet extends SolonHttpServlet {
    @Override
    protected void preHandle(Context ctx) {
        if (XServerProp.output_meta) {
            ctx.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }
    }
}
