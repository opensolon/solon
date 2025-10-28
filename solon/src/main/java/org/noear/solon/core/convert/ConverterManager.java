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
package org.noear.solon.core.convert;

import org.noear.solon.core.util.EgggUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 转换管理器
 *
 * @author noear
 * @since 2.4
 */
public class ConverterManager {
    private Map<Type, Map<Type, Converter>> cLib = new HashMap<>();

    private Map<Type, Map<Class<?>, ConverterFactory>> cfLib = new HashMap<>();

    /**
     * 注册转换器
     *
     * @param converter 转换器
     */
    public <S, T> void register(Converter<S, T> converter) {
        Map<String, Type> giMap = EgggUtil.findGenericInfo(converter.getClass(), Converter.class);
        Type sType = null;
        Type tType = null;

        if (giMap != null) {
            sType = giMap.get("S");
            tType = giMap.get("T");
        }

        if (sType == null || sType == Object.class) {
            throw new IllegalArgumentException("Invalid converter source type: " + converter.getClass().getName());
        }

        if (tType == null || tType == Object.class) {
            throw new IllegalArgumentException("Invalid converter source type: " + converter.getClass().getName());
        }

        Map<Type, Converter> tmp = cLib.get(sType);
        if (tmp == null) {
            tmp = new HashMap<>();
            cLib.put(sType, tmp);
        }

        tmp.put(tType, converter);
    }

    /**
     * 注册转换器
     *
     * @param converterFactory 转换器工厂
     */
    public <S, R> void register(ConverterFactory<S, R> converterFactory) {
        Map<String, Type> giMap = EgggUtil.findGenericInfo(converterFactory.getClass(), ConverterFactory.class);
        Type sType = null;
        Type rType = null;

        if (giMap != null) {
            sType = giMap.get("S");
            rType = giMap.get("R");
        }

        if (sType == null || sType == Object.class) {
            throw new IllegalArgumentException("Invalid converterFactory source type: " + converterFactory.getClass().getName());
        }

        if (rType == null || rType == Object.class || (rType instanceof Class) == false) {
            throw new IllegalArgumentException("Invalid converterFactory result type: " + converterFactory.getClass().getName());
        }

        Map<Class<?>, ConverterFactory> tmp = cfLib.get(sType);
        if (tmp == null) {
            tmp = new HashMap<>();
            cfLib.put(sType, tmp);
        }

        tmp.put((Class<?>) rType, converterFactory);
    }

    /**
     * 查找转换器
     *
     * @param sourceType 源类型
     * @param tagertType 目标类型
     */
    public <S, T> Converter<S, T> find(Class<S> sourceType, Class<T> tagertType) {
        Map<Type, Converter> cMap = cLib.get(sourceType);
        if (cMap == null) {
            return findInFactory(sourceType, tagertType);
        } else {
            Converter c = cMap.get(tagertType);

            if (c == null) {
                return findInFactory(sourceType, tagertType);
            } else {
                return c;
            }
        }
    }

    /**
     * 查找转换器（在工厂里找）
     *
     * @param sourceType 源类型
     * @param tagertType 目标类型
     */
    public <S, T> Converter<S, T> findInFactory(Class<S> sourceType, Class<T> tagertType) {
        Map<Class<?>, ConverterFactory> cfMap = cfLib.get(sourceType);

        if (cfMap == null) {
            return null;
        } else {
            for (Map.Entry<Class<?>, ConverterFactory> kv : cfMap.entrySet()) {
                if (kv.getKey().isAssignableFrom(tagertType)) {
                    return kv.getValue().getConverter(tagertType);
                }
            }
        }

        return null;
    }
}
