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

import org.noear.solon.core.FactoryManager;

/**
 * 作用域变量
 *
 * @since 3.7.4
 * */
public interface ScopeLocal<T> {
    static <T> ScopeLocal<T> newInstance() {
        return FactoryManager.getGlobal().newScopeLocal(ScopeLocal.class);
    }

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
    <R, X extends Throwable> R with(T value, CallableTx<? extends R, X> callable) throws X;

    /// ////////////////////////////

    /**
     * @deprecated 3.7.4
     */
    @Deprecated
    ScopeLocal<T> set(T value);

    /**
     * @deprecated 3.7.4
     */
    @Deprecated
    void remove();
}