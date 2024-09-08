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
 * Bean 分类构建器
 *
 * @author noear
 * @since 2.9
 */
public class BeanBuilderTyped<T extends Annotation> implements BeanBuilder<T> {
    //分类构建器字典
    private Map<Class<?>, BeanBuilder<T>> map;
    //默认构建器
    private BeanBuilder<T> def;

    /**
     * 配置默认构建器
     */
    public BeanBuilderTyped defBuilder(BeanBuilder<T> builder) {
        this.def = builder;
        return this;
    }

    /**
     * 配置分类构建器
     */
    public BeanBuilderTyped putBuilder(Class<?> type, BeanBuilder<T> builder) {
        if (map == null) {
            //要使用有顺序的 LinkedHashMap
            map = new LinkedHashMap<>();
        }

        map.putIfAbsent(type, builder);
        return this;
    }

    /**
     * 构建
     */
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, T anno) throws Throwable {
        doBuildTyped(clz, bw, anno);
    }

    /**
     * 分类构建
     */
    protected boolean doBuildTyped(Class<?> clz, BeanWrap bw, T anno) throws Throwable {
        if (map != null) {
            for (Map.Entry<Class<?>, BeanBuilder<T>> kv : map.entrySet()) {
                if (kv.getKey().isAssignableFrom(clz)) {
                    kv.getValue().doBuild(clz, bw, anno);
                    return true;
                }
            }
        }

        if (def != null) {
            def.doBuild(clz, bw, anno);
            return true;
        } else {
            return false;
        }
    }
}