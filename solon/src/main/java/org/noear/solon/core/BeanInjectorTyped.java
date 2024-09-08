/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bean 分类注入器
 *
 * @author noear
 * @since 2.9
 */
public class BeanInjectorTyped<T extends Annotation> implements BeanInjector<T> {
    //分类注入器字典
    private Map<Class<?>, BeanInjector<T>> map;
    //默认注入器
    private BeanInjector<T> def;

    /**
     * 配置默认注入器
     */
    public BeanInjectorTyped defInjector(BeanInjector<T> injector) {
        this.def = injector;
        return this;
    }

    /**
     * 配置分类注入器
     */
    public BeanInjectorTyped putInjector(Class<?> type, BeanInjector<T> injector) {
        if (map == null) {
            //要使用有顺序的 LinkedHashMap
            map = new LinkedHashMap<>();
        }

        map.putIfAbsent(type, injector);
        return this;
    }

    /**
     * 注入
     */
    @Override
    public void doInject(VarHolder varH, T anno) {
        doInjectTyped(varH, anno);
    }

    /**
     * 分类注入
     */
    protected boolean doInjectTyped(VarHolder varH, T anno) {
        if (map != null) {
            for (Map.Entry<Class<?>, BeanInjector<T>> kv : map.entrySet()) {
                if (kv.getKey().isAssignableFrom(varH.getType())) {
                    kv.getValue().doInject(varH, anno);
                    return true;
                }
            }
        }

        if (def != null) {
            def.doInject(varH, anno);
            return true;
        } else {
            return false;
        }
    }
}