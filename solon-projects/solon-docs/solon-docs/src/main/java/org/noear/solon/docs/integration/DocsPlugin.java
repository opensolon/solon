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
package org.noear.solon.docs.integration;

import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.docs.integration.properties.DocDocketProperties;
import org.noear.solon.docs.integration.properties.DocsProperties;

/**
 * @author noear
 * @since 2.2
 */
public class DocsPlugin implements Plugin {
    public static final String SOLON_DOCS = "solon.docs";

    @Override
    public void start(AppContext context) throws Throwable {
        final Props docsProps = context.cfg().getProp(SOLON_DOCS);
        final DocsProperties docsProperties;
        if (docsProps.size() > 0) {
            docsProperties = docsProps.toBean(DocsProperties.class);
        } else {
            docsProperties = new DocsProperties();
        }

        //加载 solon.docs.routes
        if (Utils.isNotEmpty(docsProperties.getRoutes())) {
            for (DocDocketProperties docDocket : docsProperties.getRoutes()) {
                if (Utils.isEmpty(docDocket.getId())) {
                    throw new IllegalArgumentException("Docs route id is empty");
                }

                BeanWrap docBw = context.wrap(docDocket.getId(), docDocket);
                //按名字注册
                context.putWrap(docDocket.getId(), docBw);
                //对外发布
                context.beanPublish(docBw);
            }
        }

        //加载 solon.docs.discover.pathPattern
        if (docsProperties.getDiscover().isEnabled()) {
            if (ClassUtil.hasClass(() -> Discovery.class)) {
                DiscoverLocator discoverLocator = new DiscoverLocator(context, docsProperties.getDiscover());

                if (docsProperties.getDiscover().isSyncStatus()) {
                    //同步状态
                    EventBus.subscribe(Discovery.class, discoverLocator);
                }

                //加入生命周期
                context.lifecycle(discoverLocator);
            } else {
                LogUtil.global().warn("Solon docs discover: missing solon cloud discovery");
            }
        }
    }
}