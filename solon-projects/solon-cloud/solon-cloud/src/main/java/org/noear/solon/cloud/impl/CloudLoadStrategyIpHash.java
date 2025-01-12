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

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.core.handle.Context;
import org.noear.solon.lang.Nullable;

/**
 * IP_Hash 负载策略
 *
 * @author noear
 * @since 2.2
 */
public class CloudLoadStrategyIpHash implements CloudLoadStrategy {
    @Nullable
    @Override
    public String getServer(Discovery discovery, int port) {
        String ip = Context.current().realIp();
        Instance instance = discovery.instanceGet(ip.hashCode(), port);

        if (instance == null) {
            return null;
        } else {
            return instance.uri();
        }
    }
}
