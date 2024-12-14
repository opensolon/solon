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
package org.noear.solon.rx.r2dbc.impl;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.rx.r2dbc.bound.RxRowConverter;
import org.noear.solon.rx.r2dbc.bound.RxRowConverterFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 默认转换工厂
 *
 * @author noear
 * @since 3.0
 */
public class DefaultRxConverter implements RxRowConverterFactory<Object> {
    private static RxRowConverterFactory instance = new DefaultRxConverter();

    static {
        if (Solon.app() != null) {
            Solon.context().getBeanAsync(RxRowConverterFactory.class, bean -> {
                instance = bean;
            });
        }
    }

    /**
     * 获取实例
     */
    public static RxRowConverterFactory getInstance() {
        return instance;
    }

    /**
     * 设置实例
     */
    public static void setInstance(RxRowConverterFactory instance) {
        if (instance != null) {
            DefaultRxConverter.instance = instance;
        }
    }

    @Override
    public RxRowConverter<Object> create(Class<?> tClass) {
        return new RowConverterImpl(tClass);
    }

    private static class RowConverterImpl implements RxRowConverter<Object> {
        private final Class<?> tClass;

        public RowConverterImpl(Class<?> tClass) {
            this.tClass = tClass;
        }


        @Override
        public Object convert(Row row, RowMetadata metaData) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 1; i <= metaData.getColumnMetadatas().size(); i++) {
                String name = metaData.getColumnMetadata(i).getName();
                Object value = row.get(i);
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