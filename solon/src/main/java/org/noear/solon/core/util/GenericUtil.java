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

    private static final Map<Type, Map<String, Type>> genericInfoCached = new ConcurrentHashMap<>();

    /**
     * 获取泛型变量和泛型实际类型的对应关系Map
     *
     * @param type 被解析的包含泛型参数的类
     * @return 泛型对应关系Map
     */
    public static Map<String, Type> getGenericInfo(Type type) {
        return genericInfoCached.computeIfAbsent(type, GenericUtil::createTypeGenericMap);
    }


    /**
     * 创建类中所有的泛型变量和泛型实际类型的对应关系Map
     *
     * @param type 被解析的包含泛型参数的类
     * @return 泛型对应关系Map
     */
    private static Map<String, Type> createTypeGenericMap(Type type) {
        try {
            final Map<String, Type> typeMap = new HashMap<>();

            // 按继承层级寻找泛型变量和实际类型的对应关系
            // 在类中，对应关系分为两类：
            // 1. 父类定义变量，子类标注实际类型
            // 2. 父类定义变量，子类继承这个变量，让子类的子类去标注，以此类推
            // 此方法中我们将每一层级的对应关系全部加入到Map中，查找实际类型的时候，根据传入的泛型变量，
            // 找到对应关系，如果对应的是继承的泛型变量，则递归继续找，直到找到实际或返回null为止。
            // 如果传入的非Class，例如TypeReference，获取到泛型参数中实际的泛型对象类，继续按照类处理
            while (null != type) {
                final ParameterizedType parameterizedType = toParameterizedType(type, typeMap);
                if (null == parameterizedType) {
                    break;
                }
                final Type[] typeArguments = parameterizedType.getActualTypeArguments();
                final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                final TypeVariable[] typeParameters = rawType.getTypeParameters();

                Type value;
                for (int i = 0; i < typeParameters.length; i++) {
                    value = typeArguments[i];
                    // 跳过泛型变量对应泛型变量的情况
                    if (false == value instanceof TypeVariable) {
                        if (checkNoTypeVariable(value)) {
                            typeMap.put(typeParameters[i].getTypeName(), value);
                        }
                    }
                }

                type = rawType;
            }

            return typeMap;
        } catch (Throwable ex) {
            throw new IllegalStateException("Can't create generic info: " + type, ex);
        }
    }

    private static boolean checkNoTypeVariable(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            for (Type rawType : parameterizedType.getActualTypeArguments()) {
                if (rawType instanceof TypeVariable) {
                    //说明没有真实的泛型传入
                    return false;
                }
            }
        }

        return true;
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
        if(genericInfo == null || genericInfo.isEmpty()) {
            return type;
        }

        if (type instanceof TypeVariable) {
            //如果是类型变量，则重新构建类型
            Type typeTmp = genericInfo.get(type.getTypeName());

            if(typeTmp == null){
                return type;
            }else{
                return reviewType(typeTmp, genericInfo);
            }
        }  else if(type instanceof WildcardType) {
            WildcardType typeTmp = (WildcardType) type;

            if (typeTmp.getUpperBounds().length > 0) {
                //? ext T
                Type type1 = typeTmp.getUpperBounds()[0];

                if (type1 instanceof TypeVariable) {
                    return reviewType(type1, genericInfo);
                }
            }

            if (typeTmp.getLowerBounds().length > 0) {
                //? sup T
                Type type1 = typeTmp.getLowerBounds()[0];

                if (type1 instanceof TypeVariable) {
                    return reviewType(type1, genericInfo);
                }
            }

            return type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType typeTmp = (ParameterizedType) type;
            Type[] typeArgs = typeTmp.getActualTypeArguments();
            boolean typeChanged = false;

            for (int i = 0; i < typeArgs.length; i++) {
                Type t1 = typeArgs[i];
                typeArgs[i] = reviewType(t1, genericInfo);
                if (typeArgs[i] != t1) {
                    typeChanged = true;
                }
            }

            if (typeChanged) {
                return new ParameterizedTypeImpl((Class<?>) typeTmp.getRawType(), typeArgs, typeTmp.getOwnerType());
            } else {
               return type;
            }
        } else if (type instanceof GenericArrayType) {
            GenericArrayType typeTmp = (GenericArrayType) type;

            if (typeTmp.getGenericComponentType() instanceof Class == false) {
                Type typCom = reviewType(typeTmp.getGenericComponentType(), genericInfo);
                return new GenericArrayTypeImpl(typCom);
            } else {
                return typeTmp;
            }
        }

        return type;
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