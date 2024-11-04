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
package org.noear.solon.data.sqlink.base.sqlExt;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kiryu1223
 * @since 3.0
 */
public abstract class BaseSqlExtension {
    public abstract ISqlExpression parse(IConfig config, Method sqlFunc, List<ISqlExpression> args);

    private static final Map<Class<? extends BaseSqlExtension>, BaseSqlExtension> sqlExtensionCache = new ConcurrentHashMap<>();

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
