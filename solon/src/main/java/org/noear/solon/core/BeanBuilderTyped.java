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
    //分类构建器
    private Map<Class<?>, BeanBuilder<T>> typedBuilders;
    //默认构建器
    private BeanBuilder<T> defaultBuilder;

    public BeanBuilderTyped defBuilder(BeanBuilder<T> beanBuilder) {
        this.defaultBuilder = beanBuilder;
        return this;
    }

    /**
     * 添加分类构建器
     */
    public BeanBuilderTyped putBuilder(Class<?> type, BeanBuilder<T> beanBuilder) {
        if (typedBuilders == null) {
            //要使用有顺序的 LinkedHashMap
            typedBuilders = new LinkedHashMap<>();
        }

        typedBuilders.putIfAbsent(type, beanBuilder);
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
        if (typedBuilders != null) {
            for (Map.Entry<Class<?>, BeanBuilder<T>> kv : typedBuilders.entrySet()) {
                if (kv.getKey().isAssignableFrom(clz)) {
                    kv.getValue().doBuild(clz, bw, anno);
                    return true;
                }
            }
        }

        if (defaultBuilder != null) {
            defaultBuilder.doBuild(clz, bw, anno);
            return true;
        } else {
            return false;
        }
    }
}