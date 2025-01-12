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
package org.noear.solon.vault.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.vault.VaultUtils;
import org.noear.solon.vault.annotation.VaultInject;

import java.util.Properties;

/**
 * 脱敏注入器
 *
 * @author noear
 * @since 1.9
 */
public class VaultBeanInjector implements BeanInjector<VaultInject> {
    @Override
    public void doInject(VarHolder vh, VaultInject anno) {
        beanInject(vh, anno.value(), anno.required(), anno.autoRefreshed());
    }

    protected void beanInject(VarHolder vh, String name, boolean required, boolean autoRefreshed) {
        vh.required(required);

        if (name.startsWith("${")) {
            //
            // @Inject("${xxx}") //注入配置 ${xxx} or ${xxx:def},只适合单值
            //
            String name2 = name.substring(2, name.length() - 1).trim();

            beanInjectConfig(vh, name2);

            if (autoRefreshed && vh.isField()) {
                vh.context().cfg().onChange((key, val) -> {
                    if (key.startsWith(name2)) {
                        beanInjectConfig(vh, name2);
                    }
                });
            }
        }
    }

    private void beanInjectConfig(VarHolder vh, String name) {
        if (Properties.class == vh.getType()) {
            //如果是 Properties
            Properties val = vh.context().cfg().getProp(name);

            //脱敏处理
            val = VaultUtils.guard(val);

            vh.setValue(val);
        } else {
            //2.然后尝试获取配置
            String def = null;
            int defIdx = name.indexOf(":");
            if (defIdx > 0) {
                if (name.length() > defIdx + 1) {
                    def = name.substring(defIdx + 1).trim();
                } else {
                    def = "";
                }
                name = name.substring(0, defIdx).trim();
            }

            String val = vh.context().cfg().get(name);

            if (def != null) {
                if (Utils.isEmpty(val)) {
                    val = def;
                }
            }

            //脱敏处理
            val = VaultUtils.guard(val);

            if (val == null) {
                Class<?> pt = vh.getType();

                if (pt.getName().startsWith("java.lang.") || pt.isPrimitive()) {
                    //如果是java基础类型，则不注入配置值
                } else {
                    //尝试转为实体
                    Properties val0 = vh.context().cfg().getProp(name);
                    if (val0.size() > 0) {
                        //脱敏处理
                        val0 = VaultUtils.guard(val0);

                        //如果找到配置了
                        Object val2 = PropsConverter.global().convert(val0, null, pt, vh.getGenericType());
                        vh.setValue(val2);
                    }
                }
            } else {
                Object val2 = ConvertUtil.to(vh.getType(), vh.getGenericType(), val);

                vh.setValue(val2);
            }
        }
    }
}
