/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.exception.CloudConfigException;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.PropsConverter;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.util.ConvertUtil;
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
    @Override
    public void doInject(VarHolder vh, CloudConfig anno) {
        if (CloudClient.config() == null) {
            throw new IllegalArgumentException("Missing CloudConfigService component");
        }

        vh.required(anno.required());

        //支持${xxx}配置
        String name = Solon.cfg().getByTmpl(Utils.annoAlias(anno.value(), anno.name()));
        //支持${xxx}配置
        String group = Solon.cfg().getByTmpl(anno.group());

        if(Utils.isEmpty(name)){
            throw new CloudConfigException("@CloudConfig missing value or name");
        }

        Object tmp1 = build(vh.getType(), group, name);
        if (tmp1 != null) {
            vh.setValue(tmp1);
        }

        if (vh.isField() && anno.autoRefreshed()) {
            CloudClient.config().attention(group, name, (cfg) -> {
                Object tmp2 = build0(vh.getType(), cfg);
                if (tmp2 != null) {
                    vh.setValue(tmp2);
                }
            });
        }
    }

    public Object build(Class<?> type, String group, String name) {
        Config cfg = CloudClient.config().pull(group, name);

        return build0(type, cfg);
    }


    private Object build0(Class<?> type, Config cfg) {
        if (cfg == null || cfg.value() == null) {
            return null;
        }

        if (Properties.class.isAssignableFrom(type)) {
            return cfg.toProps();
        }

        if (type.getName().startsWith("java.lang.") || type.isPrimitive()) {
            //如果是java基础类型，则为null（后面统一地 isPrimitive 做处理）
            //
            return ConvertUtil.to(type, cfg.value());
        } else {
            //尝试转为实体
            //
            Properties val0 = cfg.toProps();
            return PropsConverter.global().convert(val0, null, type, null);
        }
    }
}
