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
package org.noear.solon.expression.context;

import org.noear.solon.expression.exception.EvaluationException;
import org.noear.solon.expression.snel.PropertyHolder;
import org.noear.solon.expression.snel.ReflectionUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * 标准上下文
 *
 * @author noear
 * @since 3.2
 */
public class StandardContext implements Function<String, Object> {
    private final Object target;
    private final boolean isMap;
    private Properties properties;

    public StandardContext(Object target) {
        this.target = target;
        this.isMap = target instanceof Map;
    }

    public StandardContext() {
        this.target = Collections.emptyMap();
        this.isMap = true;
    }

    /**
     * 属性设置（用于模板表达式）
     */
    public StandardContext properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * 属性获取（用于模板表达式）
     */
    public Properties properties() {
        return properties;
    }

    @Override
    public Object apply(String name) {
        if ("root".equals(name)) {
            return target;
        }

        if (isMap) {
            return ((Map) target).get(name);
        } else {
            PropertyHolder tmp = ReflectionUtil.getProperty(target.getClass(), name);

            try {
                return tmp.getValue(target);
            } catch (Throwable e) {
                throw new EvaluationException("Failed to access property: " + name, e);
            }
        }
    }
}