package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
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
        if (CloudClient.config() == null) {
            throw new IllegalArgumentException("Missing CloudConfigService component");
        }

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
            //支持${xxx}配置
            String name = Solon.cfg().getByParse(Utils.annoAlias(anno.value(), anno.name()));
            //支持${xxx}配置
            String group = Solon.cfg().getByParse(anno.group());

            Config config = CloudClient.config().pull(group, name);
            if (config != null) {
                handler.handler(config);
            }

            //关注配置
            CloudClient.config().attention(group, name, handler);
        }
    }
}
