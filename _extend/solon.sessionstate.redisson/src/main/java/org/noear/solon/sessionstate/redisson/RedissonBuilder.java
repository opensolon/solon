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

        if (server_str.contains(",")) {
            //集群
            ClusterServersConfig serverConfig = config.useClusterServers();

            //注入一般配置
            Utils.injectProperties(serverConfig, prop);

            //设置关键配置
            serverConfig.addNodeAddress(server_str.split(","))
                    .setUsername(user_str)
                    .setPassword(password_str);
        } else {
            //单例
            SingleServerConfig serverConfig = config.useSingleServer();

            //注入一般配置
            Utils.injectProperties(serverConfig, prop);

            //设置关键配置
            serverConfig.setAddress(server_str)
                    .setUsername(user_str)
                    .setPassword(password_str)
                    .setDatabase(db);
        }

        return Redisson.create(config);
    }
}
