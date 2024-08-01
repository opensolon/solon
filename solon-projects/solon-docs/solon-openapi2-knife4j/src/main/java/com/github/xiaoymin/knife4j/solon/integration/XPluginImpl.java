/*
 * Copyright 2017-2024 noear.org and authors
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
package com.github.xiaoymin.knife4j.solon.integration;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

import java.util.Map;

/**
 * @author noear
 * @since 2.2
 */
public class XPluginImpl implements Plugin {
    public static final String SOLON_DOCS_ROUTES = "solon.docs.routes";
    public static final String SOLON_DOCS_DISCOVER_PATHPREFIX = "solon.docs.discover.pathPrefix"; //manual, discover
    public static final String SOLON_DOCS_DISCOVER_SYNCSTATUS = "solon.docs.discover.syncStatus"; //manual, discover
    public static final String SOLON_DOCS_DISCOVER_EXCLUDED = "solon.docs.discover.excluded"; //manual, discover


    @Override
    public void start(AppContext context) throws Throwable {
        BeanWrap beanWrap = context.beanMake(OpenApiExtensionResolver.class);
        OpenApiExtensionResolver openApiExtensionResolver = beanWrap.raw();

        if (openApiExtensionResolver.getSetting().isEnable()) {
            String uiPath = "/";
            if (openApiExtensionResolver.getSetting().isProduction()) {
                if (Solon.cfg().isFilesMode() == false) {
                    //生产环境，只有 files 模式才能用（即开发模式）
                    StaticMappings.add(uiPath, new ClassPathStaticRepository("META-INF/resources"));
                }
            } else {
                //非生产环境，一直可用
                StaticMappings.add(uiPath, new ClassPathStaticRepository("META-INF/resources"));
            }

            Solon.app().add(uiPath, OpenApi2Controller.class);
        }


        //加载 solon.docs.routes
        Map<String, Props> docMap = context.cfg().getGroupedProp(SOLON_DOCS_ROUTES);
        for (Map.Entry<String, Props> kv : docMap.entrySet()) {
            DocDocket docDocket = kv.getValue().getBean(DocDocket.class);

            BeanWrap docBw = context.wrap(kv.getKey(), docDocket);
            context.putWrap(kv.getKey(), docBw);
        }

        //加载 solon.docs.discover.pathPrefix
        String discover_pathPrefix = Solon.cfg().get(SOLON_DOCS_DISCOVER_PATHPREFIX);
        if (Utils.isNotEmpty(discover_pathPrefix)) {
            if (ClassUtil.hasClass(() -> Discovery.class)) {
                EventBus.subscribe(Discovery.class, new DiscoveryEventListener(context));
            } else {
                LogUtil.global().warn("Solon docs discover: missing solon cloud discovery");
            }
        }
    }
}
