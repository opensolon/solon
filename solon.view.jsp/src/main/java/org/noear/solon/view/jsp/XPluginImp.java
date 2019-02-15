package org.noear.solon.view.jsp;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        JspRender render = new JspRender();

        app.renderSet(render);
    }
}
