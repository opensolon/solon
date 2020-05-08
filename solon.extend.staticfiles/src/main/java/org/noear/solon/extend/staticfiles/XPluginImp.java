package org.noear.solon.extend.staticfiles;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XHandlerLink;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //通过动态控制是否启用
        //
        if("0".equals(System.getProperty("org.noear.solon.extend.staticfiles.enabled"))){
            return;
        }

        if(XUtil.getResource("/static") != null) {
            XHandlerLink link = new XHandlerLink();
            link.node = new XResourceHandler("/static");
            link.nextNode = app.handlerGet();

            app.handlerSet(link);
        }
    }
}
