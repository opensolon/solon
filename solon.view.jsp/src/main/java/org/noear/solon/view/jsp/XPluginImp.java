package org.noear.solon.view.jsp;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if(XUtil.loadClass("javax/servlet/ServletResponse")==null){
            return;
        }

        JspRender render = new JspRender();

        XRenderManager.register(render);
    }
}
