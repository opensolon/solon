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
 * 配置注入器
 *
 * @author noear
 * @since 1.2
 */
public class CloudConfigBeanInjector implements BeanInjector<CloudConfig> {
    public static final CloudConfigBeanInjector instance = new CloudConfigBeanInjector();

    @Override
    public void doInject(VarHolder varH, CloudConfig anno) {
        if (CloudClient.config() == null) {
            throw new IllegalArgumentException("Missing CloudConfigService component");
        }

        Object tmp1 = build(varH.getType(), anno);
        if (tmp1 != null) {
            varH.setValue(tmp1);
        }

        if (varH.isField() && anno.autoRefreshed()) {
            CloudClient.config().attention(anno.group(), anno.value(), (cfg) -> {
                Object tmp2 = build0(varH.getType(), cfg);
                if (tmp2 != null) {
                    varH.setValue(tmp2);
                }
            });
        }
    }

    public Object build(Class<?> type, CloudConfig anno) {
        Config cfg = CloudClient.config().pull(anno.group(), anno.value());

        return build0(type, cfg);
    }


    private Object build0(Class<?> type, Config cfg) {
        if (cfg == null || cfg.value() == null) {
            return null;
        }

        if (Properties.class.isAssignableFrom(type)) {
            return cfg.toProps();
        }

        if (type.getName().startsWith("java.") || type.isPrimitive()) {
            //如果是java基础类型，则为null（后面统一地 isPrimitive 做处理）
            //
            return ConvertUtil.to(type, cfg.value());
        } else {
            //尝试转为实体
            //
            Properties val0 = cfg.toProps();
            return ClassWrap.get(type).newBy(val0);
        }
    }
}
