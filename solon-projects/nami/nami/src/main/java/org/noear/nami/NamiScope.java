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
package org.noear.nami;

import org.noear.solon.core.util.CallableTx;
import org.noear.solon.core.util.RunnableTx;
import org.noear.solon.core.util.ScopeLocal;
import org.noear.solon.lang.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nami 域
 *
 * @author noear
 * @since 3.7.4
 */
public class NamiScope {
    private static final ScopeLocal<Map<String, String>> LOCAL = ScopeLocal.newInstance();

    public static <X extends Throwable> void with(RunnableTx<X> runnable) throws X {
        //如果有，传递下去
        Map<String, String> tmp = LOCAL.get();
        if (tmp == null) {
            //如果没有，则新建
            tmp = new LinkedHashMap<>();
        }

        LOCAL.with(tmp, () -> {
            runnable.run();
            return null;
        });
    }

    public static <R, X extends Throwable> R with(CallableTx<R, X> callable) throws X {
        //如果有，传递下去
        Map<String, String> tmp = LOCAL.get();
        if (tmp == null) {
            //如果没有，则新建
            tmp = new LinkedHashMap<>();
        }

        return LOCAL.with(tmp, callable);
    }


    /**
     * 上下文
     */
    public static @Nullable Map<String, String> getData() {
        return LOCAL.get();
    }
}