package org.noear.solon.extend.staticfiles;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.HandlerPipeline;
import org.noear.solon.extend.staticfiles.repository.ClassPathStaticRepository;

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

        //尝试添加默认静态资源地址
        if (Utils.getResource(XPluginProp.RES_STATIC_LOCATION) != null) {
            StaticMappings.add("/", new ClassPathStaticRepository(XPluginProp.RES_STATIC_LOCATION));
        }

        //尝试启动静态代理
        if (StaticMappings.count() > 0) {
            //1.加载自定义的mime
            //
            NvMap mimeTypes = app.cfg().getXmap("solon.mime");
            mimeTypes.forEach((k, v) -> {
                StaticMimes.putIfAbsent("." + k, v);
            });


            //2.切换代理（让静态文件优先）
            HandlerPipeline pipeline = new HandlerPipeline();
            pipeline.next(new StaticResourceHandler()).next(app.handlerGet());
            app.handlerSet(pipeline);
        }
    }
}
