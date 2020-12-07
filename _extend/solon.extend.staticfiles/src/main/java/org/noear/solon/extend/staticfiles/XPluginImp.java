package org.noear.solon.extend.staticfiles;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.HandlerLink;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        //通过动态控制是否启用
        //

        if (app.enableStaticfiles() == false) {
            return;
        }

        if ("0".equals(app.cfg().get("org.noear.solon.extend.staticfiles.enabled"))) {
            return;
        }

        if (Utils.getResource("/static") != null) {
            //1.加载自定义的mime
            //
            NvMap mimeTypes = app.cfg().getXmap("solon.mime");
            mimeTypes.forEach((k, v) -> {
                StaticFiles.instance().putIfAbsent("." + k, v);
            });

            StaticMappings.instance().add("/", "/static/");

            //2.切换代理（让静态文件优先）
            HandlerLink link = new HandlerLink();
            link.node = new StaticResourceHandler();
            link.nextNode = app.handlerGet();

            app.handlerSet(link);
        }
    }
}
