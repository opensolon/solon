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
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.proxy.aot.AotProxy;
import org.noear.solon.core.AppContext;
import org.noear.solon.proxy.asm.AsmProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Bean 调用处理
 *
 * @author noear
 * @since 1.5
 * */
public class BeanInvocationHandler implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(BeanInvocationHandler.class);

    private Object target;
    private Object proxy;
    private InvocationHandler handler;
    private final AppContext context;

    /**
     * @since 1.6
     */
    public BeanInvocationHandler(AppContext context, Object target, InvocationHandler handler) {
        this(context, target.getClass(), target, handler);
    }

    /**
     * @since 1.6
     * @since 2.1
     */
    public BeanInvocationHandler(AppContext context, Class<?> clazz, Object target, InvocationHandler handler) {
        this.context = context;
        this.target = target;
        this.handler = handler;

        //支持 AOT 生成的代理 (支持 Graalvm Native  打包)
        if (NativeDetector.isNotAotRuntime()) {
            this.proxy = AotProxy.newProxyInstance(context, this, clazz);
        }

        if (this.proxy == null) {
            //支持 ASM（兼容旧的包，不支持 Graalvm Native  打包）
            this.proxy = AsmProxy.newProxyInstance(context, this, clazz);
        }

        //调试时打印信息
        if (Solon.cfg().isDebugMode()) {
            if (this.proxy != null) {
                log.trace("Proxy class:" + this.proxy.getClass().getName());
            }
        }
    }

    public Object getProxy() {
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (handler == null) {
            method.setAccessible(true);

            Object result = context.methodGet(method).invokeByAspect(target, args);

            return result;
        } else {
            return handler.invoke(target, method, args);
        }
    }
}
