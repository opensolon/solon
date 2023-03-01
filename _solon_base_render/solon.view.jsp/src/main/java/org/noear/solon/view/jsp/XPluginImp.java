package org.noear.solon.view.jsp;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;

public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        if (ClassUtil.loadClass("javax.servlet.ServletResponse") == null) {
            LogUtil.global().warn("View: javax.servlet.ServletResponse not exists! JspRender failed to load.");
            return;
        }

        JspRender render = JspRender.global();

        RenderManager.register(render);
        RenderManager.mapping(".jsp", render);
    }
}
