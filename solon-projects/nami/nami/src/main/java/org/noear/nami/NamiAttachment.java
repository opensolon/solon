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

import org.noear.solon.util.CallableTx;
import org.noear.solon.util.RunnableTx;
import org.noear.solon.util.ScopeLocal;
import org.noear.solon.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Nami 请求附件（通过 ThreadLocal 切换）
 *
 * @author noear
 * @since 1.4
 */
public final class NamiAttachment {
    private final static Logger log = LoggerFactory.getLogger(NamiAttachment.class);
    private final static ScopeLocal<Map<String, String>> LOCAL = ScopeLocal.newInstance(NamiAttachment.class);


    /**
     * @since 3.8.0
     */
    public static void with(Runnable runnable) {
        Map<String, String> data = LOCAL.get();
        if (data == null) {
            data = new HashMap<>();
        }

        LOCAL.with(data, runnable);
    }

    /**
     * @since 3.8.0
     */
    public static <R> R with(Supplier<R> callable) {
        Map<String, String> data = LOCAL.get();
        if (data == null) {
            data = new HashMap<>();
        }

        return LOCAL.with(data, callable);
    }

    /**
     * @since 3.8.0
     */
    public static <X extends Throwable> void withOrThrow(RunnableTx<X> runnable) throws X {
        Map<String, String> data = LOCAL.get();
        if (data == null) {
            data = new HashMap<>();
        }

        LOCAL.withOrThrow(data, runnable);
    }

    /**
     * @since 3.8.0
     */
    public static <R, X extends Throwable> R withOrThrow(CallableTx<R, X> callable) throws X {
        Map<String, String> data = LOCAL.get();
        if (data == null) {
            data = new HashMap<>();
        }

        return LOCAL.withOrThrow(data, callable);
    }

    /// ///////////////////


    public static @Nullable Map<String, String> getData() {
        return LOCAL.get();
    }

    public static void put(String name, String value) {
        Map<String, String> data = LOCAL.get();
        if (data != null) {
            data.put(name, value);
        } else {
            log.error("Attachment is null, please use `NamiAttachment.with(() -> NamiAttachment.put(k, v))`");
        }
    }

    public static String get(String name) {
        Map<String, String> data = LOCAL.get();
        if (data != null) {
            return data.get(name);
        } else {
            log.error("Attachment is null, please use `NamiAttachment.with(() -> NamiAttachment.get(k))`");
            return null;
        }
    }

    public static void remove(String name) {
        Map<String, String> data = LOCAL.get();
        if (data != null) {
            data.remove(name);
        } else {
            log.error("Attachment is null, please use `NamiAttachment.with(() -> NamiAttachment.remove(k))`");
        }
    }

    public static void clear() {
        LOCAL.set(null);
    }
}