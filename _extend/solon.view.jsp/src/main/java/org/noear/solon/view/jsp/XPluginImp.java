package org.noear.solon.view.jsp;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.PrintUtil;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(Solon app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        if (Utils.loadClass("javax.servlet.ServletResponse") == null) {
            PrintUtil.redln("solon:: javax.servlet.ServletResponse not exists! JspRender failed to load.");
            return;
        }

        JspRender render = JspRender.global();

        Bridge.renderRegister(render);
        Bridge.renderMapping(".jsp", render);
    }
}
