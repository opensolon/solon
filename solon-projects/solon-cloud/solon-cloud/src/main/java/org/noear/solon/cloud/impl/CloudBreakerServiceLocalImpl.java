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
import org.noear.solon.cloud.model.BreakerEntrySim;
import org.noear.solon.cloud.model.BreakerException;
import org.noear.solon.cloud.service.CloudBreakerService;
import org.noear.solon.core.Props;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地熔断服务
 *
 * 常用指标：
 * sbc：并发链接数，Simultaneous Browser Connections
 * qps：每秒请求数，Query Per Second
 *
 * @author noear
 * @since 1.3
 */
public abstract class CloudBreakerServiceLocalImpl implements CloudBreakerService {
    static final String CONFIG_PREFIX = "solon.cloud.local.breaker";
    static final String CONFIG_DEF = "root";

    private Map<String, BreakerEntrySim> breakers = new ConcurrentHashMap<>();
    private int rootValue = 0;

    public CloudBreakerServiceLocalImpl() {
        Props props = Solon.cfg().getProp(CONFIG_PREFIX);

        if (props.size() > 0) {
            //默认值
            rootValue = props.getInt(CONFIG_DEF, 0);

            //初始化
            //
            for (Object k : props.keySet()) {
                if (k instanceof String) {
                    String key = (String) k;
                    int val = props.getInt(key, 0);
                    if (val > 0) {
                        breakers.put(key, create(key, val));
                    }
                }
            }

            //增加配置变化监听
            //
            Solon.cfg().onChange((key, val) -> {
                if (key.startsWith(CONFIG_PREFIX)) {
                    String name = key.substring(CONFIG_PREFIX.length() + 1);
                    BreakerEntrySim tmp = breakers.get(name);
                    if (tmp != null) {
                        tmp.reset(Integer.parseInt(val));
                    }
                }
            });
        }
    }

    protected abstract BreakerEntrySim create(String name, int value);

    @Override
    public AutoCloseable entry(String breakerName) throws BreakerException {
        BreakerEntrySim tmp = null;

        if (rootValue > 0) {
            tmp = breakers.computeIfAbsent(breakerName, k -> create(breakerName, rootValue));
        } else {
            tmp = breakers.get(breakerName);
        }

        if (tmp == null) {
            throw new IllegalArgumentException("Missing breaker configuration: " + breakerName);
        } else {
            return tmp.enter();
        }
    }
}
