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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Nami 请求附件（通过 ThreadLocal 切换）
 *
 * @author noear
 * @since 1.4
 * @since 3.8.1
 */
@Deprecated
public final class NamiAttachment {
    private final static Logger log = LoggerFactory.getLogger(NamiAttachment.class);

    /**
     * @since 3.8.0
     */
    public static <X extends Throwable> void with(RunnableTx<X> runnable) throws X {
        NamiAttach.currentWith(t -> {
            runnable.run();
        });
    }

    /**
     * @since 3.8.0
     */
    public static <R, X extends Throwable> R with(CallableTx<R, X> callable) throws X {
        return NamiAttach.currentWith(t -> {
            return callable.call();
        });
    }

    /**
     * @since 3.8.0
     */
    public static <X extends Throwable> void withOrThrow(RunnableTx<X> runnable) throws X {
        NamiAttach.currentWith(t -> {
            runnable.run();
        });
    }

    /**
     * @since 3.8.0
     */
    public static <R, X extends Throwable> R withOrThrow(CallableTx<R, X> callable) throws X {
        return NamiAttach.currentWith(t -> {
            return callable.call();
        });
    }

    /// ///////////////////


    private static NamiAttach getContextMap0() {
        NamiAttach tmp = NamiAttach.LOCAL.get();
        if (tmp == null) {
            tmp = new NamiAttach();
            NamiAttach.LOCAL.set(tmp);
        }

        return tmp;
    }

    public static Map<String, String> getDataOrNull() {
        NamiAttach tmp = NamiAttach.LOCAL.get();
        if (tmp == null) {
            return null;
        } else {
            return tmp.getData();
        }
    }

    public static Map<String, String> getData() {
        log.warn("NamiAttachment is deprecated, please use `NamiAttach.currentWith(attach -> ...`");

        return getContextMap0().getData();
    }

    public static void put(String name, String value) {
        log.warn("NamiAttachment is deprecated, please use `NamiAttach.currentWith(attach -> ...`");

        getContextMap0().put(name, value);
    }

    public static String get(String name) {
        log.warn("NamiAttachment is deprecated, please use `NamiAttach.currentWith(attach -> ...`");

        return getContextMap0().get(name);
    }

    public static void remove(String name) {
        log.warn("NamiAttachment is deprecated, please use `NamiAttach.currentWith(attach -> ...`");

        getContextMap0().remove(name);
    }

    public static void clear() {
        NamiAttach.LOCAL.remove();
    }
}