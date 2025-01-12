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
package org.noear.solon.cloud.utils;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Discovery;

/**
 * 发现辐助工具
 *
 * @author noear
 * @since 2.2
 */
public class DiscoveryUtils {
    /**
     * 尝试加载发现代理
     */
    public static void tryLoadAgent(Discovery discovery, String group, String service) {
        if (discovery.agent() != null) {
            return;
        }

        if (CloudClient.config() != null) {
            //前缀在前，方便相同配置在一起
            String agent = CloudClient.config().pull(group, "discovery.agent." + service).value();

            if (Utils.isNotEmpty(agent)) {
                discovery.agent(agent);
            } else {
                //为了后面不再做重复检测
                discovery.agent("");
            }
        }
    }
}