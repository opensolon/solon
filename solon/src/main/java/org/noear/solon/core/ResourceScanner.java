package org.noear.solon.core;

import java.util.Set;
import java.util.function.Predicate;

/**
 * 资源扫描器
 *
 * @author noear
 * @since 1.5
 */
public interface ResourceScanner {

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param classLoader 类加载器
     * @param path   路径
     * @param filter 过滤条件
     */
    Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter);
}
