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

import org.noear.solon.core.handle.*;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.util.ScopeLocalJdk8;
import org.noear.solon.util.ScopeLocal;

import java.lang.reflect.AnnotatedElement;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    public FactoryManager() {
        actionLoaderFactory = ClassUtil.tryInstance("org.noear.solon.extend.impl.ActionLoaderFactoryExt");
    }

    /// ///////
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
    public void threadLocalFactory(BiFunction<Class<?>, Boolean, ThreadLocal> factory) {
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

    /// ///////
    //
    // scopeLocalFactory 对接
    //

    private Function<Class<?>, ScopeLocal> scopeLocalFactory = ScopeLocalJdk8::new;

    public void scopeLocalFactory(Function<Class<?>, ScopeLocal> factory) {
        if (factory != null) {
            this.scopeLocalFactory = factory;
        }
    }

    public <T> ScopeLocal<T> newScopeLocal(Class<?> applyFor) {
        return scopeLocalFactory.apply(applyFor);
    }

    /// ///////
    //
    // loadBalanceFactory 对接
    //
    private LoadBalance.Factory loadBalanceFactory = (g, s) -> null;

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


    /// ///////
    //
    // mvcFactory 对接
    //

    /**
     * @deprecated 3.6
     *
     */
    @Deprecated
    public boolean hasMvcFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated 3.6
     *
     */
    public MvcFactory mvcFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated 3.6
     *
     */
    public void mvcFactory(MvcFactory factory) {
        throw new UnsupportedOperationException();
    }

    /// ///////
    //
    // loadBalanceFactory 对接
    //

    private ActionLoaderFactory actionLoaderFactory;

    public ActionLoaderFactory actionLoaderFactory() {
        if (actionLoaderFactory == null) {
            throw new IllegalStateException("The 'solon-handle' plugin is missing");
        }

        return actionLoaderFactory;
    }

    public void actionLoaderFactory(ActionLoaderFactory factory) {
        if (factory != null) {
            this.actionLoaderFactory = factory;
        }
    }

    /**
     * 创建动作加载器
     */
    public ActionLoader createLoader(BeanWrap wrap) {
        return createLoader(wrap, wrap.remoting());
    }

    /**
     * 创建动作加载器
     */
    public ActionLoader createLoader(BeanWrap wrap, boolean remoting) {
        return actionLoaderFactory().createLoader(wrap, remoting);
    }

    /**
     * 分析动作参数
     */
    public void resolveActionParamTry(ActionParam vo, AnnotatedElement element) {
        if (actionLoaderFactory != null) {
            actionLoaderFactory.resolveActionParam(vo, element);
        }
    }

    public EntityConverter entityConverterDefault() {
        return actionLoaderFactory().getEntityConverterDefault();
    }
}
