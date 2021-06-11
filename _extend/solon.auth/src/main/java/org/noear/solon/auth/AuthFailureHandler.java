package org.noear.solon.auth;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

/**
 * @author noear 2021/6/2 created
 */
@FunctionalInterface
public interface AuthFailureHandler {
    void onFailure(Context ctx, Result rst) throws Throwable;
}
