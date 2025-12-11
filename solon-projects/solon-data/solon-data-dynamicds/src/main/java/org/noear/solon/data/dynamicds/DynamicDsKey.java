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

import org.noear.solon.core.FactoryManager;
import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.core.util.ScopeLocal;
import org.noear.solon.core.util.SupplierEx;

/**
 * 动态数据源 Key 管理
 *
 * @author noear
 * @since 2.5
 * @since 3.1
 */
public class DynamicDsKey {
    static ScopeLocal<String> targetLocal = FactoryManager.getGlobal().newScopeLocal(DynamicDsKey.class);

    /**
     * 获取当前 key
     */
    public static String current() {
        return targetLocal.get();
    }


    public static void use(String name, RunnableEx runnable) throws Throwable {
        targetLocal.with(name, () -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T use(String name, SupplierEx<T> supplier) throws Throwable {
       return targetLocal.with(name, supplier::get);
    }


    /**
     * 移除 key
     *
     * @deprecated 3.7.4 请使用 {@link #use(String, RunnableEx)} ()}
     */
    @Deprecated
    public static void remove() {
        targetLocal.remove();
    }

    /**
     * 使用 key
     *
     * @deprecated 3.7.4 请使用 {@link #use(String, RunnableEx)}
     */
    @Deprecated
    public static void use(String name) {
        if (name == null) {
            targetLocal.remove();
        } else {
            targetLocal.set(name);
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