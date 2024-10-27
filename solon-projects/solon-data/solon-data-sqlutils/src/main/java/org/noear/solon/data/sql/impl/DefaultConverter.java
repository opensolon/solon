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
package org.noear.solon.data.sql.impl;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.data.sql.Row;

/**
 * @author noear
 * @since 3.0
 */
public class DefaultConverter implements Row.Converter {
    private static Row.Converter instance = new DefaultConverter();

    static {
        if (Solon.app() != null) {
            Solon.context().getBeanAsync(Row.Converter.class, bean -> {
                instance = bean;
            });
        }
    }

    /**
     * 获取实例
     */
    public static Row.Converter getInstance() {
        return instance;
    }

    /**
     * 设置实例
     */
    public static void setInstance(Row.Converter instance) {
        if (instance != null) {
            DefaultConverter.instance = instance;
        }
    }

    /**
     * 转换
     */
    @Override
    public Object convert(Row row, Class<?> type) {
        return ONode.load(row.toMap()).toObject(type);
    }
}
