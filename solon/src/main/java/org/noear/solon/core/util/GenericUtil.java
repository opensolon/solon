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
package org.noear.solon.core.util;

import org.noear.eggg.GenericResolver;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 泛型处理工具
 *
 * @author 颖
 * @since 1.5
 * @since 3.0
 */
public class GenericUtil {
    /**
     * 分析类型参数
     *
     * <pre>{@code
     * public class DemoEventListener extend EventListener<Demo>{ }
     * Class<?>[] tArgs = GenericUtil.resolveTypeArguments(DemoEventListener.class, EventListener.class);
     * assert tArgs[0] == Demo.class
     * }</pre>
     * @param clazz     类型
     * @param genericIfc 泛型接口
     * @deprecated 3.7 {@link EgggUtil#findGenericList(Type, Class)}
     * */
    @Deprecated
    public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        List<Class<?>> types = new ArrayList<>();
        for (Type type : EgggUtil.findGenericList(clazz, genericIfc)) {
            if (type instanceof Class<?>) {
                types.add((Class<?>) type);
            }
        }

        return types.toArray(new Class<?>[types.size()]);
    }



    /**
     * 转换为参数化类型
     *
     * @deprecated 3.7
     * */
    @Deprecated
    public static ParameterizedType toParameterizedType(Type type) throws RuntimeException{
        return toParameterizedType(type, null);
    }

    /**
     * 转换为参数化类型
     *
     * @param genericInfo 泛型信息
     * @since 3.0
     * @deprecated 3.7
     * */
    @Deprecated
    public static ParameterizedType toParameterizedType(Type type, Map<String, Type> genericInfo) throws RuntimeException{
       return GenericResolver.getDefault().toParameterizedType(type, genericInfo);
    }

    ///////////////////////////


    private static final Map<Type,Map<String, Type>> genericInfoCached = new ConcurrentHashMap<>();

    /**
     * 获取泛型变量和泛型实际类型的对应关系Map
     *
     * @param type 被解析的包含泛型参数的类
     * @return 泛型对应关系Map
     */
    public static Map<String, Type> getGenericInfo(Type type) {
        return genericInfoCached.computeIfAbsent(type,k->GenericResolver.getDefault().createTypeDeepGenericMap(type));
    }





    /**
     * 审查类型
     *
     * @param type        原始类型
     * @param genericInfo 泛型信息类
     * @since 3.0
     * @deprecated 3.7
     * */
    @Deprecated
    public static Type reviewType(Type type, Type genericInfo) {
        if (type instanceof TypeVariable || type instanceof ParameterizedType) {
            return reviewType(type, EgggUtil.getTypeEggg(genericInfo).getGenericInfo());
        } else {
            return type;
        }
    }

    /**
     * 审查类型
     *
     * @param type        原始类型
     * @param genericInfo 泛型信息
     * @since 3.0
     * @deprecated 3.7
     * */
    @Deprecated
    public static Type reviewType(Type type, Map<String, Type> genericInfo) {
        return GenericResolver.getDefault().reviewType(type, genericInfo);
    }

    /// ////////////////

    /**
     * 参数类型（泛型）匹配
     *
     * @param checkType 检测类型
     * @param sourceType 源类型
     * @deprecated 3.7 {@link #typeMatched(Type, Type)}
     * */
    @Deprecated
    public static boolean genericMatched(ParameterizedType checkType, ParameterizedType sourceType) {
        return typeMatched(checkType,sourceType);
    }

    /**
     * 泛型类型匹配 (检查是否等效或兼容)
     *
     * @param checkType  检测类型（模式）
     * @param sourceType 源类型（实例）
     */
    public static boolean typeMatched(Type checkType, Type sourceType) {
        // 1. 快速路径和基本检查
        if (checkType == sourceType) {
            return true;
        }
        if (checkType == null || sourceType == null) {
            return false;
        }
        if (checkType.equals(sourceType)) {
            return true;
        }

        // 2. 类型分发处理
        return dispatchTypeMatch(checkType, sourceType);
    }

    /**
     * 类型分发匹配（线性分发）
     * 仅根据 checkType 的类型进行分发，简化逻辑。
     */
    private static boolean dispatchTypeMatch(Type checkType, Type sourceType) {
        // Class 和 TypeVariable：在 typeMatched 的 fast path 中未匹配到，则认为不等效。

        if (checkType instanceof ParameterizedType) {
            return matchParameterizedType((ParameterizedType) checkType, sourceType);
        }

        if (checkType instanceof WildcardType) {
            return matchWildcardType((WildcardType) checkType, sourceType);
        }

        if (checkType instanceof GenericArrayType) {
            return matchGenericArrayType((GenericArrayType) checkType, sourceType);
        }

        return false;
    }

    /**
     * 参数化类型匹配
     */
    private static boolean matchParameterizedType(ParameterizedType checkType, Type sourceType) {
        // 1. sourceType 必须是 ParameterizedType
        if (!(sourceType instanceof ParameterizedType)) {
            return false;
        }

        ParameterizedType sourcePType = (ParameterizedType) sourceType;

        // 2. 检查原始类型和所有者类型
        if (!typeMatched(checkType.getRawType(), sourcePType.getRawType()) ||
                !matchOwnerType(checkType.getOwnerType(), sourcePType.getOwnerType())) {
            return false;
        }

        // 3. 检查类型参数数量
        Type[] checkArgs = checkType.getActualTypeArguments();
        Type[] sourceArgs = sourcePType.getActualTypeArguments();

        if (checkArgs.length != sourceArgs.length) {
            return false;
        }

        // 4. 递归检查类型参数
        for (int i = 0; i < checkArgs.length; i++) {
            // 参数之间递归使用 typeMatched 检查等效性
            if (!typeMatched(checkArgs[i], sourceArgs[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * 所有者类型匹配
     */
    private static boolean matchOwnerType(Type owner1, Type owner2) {
        // 使用 typeMatched 递归检查所有者类型
        return owner1 == owner2 || (owner1 != null && owner2 != null && typeMatched(owner1, owner2));
    }

    /**
     * 泛型数组类型匹配
     */
    private static boolean matchGenericArrayType(GenericArrayType checkArray, Type sourceType) {
        if (sourceType instanceof GenericArrayType) {
            // 泛型数组 vs 泛型数组
            GenericArrayType sourceArray = (GenericArrayType) sourceType;
            return typeMatched(checkArray.getGenericComponentType(), sourceArray.getGenericComponentType());
        }

        if (sourceType instanceof Class) {
            // 泛型数组 vs Class 数组 (e.g., T[] vs String[])
            return matchGenericArrayToClass(checkArray, (Class<?>) sourceType);
        }

        return false;
    }

    /**
     * 泛型数组与 Class 数组匹配
     */
    private static boolean matchGenericArrayToClass(GenericArrayType genericArray, Class<?> classArray) {
        if (!classArray.isArray()) {
            return false;
        }

        Type componentType = genericArray.getGenericComponentType();
        Class<?> arrayComponentType = classArray.getComponentType();

        // 提取组件类型的原始类
        Class<?> genericComponentClass = extractRawClass(componentType);

        // 检查组件是否兼容 (e.g., String[] is assignable from T[] where T is Object)
        return genericComponentClass != null && arrayComponentType.isAssignableFrom(genericComponentClass);
    }

    /**
     * 通配符类型匹配
     * 【修正】禁止 WildcardType 匹配 TypeVariable 或 GenericArrayType 占位符。
     */
    private static boolean matchWildcardType(WildcardType wildcard, Type actualType) {
        // 1. WildcardType vs WildcardType: 在 fast path 中已检查 equals()。
        if (actualType instanceof WildcardType) {
            return false;
        }

        // 2. 【关键修正】禁止 WildcardType 匹配 TypeVariable 或 GenericArrayType 占位符。
        if (actualType instanceof TypeVariable || actualType instanceof GenericArrayType) {
            return false;
        }

        // 3. 检查 Class/ParameterizedType 的原始类边界
        Class<?> actualClass = extractRawClass(actualType);

        if (actualClass != null) {
            // 此时 actualClass 来源于 Class 或 ParameterizedType
            return matchWildcardToBounds(wildcard, actualClass);
        }

        // 覆盖其他未知 Type 实现。
        return false;
    }

    /**
     * 通配符边界与原始类匹配
     * 仅检查 Class 类型的上下界，避免复杂泛型子类型检查。
     */
    private static boolean matchWildcardToBounds(WildcardType w1, Class<?> s1) {
        // 检查上界 (? extends Number)
        for (Type upperBound : w1.getUpperBounds()) {
            Class<?> boundClass = extractRawClass(upperBound);
            // 边界检查：实际类型 s1 必须是上界 boundClass 的子类型
            if (boundClass != null && !boundClass.isAssignableFrom(s1)) {
                return false;
            }
        }

        // 检查下界 (? super Integer)
        for (Type lowerBound : w1.getLowerBounds()) {
            Class<?> boundClass = extractRawClass(lowerBound);
            // 边界检查：下界 boundClass 必须是实际类型 s1 的子类型
            if (boundClass != null && !s1.isAssignableFrom(boundClass)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 从 Type 中提取原始 Class
     *
     * @param type 泛型类型
     * @return 原始 Class，无法解析返回 null
     */
    private static Class<?> extractRawClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            return rawType instanceof Class ? (Class<?>) rawType : null;
        } else if (type instanceof GenericArrayType) {
            // 处理泛型数组
            return extractArrayClass((GenericArrayType) type);
        } else if (type instanceof WildcardType) {
            // 取第一个上界，默认为 Object.class
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            return upperBounds.length > 0 ? extractRawClass(upperBounds[0]) : Object.class;
        } else if (type instanceof TypeVariable) {
            // 取第一个边界，默认为 Object.class
            Type[] bounds = ((TypeVariable<?>) type).getBounds();
            return bounds.length > 0 ? extractRawClass(bounds[0]) : Object.class;
        }
        return null;
    }

    /**
     * 提取数组 Class
     */
    private static Class<?> extractArrayClass(GenericArrayType arrayType) {
        Type componentType = arrayType.getGenericComponentType();
        Class<?> componentClass = extractRawClass(componentType);

        if (componentClass != null) {
            try {
                // 动态创建数组 Class
                return Array.newInstance(componentClass, 0).getClass();
            } catch (Exception e) {
                // 忽略异常
            }
        }
        // 安全回退
        return Object[].class;
    }
}