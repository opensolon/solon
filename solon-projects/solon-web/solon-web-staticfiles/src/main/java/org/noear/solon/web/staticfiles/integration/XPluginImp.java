/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.web.staticfiles.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.web.staticfiles.*;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;
import org.noear.solon.web.staticfiles.repository.ExtendStaticRepository;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        //通过动态控制是否启用
        //

        if (context.app().enableStaticfiles() == false) {
            return;
        }

        if (StaticConfig.isEnable() == false) {
            return;
        }

        //加载一个配置
        StaticConfig.getCacheMaxAge();

        //1.尝试添加默认静态资源地址
        if (ResourceUtil.hasResource(StaticConfig.RES_WEB_INF_STATIC_LOCATION)) {
            ///第一优化
            StaticMappings.add("/", new ClassPathStaticRepository(StaticConfig.RES_WEB_INF_STATIC_LOCATION));
        }else{
            if (ResourceUtil.hasResource(StaticConfig.RES_STATIC_LOCATION)) {
                ///第二优化
                StaticMappings.add("/", new ClassPathStaticRepository(StaticConfig.RES_STATIC_LOCATION));
            }
        }


        //2.添加映射
        List<Map> mapList = Solon.cfg().toBean(StaticConfig.PROP_MAPPINGS, ArrayList.class);
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
                    StaticMappings.add(path,  new ExtendStaticRepository());
                } else if (ResourceUtil.hasClasspath(repository)) {
                    repository = ResourceUtil.remSchema(repository);
                    StaticMappings.add(path,  new ClassPathStaticRepository(repository));
                } else {
                    StaticMappings.add(path,  new FileStaticRepository(repository));
                }
            }
        }

        //尝试启动静态代理（也可能在后面动态添加仓库）

        //3.加载自定义 mime
        context.cfg().getMap("solon.mime.").forEach((key, val) -> {
            StaticMimes.add("." + key, val);
        });

        //4.切换代理（让静态文件优先）
        context.app().handler().prev(new StaticResourceHandler());
    }
}