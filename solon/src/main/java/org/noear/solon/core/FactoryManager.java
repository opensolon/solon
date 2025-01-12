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
package org.noear.solon.core;

import org.noear.solon.core.util.ClassUtil;

import java.util.function.BiFunction;

/**
 * 工厂管理器（后续会迁入更多的工厂管理）
 *
 * @author noear
 * @since 2.5
 */
public final class FactoryManager {
    private static final FactoryManager global = new FactoryManager();

    public static FactoryManager getGlobal() {
        return global;
    }

    public FactoryManager(){
        mvcFactory = ClassUtil.tryInstance("org.noear.solon.core.mvc.MvcFactoryDefault");
    }

    //////////
    //
    // threadLocalFactory
    //
    private BiFunction<Class<?>, Boolean, ThreadLocal> threadLocalFactory = (applyFor, inheritance0) -> {
        if (inheritance0) {
            return new InheritableThreadLocal<>();
        } else {
            return new ThreadLocal<>();
        }
    };

    /**
     * 配置线程状态管理工厂
     */
    public <T> void threadLocalFactory(BiFunction<Class<?>, Boolean, ThreadLocal> factory) {
        if (factory != null) {
            threadLocalFactory = factory;
        }
    }

    /**
     * 创建线程状态
     *
     * @param applyFor     申请应用的类
     * @param inheritance0 原始可继随性
     */
    public <T> ThreadLocal<T> newThreadLocal(Class<?> applyFor, boolean inheritance0) {
        return threadLocalFactory.apply(applyFor, inheritance0);
    }

    //////////
    //
    // loadBalanceFactory 对接
    //
    protected LoadBalance.Factory loadBalanceFactory = (g, s) -> null;

    /**
     * 配置负载工厂
     */
    public void loadBalanceFactory(LoadBalance.Factory factory) {
        if (factory != null) {
            loadBalanceFactory = factory;
        }
    }

    /**
     * 创建负载
     */
    public LoadBalance newLoadBalance(String group, String service) {
        return loadBalanceFactory.create(group, service);
    }


    //////////
    //
    // mvcFactory 对接
    //
    private MvcFactory mvcFactory;

    public boolean hasMvcFactory() {
        return mvcFactory != null;
    }

    public MvcFactory mvcFactory() {
        if (mvcFactory == null) {
            throw new IllegalStateException("The 'solon-mvc' plugin is missing");
        }
        return mvcFactory;
    }

    public void mvcFactory(MvcFactory factory) {
        if (factory != null) {
            mvcFactory = factory;
        }
    }
}
