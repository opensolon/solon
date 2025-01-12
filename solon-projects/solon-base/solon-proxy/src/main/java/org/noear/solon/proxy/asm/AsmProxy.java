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
package org.noear.solon.proxy.asm;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.exception.ConstructionException;
import org.noear.solon.core.util.ClassUtil;
import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AsmProxy {
    public static final int ASM_VERSION = Opcodes.ASM9;
    // 动态生成代理类的后缀
    public static final String PROXY_CLASSNAME_SUFFIX = "$$SolonAsmProxy";
    // 方法名
    private static final String METHOD_SETTER = "setInvocationHandler";


    public static Class<?> getProxyClass(AppContext context, Class<?> targetClass) throws Exception {
        String proxyClassName = targetClass.getName() + PROXY_CLASSNAME_SUFFIX;

        //目标代理类名
        Class<?> proxyClass = null;

        //确定代理类加载器
        AsmProxyClassLoader classLoader = context.attachGet(AsmProxyClassLoader.class);
        if (classLoader == null) {
            classLoader = new AsmProxyClassLoader(context.getClassLoader());
            context.attachSet(AsmProxyClassLoader.class, classLoader);
        } else {
            //尝试获取类
            proxyClass = ClassUtil.loadClass(classLoader, proxyClassName);
        }

        if (proxyClass == null) {
            //构建新的代理类
            proxyClass = ClassCodeBuilder.build(targetClass, classLoader);
        }

        return proxyClass;
    }

    /**
     * 返回一个动态创建的代理类，此类继承自 targetClass
     *
     * @param invocationHandler 代理类中每一个方法调用时的回调接口
     * @param targetClass       被代理对象
     * @return 代理实例
     */
    public static Object newProxyInstance(AppContext context,
                                          InvocationHandler invocationHandler,
                                          Class<?> targetClass) throws ConstructionException {

        return newProxyInstance(context, invocationHandler, targetClass, null, null);

    }

    /**
     * 返回一个动态创建的代理类，此类继承自 targetClass
     *
     * @param invocationHandler 代理类中每一个方法调用时的回调接口
     * @param targetClass       被代理对象
     * @param targetConstructor 被代理对象的某一个构造器，用于决定代理对象实例化时采用哪一个构造器
     * @param targetParam       被代理对象的某一个构造器的参数，用于实例化构造器
     * @return 代理实例
     */
    public static Object newProxyInstance(AppContext context,
                                          InvocationHandler invocationHandler,
                                          Class<?> targetClass,
                                          Constructor<?> targetConstructor,
                                          Object[] targetParam) throws ConstructionException {
        if (targetClass == null) {
            throw new IllegalArgumentException("The targetClass is null");
        }

        if (invocationHandler == null) {
            throw new IllegalArgumentException("The invocationHandler is null");
        }

        try {
            //支持“带参”构造函数
            if (targetConstructor == null) {
                targetConstructor = targetClass.getDeclaredConstructor();
                targetParam = new Object[]{};
            }

            Class<?> proxyClass = getProxyClass(context, targetClass);

            // 实例化代理对象
            return newInstance(proxyClass, invocationHandler, targetConstructor, targetParam);
        } catch (Throwable e) {
            throw new ConstructionException("New proxy instance failed: " + targetClass.getName(), e);
        }
    }

    /**
     * 根据被代理类的构造器，构造代理类对象。生成代理类的实例时调用其setter方法
     */
    private static Object newInstance(Class<?> proxyClass,
                                      InvocationHandler invocationHandler,
                                      Constructor<?> targetConstructor,
                                      Object[] targetParam) throws Exception {
        Class<?>[] parameterTypes = targetConstructor.getParameterTypes();
        Constructor<?> constructor = proxyClass.getConstructor(parameterTypes);
        Object instance = constructor.newInstance(targetParam);
        Method setterMethod = proxyClass.getDeclaredMethod(METHOD_SETTER, InvocationHandler.class);
        setterMethod.setAccessible(true);
        setterMethod.invoke(instance, invocationHandler);

        return instance;
    }
}