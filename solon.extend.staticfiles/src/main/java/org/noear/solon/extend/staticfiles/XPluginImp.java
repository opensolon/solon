package org.noear.solon.extend.staticfiles;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XHandlerLink;
import org.noear.solon.core.XPlugin;

/**
 * 插件
 * */
public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if(XUtil.getResource("/static") != null) {
            XHandlerLink link = new XHandlerLink();
            link.node = new XResourceHandler("/static");
            link.nextNode = app.handlerGet();

            app.handlerSet(link);
        }
    }
}
