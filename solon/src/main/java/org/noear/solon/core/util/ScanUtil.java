package org.noear.solon.core.util;

import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.ResourceScanner;
import org.noear.solon.core.ResourceScannerImpl;

import java.util.*;
import java.util.function.Predicate;

/**
 * 资源扫描工具（用于扫描插件配置等资源...）
 *
 * @author noear
 * @author 馒头虫/瓢虫
 * @since 1.0
 * */
public class ScanUtil {
    static ResourceScanner scanner = new ResourceScannerImpl();

    /**
     * 设置扫描器
     * */
    public static void setScanner(ResourceScanner scanner) {
        if (scanner != null) {
            ScanUtil.scanner = scanner;
        }
    }

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param path   路径
     * @param filter 过滤条件
     */
    public static Set<String> scan(String path, Predicate<String> filter) {
        return scan(JarClassLoader.global(), path, filter);
    }

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param classLoader 类加载器
     * @param path   路径
     * @param filter 过滤条件
     */
    public static Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter) {
        return scanner.scan(classLoader, path, filter);
    }
}