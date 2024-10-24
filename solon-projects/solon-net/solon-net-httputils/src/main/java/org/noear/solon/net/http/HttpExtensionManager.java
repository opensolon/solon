package org.noear.solon.net.http;

import java.util.*;

/**
 * Http 扩展管理
 *
 * @author noear
 * @since 2.9
 */
public class HttpExtensionManager {
    private static Set<HttpExtension> extensions = new LinkedHashSet<>();

    /**
     * 添加扩展
     */
    public static void add(HttpExtension extension) {
        extensions.add(extension);
    }

    /**
     * 移除扩展
     */
    public static void remove(HttpExtension extension) {
        extensions.remove(extension);
    }

    /**
     * 获取所有扩展
     */
    public static Collection<HttpExtension> getExtensions() {
        return extensions;
    }
}
