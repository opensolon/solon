package org.noear.nami;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.2
 */
public final class NamiContext {
    private final Map<String, String> headers = new LinkedHashMap<>();

    /**
     * 设置头信息
     */
    public NamiContext headerSet(String name, String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * 获取头信息
     */
    public Map<String, String> headers() {
        return headers;
    }

    private final static ThreadLocal<NamiContext> threadLocal = new ThreadLocal<>();

    /**
     * 移除当前线程的上下文
     */
    public static void currentRemove() {
        threadLocal.remove();
    }

    public static NamiContext currentGet() {
        return threadLocal.get();
    }

    /**
     * 获取当前线程的上下文
     */
    public static NamiContext current() {
        NamiContext tmp = threadLocal.get();
        if (tmp == null) {
            tmp = new NamiContext();
            threadLocal.set(tmp);
        }

        return tmp;
    }
}
