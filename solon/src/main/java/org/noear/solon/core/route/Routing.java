package org.noear.solon.core.route;

import org.noear.solon.core.handle.MethodType;

/**
 * 路由记录
 *
 * @author noear
 * @since 1.3
 */
public interface Routing<T> {
    int index();
    String path();
    MethodType method();
    T target();

    boolean matches(MethodType method2, String path2);
}
