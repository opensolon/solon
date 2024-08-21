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
package org.noear.solon.cloud.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.utils.DiscoveryUtils;

import java.util.Collection;

/**
 * 云端注册与发现服务代理
 *
 * @author noear
 * @since 2.2
 */
public class CloudDiscoveryServiceProxy implements  CloudDiscoveryService {
    CloudDiscoveryService real;

    public CloudDiscoveryServiceProxy(CloudDiscoveryService real) {
        this.real = real;
    }

    @Override
    public void register(String group, Instance instance) {
        real.register(group, instance);
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {
        real.registerState(group, instance, health);
    }

    @Override
    public void deregister(String group, Instance instance) {
        real.deregister(group, instance);
    }

    @Override
    public Discovery find(String group, String service) {
        Discovery discovery = real.find(group, service);

        //尝试增加发现代理
        DiscoveryUtils.tryLoadAgent(discovery, group, service);

        return discovery;
    }

    private Collection<String> findServicesCached;

    @Override
    public Collection<String> findServices(String group) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        if (group != null && group.equals(Solon.cfg().appGroup())) {
            //如果是当前应用分组，则缓存下
            if (findServicesCached == null) {
                findServicesCached = real.findServices(group);
            }

            return findServicesCached;
        }

        return real.findServices(group);
    }

    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        real.attention(group, service, observer);
    }
}