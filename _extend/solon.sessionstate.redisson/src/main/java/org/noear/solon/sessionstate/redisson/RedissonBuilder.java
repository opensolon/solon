package org.noear.solon.sessionstate.redisson;

import org.noear.solon.Utils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

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
        String maxTotal_str = prop.getProperty("maxTotal");
        String user_str = prop.getProperty("user");
        String password_str = prop.getProperty("password");


        int db = 0;
        int maxTotal = 200;

        if (Utils.isNotEmpty(db_str)) {
            db = Integer.parseInt(db_str);
        }

        if (Utils.isNotEmpty(maxTotal_str)) {
            maxTotal = Integer.parseInt(maxTotal_str);
        }

        //
        // 开始实例化 redissonClient
        //
        Config config = new Config();
        Utils.injectProperties(config, prop);

        if (server_str.contains(",")) {
            //集群
            config.useClusterServers()
                    .addNodeAddress(server_str.split(","))
                    .setUsername(user_str)
                    .setPassword(password_str);
        } else {
            //单例
            config.useSingleServer()
                    .setAddress(server_str)
                    .setUsername(user_str)
                    .setPassword(password_str)
                    .setDatabase(db);
        }

        return Redisson.create(config);
    }
}
