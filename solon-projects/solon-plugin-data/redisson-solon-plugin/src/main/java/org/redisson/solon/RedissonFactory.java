package org.redisson.solon;

import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.IOException;
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
            try {
                Config config = Config.fromYAML(url);
                return Redisson.create(config);
            }catch (IOException ex){
                throw new IllegalStateException("Invalid redisson configuration",ex);
            }
        }

        String configTxt = properties.getProperty("config");
        if (Utils.isNotEmpty(configTxt)) {
            try {
                Config config = Config.fromYAML(configTxt);
                return Redisson.create(config);
            }catch (IOException ex){
                throw new IllegalStateException("Invalid redisson configuration",ex);
            }

        }

        throw new IllegalStateException("Invalid redisson configuration");
    }
}
