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

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.core.bean.LifecycleSimpleBean;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.integration.properties.DiscoverProperties;

import java.util.Collection;

/**
 * 服务发现事件监听器
 *
 * @author noear
 * @since 2.9
 */
public class DiscoveryEventListener implements EventListener<Discovery>, LifecycleSimpleBean {
    private final AppContext appContext;
    private final DiscoverProperties discover;

    public DiscoveryEventListener(AppContext appContext, DiscoverProperties discover) {
        this.appContext = appContext;
        this.discover = discover;
    }

    /**
     * 开始
     */
    @Override
    public void start() {
        if (discover.getIncludedServices().size() > 0) {
            for (String tmp : discover.getIncludedServices()) {
                String[] ss = tmp.split(":");
                if (ss.length > 1) {
                    LoadBalance.get(ss[0], ss[1]);
                } else {
                    LoadBalance.get(ss[0]);
                }
            }
        }
    }

    /**
     * 开始之后
     */
    @Override
    public void postStart() throws Throwable {
        if (CloudClient.loadBalance().count() < discover.getIncludedServices().size()) {
            //条件档一下，避免与网关重复加载
            Collection<String> serviceNames = CloudClient.discovery().findServices("");

            if (Utils.isNotEmpty(serviceNames)) {
                for (String name : serviceNames) {
                    LoadBalance.get(name);
                }
            }
        }
    }

    @Override
    public void onEvent(Discovery discovery) throws Throwable {
        if (discover.getExcludedServices().contains(discovery.service())) {
            //排除
            return;
        }

        Object tmp = appContext.getBean(discovery.service());

        if (tmp == null) {
            //自动创建（如果还没有）
            DocDocket docDocket = new DocDocket();
            docDocket.groupName(discovery.service());
            docDocket.upstream(discovery.service(), discovery.service(), discover.getUriPattern().replace("{service}", discovery.service()));

            if (Utils.isNotEmpty(discover.getBasicAuth())) {
                docDocket.basicAuth().putAll(discover.getBasicAuth());
            }

            if (discover.isSyncStatus()) {
                //如果要同步状态
                docDocket.enable(discovery.clusterSize() > 0);
            }

            BeanWrap beanWrap = appContext.wrap(discovery.service(), docDocket);
            appContext.putWrap(discovery.service(), beanWrap);
        } else if (discover.isSyncStatus()) {
            //如果要同步状态
            if (tmp instanceof DocDocket) {
                DocDocket docDocket = (DocDocket) tmp;
                docDocket.enable(discovery.clusterSize() > 0);
            }
        }
    }
}