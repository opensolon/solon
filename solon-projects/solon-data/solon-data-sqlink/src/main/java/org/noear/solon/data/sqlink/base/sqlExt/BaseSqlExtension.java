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
package org.noear.solon.data.sqlink.base.sqlExt;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL扩展函数
 *
 * @author kiryu1223
 * @since 3.0
 */
public abstract class BaseSqlExtension {

    /**
     * 运行时生成需要的SQL表达式
     *
     * @param config 配置
     * @param method 函数
     * @param args   参数
     */
    public abstract ISqlExpression parse(SqLinkConfig config, Method method, List<ISqlExpression> args);

    private static final Map<Class<? extends BaseSqlExtension>, BaseSqlExtension> sqlExtensionCache = new ConcurrentHashMap<>();

    /**
     * 获取缓存
     */
    public static BaseSqlExtension getCache(Class<? extends BaseSqlExtension> c) {
        BaseSqlExtension baseSqlExtension = sqlExtensionCache.get(c);
        if (baseSqlExtension == null) {
            try {
                baseSqlExtension = c.newInstance();
                sqlExtensionCache.put(c, baseSqlExtension);
            }
            catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return baseSqlExtension;
    }
}
