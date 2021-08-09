package org.noear.solon.extend.staticfiles;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.HandlerPipeline;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        //通过动态控制是否启用
        //

        if (app.enableStaticfiles() == false) {
            return;
        }

        if (XPluginProp.enable() == false) {
            return;
        }

        //加载一个配置
        XPluginProp.maxAge();

        if (Utils.getResource(XPluginProp.RES_LOCATION) != null) {
            //1.加载自定义的mime
            //
            NvMap mimeTypes = app.cfg().getXmap("solon.mime");
            mimeTypes.forEach((k, v) -> {
                StaticMimes.instance().putIfAbsent("." + k, v);
            });

            StaticMappings.instance().add("/", XPluginProp.RES_LOCATION);

            //2.切换代理（让静态文件优先）
            HandlerPipeline pipeline = new HandlerPipeline();
            pipeline.next(new StaticResourceHandler()).next(app.handlerGet());

            app.handlerSet(pipeline);
        }
    }
}
