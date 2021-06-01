package org.noear.solon.extend.auth;

import org.noear.solon.core.handle.Context;

/**
 * 路径匹配器
 *
 * @author noear
 * @since 1.4
 */
public interface PathMatchers {
    boolean matches(Context ctx, String path);
}
