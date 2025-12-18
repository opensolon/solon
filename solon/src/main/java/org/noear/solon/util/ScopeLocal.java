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
package org.noear.solon.util;

import org.noear.solon.core.FactoryManager;

import java.util.function.Supplier;

/**
 * 作用域变量
 *
 * <pre>{@code
 * static ScopeLocal<User> LOCAL = ScopeLocal.newInstance();
 *
 * LOCAL.with(new User(), () -> {
 *     String name = LOCAL.get().getName();
 * });
 * }</pre>
 * @since 3.8.0
 * */
public interface ScopeLocal<T> {
    static <T> ScopeLocal<T> newInstance() {
        return newInstance(ScopeLocal.class);
    }

    static <T> ScopeLocal<T> newInstance(Class<?> applyFor) {
        return FactoryManager.getGlobal().newScopeLocal(applyFor);
    }

    /// //////////////////////////


    /**
     * 获取
     */
    T get();

    /**
     * 使用值并运行
     */
    void with(T value, Runnable runnable);

    /**
     * 使用值并调用
     */
    <R> R with(T value, Supplier<R> callable);

    /**
     * 使用值并运行
     */
    <X extends Throwable> void withOrThrow(T value, RunnableTx<X> runnable) throws X;

    /**
     * 使用值并调用
     */
    <R, X extends Throwable> R withOrThrow(T value, CallableTx<? extends R, X> callable) throws X;

    /// ////////////////////////////

    /**
     * @deprecated 3.8.0
     */
    @Deprecated
    ScopeLocal<T> set(T value);

    /**
     * @deprecated 3.8.0
     */
    @Deprecated
    void remove();
}