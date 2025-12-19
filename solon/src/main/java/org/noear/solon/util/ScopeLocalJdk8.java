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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * 作用域变量
 *
 * @since 3.8.0
 * */
public class ScopeLocalJdk8<T> implements ScopeLocal<T> {
    private static final Logger log = LoggerFactory.getLogger(ScopeLocalJdk8.class);
    private final ThreadLocal<T> ref = FactoryManager.getGlobal().newThreadLocal(ScopeLocalJdk8.class, false);

    public ScopeLocalJdk8() {

    }

    public ScopeLocalJdk8(Class<?> applyFor) {

    }

    @Override
    public T get() {
        return ref.get();
    }

    @Override
    public void with(T value, Runnable runnable) {
        T bak = ref.get();
        try {
            ref.set(value);
            runnable.run();
        } finally {
            if (bak == null) {
                ref.remove();
            } else {
                ref.set(bak);
            }
        }
    }

    @Override
    public <R> R with(T value, Supplier<R> callable) {
        T bak = ref.get();
        try {
            ref.set(value);
            return callable.get();
        } finally {
            if (bak == null) {
                ref.remove();
            } else {
                ref.set(bak);
            }
        }
    }

    @Override
    public <X extends Throwable> void withOrThrow(T value, RunnableTx<X> runnable) throws X {
        T bak = ref.get();
        try {
            ref.set(value);
            runnable.run();
        } finally {
            if (bak == null) {
                ref.remove();
            } else {
                ref.set(bak);
            }
        }
    }

    @Override
    public <R, X extends Throwable> R withOrThrow(T value, CallableTx<? extends R, X> callable) throws X {
        T bak = ref.get();
        try {
            ref.set(value);
            return callable.call();
        } finally {
            if (bak == null) {
                ref.remove();
            } else {
                ref.set(bak);
            }
        }
    }

    /// ///////////////////////////////////////

    @Override
    public ScopeLocal<T> set(T value) {
        log.warn("ScopeLocal.set will be removed, please use ScopeLocal.with");

        ref.set(value);
        return this;
    }

    @Override
    public void remove() {
        log.warn("ScopeLocal.remove will be removed, please use ScopeLocal.with");

        ref.remove();
    }
}