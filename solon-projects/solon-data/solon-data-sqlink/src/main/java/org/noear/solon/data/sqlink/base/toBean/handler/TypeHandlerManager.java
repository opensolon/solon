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
package org.noear.solon.data.sqlink.base.toBean.handler;

import org.noear.solon.data.sqlink.base.toBean.handler.impl.datetime.*;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.number.*;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.other.URLTypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.varchar.CharTypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.varchar.StringTypeHandler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 类型处理器管理器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class TypeHandlerManager {
    private static final Map<Type, ITypeHandler<?>> cache = new HashMap<>();
    private static final Map<Class<? extends ITypeHandler<?>>, ITypeHandler<?>> handlerCache = new HashMap<>();
    private static final UnKnowTypeHandler<?> unKnowTypeHandler = new UnKnowTypeHandler<>();

    /**
     * 设置处理器
     */
    public static <T> void set(ITypeHandler<T> typeHandler) {
        Type actualType = typeHandler.getGenericType();
        warpBaseType(actualType, typeHandler);
        cache.put(actualType, typeHandler);
    }

    static {
        //varchar
        set(new CharTypeHandler());
        set(new StringTypeHandler());

        //number
        set(new ByteTypeHandler());
        set(new ShortTypeHandler());
        set(new IntTypeHandler());
        set(new LongTypeHandler());
        set(new BoolTypeHandler());
        set(new FloatTypeHandler());
        set(new DoubleTypeHandler());
        set(new BigIntegerTypeHandler());
        set(new BigDecimalTypeHandler());

        //datetime
        set(new DateTypeHandler());
        set(new UtilDateHandler());
        set(new TimeTypeHandler());
        set(new TimestampTypeHandler());
        set(new LocalDateTimeTypeHandler());
        set(new LocalDateTypeHandler());
        set(new LocalTimeTypeHandler());

        //other
        set(new URLTypeHandler());
    }

    /**
     * 获取处理器, 获取失败时返回默认处理器
     *
     * @param type 目标类型
     */
    public static <T> ITypeHandler<T> get(Type type) {
        ITypeHandler<T> iTypeHandler = (ITypeHandler<T>) cache.get(type);
        if (iTypeHandler == null) {
            return (ITypeHandler<T>) unKnowTypeHandler;
        }
        return iTypeHandler;
    }

    /**
     * 通过处理器类型获取处理器
     *
     * @param handlerType 处理器类型
     */
    public static <T> ITypeHandler<T> getByHandlerType(Class<? extends ITypeHandler<T>> handlerType) {
        ITypeHandler<T> typeHandler = (ITypeHandler<T>) handlerCache.get(handlerType);
        if (typeHandler == null) {
            try {
                typeHandler = handlerType.newInstance();
                handlerCache.put(handlerType, typeHandler);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return typeHandler;
    }

    /**
     * 封装基础类型
     */
    private static void warpBaseType(Type actualType, ITypeHandler<?> typeHandler) {
        if (actualType == Character.class) {
            cache.put(char.class, typeHandler);
        }
        else if (actualType == Byte.class) {
            cache.put(byte.class, typeHandler);
        }
        else if (actualType == Short.class) {
            cache.put(short.class, typeHandler);
        }
        else if (actualType == Integer.class) {
            cache.put(int.class, typeHandler);
        }
        else if (actualType == Long.class) {
            cache.put(long.class, typeHandler);
        }
        else if (actualType == Float.class) {
            cache.put(float.class, typeHandler);
        }
        else if (actualType == Double.class) {
            cache.put(double.class, typeHandler);
        }
        else if (actualType == Boolean.class) {
            cache.put(boolean.class, typeHandler);
        }
    }
}
