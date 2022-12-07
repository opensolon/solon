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
import org.noear.solon.web.staticfiles.StaticConfig;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;
import org.noear.solon.web.staticfiles.repository.ExtendStaticRepository;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        //通过动态控制是否启用
        //

        if (Solon.app().enableStaticfiles() == false) {
            return;
        }

        if (StaticConfig.isEnable() == false) {
            return;
        }

        //加载一个配置
        StaticConfig.getCacheMaxAge();

        //尝试添加默认静态资源地址
        if (Utils.getResource(StaticConfig.RES_STATIC_LOCATION) != null) {
            StaticMappings.add("/", new ClassPathStaticRepository(StaticConfig.RES_STATIC_LOCATION));
        }

        if (Utils.getResource(StaticConfig.RES_WEB_INF_STATIC_LOCATION) != null) {
            StaticMappings.add("/", new ClassPathStaticRepository(StaticConfig.RES_WEB_INF_STATIC_LOCATION));
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

        //3.添加印射
        List<Map> mapList = Solon.cfg().getBean(StaticConfig.PROP_MAPPINGS, ArrayList.class);
        if (mapList != null) {
            for (Map map : mapList) {
                String path = (String) map.get("path");
                String repository = (String) map.get("repository");

                if (Utils.isEmpty(path) || Utils.isEmpty(repository)) {
                    continue;
                }

                if (path.startsWith("/") == false) {
                    path = "/" + path;
                }

                if (path.endsWith("/") == false) {
                    path = path + "/";
                }

                if (repository.startsWith(":")) {
                    StaticMappings.add(path, false, new ExtendStaticRepository());
                } else if (repository.startsWith("classpath:")) {
                    repository = repository.substring(10);
                    StaticMappings.add(path, false, new ClassPathStaticRepository(repository));
                } else {
                    StaticMappings.add(path, false, new FileStaticRepository(repository));
                }
            }
        }
    }
}