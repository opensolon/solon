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

/**
 * 对象创建器工厂
 *
 * @author kiryu1223
 * @since 3.0
 */
public class BeanCreatorFactory {
    private static final Map<Class<?>, AbsBeanCreator<?>> cache = new ConcurrentHashMap<>();

    /**
     * 创建对象创建器
     *
     * @param target 目标类型
     */
    protected <T> AbsBeanCreator<T> create(Class<T> target) {
        return new DefaultBeanCreator<>(target);
    }

    /**
     * 获取对象创建器
     *
     * @param target 目标类型
     */
    public <T> AbsBeanCreator<T> get(Class<T> target) {
        AbsBeanCreator<T> creator = (AbsBeanCreator<T>) cache.get(target);
        if (creator == null) {
            creator = create(target);
            cache.put(target, creator);
        }
        return creator;
    }
}
