package org.noear.solon.view.jsp;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        if (ClassUtil.loadClass("jakarta.servlet.ServletResponse") == null) {
            LogUtil.global().warn("View: jakarta.servlet.ServletResponse not exists! JspRender failed to load.");
            return;
        }

        JspRender render = JspRender.global();

        RenderManager.register(render);
        RenderManager.mapping(".jsp", render);
    }
}
