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
package org.noear.solon.net.http;

import org.noear.solon.Utils;
import org.noear.solon.core.LoadBalance;

/**
 * 负截均衡工具类
 *
 * @author noear
 * @since 2.8
 */
public final class LoadBalanceUtils {
    /**
     * 获取服务地址
     *
     * @param group   分组
     * @param service 服务
     */
    public static String getServer(String group, String service) {
        LoadBalance upstream = null;
        if (Utils.isEmpty(group)) {
            upstream = LoadBalance.parse(service);
        } else {
            upstream = LoadBalance.get(group, service);
        }

        if (upstream == null) {
            throw new IllegalStateException("No service upstream found: " + service);
        }

        String server = upstream.getServer();

        if (Utils.isEmpty(server)) {
            throw new IllegalStateException("No service address found: " + service);
        }

        return server;
    }
}
