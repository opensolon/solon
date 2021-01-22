package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.model.Config;

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
        Object val2 = build(varH.getType(), anno);

        if (val2 != null) {
            varH.setValue(val2);
        }
    }

    public Object build(Class<?> type, CloudConfig anno) {
        if (CloudClient.config() == null) {
            return null;
        }

        Config cfg = CloudClient.config().get(anno.group(), anno.value());

        if (cfg == null || cfg.getValue() == null) {
            return null;
        }

        if (Properties.class.isAssignableFrom(type)) {
            return cfg.toProps();
        }

        if (type.getName().startsWith("java.") || type.isPrimitive()) {
            //如果是java基础类型，则为null（后面统一地 isPrimitive 做处理）
            //
            return ConvertUtil.to(type, cfg.getValue());
        } else {
            //尝试转为实体
            //
            Properties val0 = cfg.toProps();
            return ClassWrap.get(type).newBy(val0);
        }
    }
}
