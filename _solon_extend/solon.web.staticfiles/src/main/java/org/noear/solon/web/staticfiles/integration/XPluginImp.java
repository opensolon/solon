package org.noear.solon.web.staticfiles.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.HandlerPipeline;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.StaticMimes;
import org.noear.solon.web.staticfiles.StaticResourceHandler;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        //通过动态控制是否启用
        //

        if (Solon.app().enableStaticfiles() == false) {
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

        //尝试启动静态代理（也可能在后面动态添加仓库）

        //1.加载自定义的mime
        NvMap mimeTypes = Solon.cfg().getXmap("solon.mime");
        mimeTypes.forEach((k, v) -> {
            StaticMimes.add("." + k, v);
        });


        //2.切换代理（让静态文件优先）
        HandlerPipeline pipeline = new HandlerPipeline();
        pipeline.next(new StaticResourceHandler()).next(Solon.app().handlerGet());
        Solon.app().handlerSet(pipeline);
    }
}
