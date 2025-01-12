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
package org.apache.shardingsphere.solon;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.noear.solon.Utils;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.ResourceUtil;

import javax.sql.DataSource;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author noear
 * @since 2.2
 */
public class ShardingSphereSupplier implements Supplier<DataSource> {

    private final Properties properties;

    public ShardingSphereSupplier(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource get() {
        try {
            String fileUri = properties.getProperty("file");
            if (Utils.isNotEmpty(fileUri)) {
                URL resource = ResourceUtil.findResource(fileUri);
                if (resource == null) {
                    throw new IllegalStateException("The sharding sphere configuration file does not exist");
                }
                try (InputStream in = resource.openStream()) {
                    byte[] bytes = IoUtil.transferToBytes(in);
                    return YamlShardingSphereDataSourceFactory.createDataSource(bytes);
                }
            }
            String configTxt = properties.getProperty("config");
            if (Utils.isNotEmpty(configTxt)) {
                return YamlShardingSphereDataSourceFactory.createDataSource(configTxt.getBytes());
            }
        } catch (Exception e) {
            throw new IllegalStateException("The sharding sphere configuration failed", e);
        }

        throw new IllegalStateException("Invalid sharding sphere configuration");
    }
}
