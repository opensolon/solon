package org.noear.solon.sessionstate.redisson;

import org.noear.solon.Utils;
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
     *   driverType: "redis"
     *   keyHeader: "demo" #默认为 ${solon.app.name} ，可不配置
     *   defSeconds: 30 #默认为 30，可不配置
     *   server: "localhost:6379"
     *   db: 0 #默认为 0，可不配置
     *   password: ""
     *   maxTotal: 200 #默认为 200，可不配
     *   ...
     * </pre></code>
     *
     * */
    public static RedissonClient build(Properties prop){
        String server_str = prop.getProperty("server");
        String db_str = prop.getProperty("db");
        String maxTotal_str = prop.getProperty("maxTotal");


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
        if(server_str.contains(",")){
            //集群
        }else{
            //单例
        }

        return null;
    }
}
