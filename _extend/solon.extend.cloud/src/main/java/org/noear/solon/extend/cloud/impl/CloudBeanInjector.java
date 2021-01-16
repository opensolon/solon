package org.noear.solon.extend.cloud.impl;

import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.extend.cloud.CloudManager;
import org.noear.solon.extend.cloud.annotation.CloudConfig;
import org.noear.solon.extend.cloud.model.Config;

import java.util.Properties;

/**
 * 配置注入
 *
 * @author noear
 * @since 1.2
 */
public class CloudBeanInjector implements BeanInjector<CloudConfig> {
    public static final CloudBeanInjector instance = new CloudBeanInjector();

    @Override
    public void doInject(VarHolder varH, CloudConfig anno) {
        if (CloudManager.configService() == null) {
            return;
        }

        String[] ss = anno.value().split("/");
        String group = ss[0];
        String key = (ss.length > 1 ? ss[1] : "*");

        if ("*".equals(key)) {
            return;
        }

        Config config = CloudManager.configService().get(group, key);

        if (config == null || config.value == null) {
            return;
        }

        Object val2 = build(varH.getType(), config);

        if (val2 != null) {
            varH.setValue(val2);
        }
    }

    public Object build(Class<?> type, Config cfg) {
        //Properties
        if (Properties.class.isAssignableFrom(type)) {
            return cfg.toProps();
        }

        return ConvertUtil.to(type, cfg.value);
    }
}
