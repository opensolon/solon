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
package org.noear.solon.proxy;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.proxy.aot.AotProxy;
import org.noear.solon.proxy.asm.AsmProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 代理工具（增强基础代理能力）
 *
 * @author noear
 * @since 1.5
 * @since 2.1
 * @since 3.0
 * */
public class ProxyUtil {
    /**
     * 构建代理实例
     */
    public static <T> T newProxyInstance(BeanWrap bw) {
        if (bw.proxy() == null) {
            bw.proxySet(BeanProxy.getGlobal());
        }

        return bw.get();
    }

    /**
     * 构建代理实例
     *
     * @param clazz   类
     * @param handler 调用处理
     */
    public static <T> T newProxyInstance(Class<T> clazz, InvocationHandler handler) {
        return newProxyInstance(Solon.context(), clazz, handler);
    }

    /**
     * 构建代理实例
     *
     * @param context 应用上下文
     * @param clazz   类
     * @param handler 调用处理
     */
    public static <T> T newProxyInstance(AppContext context, Class<T> clazz, InvocationHandler handler) {
        assert context != null;
        assert clazz != null;

        if (clazz.isInterface()) {
            return (T) Proxy.newProxyInstance(context.getClassLoader(), new Class[]{clazz}, handler);
        } else {
            Object proxy = null;
            //支持 AOT 生成的代理 (支持 Graalvm Native  打包)
            if (NativeDetector.isNotAotRuntime()) {
                proxy = AotProxy.newProxyInstance(context, handler, clazz, new Object[0]);
            }

            if (proxy == null) {
                //支持 ASM（兼容旧的包，不支持 Graalvm Native  打包）
                proxy = AsmProxy.newProxyInstance(context, handler, clazz, null, new Object[0]);
            }

            return (T) proxy;
        }
    }
}