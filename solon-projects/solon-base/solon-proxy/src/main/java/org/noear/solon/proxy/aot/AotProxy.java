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
package org.noear.solon.proxy.aot;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.exception.ConstructionException;
import org.noear.solon.core.util.ClassUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

/**
 * @author noear
 * @since 2.1
 */
public class AotProxy {
    public static final String PROXY_CLASSNAME_SUFFIX = "$$SolonAotProxy";

    /**
     * 返回一个动态创建的代理类，此类继承自 targetClass（或许是算静态代理类）
     *
     * @param invocationHandler 代理类中每一个方法调用时的回调接口
     * @param targetClass       被代理对象
     * @return 代理实例
     */
    public static Object newProxyInstance(AppContext context,
                                          InvocationHandler invocationHandler,
                                          Class<?> targetClass,
                                          Object[] args) {
        //支持APT (支持 Graalvm Native  打包)
        String proxyClassName = targetClass.getName() + PROXY_CLASSNAME_SUFFIX;
        Class<?> proxyClass = ClassUtil.loadClass(context.getClassLoader(), proxyClassName);

        if (proxyClass == null) {
            return null;
        } else {
            try {
                if (args == null) {
                    args = new Object[0];
                }

                Constructor constructor = proxyClass.getConstructor(InvocationHandler.class, Object[].class);
                return constructor.newInstance(invocationHandler, args);
            } catch (Exception e) {
                throw new ConstructionException("Failed to generate the proxy instance: " + targetClass.getName(), e);
            }
        }
    }
}
