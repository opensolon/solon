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
package org.noear.solon.docs.integration;

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

import java.util.Map;

/**
 * @author noear
 * @since 2.2
 */
public class XPluginImpl implements Plugin {
    public static final String SOLON_DOCS_ROUTES = "solon.docs.routes";
    public static final String SOLON_DOCS_DISCOVER_URIPATTERN = "solon.docs.discover.uriPattern"; //manual, discover
    public static final String SOLON_DOCS_DISCOVER_SYNCSTATUS = "solon.docs.discover.syncStatus"; //manual, discover
    public static final String SOLON_DOCS_DISCOVER_EXCLUDED = "solon.docs.discover.excluded"; //manual, discover
    public static final String SOLON_DOCS_DISCOVER_INCLUDED = "solon.docs.discover.included"; //manual, discover
    public static final String SOLON_DOCS_DISCOVER_BASICAUTH = "solon.docs.discover.basicAuth"; //manual, discover


    @Override
    public void start(AppContext context) throws Throwable {
        //加载 solon.docs.routes
        Map<String, Props> docMap = context.cfg().getGroupedProp(SOLON_DOCS_ROUTES);
        if (docMap.size() > 0) {
            for (Map.Entry<String, Props> kv : docMap.entrySet()) {
                DocDocket docDocket = kv.getValue().getBean(DocDocket.class);

                BeanWrap docBw = context.wrap(kv.getKey(), docDocket);
                //按名字注册
                context.putWrap(kv.getKey(), docBw);
                //对外发布
                context.wrapPublish(docBw);
            }
        }

        //加载 solon.docs.discover.pathPattern
        String discover_pathPattern = Solon.cfg().get(SOLON_DOCS_DISCOVER_URIPATTERN);
        if (Utils.isNotEmpty(discover_pathPattern)) {
            if (ClassUtil.hasClass(() -> Discovery.class)) {
                DiscoveryEventListener eventListener = new DiscoveryEventListener(context, discover_pathPattern);
                //订阅
                EventBus.subscribe(Discovery.class, eventListener);
                //开始
                eventListener.start();
            } else {
                LogUtil.global().warn("Solon docs discover: missing solon cloud discovery");
            }
        }
    }
}