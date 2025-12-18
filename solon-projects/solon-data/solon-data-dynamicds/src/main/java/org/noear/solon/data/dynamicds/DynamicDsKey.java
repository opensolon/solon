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
package org.noear.solon.data.dynamicds;

import org.noear.solon.util.CallableTx;
import org.noear.solon.util.RunnableTx;
import org.noear.solon.util.ScopeLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动态数据源 Key 管理
 *
 * @author noear
 * @since 2.5
 * @since 3.1
 */
public class DynamicDsKey {
    private static final Logger log = LoggerFactory.getLogger(DynamicDsKey.class);
    private static final ScopeLocal<String> LOCAL = ScopeLocal.newInstance(DynamicDsKey.class);


    /**
     * @since 3.8.0
     * */
    public static <X extends Throwable> void with(String name, RunnableTx<X> runnable) throws X {
        LOCAL.withOrThrow(name, runnable);
    }

    /**
     * @since 3.8.0
     * */
    public static <T, X extends Throwable> T with(String name, CallableTx<T,X> callable) throws X {
        return LOCAL.withOrThrow(name, callable);
    }

    /**
     * 获取当前 key
     */
    public static String current() {
        return LOCAL.get();
    }


    /**
     * 移除 key
     *
     * @deprecated 3.8.0 请使用 {@link #with(String, RunnableTx)} ()}
     */
    @Deprecated
    public static void remove() {
        log.warn("DynamicDsKey.remove will be removed, please use DynamicDsKey.with");

        LOCAL.remove();
    }

    /**
     * 使用 key
     *
     * @deprecated 3.8.0 请使用 {@link #with(String, RunnableTx)}
     */
    @Deprecated
    public static void use(String name) {
        log.warn("DynamicDsKey.use will be removed, please use DynamicDsKey.with");

        if (name == null) {
            LOCAL.remove();
        } else {
            LOCAL.set(name);
        }
    }

    /**
     * 获取当前 key
     *
     * @deprecated 3.1 {@link #current()}
     */
    @Deprecated
    public static String getCurrent() {
        return current();
    }

    /**
     * 设置当前 key
     *
     * @deprecated 3.1 {@link #use(String)}
     */
    @Deprecated
    public static void setCurrent(String name) {
        use(name);
    }
}