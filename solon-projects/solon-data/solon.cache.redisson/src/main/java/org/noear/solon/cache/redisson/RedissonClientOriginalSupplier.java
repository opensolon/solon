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