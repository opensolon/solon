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
package org.noear.solon.core.util;

import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.exception.ConstructionException;
import org.noear.solon.core.wrap.ClassWrap;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

/**
 * 类操作工具
 *
 * @author noear
 * @since 2.2
 * @since 3.0
 */
public class ClassUtil {
    /**
     * 是否为数字类型
     */
    public static boolean isNumberType(Class<?> clz) {
        if (clz.isPrimitive()) {
            return clz == Byte.TYPE || clz == Short.TYPE || clz == Integer.TYPE ||
                    clz == Long.TYPE || clz == Float.TYPE || clz == Double.TYPE;
        }

        return Number.class.isAssignableFrom(clz);
    }

    /**
     * 是否存在某个类
     *
     * <pre><code>
     * if(ClassUtil.hasClass(()->DemoTestClass.class)){
     *     ...
     * }
     * </code></pre>
     *
     * @param test 检测函数
     */
    public static boolean hasClass(SupplierEx<Class<?>> test) {
        try {
            test.get();
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 根据字符串加载为一个类（如果类不存在返回 null）
     *
     * @param className 类名称
     */
    public static Class<?> loadClass(String className) {
        return loadClass(null, className);
    }

    /**
     * 根据字符串加载为一个类（如果类不存在返回 null）
     *
     * @param classLoader 类加载器
     * @param className   类名称
     */
    public static Class<?> loadClass(ClassLoader classLoader, String className) {
        try {
            if (classLoader == null) {
                return Class.forName(className);
            } else {
                return classLoader.loadClass(className);
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return null;
        }
    }

    /**
     * 尝试根据类名实例化一个对象（如果类不存在返回 null）
     *
     * @param className 类名称
     */
    public static <T> T tryInstance(String className) {
        return tryInstance(className, null);
    }


    /**
     * 尝试根据类名实例化一个对象（如果类不存在返回 null）
     *
     * @param className 类名称
     * @param prop      属性
     */
    public static <T> T tryInstance(String className, Properties prop) {
        return tryInstance(AppClassLoader.global(), className, prop);
    }


    /**
     * 尝试根据类名实例化一个对象（如果类不存在返回 null）
     *
     * @param classLoader 类加载器
     * @param className   类名称
     */
    public static <T> T tryInstance(ClassLoader classLoader, String className) {
        return tryInstance(classLoader, className, null);
    }


    /**
     * 尝试根据类名实例化一个对象（如果类不存在返回 null）
     *
     * @param classLoader 类加载器
     * @param className   类名称
     * @param prop        属性
     */
    public static <T> T tryInstance(ClassLoader classLoader, String className, Properties prop) {
        Class<?> clz = loadClass(classLoader, className);

        return tryInstance(clz, prop);
    }

    public static <T> T tryInstance(Class<?> clz, Properties prop) {
        if (clz == null) {
            return null;
        } else {
            return newInstance(clz, prop);
        }
    }

    /**
     * 根据类名实例化一个对象
     *
     * @param clz 类
     */
    public static <T> T newInstance(Class<?> clz) throws ConstructionException {
        return newInstance(clz, null);
    }


    /**
     * 根据类名实例化一个对象
     *
     * @param clz  类
     * @param prop 属性
     */
    public static <T> T newInstance(Class<?> clz, Properties prop) throws ConstructionException {
        try {
            if (prop == null) {
                return (T) clz.getDeclaredConstructor().newInstance();
            } else {
                return (T) clz.getConstructor(Properties.class).newInstance(prop);
            }
        } catch (Exception e) {
            throw new ConstructionException("Instantiation failure: " + clz.getName(), e);
        }
    }

    /**
     * 根据类名和参数类型实例化一个对象
     *
     * @param clz   类
     * @param types 构建参数类型
     * @param args  参数
     */
    public static Object newInstance(Class<?> clz, Class<?>[] types, Object[] args) {
        try {
            Constructor<?> constructor = clz.getDeclaredConstructor(types);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new ConstructionException("Instantiation failure: " + clz.getName(), e);
        }
    }

    /**
     * 根据构造函数实例化一个对象
     *
     * @param constructor 构造器
     * @param args        参数
     */
    public static Object newInstance(Constructor constructor, Object[] args) {
        if (constructor == null) {
            throw new IllegalArgumentException("constructor is null");
        }

        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new ConstructionException("Instantiation failure: " + constructor.getDeclaringClass().getName(), e);
        }
    }


    /////////////////

    private static final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

    /**
     * 分析类加载器
     */
    public static ClassLoader resolveClassLoader(Type type) {
        ClassLoader loader = AppClassLoader.global();

        if (type != null) {
            Class<?> clz = getTypeClass(type);

            if (clz != Object.class) {
                ClassLoader cl = clz.getClassLoader();
                if (cl != systemClassLoader) {
                    loader = cl;
                }
            }
        }

        return loader;
    }

    /**
     * 获取类
     */
    public static Class<?> getTypeClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            //ParameterizedType
            return getTypeClass(((ParameterizedType) type).getRawType());
        } else {
            //TypeVariable
            return Object.class;
        }
    }

    /**
     * 比较参数类型
     */
    public static boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
        if (params1.length == params2.length) {
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] != params2[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 查找 method
     */
    public static Collection<Method> findPublicMethods(Class<?> clz) {
        return ClassWrap.get(clz).findPublicMethods();
    }

    //A.class
    //a.*.class
    //a.**.*.class
    //a.**.b.*.class

    //a.*
    //a.**.*
    //a.**.b.*
    //a.**.b.*Mapper
    //a.**.b //算包名
    //a.**.B //算类名

    private static Predicate<Class<?>> SCAN_CLASSES_FILTER_DEF = clz -> true;

    /**
     * 扫描类
     *
     * @param clzExpr 类表达式（基于 import 表达式扩展）
     * @since 3.0
     */
    public static Collection<Class<?>> scanClasses(String clzExpr) {
        return scanClasses(AppClassLoader.global(), clzExpr, SCAN_CLASSES_FILTER_DEF);
    }

    /**
     * 扫描类
     *
     * @param clzExpr   类名表达式（基于 import 表达式扩展）
     * @param clzFilter 类过滤器
     * @since 3.0
     */
    public static Collection<Class<?>> scanClasses(String clzExpr, Predicate<Class<?>> clzFilter) {
        return scanClasses(AppClassLoader.global(), clzExpr, clzFilter);
    }

    /**
     * 扫描类
     *
     * @param classLoader 类加载器
     * @param clzExpr     类名表达式（基于 import 表达式扩展）
     * @since 3.0
     */
    public static Collection<Class<?>> scanClasses(ClassLoader classLoader, String clzExpr) {
        return scanClasses(classLoader, clzExpr, SCAN_CLASSES_FILTER_DEF);
    }

    /**
     * 扫描类
     *
     * @param classLoader 类加载器
     * @param clzExpr     类名表达式（基于 import 表达式扩展）
     * @param clzFilter   类过滤器
     * @since 3.0
     */
    public static Collection<Class<?>> scanClasses(ClassLoader classLoader, String clzExpr, Predicate<Class<?>> clzFilter) {
        List<Class<?>> clzList = new ArrayList<>();

        if (clzExpr.indexOf('*') < 0) {
            if (clzExpr.endsWith(".class")) {
                //说明是单个类
                clzExpr = clzExpr.substring(0, clzExpr.length() - 6);

                Class<?> clz = ClassUtil.loadClass(classLoader, clzExpr);
                if (clz != null && clzFilter.test(clz)) {
                    clzList.add(clz);
                }

                return clzList;
            } else {
                int idx = clzExpr.lastIndexOf('.');
                if (idx > 0 && Character.isLowerCase(clzExpr.charAt(idx + 1))) { //44=$ 97=a
                    //开头为小写，算作是包名
                    clzExpr = clzExpr + ".*";
                } else {
                    Class<?> clz = ClassUtil.loadClass(classLoader, clzExpr);
                    if (clz != null && clzFilter.test(clz)) {
                        clzList.add(clz);
                    }

                    return clzList;
                }
            }
        }


        //说明是一批类
        if (clzExpr.endsWith(".class")) {
            clzExpr = clzExpr.substring(0, clzExpr.length() - 6);
        } else {
            int idx = clzExpr.lastIndexOf('.');
            if (idx > 0 && clzExpr.indexOf('*', idx) < 0) {
                //.后面没带*，可能是包名
                if (Character.isLowerCase(clzExpr.charAt(idx + 1))) { //44=$ 97=a
                    //开头为小写，算作是包名
                    clzExpr = clzExpr + ".*";
                }
            }
        }

        clzExpr = clzExpr.replace('.', '/');
        clzExpr = clzExpr + ".class"; //查找时要带 class

        ResourceUtil.scanResources(classLoader, clzExpr).forEach(name -> {
            String className = name.substring(0, name.length() - 6);
            className = className.replace('/', '.');

            Class<?> clz = ClassUtil.loadClass(classLoader, className);
            if (clz != null && clzFilter.test(clz)) {
                clzList.add(clz);
            }
        });

        return clzList;
    }
}