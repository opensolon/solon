package org.noear.solon.extend.staticfiles;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handler.HandlerLink;
import org.noear.solon.core.ParamMap;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(Solon app) {
        //通过动态控制是否启用
        //

        if(app.enableStaticfiles() == false){
            return;
        }

        if ("0".equals(app.prop().get("org.noear.solon.extend.staticfiles.enabled"))) {
            return;
        }

        if (Utils.getResource("/static") != null) {
            //1.加载自定义的mime
            //
            ParamMap mimeTypes = app.prop().getXmap("solon.mime");
            mimeTypes.forEach((k, v) -> {
                XStaticFiles.instance().putIfAbsent("." + k, v);
            });

            //2.切换代理（让静态文件优先）
            HandlerLink link = new HandlerLink();
            link.node = new XResourceHandler("/static");
            link.nextNode = app.handlerGet();

            app.handlerSet(link);
        }
    }
}
