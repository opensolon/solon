package org.noear.solon.extend.staticfiles;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XHandlerLink;
import org.noear.solon.core.XMap;
import org.noear.solon.core.XPlugin;

import java.util.Properties;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //通过动态控制是否启用
        //
        if ("0".equals(XApp.cfg().get("org.noear.solon.extend.staticfiles.enabled"))) {
            return;
        }

        if (XUtil.getResource("/static") != null) {
            //1.加载自定义的mime
            //
            XMap mimeTypes = app.prop().getXmap("solon.mime");
            mimeTypes.forEach((k, v) -> {
                XStaticFiles.instance().putIfAbsent(k, v);
            });

            //2.切换代理（让静态文件优先）
            XHandlerLink link = new XHandlerLink();
            link.node = new XResourceHandler("/static");
            link.nextNode = app.handlerGet();

            app.handlerSet(link);
        }
    }
}
