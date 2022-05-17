package org.noear.solon.view.jsp;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.PrintUtil;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        if (Utils.loadClass("javax.servlet.ServletResponse") == null) {
            PrintUtil.warn("solon: javax.servlet.ServletResponse not exists! JspRender failed to load.");
            return;
        }

        JspRender render = JspRender.global();

        RenderManager.register(render);
        RenderManager.mapping(".jsp", render);
    }
}
