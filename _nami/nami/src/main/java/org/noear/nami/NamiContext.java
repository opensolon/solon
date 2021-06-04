package org.noear.nami;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nami 请求上下文
 *
 * @author noear
 * @since 1.4
 */
public final class NamiContext {
    private final static ThreadLocal<Map<String, String>> threadLocal = new InheritableThreadLocal<>();

    private final static Map<String, String> getContextMap0() {
        Map<String, String> tmp = threadLocal.get();
        if (tmp == null) {
            tmp = new LinkedHashMap<>();
            threadLocal.set(tmp);
        }

        return tmp;
    }


    public static Map<String, String> getContextMap() {
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
        getContextMap0().clear();
    }
}
