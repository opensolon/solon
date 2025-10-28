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
     * */
    public static boolean genericMatched(ParameterizedType checkType, ParameterizedType sourceType) {
        if (sourceType.getActualTypeArguments().length == checkType.getActualTypeArguments().length) {
            if (sourceType.getTypeName().equals(checkType.getTypeName())) {
                return true;
            } else {
                if (sourceType.getRawType().equals(checkType.getRawType())) {
                    Type[] typesC = checkType.getActualTypeArguments();
                    Type[] typesS = sourceType.getActualTypeArguments();

                    boolean isOk = true;
                    for (int i = 0; i < typesC.length; i++) {
                        Type c1 = typesC[i];
                        Type s1 = typesS[i];

                        if (c1 instanceof Class) {
                            isOk = c1.equals(s1);
                        } else if (c1 instanceof ParameterizedType) {
                            if (s1 instanceof ParameterizedType) {
                                isOk = genericMatched((ParameterizedType) c1, (ParameterizedType) s1);
                            } else {
                                isOk = false;
                            }
                        } else if (c1 instanceof GenericArrayType) {
                            isOk = c1.equals(s1);
                        } else if (c1 instanceof WildcardType) {
                            if (s1 instanceof Class) {
                                isOk = wildcardMatched((WildcardType) c1, (Class<?>) s1);
                            } else if (s1 instanceof ParameterizedType) {
                                Type s2 = ((ParameterizedType) s1).getRawType();
                                if (s2 instanceof Class) {
                                    isOk = wildcardMatched((WildcardType) c1, (Class<?>) s2);
                                }
                            }

                            //其它情况算 ok
                        } else {
                            isOk = false;
                        }

                        if (isOk == false) {
                            break;
                        }
                    }

                    if (isOk) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 通配类型匹配
     * */
    private static boolean wildcardMatched(WildcardType w1, Class<?> s1) {
        for (Type b1 : w1.getUpperBounds()) {
            if (b1 instanceof Class) {
                if (((Class<?>) b1).isAssignableFrom(s1) == false) {
                    return false;
                }
            }
        }

        for (Type b1 : w1.getLowerBounds()) {
            if (b1 instanceof Class) {
                if ((s1).isAssignableFrom((Class<?>) b1) == false) {
                    return false;
                }
            }
        }

        return true;
    }
}