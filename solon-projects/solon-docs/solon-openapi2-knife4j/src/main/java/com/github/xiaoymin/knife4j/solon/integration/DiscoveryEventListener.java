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

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.docs.DocDocket;

import java.util.Collection;

/**
 * 服务发现事件监听器
 *
 * @author noear
 * @since 2.9
 */
public class DiscoveryEventListener implements EventListener<Discovery> {
    private final AppContext appContext;
    private final boolean syncStatus;
    private final Collection<String> excluded;

    public DiscoveryEventListener(AppContext appContext) {
        this.appContext = appContext;
        this.syncStatus = appContext.cfg().getBool(XPluginImpl.SOLON_DOCS_DISCOVER_SYNCSTATUS, false);
        this.excluded = appContext.cfg().getList(XPluginImpl.SOLON_DOCS_DISCOVER_EXCLUDED);
    }

    @Override
    public void onEvent(Discovery discovery) throws Throwable {
        if(excluded.contains(discovery.service())){
            //排除
            return;
        }

        Object tmp = appContext.getBean(discovery.service());

        if (tmp == null) {
            //自动创建（如果还没有）
            DocDocket docDocket = new DocDocket();
            docDocket.groupName(discovery.service());
            docDocket.upstream(discovery.service(), "swagger/v2?group=" + discovery.service());

            if (syncStatus) {
                //如果要同步状态
                docDocket.enable(discovery.clusterSize() > 0);
            }

            BeanWrap beanWrap = appContext.wrap(discovery.service(), docDocket);
            appContext.putWrap(discovery.service(), beanWrap);
        } else if (syncStatus) {
            //如果要同步状态
            if (tmp instanceof DocDocket) {
                DocDocket docDocket = (DocDocket) tmp;
                docDocket.enable(discovery.clusterSize() > 0);
            }
        }
    }
}