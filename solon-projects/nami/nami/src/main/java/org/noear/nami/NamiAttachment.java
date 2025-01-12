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

import org.noear.solon.core.FactoryManager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nami 请求附件（通过 ThreadLocal 切换）
 *
 * @author noear
 * @since 1.4
 */
public final class NamiAttachment {
    private final static ThreadLocal<Map<String, String>> threadMap = FactoryManager.getGlobal().newThreadLocal(NamiAttachment.class, false);

    private static Map<String, String> getContextMap0() {
        Map<String, String> tmp = threadMap.get();
        if (tmp == null) {
            tmp = new LinkedHashMap<>();
            threadMap.set(tmp);
        }

        return tmp;
    }


    public static Map<String, String> getData() {
        return Collections.unmodifiableMap(getContextMap0());
    }

    public static void put(String name, String value) {
        getContextMap0().put(name, value);
    }

    public static String get(String name) {
        return getContextMap0().get(name);
    }

    public static void remove(String name) {
        getContextMap0().remove(name);
    }

    public static void clear() {
        threadMap.set(null);
    }
}
