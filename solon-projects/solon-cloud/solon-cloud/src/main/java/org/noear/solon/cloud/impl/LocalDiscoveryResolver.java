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
package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.core.Props;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 本地发现解析器
 *
 * @author noear
 * @since 1.3
 */
public class LocalDiscoveryResolver {
    static final String CONFIG_PREFIX = "solon.cloud.local.discovery.service";

    /**
     * 注册到负载器工厂
     *
     * @param group 服务分组
     */
    public static void register(String group) {
        Map<String, Discovery> discoveryMap = resolve();

        //
        // 默认以[group=""]进行注册；如果需要特定组，可重新进行注册
        //
        String groupNew = (group == null ? "" : group);

        discoveryMap.forEach((service, discovery) -> {
            CloudClient.loadBalance().register(groupNew, service, discovery);
        });
    }

    /**
     * 解析
     */
    public static Map<String, Discovery> resolve() {
        Map<String, Discovery> discoveryMap = new LinkedHashMap<>();

        Props props = Solon.cfg().getProp(CONFIG_PREFIX);

        if (props.size() > 0) {
            props.forEach((k, v) -> {
                if (k instanceof String && v instanceof String) {
                    String service = ((String) k).split("\\[")[0];
                    URI url = URI.create((String) v);
                    resolveDo(discoveryMap, new Instance(service, url.getHost(), url.getPort()).protocol(url.getScheme()));
                }
            });
        }

        return discoveryMap;
    }

    private static void resolveDo(Map<String, Discovery> discoveryMap, Instance instance) {
        Discovery discovery = discoveryMap.get(instance.service());
        if (discovery == null) {
            discovery = new Discovery(Solon.cfg().appGroup(), instance.service());
            discoveryMap.put(instance.service(), discovery);
        }

        discovery.instanceAdd(instance);
    }
}