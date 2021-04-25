package org.noear.solon.core.route;

import org.noear.solon.core.handle.MethodType;

/**
 * @author noear 2021/4/26 created
 */
public interface Routing<T> {
    int index();
    String path();
    MethodType method();
    T target();

    boolean matches(MethodType method2, String path2);
}
