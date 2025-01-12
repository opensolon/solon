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
package org.noear.solon.data.sqlink.base.toBean.beancreator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 对象创建器
 *
 * @author kiryu1223
 * @since 3.0
 */
public abstract class AbsBeanCreator<T> {
    /**
     * 目标类型
     */
    protected final Class<T> target;
    /**
     * 创建器
     */
    protected final Supplier<T> supplier;
    /**
     * 字段设置器
     */
    protected final Map<String, ISetterCaller<T>> setters = new ConcurrentHashMap<>();

    protected AbsBeanCreator(Class<T> target) {
        this.target = target;
        this.supplier = initBeanCreator(target);
    }

    /**
     * 初始化对象创建器
     */
    protected abstract Supplier<T> initBeanCreator(Class<T> target);

    /**
     * 初始化字段设置器
     */
    protected abstract ISetterCaller<T> initBeanSetter(String property);

    /**
     * 获取对象创建器
     */
    public Supplier<T> getBeanCreator() {
        return supplier;
    }

    /**
     * 根据字段名获取字段设置器
     *
     * @param fieldName 字段名
     */
    public ISetterCaller<T> getBeanSetter(String fieldName) {
        ISetterCaller<T> setterCaller = setters.get(fieldName);
        if (setterCaller == null) {
            setterCaller = initBeanSetter(fieldName);
            setters.put(fieldName, setterCaller);
        }
        return setterCaller;
    }
}
