package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.ClassWrap;

import java.util.Properties;

/**
 * @author noear
 * @since 1.4
 */
public class CloudConfigBeanBuilder implements BeanBuilder<CloudConfig> {
    public static final CloudConfigBeanBuilder instance = new CloudConfigBeanBuilder();

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, CloudConfig anno) throws Exception {
        CloudConfigHandler handler;
        if (bw.raw() instanceof CloudConfigHandler) {
            handler = bw.raw();
        } else {
            handler = (Config cfg) -> {
                Properties val0 = cfg.toProps();
                ClassWrap.get(clz).fill(bw.raw(), val0::getProperty);
            };
        }

        CloudManager.register(anno, handler);

        if (CloudClient.config() != null) {
            Config config = CloudClient.config().pull(anno.group(), anno.value());
            if (config != null) {
                handler.handler(config);
            }

            //关注配置
            CloudClient.config().attention(anno.group(), anno.value(), handler);
        }
    }
}
