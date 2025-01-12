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
package org.noear.solon.sessionstate.redisson;

import org.noear.solon.Utils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.Properties;

/**
 * @author noear
 * @since 1.7
 */
public class RedissonBuilder {
    /**
     * <code><pre>
     * test.cache1:
     *   server: "localhost:6379"
     *   db: 0 #默认为 0，可不配置
     *   password: ""
     *   maxTotal: 200 #默认为 200，可不配
     *   ...
     * </pre></code>
     */
    public static RedissonClient build(Properties prop) {
        String server_str = prop.getProperty("server");
        String db_str = prop.getProperty("db");
        String user_str = prop.getProperty("user");
        String password_str = prop.getProperty("password");


        int db = 0;

        if (Utils.isNotEmpty(db_str)) {
            db = Integer.parseInt(db_str);
        }

        //
        // 开始实例化 redissonClient
        //
        Config config = new Config();

        if (server_str.contains(",")) {
            //集群
            ClusterServersConfig serverConfig = config.useClusterServers();

            //注入一般配置
            Utils.injectProperties(serverConfig, prop);

            //设置关键配置
            String[] address = resolveServers(server_str.split(","));
            serverConfig.addNodeAddress(address)
                    .setUsername(user_str)
                    .setPassword(password_str);
        } else {
            //单例
            SingleServerConfig serverConfig = config.useSingleServer();

            //注入一般配置
            Utils.injectProperties(serverConfig, prop);

            //设置关键配置
            String[] address = resolveServers(server_str);
            serverConfig.setAddress(address[0])
                    .setUsername(user_str)
                    .setPassword(password_str)
                    .setDatabase(db);
        }

        return Redisson.create(config);
    }

    private static String[] resolveServers(String... servers) {
        String[] uris = new String[servers.length];

        for (int i = 0; i < servers.length; i++) {
            String sev = servers[i];

            if (sev.contains("://")) {
                uris[i] = sev;
            } else {
                uris[i] = "redis://" + sev;
            }
        }

        return uris;
    }
}
