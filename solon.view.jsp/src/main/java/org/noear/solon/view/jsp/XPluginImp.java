package org.noear.solon.view.jsp;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;
import org.noear.solon.ext.PrintUtil;

public class XPluginImp implements XPlugin {
    public static boolean output_meta = false;

    @Override
    public void start(XApp app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        if (XUtil.loadClass("javax.servlet.ServletResponse") == null) {
            PrintUtil.redln("solon:: javax.servlet.ServletResponse not exists! JspRender failed to load.");
            return;
        }

        JspRender render = JspRender.global();

        XRenderManager.register(render);
        XRenderManager.mapping(".jsp", render);
    }
}
