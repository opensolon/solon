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
package org.noear.solon.data.sqlink.base.generate;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态生成器
 *
 * @author kiryu1223
 * @since 3.0
 */
public abstract class DynamicGenerator<T> {
    /**
     * 动态生成
     *
     * @param config        配置
     * @param fieldMetaData 字段元数据
     */
    public abstract T generate(SqLinkConfig config, FieldMetaData fieldMetaData);

    private static final Map<Class<? extends DynamicGenerator<?>>, DynamicGenerator<?>> cache = new ConcurrentHashMap<>();

    public static <T> DynamicGenerator<T> get(Class<? extends DynamicGenerator<T>> c) {
        DynamicGenerator<T> generator = (DynamicGenerator<T>) cache.get(c);
        if (generator == null) {
            try {
                generator = c.newInstance();
                cache.put(c, generator);
            }
            catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return generator;
    }
}
