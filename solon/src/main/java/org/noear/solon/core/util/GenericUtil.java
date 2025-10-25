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
import org.noear.eggg.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.util.*;

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
     * */
    public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        for (Type supIfc : getGenericInterfaces(clazz)) {
            if (supIfc instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) supIfc;
                Class<?> rawClz = (Class<?>) type.getRawType();

                if (rawClz == genericIfc || getDeepGenericInterfaces(rawClz).contains(genericIfc)) {
                    return Arrays.stream(type.getActualTypeArguments())
                            .filter(item -> item instanceof Class<?>)
                            .map(item -> (Class<?>) item)
                            .toArray(Class[]::new);
                }
            } else if (supIfc instanceof Class<?>) {
                Class<?>[] classes = resolveTypeArguments((Class<?>) supIfc, genericIfc);
                if (classes != null) {
                    return classes;
                }
            }
        }

        Type supClz = getGenericSuperclass(clazz);
        if (supClz instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) supClz;
            return Arrays.stream(type.getActualTypeArguments())
                    .filter(item -> item instanceof Class<?>)
                    .map(item -> (Class<?>) item)
                    .toArray(Class[]::new);
        }

        return null;
    }



    /**
     * 转换为参数化类型
     * */
    public static ParameterizedType toParameterizedType(Type type) throws RuntimeException{
        return toParameterizedType(type, null);
    }

    /**
     * 转换为参数化类型
     *
     * @param genericInfo 泛型信息
     * @since 3.0
     * */
    public static ParameterizedType toParameterizedType(Type type, Map<String, Type> genericInfo) throws RuntimeException{
        if (type == null) {
            return null;
        }

        ParameterizedType result = null;
        if (type instanceof ParameterizedType) {
            result = (ParameterizedType) type;

            if (Assert.isEmpty(genericInfo) == false) {
                //如果有泛型信息，做二次分析转换变量符
                boolean typeArgsChanged = false;
                Type[] typeArgs = result.getActualTypeArguments();
                Class<?> rawClz = (Class<?>) result.getRawType();
                for (int i = 0; i < typeArgs.length; i++) {
                    Type typeArg1 = typeArgs[i];
                    if (typeArg1 instanceof TypeVariable) {
                        typeArg1 = genericInfo.get(typeArg1.getTypeName());
                        if (typeArg1 != null) {
                            typeArgsChanged = true;
                            typeArgs[i] = typeArg1;
                        }
                    }
                }

                if (typeArgsChanged) {
                    result = new ParameterizedTypeImpl(rawClz, typeArgs, result.getOwnerType());
                }
            }
        } else if (type instanceof Class) {
            final Class<?> clazz = (Class<?>) type;
            Type genericSuper = getGenericSuperclass(clazz);
            if (null == genericSuper || Object.class.equals(genericSuper)) {
                // 如果类没有父类，而是实现一些定义好的泛型接口，则取接口的 Type
                final Type[] genericInterfaces = getGenericInterfaces(clazz);
                if (genericInterfaces != null && genericInterfaces.length > 0) {
                    // 默认取第一个实现接口的泛型 Type
                    genericSuper = genericInterfaces[0];
                }
            }

            result = toParameterizedType(genericSuper, genericInfo);
        }
        return result;
    }

    ///////////////////////////


    /**
     * 获取泛型变量和泛型实际类型的对应关系Map
     *
     * @param type 被解析的包含泛型参数的类
     * @return 泛型对应关系Map
     */
    public static Map<String, Type> getGenericInfo(Type type) {
        return GenericResolver.getDefault().getGenericInfo(type);
    }





    /**
     * 审查类型
     *
     * @param type        原始类型
     * @param genericInfo 泛型信息类
     * @since 3.0
     * */
    public static Type reviewType(Type type, Type genericInfo) {
        if (type instanceof TypeVariable || type instanceof ParameterizedType) {
            return reviewType(type, getGenericInfo(genericInfo));
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
     * */
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
    public static boolean wildcardMatched(WildcardType w1, Class<?> s1) {
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

    private static Type getGenericSuperclass(Class<?> clazz) throws RuntimeException{
//        try {
            return clazz.getGenericSuperclass();
//        } catch (Throwable e) {
//            if (e instanceof TypeNotPresentException) {
//                return null;
//            } else if (e instanceof ClassNotFoundException) {
//                return null;
//            } else if (e instanceof RuntimeException) {
//                throw e;
//            } else {
//                throw new RuntimeException(e);
//            }
//        }
    }

    //private static final Type[] GENERIC_INTERFACES_EMPTY = new  Type[0];

    private static Type[] getGenericInterfaces(Class<?> clazz) {
//        try {
            return clazz.getGenericInterfaces();
//        } catch (Throwable e) {
//            if (e instanceof TypeNotPresentException) {
//                return GENERIC_INTERFACES_EMPTY;
//            } else if (e instanceof ClassNotFoundException) {
//                return GENERIC_INTERFACES_EMPTY;
//            } else if (e instanceof RuntimeException) {
//                throw e;
//            } else {
//                throw new RuntimeException(e);
//            }
//        }
    }

    /**
     * 获取指定类的所有父接口
     *
     * @param clazz 要获取的类
     * @return 所有父接口
     */
    private static List<Class<?>> getDeepGenericInterfaces(Class<?> clazz) {
        return getDeepGenericInterfaces(clazz, new ArrayList<>());
    }

    /**
     * 获取指定类的所有父类
     *
     * @param clazz 要获取的类
     * @return 所有父类
     */
    private static List<Class<?>> getDeepGenericInterfaces(Class<?> clazz, List<Class<?>> classes) {
        for (Type supIfc : getGenericInterfaces(clazz)) {
            if (supIfc instanceof ParameterizedType) {
                Class<?> rawClz = (Class<?>) ((ParameterizedType) supIfc).getRawType();
                classes.add(rawClz);
                getDeepGenericInterfaces(rawClz, classes);
            }
        }
        return classes;
    }
}