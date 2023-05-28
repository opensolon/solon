package org.redisson.solon;

import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.File;
import java.net.URL;
import java.util.Properties;

/**
 * @author noear
 * @since 2.2
 */
public class RedissonFactory {
    private Properties properties;

    public RedissonFactory(Properties properties) {
        this.properties = properties;
    }

    public RedissonClient create(){
        String fileUri = properties.getProperty("file");
        if (Utils.isNotEmpty(fileUri)) {
            URL url = ResourceUtil.findResource(fileUri);
            //Config config = new Config(new File(url.getFile()));
        }

        String configTxt = properties.getProperty("config");
        if (Utils.isNotEmpty(configTxt)) {
            //return YamlShardingSphereDataSourceFactory.createDataSource(configTxt.getBytes());
        }

        throw new IllegalStateException("Invalid sharding sphere configuration");
    }
}
