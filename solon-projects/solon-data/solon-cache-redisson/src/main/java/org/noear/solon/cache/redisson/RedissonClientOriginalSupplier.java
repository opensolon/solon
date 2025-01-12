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
package org.noear.solon.cache.redisson;

import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.net.URL;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * RedissonClient 提供者（使用原生配置）
 *
 * @author noear
 * @since 2.8
 */
public class RedissonClientOriginalSupplier implements Supplier<RedissonClient> {
    private Properties properties;

    private Consumer<Config> configHandler;

    /**
     * 添加配置处理
     */
    public RedissonClientOriginalSupplier withConfig(Consumer<Config> configHandler) {
        this.configHandler = configHandler;
        return this;
    }

    public RedissonClientOriginalSupplier(Properties properties) {
        this.properties = properties;
    }

    @Override
    public RedissonClient get() {
        try {
            String fileUri = properties.getProperty("file");
            if (Utils.isNotEmpty(fileUri)) {
                URL url = ResourceUtil.findResource(fileUri);
                Config config = Config.fromYAML(url);

                if (configHandler != null) {
                    configHandler.accept(config);
                }

                return Redisson.create(config);
            }

            String configTxt = properties.getProperty("config");
            if (Utils.isNotEmpty(configTxt)) {
                Config config = Config.fromYAML(configTxt);

                if (configHandler != null) {
                    configHandler.accept(config);
                }

                return Redisson.create(config);
            }
        } catch (Exception e) {
            throw new IllegalStateException("The redisson configuration failed", e);
        }

        throw new IllegalStateException("Invalid redisson configuration");
    }
}