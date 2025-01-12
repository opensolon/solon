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
package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 云端配置观察者实现
 *
 * @author noear
 * @since 1.2
 */
public class CloudConfigObserverEntity implements CloudConfigHandler {
    static final Logger log = LoggerFactory.getLogger(CloudConfigObserverEntity.class);

    public final String group;
    public final String key;
    public final CloudConfigHandler handler;

    public CloudConfigObserverEntity(String group, String key, CloudConfigHandler handler) {
        this.group = group;
        this.key = key;
        this.handler = handler;
    }

    @Override
    public void handle(Config config) {
        try {
            handler.handle(config);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}
