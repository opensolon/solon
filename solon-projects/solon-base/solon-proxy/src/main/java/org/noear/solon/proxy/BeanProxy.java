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

import org.noear.solon.core.BeanWrap;

import java.lang.reflect.InvocationHandler;

/**
 * @author noear
 * @since 1.6
 */
public class BeanProxy implements BeanWrap.Proxy {
    private static final BeanProxy global = new BeanProxy();

    public static BeanProxy getGlobal() {
        return global;
    }

    InvocationHandler handler;

    private BeanProxy() {
    }

    protected BeanProxy(InvocationHandler handler) {
        this.handler = handler;
    }

    /**
     * 获取代理
     */
    @Override
    public Object getProxy(BeanWrap bw, Object bean) {
        return new BeanInvocationHandler(handler, bw, bean).getProxy();
    }
}