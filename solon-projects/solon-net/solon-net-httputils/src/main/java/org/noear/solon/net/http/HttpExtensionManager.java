package org.noear.solon.net.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Http 扩展管理
 *
 * @author noear
 * @since 2.9
 */
public class HttpExtensionManager {
    private static List<HttpExtension> extensions = new ArrayList<>();

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
