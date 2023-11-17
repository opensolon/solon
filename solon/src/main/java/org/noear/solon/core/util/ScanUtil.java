package org.noear.solon.core.util;

import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.ResourceScanner;

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
    static ResourceScanner global;

    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        global = ClassUtil.tryInstance("org.noear.solon.extend.impl.ResourceScannerExt");

        if (global == null) {
            global = new ResourceScanner();
        }
    }

    /**
     * 设置扫描器（用户层扩展）
     * */
    public static void setScanner(ResourceScanner scanner) {
        if (scanner != null) {
            ScanUtil.global = scanner;
        }
    }

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param path   路径
     * @param filter 过滤条件
     */
    public static Set<String> scan(String path, Predicate<String> filter) {
        return scan(AppClassLoader.global(), path, filter);
    }

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param classLoader 类加载器
     * @param path   路径
     * @param filter 过滤条件
     */
    public static Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter) {
        return global.scan(classLoader, path, filter);
    }
}