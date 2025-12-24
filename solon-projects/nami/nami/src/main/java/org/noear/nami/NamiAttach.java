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

import org.noear.solon.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Nami 请求附件
 *
 * @author noear
 * @since 3.8.1
 */
public class NamiAttach {
    private final static Logger log = LoggerFactory.getLogger(NamiAttach.class);
    protected final static ScopeLocal<NamiAttach> LOCAL = ScopeLocal.newInstance(NamiAttach.class);

    /**
     * 获取当前线程的上下文
     */
    public static NamiAttach current() {
        return LOCAL.get();
    }

    public static <T> T ifPresent(Function<NamiAttach, T> consumer) {
        NamiAttach tmp = current();

        if (tmp != null) {
            return consumer.apply(tmp);
        } else {
            log.error("Attach is null, please use `NamiAttach.currentWith(attach -> ...`");
            return null;
        }
    }

    /**
     * 使用当前域的上下文
     *
     * @since 3.8.1
     */
    public static <X extends Throwable> void currentWith(ConsumerTx<NamiAttach, X> runnable) throws X {
        NamiAttach tmp = LOCAL.get();

        if (tmp == null) {
            tmp = new NamiAttach();
        }

        LOCAL.withOrThrow(tmp, () -> {
            runnable.accept(LOCAL.get());
        });
    }

    /**
     * 使用当前域的上下文
     *
     * @since 3.8.1
     */
    public static <R, X extends Throwable> R currentWith(FunctionTx<NamiAttach, R, X> callable) throws X {
        NamiAttach tmp = LOCAL.get();
        if (tmp == null) {
            tmp = new NamiAttach();
        }

        return LOCAL.withOrThrow(tmp, () -> {
            return callable.apply(LOCAL.get());
        });
    }

    /// ///////////////////////


    private Map<String, String> data = new HashMap<>();

    public Map<String, String> getData() {
        return data;
    }

    public String put(String key, String value) {
        return data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public String remove(String key) {
        return data.remove(key);
    }

    public void clear() {
        data.clear();
    }
}
