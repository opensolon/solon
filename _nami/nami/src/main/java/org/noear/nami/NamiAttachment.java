package org.noear.nami;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nami 请求附件
 *
 * @author noear
 * @since 1.4
 */
public final class NamiAttachment {
    private final static ThreadLocal<Map<String, String>> threadMap = new InheritableThreadLocal<>();

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
