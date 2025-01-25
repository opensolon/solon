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

/**
 * 动态数据源 Key 管理
 *
 * @author noear
 * @since 2.5
 * @since 3.1
 */
public class DynamicDsKey {
    static ThreadLocal<String> targetThreadLocal = FactoryManager.getGlobal().newThreadLocal(DynamicDsKey.class, false);

    /**
     * 移除 key
     */
    public static void remove() {
        targetThreadLocal.remove();
    }

    /**
     * 获取当前 key
     */
    public static String current() {
        return targetThreadLocal.get();
    }

    /**
     * 使用 key
     */
    public static void use(String name) {
        if (name == null) {
            targetThreadLocal.remove();
        } else {
            targetThreadLocal.set(name);
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
