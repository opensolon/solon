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
import org.noear.solon.data.sql.bound.RowConverter;
import org.noear.solon.data.sql.bound.RowConverterFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 默认转换工厂
 *
 * @author noear
 * @since 3.0
 */
public class DefaultConverter implements RowConverterFactory<Object> {
    private static RowConverterFactory instance = new DefaultConverter();

    static {
        if (Solon.app() != null) {
            Solon.context().getBeanAsync(RowConverterFactory.class, bean -> {
                instance = bean;
            });
        }
    }

    /**
     * 获取实例
     */
    public static RowConverterFactory getInstance() {
        return instance;
    }

    /**
     * 设置实例
     */
    public static void setInstance(RowConverterFactory instance) {
        if (instance != null) {
            DefaultConverter.instance = instance;
        }
    }

    @Override
    public RowConverter<Object> create(Class<?> tClass) {
        return new RowConverterImpl(tClass);
    }

    private static class RowConverterImpl implements RowConverter<Object> {
        private final Class<?> tClass;
        private ResultSetMetaData metaData;

        public RowConverterImpl(Class<?> tClass) {
            this.tClass = tClass;
        }

        @Override
        public Object convert(ResultSet rs) throws SQLException {
            if (metaData == null) {
                metaData = rs.getMetaData();
            }

            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i);
                Object value = rs.getObject(i);
                map.put(name, value);
            }

            if (Map.class == tClass) {
                return map;
            } else {
                return ONode.load(map).toObject(tClass);
            }
        }
    }
}