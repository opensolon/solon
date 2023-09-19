package org.noear.solon.core.route;

import org.noear.solon.core.handle.MethodType;

/**
 * 路由记录
 *
 * @author noear
 * @since 1.3
 */
public interface Routing<T> {
    /**
     * 顺序位
     */
    int index();

    /**
     * 路径
     */
    String path();

    /**
     * 方法
     */
    MethodType method();

    /**
     * 路由目标
     */
    T target();

    /**
     * 是否匹配
     */
    boolean matches(MethodType method2, String path2);

    /**
     * 匹配程度（0,不匹配；1,匹配路径；2,完全匹配）
     *
     * @since 2.5
     */
    int degrees(MethodType method2, String path2);
}
